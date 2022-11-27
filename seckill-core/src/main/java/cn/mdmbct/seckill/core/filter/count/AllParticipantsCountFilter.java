package cn.mdmbct.seckill.core.filter.count;

import cn.mdmbct.seckill.core.Participant;
import org.redisson.api.RedissonClient;

/**
 * Participation count will increase regardless of whether participant win the lottery or not.
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/22 下午8:17
 * @modified mdmbct
 * @since 0.1
 */
public class AllParticipantsCountFilter<R> extends CountFilter<R> {

    private AllParticipantsCountFilter(int order, int participantsCountLimit, Counter counter) {
        super(order, participantsCountLimit, counter);
    }

    public static <R> AllParticipantsCountFilter<R> localCount(int order, int participantsCountLimit) {
        return new AllParticipantsCountFilter<>(order, participantsCountLimit, new LocalAllParticipantsCount());
    }

    public static <R> AllParticipantsCountFilter<R> redisCount(int order, int participantsCountLimit, RedissonClient redissonClient, String seckillId) {
        return new AllParticipantsCountFilter<>(order, participantsCountLimit,
                new RedisAllParticipantsCount(redissonClient, seckillId));
    }


    @Override
    public String notPassMsg() {
        return "参与人数过多！";
    }

    @Override
    public boolean doFilter(Participant participant, String awardId) {
        return counter.increaseOne() <= countLimit;
    }

    @Override
    public void clear() {
        super.clear();
        counter.clear();
    }
}
