package cn.mdmbct.seckill.core.filter.token;

import cn.mdmbct.seckill.core.activity.ActivityConf;
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
public class RedissonTokenLimitFilter extends TokenLimitFilter {

    private final RRateLimiter rateLimiter;

    public RedissonTokenLimitFilter(RedissonClient redissonClient,
                                    int order, int ratePerSec, long timeout, RateType rateType,
                                    RedisNoAcquireParticipantCache cache,
                                    ActivityConf conf) {
        super(order, ratePerSec, timeout, cache, conf);
        this.rateLimiter = redissonClient.getRateLimiter("DSK:" + conf.getId() + ":token");
        this.rateLimiter.setRate(rateType, ratePerSec, 1, RateIntervalUnit.SECONDS);
        this.rateLimiter.expire(Duration.ofMillis(conf.getCacheExpiredTime()));
    }

    @Override
    public boolean tryAcquireOne() {
        return rateLimiter.tryAcquire(timeout, TimeUnit.MILLISECONDS);
    }
}
