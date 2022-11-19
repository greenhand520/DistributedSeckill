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
     * 平滑突发限流
     *
     * @param order order
     * @param tokenPerSec token per sec
     * @param timeout timeout
     * @param cache       {@link LocalNoAcqParticipantCache}
     * @see TokenLimitFilter#TokenLimitFilter(int, int, long, NoAcquireParticipantCache, AwardSeckill)
     */
    public LocalTokenLimitFilter(int order, int tokenPerSec,
                                 long timeout,
                                 NoAcquireParticipantCache cache,
                                 AwardSeckill seckill) {
        super(order, tokenPerSec, timeout, cache, seckill);
        this.rateLimiter = RateLimiter.create(tokenPerSec);
    }

    /**
     * 平滑预热限流
     *
     * @param tokenPerSec 每秒令牌数
     * @param warmupTime  预热时间
     * @param unit        预热时间单位
     * @param timeout     获取令牌超时时间 超时无法获取到令牌 单位ms
     * @param cache       没有拿到令牌的用户id缓存 <br>
     *                    如果不设为null 则某用户未拿到令牌时会将其放入缓存 <br>
     *                    下次该用户的请求过来 不申请令牌 直接通过 <br>
     *                    如果为空 则忽略上面  <br>
     *                    缓存目前有2个实现类 ：<br>
     *                    {@link LocalNoAcqParticipantCache} <br>
     *                    {@link RedisNoAcquireParticipantCache}
     */
    public LocalTokenLimitFilter(int order,
                                 int tokenPerSec,
                                 int warmupTime,
                                 TimeUnit unit,
                                 long timeout,
                                 NoAcquireParticipantCache cache,
                                 AwardSeckill seckill) {
        super(order, tokenPerSec, timeout, cache, seckill);
        this.rateLimiter = RateLimiter.create(tokenPerSec, warmupTime, unit);
    }

    @Override
    public boolean tryAcquireOne() {
        return rateLimiter.tryAcquire(timeout, TimeUnit.MILLISECONDS);
    }

}
