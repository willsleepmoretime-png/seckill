package com.seckill;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RedisConfigTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void testRedisConnection() {
        // 1. 写
        redisTemplate.opsForValue().set("test:hello", "world", 60, TimeUnit.SECONDS);

        // 2. 读
        Object value = redisTemplate.opsForValue().get("test:hello");
        assertEquals("world", value);

        // 3. 删
        Boolean deleted = redisTemplate.delete("test:hello");
        assertTrue(deleted);

        System.out.println("✅ Redis 读写测试通过");
    }

    @Test
    void testStoreObject() {
        // 测试存对象(验证 JSON 序列化器)
        UserDemo user = new UserDemo(1L, "albatross", "13800138000");
        redisTemplate.opsForValue().set("test:user", user, 60, TimeUnit.SECONDS);

        Object retrieved = redisTemplate.opsForValue().get("test:user");
        System.out.println("✅ 取回的对象: " + retrieved);
        assertNotNull(retrieved);

        redisTemplate.delete("test:user");
    }

    // 内部类做演示用
    public static class UserDemo {
        private Long id;
        private String name;
        private String phone;

        public UserDemo() {}   // ⚠️ JSON 反序列化必须有无参构造

        public UserDemo(Long id, String name, String phone) {
            this.id = id;
            this.name = name;
            this.phone = phone;
        }

        // getter/setter 省略,自己用 IDEA 生成或加 @Data
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        @Override
        public String toString() {
            return "UserDemo{id=" + id + ", name='" + name + "', phone='" + phone + "'}";
        }
    }
}