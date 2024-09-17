package com.yupi.usercenter.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.annotation.ApplicationScope;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * @Author xuao
 * @Date 2024/9/5 19:11
 * @Description:
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedissonTest {

    @Resource
    RedissonClient redissonClient;

    @Test
    public void test(){
        ArrayList<String> list = new ArrayList<>();
        list.add("1111");
        RList<Object> clientList = redissonClient.getList("list");
        clientList.add("徐奥");
        clientList.add("xuao");

        System.out.println("redis:"+clientList.get(0));
    }
}
