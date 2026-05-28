    package com.seckill.user.domain.constant;

    import com.baomidou.mybatisplus.annotation.EnumValue;

    public enum  UserStatusEnum {
        NORMAL(0,"正常"),
        DISABLED(1, "禁用");

        @EnumValue
        private final int code;

        private final String msg;
        UserStatusEnum(int code,String msg){
            this.code=code;
            this.msg=msg;

        }
        public int value(){
            return code;
        }

        public String getMsg(){
            return msg;
        }

        public static  UserStatusEnum fromCode(Integer code){
            //传入的是数据中的0,或者1
            if(code==null){
                throw new IllegalArgumentException("code 不能为 null");
            }
            for (UserStatusEnum status : UserStatusEnum.values()){
               if(code.equals(status.value())){
                   return status;
               }
            }
            throw  new IllegalArgumentException("未知 code: " + code);
        }

    }
