package com.yupi.usercenter.service;

import com.yupi.usercenter.common.MinDistanceUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Author xuao
 * @Date 2024/11/3 14:20
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MinDistanceTest {


    @Test
    public static void main(String[] args) {
        String str1 = "大一二";
        String str2 = "大二一";
        System.out.println("The edit distance between \"" + str1 + "\" and \"" + str2 + "\" is: " + MinDistanceUtils.minDistance(str1, str2));
    }
}
