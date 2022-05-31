package cn.edu.xupt.acat.recruitment.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


@Component
public class RedisLock {

    @Autowired
    private RedisTemplate redisTemplate;

    private static Logger logger = Logger.getLogger(RedisLock.class.toString());

    private static final Long SUCCESS = 1L;

    /**
     * 加锁
     * @param lockKey   lockKey
     * @param value value
     * @return boolean
     */
    public boolean tryLock(String lockKey, String value, long expireTime, TimeUnit unit) {

        boolean locked = false;
        int tryCount = 3;
        while (!locked && tryCount > 0) {
            locked = redisTemplate.opsForValue().setIfAbsent(lockKey, value, expireTime, unit);
            tryCount--;
            if (!locked) {
                try {
                    logger.info("try lock fail, retry count is " + tryCount + ", lock key is " + lockKey);
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    logger.warning("线程被中断" + Thread.currentThread().getId() + " " + e.getMessage());
                }
            }
        }
        return locked;
    }

    /**
     * 解锁
     * @param lockKey
     * @param value
     * @return
     */
    public Boolean unlock(String lockKey, String value) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        RedisScript<String> redisScript = new DefaultRedisScript<>(script, String.class);
        Object result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), value);
        if (SUCCESS.equals(result)) {
            return true;
        }
        return false;
    }

    public String getLockValue(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }
}