package cn.mdmbct.seckill.core.filter.count;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;

/**
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/27 下午10:51
 * @modified mdmbct
 * @since 1.0
 */
public class RedisEntireParticipationCount implements Counter {

    private final RedissonClient redissonClient;

    private final String keyPrefix;

    public RedisEntireParticipationCount(RedissonClient redissonClient, String seckillId) {
        this.redissonClient = redissonClient;
        // DSK:${seckillId}:EntireParticipationC:${participantId}
        this.keyPrefix = "DSK:" + seckillId + ":EntireParticipationC:";
    }

    @Override
    public int increaseOne(String participantId) {
        RAtomicLong count = redissonClient.getAtomicLong(keyPrefix + participantId);
        return (int) count.incrementAndGet();
    }

}
