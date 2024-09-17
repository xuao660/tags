package com.yupi.usercenter.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author xuao
 * @Date 2024/9/7 8:27
 * @Description:
 */
@Slf4j
@EnableScheduling
@Component
public class PreCacheJob {

    @Resource
    UserService userService;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    RedissonClient redisson;
    private static final Gson gson = new Gson();

    private List<Long> mainUserList = Arrays.asList(1L,2L,3L);

    @Scheduled(cron = "0 25 12 * * *")
    public void preCache(){
        log.info("执行定时任务");
        RLock lock = redisson.getLock("yupao:user:precache");
        try {
            if(lock.tryLock(0,-1, TimeUnit.SECONDS)){
                Thread.sleep(100000);
                //只缓存了一个用户的推荐列表，可以设置一个重点用户列表
                String key = "yupao:user:recommend:3";
                QueryWrapper qw = new QueryWrapper();
                Page<User> page = userService.page(new Page<>(1,10), qw);
                List<User> records = page.getRecords();
                if(CollectionUtils.isEmpty(records)){
                    log.info("此用户未获取到推荐信息");
                    return;
                }
                String json = gson.toJson(records);
                //缓存要设置过期时间
                stringRedisTemplate.opsForValue().set(key,json,30,TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //到这里线程数很少，所以不用原子操作
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }

}
