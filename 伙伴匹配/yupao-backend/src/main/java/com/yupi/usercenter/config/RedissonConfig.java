package com.yupi.usercenter.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.CborJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author xuao
 * @Date 2024/9/5 18:53
 * @Description:
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {

    private String host;
    private String port;

    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        String address = String.format("redis://%s:%s",host,port);
        System.out.println("address:"+address);
        config.useSingleServer().setAddress(address).setPassword("123456");
        config.setCodec(StringCodec.INSTANCE);
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
