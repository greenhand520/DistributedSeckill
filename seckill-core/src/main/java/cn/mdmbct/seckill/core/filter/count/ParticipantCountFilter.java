package cn.mdmbct.seckill.core.filter.count;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.filter.Filter;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * Participation count will increase regardless of whether participant win the lottery or not.
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/22 8:17
 * @modified mdmbct
 * @since 0.1
 */
public class ParticipantCountFilter<R> extends Filter<R> {

    private final int participationCountLimit;

    private final ParticipationCount participationCount;

    public ParticipantCountFilter(int order, int participationCountLimit, ParticipationCount participationCount) {
        super(order);
        this.participationCountLimit = participationCountLimit;
        this.participationCount = participationCount;
    }

    public static <R> ParticipantCountFilter<R> withLocalCount(int order, int participationCountLimit, int secTimeSlots) {
        if (secTimeSlots <= 0) {
            return new ParticipantCountFilter<>(order,
                    participationCountLimit,
                    new LocalParticipationCount());
        } else {
            return new ParticipantCountFilter<>(order,
                    participationCountLimit,
                    new LocalSlidingWindowCount(secTimeSlots, TimeUnit.SECONDS, participationCountLimit / secTimeSlots));
        }
    }

    public static <R> ParticipantCountFilter<R> withRedisCount(int order, int participationCountLimit, int secTimeSlots, RedissonClient redissonClient, String seckillId) {
        if (secTimeSlots <= 0) {
            return new ParticipantCountFilter<>(order,
                    participationCountLimit,
                    new RedisParticipationCount(redissonClient, seckillId));
        } else {
            return new ParticipantCountFilter<>(order,
                    participationCountLimit,
                    new RedisSlidingWindowCount(redissonClient, secTimeSlots, TimeUnit.SECONDS, participationCountLimit / secTimeSlots, seckillId));
        }
    }

    @Override
    public String notPassMsg() {
        return "参与次数过多！";
    }

    @Override
    public boolean doFilter(Participant participant, String awardId) {
        return participationCount.increaseOne(participant.getId()) <= participationCountLimit;
    }

    @Override
    protected void clear() {
        super.clear();
        participationCount.clear();
    }
}
