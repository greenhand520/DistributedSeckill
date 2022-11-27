package cn.mdmbct.seckill.core.filter.count;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Statistics all the participants count impl by {@link HashMap} and {@link AtomicInteger}
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/20 上午9:23
 * @modified mdmbct
 * @since 1.0
 */
public class LocalAllParticipantsCount implements Counter {

    private final Map<String, AtomicInteger> countMap;

    public LocalAllParticipantsCount() {
        this.countMap = new HashMap<>();
    }

    @Override
    public int increaseOne(String participantId) {
        AtomicInteger count = countMap.get(participantId);
        if (count == null) {
            countMap.put(participantId, new AtomicInteger(1));
            return 1;
        } else {
            return count.incrementAndGet();
        }
    }

    @Override
    public void clear() {
        countMap.clear();
    }
}
