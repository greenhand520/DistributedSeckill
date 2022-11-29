package cn.mdmbct.seckill.core.filter.count;

import cn.mdmbct.seckill.core.utils.MapUtils;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Statistics the number of times each user participated in the entire activity
 * impl by {@link HashMap} and {@link ReentrantLock}
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/27 下午10:45
 * @modified mdmbct
 * @since 1.0
 */
public class LocalEntireParticipationCount implements Counter {

    private final Map<String, ParticipantCounter> countMap;

//    private final Map<String, ReentrantLock> lockMap;

    public LocalEntireParticipationCount() {
        this.countMap = new ConcurrentHashMap<>();
//        this.lockMap = new ConcurrentHashMap<>();
    }

    @Override
    public int increaseOne(String participantId) {
        ParticipantCounter participantCounter =
                MapUtils.computeIfAbsent(countMap, participantId, id -> new ParticipantCounter(participantId));
        return participantCounter.increaseOne();
    }

    @Override
    public void clear() {
        countMap.clear();
//        lockMap.clear();
    }

    public static class ParticipantCounter {

        @Getter
        private final String participantId;

        private final AtomicInteger count;

        public ParticipantCounter(String participantId) {
            this.participantId = participantId;
            this.count = new AtomicInteger(0);
        }

        public int increaseOne() {
            return count.incrementAndGet();
        }
    }
}
