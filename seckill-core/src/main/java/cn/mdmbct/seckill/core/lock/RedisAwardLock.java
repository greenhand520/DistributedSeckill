package cn.mdmbct.seckill.core.lock;

import lombok.Getter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Distributed lock impl by redisson, suitable for multi node servers
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/18 19:30
 * @modified mdmbct
 * @since 0.1
 */
public class RedisAwardLock implements AwardLock {

    @Getter
    public static class RedisAwardLockConfig implements Serializable {

        private static final long serialVersionUID = -3007846115247289444L;

        /**
         * DSK:${seckillId}:AwardLock:${awardId}
         */
        private final String lockCachePrefix;

        private int lockWaitTime = 3;

        private int lockExpireTime = 10;

        private TimeUnit timeUnit = TimeUnit.SECONDS;

        public RedisAwardLockConfig(String seckillId, int lockWaitTime, int lockExpireTime, TimeUnit timeUnit) {
            this(seckillId);
            if (lockWaitTime <= 0 || lockExpireTime <= 0) {
                throw new IllegalArgumentException("Param 'lockWaitTime' and 'lockExpireTime' must be > 0.");
            }
            this.lockWaitTime = lockWaitTime;
            this.lockExpireTime = lockExpireTime;
            this.timeUnit = timeUnit;
        }

        /**
         * default
         * lockWaitTime = 3 <br>
         * lockExpireTime = 10
         */
        public RedisAwardLockConfig(String seckillId) {
            // DSK:${seckillId}:AwardLock:${awardId}
            this.lockCachePrefix = "DSK:" + seckillId + ":AwardLock:";
        }
    }

    private final RedissonClient redissonClient;

    private final RedisAwardLockConfig lockConfig;

    /**
     * The default lock waiting time is 3s and the expiration time is 10s
     * @param redissonClient  redisson client
     */
    public RedisAwardLock(String secKillId, RedissonClient redissonClient) {
        this(redissonClient, new RedisAwardLockConfig(secKillId));
    }

    public RedisAwardLock(RedissonClient redissonClient,
                          RedisAwardLockConfig lockConfig) {
        this.redissonClient = redissonClient;
        this.lockConfig = lockConfig;
    }

    private boolean tryLock(String lockKey, int waitTime, int expireTime, TimeUnit timeUnit) {
        // Distributed Implementation of Reentrant Locks in Java
        final RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, expireTime, timeUnit);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean tryLock(String id) {
        return tryLock(cacheKey(id),
                lockConfig.lockWaitTime,
                lockConfig.lockExpireTime,
                lockConfig.timeUnit
        );
    }

    @Override
    public void unLock(String id) {
        redissonClient.getLock(cacheKey(id)).unlock();
    }


    private String cacheKey(String id) {
        return lockConfig.lockCachePrefix + id;
    }
}
