    package com.seckill.common.entity;

    import com.baomidou.mybatisplus.annotation.IdType;
    import com.baomidou.mybatisplus.annotation.TableId;
    import lombok.Getter;


    import java.time.LocalDateTime;


    @Getter
    public abstract class BaseEntity {

        @TableId(type = IdType.ASSIGN_ID)
        private Long id;

        private LocalDateTime createTime;
        private LocalDateTime updateTime;
    }
