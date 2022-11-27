package cn.mdmbct.seckill.core.filter.count;

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

    private final Map<String, AtomicInteger> countMap;

    private final Map<String, ReentrantLock> lockMap;

    public LocalEntireParticipationCount() {
        this.countMap = new HashMap<>();
        this.lockMap = new ConcurrentHashMap<>();
    }

    @Override
    public int increaseOne(String participantId) {
        return 0;
    }

    @Override
    public void clear() {
        countMap.clear();
        lockMap.clear();
    }
}
