    package com.seckill.common.entity;

    import com.baomidou.mybatisplus.annotation.FieldFill;
    import com.baomidou.mybatisplus.annotation.IdType;
    import com.baomidou.mybatisplus.annotation.TableField;
    import com.baomidou.mybatisplus.annotation.TableId;
    import lombok.Getter;
    import lombok.Setter;


    import java.time.LocalDateTime;


    @Getter
    @Setter   // Setter 也得有,不然 MP 没法回填 id
    public abstract class BaseEntity {

        @TableId(type = IdType.ASSIGN_ID)
        private Long id;

        @TableField(fill = FieldFill.INSERT)
        private LocalDateTime createTime;

        @TableField(fill = FieldFill.INSERT_UPDATE)
        private LocalDateTime updateTime;
    }