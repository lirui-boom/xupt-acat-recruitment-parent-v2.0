package cn.edu.xupt.acat.flowcontrol.library;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Component
public class RedisFlowControl {

    private static Logger logger = Logger.getLogger(RedisFlowControl.class.toString());

    @Resource
    private RedisTemplate redisTemplate;

    //1s过期
    private static int expireTime = 1;

    private static String FLOW_CONTROL_PREFIX = "FLOW_CONTROL_";

    public boolean isPass(String key, int value) {
        String val = (String) redisTemplate.opsForValue().get(FLOW_CONTROL_PREFIX + key);
        if (val == null) {
            redisTemplate.opsForValue().setIfAbsent(FLOW_CONTROL_PREFIX + key, "1", expireTime, TimeUnit.SECONDS);
            logger.info("set redis key " + FLOW_CONTROL_PREFIX + key + " expire time " + expireTime + "s.");
            return true;
        } else{
            return redisTemplate.opsForValue().increment(FLOW_CONTROL_PREFIX + key) <= value;
        }
    }
}
