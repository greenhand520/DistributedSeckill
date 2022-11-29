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

    private final String key;

    public RedisAllParticipantsCount(RedissonClient redissonClient, String seckillId) {
        this.redissonClient = redissonClient;
        // DSK:${seckillId}:AllParticipantsC
        this.key = "DSK:" + seckillId + ":AllParticipantsC:";
    }

    @Override
    public int increaseOne() {
        RAtomicLong count = redissonClient.getAtomicLong(key);
        return (int) count.incrementAndGet();
    }

}
