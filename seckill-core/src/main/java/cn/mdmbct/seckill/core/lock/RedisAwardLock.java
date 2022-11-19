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

        private int lockWaitTime = 3;

        private int lockExpireTime = 10;

        private TimeUnit timeUnit = TimeUnit.SECONDS;

        private final String lockCachePrefix;

        private static void checkPrefix(String prefix) {
            if (prefix == null || prefix.trim().length() == 0) {
                throw new IllegalArgumentException("Param 'lockCachePrefix' must be not null.");
            }
        }

        public RedisAwardLockConfig(String lockCachePrefix) {
            checkPrefix(lockCachePrefix);
            this.lockCachePrefix = lockCachePrefix;
        }

        public RedisAwardLockConfig(int lockWaitTime, int lockExpireTime, TimeUnit timeUnit, String lockCachePrefix) {
            checkPrefix(lockCachePrefix);
            if (lockWaitTime <= 0 || lockExpireTime <= 0) {
                throw new IllegalArgumentException("Param 'lockWaitTime' and 'lockExpireTime' must be > 0.");
            }
            this.lockWaitTime = lockWaitTime;
            this.lockExpireTime = lockExpireTime;
            this.timeUnit = timeUnit;
            this.lockCachePrefix = lockCachePrefix;
        }
    }

    private final RedissonClient redissonClient;

    private final RedisAwardLockConfig lockConfig;

    /**
     * The default lock waiting time is 3s and the expiration time is 10s
     * @param redissonClient  redisson client
     * @param lockCachePrefix lock cache prefix
     */
    public RedisAwardLock(RedissonClient redissonClient, String lockCachePrefix) {
        this.redissonClient = redissonClient;
        this.lockConfig = new RedisAwardLockConfig(lockCachePrefix);
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
