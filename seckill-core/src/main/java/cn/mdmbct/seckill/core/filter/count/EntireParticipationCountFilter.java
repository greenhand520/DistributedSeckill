package cn.mdmbct.seckill.core.filter.count;

import cn.mdmbct.seckill.core.Participant;
import org.redisson.api.RedissonClient;

/**
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/27 下午10:55
 * @modified mdmbct
 * @since 1.0
 */
public class EntireParticipationCountFilter extends CountFilter {

    public EntireParticipationCountFilter(int order, int entireParticipationCountLimit, Counter counter) {
        super(order, entireParticipationCountLimit, counter);
    }

    public static EntireParticipationCountFilter localCount(int order, int entireParticipationCountLimit) {
        return new EntireParticipationCountFilter(order, entireParticipationCountLimit,
                new LocalEntireParticipationCount());
    }

    public static EntireParticipationCountFilter redisCount(RedissonClient redissonClient, int order, int entireParticipationCountLimit, String seckillId) {
        return new EntireParticipationCountFilter(order, entireParticipationCountLimit,
                new RedisEntireParticipationCount(redissonClient, seckillId));
    }

    @Override
    public boolean doFilter(Participant participant, String awardId) {
        return counter.increaseOne(participant.getId()) <= countLimit;
    }

    @Override
    public String notPassMsg() {
        return "参与次数已用完！";
    }
}
