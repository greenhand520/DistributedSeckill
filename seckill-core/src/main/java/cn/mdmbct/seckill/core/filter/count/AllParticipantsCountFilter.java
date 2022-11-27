package cn.mdmbct.seckill.core.filter.count;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.filter.Filter;
import org.redisson.api.RedissonClient;

/**
 * Participation count will increase regardless of whether participant win the lottery or not.
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/22 下午8:17
 * @modified mdmbct
 * @since 0.1
 */
public class AllParticipantsCountFilter<R> extends Filter<R> {

    private final int participantsCountLimit;

    private final Counter participationCount;

    public AllParticipantsCountFilter(int order, int participantsCountLimit, Counter participationCount) {
        super(order);
        this.participantsCountLimit = participantsCountLimit;
        this.participationCount = participationCount;
    }

    public static <R> AllParticipantsCountFilter<R> localAllCount(int order, int participantsCountLimit) {
        return new AllParticipantsCountFilter<>(order, participantsCountLimit, new LocalAllParticipantsCount());
    }

    public static <R> AllParticipantsCountFilter<R> redisAllCount(int order, int participantsCountLimit, RedissonClient redissonClient, String seckillId) {
        return new AllParticipantsCountFilter<>(order, participantsCountLimit,
                new RedisAllParticipantsCount(redissonClient, seckillId));
    }


    @Override
    public String notPassMsg() {
        return "参与人数过多！";
    }

    @Override
    public boolean doFilter(Participant participant, String awardId) {
        return participationCount.increaseOne() <= participantsCountLimit;
    }

    @Override
    public void clear() {
        super.clear();
        participationCount.clear();
    }
}
