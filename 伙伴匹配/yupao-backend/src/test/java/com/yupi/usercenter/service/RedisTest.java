package com.yupi.usercenter.service;

import com.yupi.usercenter.model.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Author xuao
 * @Date 2024/8/29 20:21
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Test
    public void test(){
        redisTemplate.opsForValue().set("name:1","xuao");
        redisTemplate.opsForValue().set("name:2","徐奥");
        stringRedisTemplate.opsForValue().set("name:3","xuao");
        stringRedisTemplate.opsForValue().set("name:4","徐奥");
        String s1 = (String) redisTemplate.opsForValue().get("name:1");
        String s2 = (String) redisTemplate.opsForValue().get("name:2");
        String s3 = (String) stringRedisTemplate.opsForValue().get("name:3");
        String s4 = (String) stringRedisTemplate.opsForValue().get("name:4");
        System.out.println(s1+" "+s2+" "+s3+" "+s4);

        User user = new User();
        user.setUserAccount("徐奥");
        user.setId(1234554L);
        redisTemplate.opsForValue().set("name:5",user);
        stringRedisTemplate.opsForValue().set("name:6",user.toString());

    }}
