package com.yupi.usercenter;

import com.yupi.usercenter.mapper.UserMapper;
import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.service.UserService;
import com.yupi.usercenter.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@SpringBootTest
class UserCenterApplicationTests {


    @Resource
    UserService userService;
    @Resource
    UserMapper userMapper;
    //线程池 50
    //一万条数据 每批次插入500 10个线程 34672
    //一万条数据 每批次1000 10个线程 34152
    //一万条数据 每批次1000 15个线程 39470
    ///一万条数据 每批次1000 20个线程 31627
    ///一万条数据 每批次1000 40个线程 35653
    ///一万条数据 每批次1000 100个线程 33098
    ///一万条数据 每批次1000 200个线程 33325

    ///十万条数据 每批次1000 200个线程 33325
    ///十万条数据 每批次2000 200个线程 33325

    //线程池 15
    //一万条数据 每批次插入 670 15个线程 38890
    //一万条数据 每批次330 30个线程 35790
    //一万条数据 每批次1000 100个线程 32521
    //一万条数据 每批次1000 150个线程 42712
    @Test
    public void contextLoads() throws Exception {
        StopWatch sw = new StopWatch();
        sw.start();

        // Consider using a smaller thread pool size if necessary
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                50, 100, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(200)
        );

        // Adjust connection pool size based on load
        CompletableFuture<Void>[] futures = new CompletableFuture[10];
        final int SIZE = 50000;

        for (int j = 0; j <10; j++) {
            final int index = j; // for lambda expression
            futures[j] = CompletableFuture.runAsync(() -> {
                ArrayList<User> users = new ArrayList<>();
                for (int i = SIZE; i < SIZE + 10000; i++) {
                    User user = new User();
                    user.setUsername("用户" + index + " j" + i);
                    user.setUserAccount("xuao" + index + " j" + i);
                    user.setAvatarUrl("");
                    user.setGender(0);
                    user.setUserPassword(DigestUtils.md5DigestAsHex("12345678".getBytes(StandardCharsets.UTF_8)));
                    user.setPhone("123456");
                    user.setEmail("1234@qq.com");
                    user.setUserStatus(0);
                    user.setUserRole(0);
                    user.setPlanetCode(String.valueOf(i));
                    user.setTags("[]");
                    users.add(user);
                }
                System.out.println(Thread.currentThread().getName());

                userService.saveBatch(users, users.size());
            }, threadPoolExecutor);
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures);
        allOf.thenRun(() -> {
            sw.stop();
            System.out.println(Thread.currentThread().getName() + " 耗时：" + sw.getTotalTimeMillis());
        }).join();

        System.out.println(Thread.currentThread().getName() + "66666");
    }



}

