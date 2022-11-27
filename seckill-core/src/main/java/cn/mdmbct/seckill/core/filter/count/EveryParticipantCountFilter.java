package cn.mdmbct.seckill.core.filter.count;

import cn.mdmbct.seckill.core.Participant;
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
public class EveryParticipantCountFilter<R> extends CountFilter<R> {

    private EveryParticipantCountFilter(int order, int participationCountLimit, Counter counter) {
        super(order, participationCountLimit, counter);
    }

    public static <R> EveryParticipantCountFilter<R> localCount(int order, int participationCountLimit) {
        return new EveryParticipantCountFilter<>(order, participationCountLimit,
                new LocalEveryParticipantSWC(TimeUnit.SECONDS));
    }

    public static <R> EveryParticipantCountFilter<R> redisCount(int order, int participationCountLimit, RedissonClient redissonClient, String seckillId) {
        return new EveryParticipantCountFilter<>(order, participationCountLimit,
                new RedisEveryParticipantSWC(redissonClient, TimeUnit.SECONDS, participationCountLimit, seckillId));
    }

    @Override
    public boolean doFilter(Participant participant, String awardId) {
        return counter.increaseOne(participant.getId()) <= countLimit;
    }

    @Override
    public String notPassMsg() {
        return "短时间参与次数过多！";
    }
}
