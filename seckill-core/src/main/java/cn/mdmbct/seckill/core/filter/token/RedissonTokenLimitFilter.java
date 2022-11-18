package cn.mdmbct.seckill.core.filter.token;

import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * Redisson的令牌限流 可针对多节点使用
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/11 上午9:42
 * @modified mdmbct
 * @since 1.0
 */
public class RedissonTokenLimitFilter<R> extends TokenLimitFilter<R> {

    private final RRateLimiter rateLimiter;

    public RedissonTokenLimitFilter(int order, int ratePerSec, long timeout,
                                    NoAcquireParticipantCache cache,
                                    RedissonClient redissonClient, String key) {
        super(order, ratePerSec, timeout, cache);
        this.rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.setRate(RateType.OVERALL, ratePerSec, 1, RateIntervalUnit.SECONDS);
    }

    @Override
    public boolean tryAcquireOne() {
        return rateLimiter.tryAcquire(timeout, TimeUnit.MILLISECONDS);
    }
}
