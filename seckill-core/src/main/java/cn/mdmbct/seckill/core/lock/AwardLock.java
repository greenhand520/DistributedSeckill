package cn.mdmbct.seckill.core.lock;

import org.redisson.api.RedissonClient;

import java.util.Set;

/**
 * award lock interface
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/18 19:27
 * @modified mdmbct
 * @since 0.1
 */
public interface AwardLock {

    /**
     * try to lock
     *
     * @param id award (divided red packet id) id
     * @return whether the lock is successful
     */
    boolean tryLock(String id);

    /**
     * release lock
     *
     * @param id award (divided red packet id) id
     */
    void unLock(String id);

    static LocalAwardLock local() {
        return new LocalAwardLock();
    }

    static RedisAwardLock redis(RedissonClient redissonClient, RedisAwardLock.RedisAwardLockConfig config) {
        return new RedisAwardLock(redissonClient, config);
    }

    static ZkAwardLock zookeeper(ZkAwardLock.ZkLockConfig config, Set<String> awardIds) {
        return new ZkAwardLock(config, awardIds);
    }

}
