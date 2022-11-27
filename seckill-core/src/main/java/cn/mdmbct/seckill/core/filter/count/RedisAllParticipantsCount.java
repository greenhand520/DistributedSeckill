package cn.mdmbct.seckill.core.filter.count;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;

/**
 * Statistics all the participant count impl by redis
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/20 上午9:29
 * @modified mdmbct
 * @since 1.0
 */
public class RedisAllParticipantsCount implements Counter {

    private final RedissonClient redissonClient;

    private final String countCachePrefix;

    public RedisAllParticipantsCount(RedissonClient redissonClient, String seckillId) {
        this.redissonClient = redissonClient;
        // DSK:${seckillId}:ParticipationCount:${participantId}
        this.countCachePrefix = "DSK:" + seckillId + ":ParticipationCount:";
    }

    @Override
    public int increaseOne(String participantId) {
        RAtomicLong count = redissonClient.getAtomicLong(countCachePrefix + participantId);
        return (int) count.incrementAndGet();
    }

}
