package com.yupi.usercenter.service;

import com.yupi.usercenter.service.impl.TeamServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author xuao
 * @Date 2024/10/2 12:46
 * @Description:
 */
@Slf4j
@SpringBootTest
public class TeamServiceTest {

    @Autowired
    TeamServiceImpl teamService;
    @Test
    void test() throws Exception {
        //非事务方法调用事务方法，非事务方法抛出异常
        //结果：事务失效，产生脏数据，异常发生前的数据插入到了数据库，异常发生后的数据没有插入
//        teamService.testA("A");

        //非事务方法调用事务方法，事务方法抛出异常
        //结果：事务失效，产生脏数据，异常发生前的数据都插入到了数据库，异常发生后的数据没有插入
        //解决方法：
        // TeamServiceImpl o = (TeamServiceImpl) AopContext.currentProxy();
        // o.testB(param);
        // B中的事务生效，无脏数据入库，A方法没有事务

        teamService.testA("B");

        //事务方法调用事务方法，第一个事务方法抛出异常
        //结果：事务生效
//        teamService.testA("A");

        //事务方法调用事务方法，第二个事务方法抛出异常
        //结果：事务生效
//        teamService.testA("B");


        //事务方法调用非事务方法，事务方法抛出异常
        //结果：事务生效，testA方法中事务生效，包括testB的脏数据都回滚了
//        teamService.testA("A");

        //事务方法调用非事务方法，非事务方法抛出异常
        //结果：事务生效
        teamService.testA("B");
        //        TeamServiceImpl o = (TeamServiceImpl) AopContext.currentProxy();

    }
}
