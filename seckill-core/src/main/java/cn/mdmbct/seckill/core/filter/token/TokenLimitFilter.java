package cn.mdmbct.seckill.core.filter.token;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.filter.Filter;


/**
 * 令牌产生速率过滤器
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/11 上午9:36
 * @modified mdmbct
 * @since 1.0
 */
public abstract class TokenLimitFilter<R> extends Filter<R> {

    /**
     * 令牌每秒产生速率
     */
    protected final int ratePerSec;

    /**
     * 获取抽奖令牌超时时间 超时无法获取到令牌 单位ms
     */
    protected final long timeout;

    private final NoAcquireParticipantCache cache;

    /**
     * @param order      过滤器顺序 自定义 一般为{@link Filter#FIRST_FILTER_ORDER}
     * @param ratePerSec 每秒产生速率
     * @param timeout    尝试获取令牌超时时间 单位ms
     * @param cache      没有获取到令牌用户的缓存 如果该对象不为空，没拿到令牌的用户将会放入缓存，下次该用户再来获取令牌直接给其一个
     */
    public TokenLimitFilter(int order, int ratePerSec, long timeout, NoAcquireParticipantCache cache) {
        super(order);
        this.ratePerSec = ratePerSec;
        this.timeout = timeout;
        this.cache = cache;
    }

    /**
     * 尝试获取一个
     *
     * @return 是否获取到
     */
    public abstract boolean tryAcquireOne();

    public boolean doFilter(Participant participant, String awardId) {

        if (cache == null) {
            // if not set cache, don't cache the participants who did not get the token
           return tryAcquireOne();
        } else {
            // cache the participants who did not get the token
            if (cache.check(participant.getId())) {
                // if participant was in cache, do next filter directly
                return true;
            } else {
                return tryAcquireOne();
            }
        }
    }

    @Override
    public String notPassMsg() {
        return "当前人数过多，请稍后重试";
    }

    @Override
    protected void clear() {
        super.clear();
        if (cache != null) {
            cache.clear();
        }

    }
}
