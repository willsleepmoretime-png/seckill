package com.seckill.common.context;

public class UserContext {

    private  static final ThreadLocal<Long> CURRENT_USER=new ThreadLocal<>();
    private UserContext(){}

    public static void setUserId(Long userId) {
        CURRENT_USER.set(userId);
    }

    public static Long getUserId() {
        return CURRENT_USER.get();
    }

    public static Long requiredUserId(){
        Long userId=CURRENT_USER.get();
        if(userId==null){
            throw new IllegalArgumentException("当前线程未设置 userId,可能拦截器未生效");
        }
        return userId;
    }

    public static void clear(){
        CURRENT_USER.remove();
    }
}
