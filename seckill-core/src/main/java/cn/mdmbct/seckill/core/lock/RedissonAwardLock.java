package cn.mdmbct.seckill.core.lock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * Distributed lock impl by redisson, suitable for multi node servers
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/18 19:30
 * @modified mdmbct
 * @since 0.1
 */
public class RedissonAwardLock implements AwardLock {

    private final RedissonClient redissonClient;

    private int lockWaitTime = 3;

    private int lockExpireTime = 10;

    private TimeUnit timeUnit = TimeUnit.SECONDS;

    private final String lockCachePrefix;

    /**
     * The default lock waiting time is 3s and the expiration time is 10s
     * @param redissonClient  redisson client
     * @param lockCachePrefix lock cache prefix
     */
    public RedissonAwardLock(RedissonClient redissonClient, String lockCachePrefix) {
        if (lockCachePrefix == null || lockCachePrefix.length() == 0) {
            throw new IllegalArgumentException("参数‘lockCachePrefix’不能为空");
        }
        this.redissonClient = redissonClient;
        this.lockCachePrefix = lockCachePrefix;
    }

    public RedissonAwardLock(RedissonClient redissonClient,
                             int lockWaitTime,
                             int lockExpireTime,
                             TimeUnit timeUnit,
                             String lockCachePrefix) {

        if (lockWaitTime <= 0 || lockExpireTime <= 0) {
            throw new IllegalArgumentException("参数‘lockWaitTime’和‘lockExpireTime’都必须大于0");
        }

        if (lockCachePrefix == null || lockCachePrefix.length() == 0) {
            throw new IllegalArgumentException("参数‘lockCachePrefix’不能为空");
        }

        this.redissonClient = redissonClient;
        this.lockWaitTime = lockWaitTime;
        this.lockExpireTime = lockExpireTime;
        this.timeUnit = timeUnit;
        this.lockCachePrefix = lockCachePrefix;
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
                lockWaitTime,
                lockExpireTime,
                timeUnit
        );
    }

    @Override
    public void unLock(String id) {
        redissonClient.getLock(cacheKey(id)).unlock();
    }


    private String cacheKey(String id) {
        return lockCachePrefix + id;
    }
}
