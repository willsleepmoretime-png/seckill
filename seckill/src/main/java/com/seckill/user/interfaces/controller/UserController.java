    package com.seckill.user.interfaces.controller;

    import com.seckill.common.context.UserContext;
    import com.seckill.common.result.Result;
    import com.seckill.order.interfaces.vo.OrderVO;
    import com.seckill.user.interfaces.dto.UserRechargeDTO;
    import com.seckill.user.interfaces.vo.UserLoginVO;
    import com.seckill.user.application.service.UserService;
    import com.seckill.user.domain.entity.User;
    import com.seckill.user.interfaces.dto.UserLoginDTO;
    import com.seckill.user.interfaces.dto.UserRegisterDTO;
    import org.springframework.web.bind.annotation.*;

    import java.math.BigDecimal;

    @RestController
    @RequestMapping("/api/user")
    public class UserController {

        private final UserService userService;
        UserController(UserService userService){
            this.userService=userService;
        }

        @PostMapping("/register")
        public Result<Void> register(@RequestBody UserRegisterDTO dto) {
            userService.register(dto.getPhone(), dto.getPassword());
            return Result.success();
        }

        @PostMapping("/login")
        public Result<UserLoginVO> login(@RequestBody UserLoginDTO dto) {
           UserLoginVO vo = userService.login(dto.getPhone(), dto.getPassword());
            return Result.success(vo);
        }

        @PostMapping("/recharge")
        public Result<Void>  recharge(@RequestBody UserRechargeDTO dto){
            Long userId = UserContext.getUserId();
            userService.recharge(userId, dto.getAmount());
            return Result.success();
        }


    }
