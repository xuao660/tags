package com.yupi.usercenter;

// [加入编程导航](https://www.code-nav.cn/) 深耕编程提升【两年半】、国内净值【最高】的编程社群、用心服务【20000+】求学者、帮你自学编程【不走弯路】

import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

import java.util.Optional;

/**
 * 启动类
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@EnableRedisHttpSession
@SpringBootApplication
@MapperScan("com.yupi.usercenter.mapper")
public class UserCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserCenterApplication.class, args);
    }

    @Autowired
    private ServerProperties serverProperties;

    @Bean
    public CookieSerializer cookieSerializer() {
        // 解决cookiePath会多一个"/" 的问题
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        String contextPath = Optional.ofNullable(serverProperties).map(ServerProperties::getServlet)
                .map(ServerProperties.Servlet::getContextPath).orElse(null);
        // 当配置了context path 时设置下cookie path ; 防止cookie path 变成 contextPath + /
        if (!StringUtils.isEmpty(contextPath)) {
            serializer.setCookiePath(contextPath);
        }
        serializer.setUseHttpOnlyCookie(true);
        serializer.setUseSecureCookie(false);
        // 干掉 SameSite=Lax
        serializer.setSameSite(null);
        return serializer;
    }
}

// https://github.com/liyupi