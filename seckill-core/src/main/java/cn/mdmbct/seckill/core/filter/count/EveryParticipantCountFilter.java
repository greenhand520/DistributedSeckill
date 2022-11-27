package cn.mdmbct.seckill.core.filter.count;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.filter.Filter;
import cn.mdmbct.seckill.core.filter.count.window.LocalEveryParticipantSWC;
import cn.mdmbct.seckill.core.filter.count.window.RedisEveryParticipantSWC;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/27 下午10:35
 * @modified mdmbct
 * @since 1.0
 */
public class EveryParticipantCountFilter<R> extends Filter<R> {

    private final int participationCountLimit;

    private final Counter participationCount;

    public EveryParticipantCountFilter(int order, int participationCountLimit, Counter participationCount) {
        super(order);
        this.participationCountLimit = participationCountLimit;
        this.participationCount = participationCount;
    }

    public static <R> EveryParticipantCountFilter<R> localEveryCount(int order, int participationCountLimit) {
        return new EveryParticipantCountFilter<>(order, participationCountLimit,
                new LocalEveryParticipantSWC(TimeUnit.SECONDS, participationCountLimit));
    }

    public static <R> EveryParticipantCountFilter<R> redisEveryCount(int order, int participationCountLimit, RedissonClient redissonClient, String seckillId) {
        return new EveryParticipantCountFilter<>(order, participationCountLimit,
                new RedisEveryParticipantSWC(redissonClient, TimeUnit.SECONDS, participationCountLimit, seckillId));
    }

    @Override
    public boolean doFilter(Participant participant, String awardId) {
        return participationCount.increaseOne(participant.getId()) <= participationCountLimit;
    }

    @Override
    public String notPassMsg() {
        return "参与次数过多！";
    }

    @Override
    public void clear() {
        super.clear();
        participationCount.clear();
    }
}
