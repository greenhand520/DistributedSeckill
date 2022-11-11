package cn.mdmbct.seckill.core.filter.token;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.filter.Filter;
import lombok.Setter;


/**
 * 令牌产生速率过滤器
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/11 上午9:36
 * @modified mdmbct
 * @since 1.0
 */
public abstract class TokenLimitFilter extends Filter {

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

    public void doFilter(Participant participant, String productId) {

        if (cache == null) {
            // 缓存为空
            if (tryAcquireOne()) {
                doNextFilter(participant, productId);
            } else {
                getFilterContext().setFilterNotPassed(this);
            }
        } else {
            // 缓存不为空
            // 在缓存里直接通过
            if (cache.check(participant.getId())) {
                doNextFilter(participant, productId);
            } else if (tryAcquireOne()) {
                doNextFilter(participant, productId);
            } else {
                getFilterContext().setFilterNotPassed(this);
                // 没拿到令牌 添加到缓存
                cache.add(participant.getId());
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
