package cn.mdmbct.seckill.core.filter.count.window;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.filter.count.CountFilter;
import cn.mdmbct.seckill.core.filter.count.Counter;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/28 下午12:54
 * @modified mdmbct
 * @since 1.0
 */
public class AllParticipantsCountPerFilter extends CountFilter {

    public AllParticipantsCountPerFilter(int order, int countLimit, Counter counter) {
        super(order, countLimit, counter);
    }

    public static AllParticipantsCountPerFilter localCount(int order, int countLimitPerSec) {
        return new AllParticipantsCountPerFilter(order, countLimitPerSec,
                new LocalAllParticipantsSWC(TimeUnit.SECONDS));
    }

    public static AllParticipantsCountPerFilter redisCount(RedissonClient redissonClient, int order, int countLimitPerSec, String seckillId) {
        return new AllParticipantsCountPerFilter(order, countLimitPerSec,
                new RedisAllParticipantsSWC(redissonClient, TimeUnit.SECONDS, countLimitPerSec, seckillId));
    }

    @Override
    public boolean doFilter(Participant participant, String awardId) {
        return counter.increaseOne() <= countLimit;
    }

    @Override
    public String notPassMsg() {
        return "参与人数过多！";
    }
}
