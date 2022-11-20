package cn.mdmbct.seckill.core.filter.token;

import cn.mdmbct.seckill.core.award.AwardSeckill;
import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.TimeUnit;

/**
 * 限流过滤器 使用令牌桶 <br>
 * 只能通过拿到令牌的线程 <br>
 * 该限流对引用该模块的微服务有效 即是单机的限流
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/21 8:57
 * @modified mdmbct
 * @since 0.1
 */
public class LocalTokenLimitFilter<R> extends TokenLimitFilter<R> {

    private final RateLimiter rateLimiter;

    /**
     * {@link RateLimiter#create(double)}
     *
     * @param order       order
     * @param tokenPerSec token per sec
     * @param timeout     timeout
     * @param cache       no acq cache {@link LocalNoAcqParticipantCache}
     * @see TokenLimitFilter#TokenLimitFilter(int, int, long, NoAcquireParticipantCache, AwardSeckill)
     */
    public LocalTokenLimitFilter(int order, int tokenPerSec,
                                 long timeout,
                                 LocalNoAcqParticipantCache cache,
                                 AwardSeckill seckill) {
        super(order, tokenPerSec, timeout, cache, seckill);
        this.rateLimiter = RateLimiter.create(tokenPerSec);
    }

    /**
     * {@link RateLimiter#create(double, long, TimeUnit)}
     *
     * @param tokenPerSec token per sec
     * @param warmupTime  warmup time
     * @param unit        warmup time unit
     * @param timeout     timeout
     * @param cache       no acq cache {@link LocalNoAcqParticipantCache}
     */
    public LocalTokenLimitFilter(int order,
                                 int tokenPerSec,
                                 int warmupTime,
                                 TimeUnit unit,
                                 long timeout,
                                 LocalNoAcqParticipantCache cache,
                                 AwardSeckill seckill) {
        super(order, tokenPerSec, timeout, cache, seckill);
        this.rateLimiter = RateLimiter.create(tokenPerSec, warmupTime, unit);
    }

    @Override
    public boolean tryAcquireOne() {
        return rateLimiter.tryAcquire(timeout, TimeUnit.MILLISECONDS);
    }

}
