package cn.mdmbct.seckill.core.filter.token;

import cn.mdmbct.seckill.core.award.AwardSeckill;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;

import java.time.Duration;
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
                                    RedissonClient redissonClient, String key, AwardSeckill seckill) {
        super(order, ratePerSec, timeout, cache, seckill);
        this.rateLimiter = redissonClient.getRateLimiter(key);
        this.rateLimiter.setRate(RateType.OVERALL, ratePerSec, 1, RateIntervalUnit.SECONDS);
        this.rateLimiter.expire(Duration.ofMillis(seckill.getExpireTime()));
    }

    @Override
    public boolean tryAcquireOne() {
        return rateLimiter.tryAcquire(timeout, TimeUnit.MILLISECONDS);
    }
}
