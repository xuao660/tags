//package com.yupi.usercenter.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
///**
// * @Author xuao
// * @Date 2024/7/30 22:16
// * @Description:
// */
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**") // 所有接口
//                .allowCredentials(true) // 是否发送cookie
//                .allowedOriginPatterns("*") // 支持域
//                .allowedMethods("GET", "POST", "PUT", "DELETE") // 支持方法
//                .allowedHeaders("*")
//                .exposedHeaders("*");
//
//    }
//}
//
