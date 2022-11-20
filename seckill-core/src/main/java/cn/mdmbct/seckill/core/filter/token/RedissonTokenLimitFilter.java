package cn.mdmbct.seckill.core.filter.token;

import cn.mdmbct.seckill.core.award.AwardSeckill;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Redisson's token current limit can be used for multiple nodes servers
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/11 上午9:42
 * @modified mdmbct
 * @since 1.0
 */
public class RedissonTokenLimitFilter<R> extends TokenLimitFilter<R> {

    private final RRateLimiter rateLimiter;

    public RedissonTokenLimitFilter(int order, int ratePerSec, long timeout, RateType rateType,
                                    RedisNoAcquireParticipantCache cache,
                                    RedissonClient redissonClient, AwardSeckill seckill) {
        super(order, ratePerSec, timeout, cache, seckill);
        this.rateLimiter = redissonClient.getRateLimiter("DSK:" + seckill.getId() + ":token");
        this.rateLimiter.setRate(rateType, ratePerSec, 1, RateIntervalUnit.SECONDS);
        this.rateLimiter.expire(Duration.ofMillis(seckill.getExpireTime()));
    }

    @Override
    public boolean tryAcquireOne() {
        return rateLimiter.tryAcquire(timeout, TimeUnit.MILLISECONDS);
    }
}
