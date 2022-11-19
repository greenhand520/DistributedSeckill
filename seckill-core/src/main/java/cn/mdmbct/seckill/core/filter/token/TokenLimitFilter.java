package cn.mdmbct.seckill.core.filter.token;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.award.AwardSeckill;
import cn.mdmbct.seckill.core.filter.Filter;


/**
 * Token limit filter. Only pass the participant who acquire a token and participation time is within the scope of activity time.
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/11 上午9:36
 * @modified mdmbct
 * @since 1.0
 */
public abstract class TokenLimitFilter<R> extends Filter<R> {

    /**
     * the number of tokens generated per second
     */
    protected final int ratePerSec;

    /**
     * obtaining token timeout. <br>
     * timeout will fail to obtain the token <br>
     * unit: ms
     */
    protected final long timeout;

    private final NoAcquireParticipantCache cache;

    private final long activityStartTime;

    private final long activityEndTime;

    /**
     * @param order      filter order. generally{@link Filter#FIRST_FILTER_ORDER}
     * @param ratePerSec the number of tokens generated per second
     * @param timeout    obtaining token timeout period. unit: ms
     * @param cache      the cache of the user who has not obtained the token.
     *                   if the object is not empty, the user who has not obtained the token will be put into the cache.
     *                   and next time gives the user a token directly.
     */
    public TokenLimitFilter(int order, int ratePerSec, long timeout, NoAcquireParticipantCache cache, AwardSeckill seckill) {
        super(order);
        this.ratePerSec = ratePerSec;
        this.timeout = timeout;
        this.cache = cache;
        this.activityStartTime = seckill.getStartTime();
        this.activityEndTime = activityStartTime + seckill.getTtl();
    }

    /**
     * try to obtain a token
     *
     * @return whether to obtain the token
     */
    public abstract boolean tryAcquireOne();

    public boolean doFilter(Participant participant, String awardId) {

        long now = System.currentTimeMillis();
        if (now <= activityEndTime && now >= activityStartTime) {
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
        } else {
            // not in activity time
            return false;
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
