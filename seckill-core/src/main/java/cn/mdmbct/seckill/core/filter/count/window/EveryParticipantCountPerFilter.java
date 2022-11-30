package cn.mdmbct.seckill.core.filter.count.window;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.filter.count.CountFilter;
import cn.mdmbct.seckill.core.filter.count.Counter;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/27 下午10:35
 * @modified mdmbct
 * @since 1.0
 */
public class EveryParticipantCountPerFilter extends CountFilter {

    public EveryParticipantCountPerFilter(int order, int participationCountLimit, Counter counter) {
        super(order, participationCountLimit, counter);
    }

    public static EveryParticipantCountPerFilter localCount(int order, int participationCountLimitPerSec) {
        return new EveryParticipantCountPerFilter(order, participationCountLimitPerSec,
                new LocalEveryParticipantSWC(TimeUnit.SECONDS));
    }

    public static EveryParticipantCountPerFilter redisCount(RedissonClient redissonClient, int order, int participationCountLimitPerSec, String seckillId) {
        return new EveryParticipantCountPerFilter(order, participationCountLimitPerSec,
                new RedisEveryParticipantSWC(redissonClient, TimeUnit.SECONDS, participationCountLimitPerSec, seckillId));
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
