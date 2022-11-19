package cn.mdmbct.seckill.core.filter;

import cn.mdmbct.seckill.core.Participant;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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

    private final ParticipationCountCache countCache;

    public ParticipantCountFilter(int order, int participationCountLimit, ParticipationCountCache countCache) {
        super(order);
        this.participationCountLimit = participationCountLimit;
        this.countCache = countCache;
    }

    public static <R> ParticipantCountFilter<R> withLocalCountCache(int order, int participationCountLimit) {
        return new ParticipantCountFilter<>(order, participationCountLimit, new LocalParticipationCountCache());
    }

    public static <R> ParticipantCountFilter<R> withRedisCountCache(int order, int participationCountLimit, RedissonClient redissonClient, String seckillId) {
        return new ParticipantCountFilter<>(order, participationCountLimit, new RedisParticipationCountCache(seckillId, redissonClient));
    }

    @Override
    public String notPassMsg() {
        return "参与次数过多！";
    }

    @Override
    public boolean doFilter(Participant participant, String awardId) {
        return countCache.getParticipationCount(participant.getId()) <= participationCountLimit;
    }

    public interface ParticipationCountCache {
        int getParticipationCount(String participantId);
    }

    public static class LocalParticipationCountCache implements ParticipationCountCache {

        private final Map<String, AtomicInteger> countMap;

        public LocalParticipationCountCache() {
            this.countMap = new HashMap<>();
        }

        @Override
        public int getParticipationCount(String participantId) {

            AtomicInteger count = countMap.get(participantId);
            if (count == null) {
                countMap.put(participantId, new AtomicInteger(1));
                return 1;
            } else {
                return count.incrementAndGet();
            }
        }
    }

    public static class RedisParticipationCountCache implements ParticipationCountCache {

        private final RedissonClient redissonClient;

        private final String countCachePrefix;

        public RedisParticipationCountCache(String seckillId, RedissonClient redissonClient) {
            this.redissonClient = redissonClient;
            // DSK:${seckillId}:ParticipationCount:${participantId}
            this.countCachePrefix = "DSK:" + seckillId + ":ParticipationCount:";
        }

        @Override
        public int getParticipationCount(String participantId) {
            RAtomicLong count = redissonClient.getAtomicLong(countCachePrefix + participantId);
            return (int) count.incrementAndGet();
        }
    }

}
