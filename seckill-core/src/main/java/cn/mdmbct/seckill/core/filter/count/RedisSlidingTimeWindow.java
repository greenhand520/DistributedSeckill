package cn.mdmbct.seckill.core.filter.count;

import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * For Mul servers nodes, Imp by redis zset & opt redis with redisson
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/8 下午5:19
 * @modified mdmbct
 * @since 1.0
 */
public class RedisSlidingTimeWindow extends SlidingTimeWindowCount {


    public RedisSlidingTimeWindow(RedissonClient redissonClient, int slot, TimeUnit timeUnit, int limit) {
        super(slot, timeUnit, limit);
    }

    @Override
    public int increaseOne(String participantId) {
        return 0;
    }

    @Override
    public void clear() {

    }
}
