package com.seckill.seckill.application.service.Impl;


import com.google.common.hash.BloomFilter;
import com.seckill.common.context.UserContext;
import com.seckill.common.exception.BusinessException;
import com.seckill.common.result.ResultCode;
import com.seckill.failurelog.domain.constant.MqMessageTypeEnum;
import com.seckill.goods.domain.repository.GoodsRepository;
import com.seckill.infrastructure.config.RabbitConfig;
import com.seckill.infrastructure.message.SeckillCorrelationData;
import com.seckill.infrastructure.message.SeckillMessage;
import com.seckill.order.application.service.OrderService;
import com.seckill.order.domain.constant.OrderStatusEnum;
import com.seckill.order.domain.entity.Order;
import com.seckill.order.domain.repository.OrderRepository;
import com.seckill.order.interfaces.vo.OrderVO;
import com.seckill.seckill.application.service.MultiLevelCacheService;
import com.seckill.seckill.application.service.SeckillService;
import com.seckill.seckill.application.service.StockService;
import com.seckill.seckill.domain.entity.Seckill;
import com.seckill.seckill.domain.repository.SeckillRepository;
import com.seckill.seckill.interfaces.assembler.SeckillAssembler;
import com.seckill.seckill.interfaces.dto.SeckillCreateDTO;
import com.seckill.seckill.interfaces.vo.SeckillVO;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeckillServiceImpl implements SeckillService {

    private final SeckillRepository seckillRepository;
    private final StockService stockService;
    private final OrderService orderService;
    private final GoodsRepository goodsRepository;
    private final RabbitTemplate rabbitTemplate;
    private final OrderRepository orderRepository;
    private final MultiLevelCacheService multiLevelCacheService;

    @Resource(name = "seckillBloomFilter")
    private BloomFilter<Long> seckillBloomFilter;
    //创建correlation 消息 用来确认 你的应用 ──①──> broker  这段信息的内容


    @Override
    public SeckillVO createSeckill(SeckillCreateDTO dto) {
        // 1. DTO → Entity
        Seckill seckill = dto.toEntity( );

        // 2. 持久化
        seckillRepository.save(seckill);
        seckillBloomFilter.put(seckill.getId());
        multiLevelCacheService.invalidateSeckillDetail(seckill.getId());
        multiLevelCacheService.invalidateSeckillList();
        // 3. Entity → VO 返回
        return SeckillAssembler.toVO(seckill);
    }

    @Override
    public SeckillVO getSeckillInfo(Long seckillId) {
        Seckill seckill = multiLevelCacheService.getSeckillDetail(seckillId);
        if (seckill == null) {
            throw new BusinessException(ResultCode.SECKILL_NOT_EXISTS);
        }
        return SeckillAssembler.toVO(seckill);
    }


    @Override
    public List<SeckillVO> listInProgress(){

       return  multiLevelCacheService.getListSeckill()
               .stream()
               .map(SeckillAssembler::toVO)
               .collect(Collectors.toList());
    }


    //假原子性 DB链接占用 不一致性（DB回滚，但是broker 照样建单）如果加了@Transactional
    @Override
    public void doSeckill(Long seckillId){
        Long userId= UserContext.getUserId();

        Seckill seckill=seckillRepository.findById(seckillId);
        SeckillMessage msg=new SeckillMessage(userId, seckillId);
        //创建correlation 消息 用来确认 你的应用 ──①──> broker  这段信息的内容
        CorrelationData ComfirmMsg=new SeckillCorrelationData(
                userId + seckillId+"ORDER_CREATE",   // 唯一 id
                msg,
                MqMessageTypeEnum.ORDER_CREATE
        );

        //创建correlation 消息 用来确认 交换机 ──①──> 队列  这段信息的内容
        CorrelationData ReturnMsg=new SeckillCorrelationData(
                userId+seckillId+"ORDER_TIMEOUT",
                msg,
                MqMessageTypeEnum.ORDER_TIMEOUT
        );

        if(seckill==null){
            throw new BusinessException(ResultCode.SECKILL_NOT_EXISTS);
        }
        Long r = stockService.tryDeduct(seckillId, userId);
        switch (r.intValue()) {     // 转 int 好匹配
            case 0  -> throw new BusinessException(ResultCode.NO_STOCK);
            case -1 -> throw new BusinessException(ResultCode.NO_PREHEAT);
            case 2  -> throw new BusinessException(ResultCode.NO_REPEAT_BUY);
            case 1  -> { /* 成功，什么都不做，往下走 */ }
            default -> throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }

        //向前端发送状态码，正常链路
        rabbitTemplate.convertAndSend(
                RabbitConfig.ORDER_EXCHANGE,
                RabbitConfig.ORDER_ROUTING,
                msg,
                ComfirmMsg
        );
        //延迟队列
        rabbitTemplate.convertAndSend(
                RabbitConfig.DELAY_EXCHANGE,
                RabbitConfig.DELAY_ROUTING,
                msg,
                message -> {
                    message.getMessageProperties().setHeader("messageId",ReturnMsg.getId());
                    message.getMessageProperties().setHeader("userId",userId);
                    message.getMessageProperties().setHeader("seckillId",seckillId);
                    message.getMessageProperties().setHeader("messageType",MqMessageTypeEnum.ORDER_CREATE.name());
                    return message;
                }
        );
    }

    //生产者
    @Override
    @Transactional
    public void handleSeckillOrder(SeckillMessage msg) {
        Long userId = msg.getUserId();
        Long seckillId = msg.getSeckillId();

        // 1. 幂等判断:直接查 order_info,避免冗余关联表
        if (orderRepository.findByMsg(userId, seckillId) != null) {
           return ;
        }
        // 2. 查活动 + 商品名
        Seckill seckill = seckillRepository.findById(seckillId);
        String goodsName = goodsRepository.findById(seckill.getGoodsId())
                .orElseThrow(() -> new BusinessException(ResultCode.GOODS_NOT_FOUND))
                .getName();
        // 3. 调 order 域建单(DB 唯一索引 uk_user_seckill 兜底幂等)
        orderService.createSeckillOrder(
                userId, seckill.getGoodsId(), seckillId, goodsName, seckill.getSeckillPrice());
    }


    //生产着 延迟队列的
    @Transactional
    @Override
    public void cancelIfUnpaid(SeckillMessage msg) {
        Long seckillId = msg.getSeckillId();
        Long userId = msg.getUserId();

        // 1. 查订单（order_info）
        Order order = orderRepository.findByMsg(userId, seckillId);

        // 2. 订单不存在 → 直接结束
        //    可能落库还没完成、或已被处理，不是错误，静默返回
        if (order == null) {
            return;
        }

        // 3. 订单不是待支付 → 啥也不做
        //    已支付：用户正常付了，不能取消
        //    已取消：之前取消过了（重复消费），幂等返回
        if (order.getStatus() != OrderStatusEnum.NOT_PAYMENT) {
            return;
        }

        // 4. 是待支付 → 取消订单（带乐观锁）
        order.cancel();
        boolean ok = orderRepository.updateById(order);   // 拿到返回值
        if (!ok) {
            return;   // 撞车了，订单已被支付改走，绝对不能回补库存
        }
        // entity 内部改状态（含状态校验）防撞车

        // 5. 取消成功 → 回补 Redis 库存（incr + srem）
        stockService.rollback(seckillId, userId);
    }

}
