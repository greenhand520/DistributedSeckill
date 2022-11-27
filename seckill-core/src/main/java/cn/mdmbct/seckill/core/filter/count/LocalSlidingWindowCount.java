package cn.mdmbct.seckill.core.filter.count;

import cn.mdmbct.seckill.core.cache.BaseLocalCache;
import cn.mdmbct.seckill.core.cache.Cache;
import cn.mdmbct.seckill.core.cache.CacheClearService;

import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * Sliding time window for single node server <br>
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/8 下午5:21
 * @modified mdmbct
 * @since 1.0
 */
public class LocalSlidingWindowCount extends SlidingWindowCount {

//    private final Cache<String, ReentrantLock> lockCache;
//    private final Cache<String, TreeSet<Long>> countCache;
//
//    public LocalSlidingWindowCount(/*int slot, */TimeUnit timeUnit, int limit, long lockCacheExpireTimeout) {
//        super(/*slot, */timeUnit, limit);
//        this.countCache = BaseLocalCache.hashMapCache(windowTime);
//        this.lockCache = BaseLocalCache.concurrentHashMapCache(lockCacheExpireTimeout);
//        lockCache.autoClear(lockCacheExpireTimeout);
//        countCache.autoClear(windowTime);
//    }
//
//    public LocalSlidingWindowCount(/*int slot, */TimeUnit timeUnit, int limit) {
//        super(/*slot, */timeUnit, limit);
//        this.countCache = BaseLocalCache.hashMapCache(windowTime);
//        this.lockCache = BaseLocalCache.concurrentHashMapCache(-1);
//        countCache.autoClear(windowTime);
//    }
//
//    @Override
//    public int increaseOne(String participantId) {
//        ReentrantLock lock = lockCache.putIfAbsent(participantId, new ReentrantLock(true), 0, true);
//        try {
//            long now = System.currentTimeMillis();
//            if (lock.tryLock() || lock.tryLock(2, TimeUnit.MILLISECONDS)) {
//                try {
//                    // the run time of code block in 'try' is short
//                    TreeSet<Long> treeSet = countCache.getValue(participantId, false);
//                    if (treeSet == null) {
//                        treeSet = new TreeSet<>();
//                        treeSet.add(now);
//                        countCache.put(participantId, treeSet, 0);
//                    } else {
//                        treeSet.add(now);
//                    }
//                    return treeSet.subSet(now - 1000, true, now, true).size();
//                } finally {
//                    lock.unlock();
//                }
//            } else {
//                System.out.print(participantId);
//                // another thread competed the ReentrantLock
//                TreeSet<Long> treeSet = countCache.getValue(participantId, false);
//                return treeSet.subSet(now - 1000, true, now, true).size();
//            }
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        // code "lock.unlock()" can not put here, because when locking fails, there is no corresponding lock when unlocking
////        finally {
////            lock.unlock();
////        }
//    }
//
//    @Override
//    public void clear() {
//        lockCache.stopAutoClear();
//        countCache.stopAutoClear();
//        lockCache.clearAll();
//        countCache.values().forEach(TreeSet::clear);
//        countCache.clearAll();
//    }

    private Cache<String, LocalParticipantSlidingCount> countCache;

    public LocalSlidingWindowCount(TimeUnit timeUnit, int limit) {
        super(timeUnit, limit);
        this.countCache = BaseLocalCache.concurrentHashMapCache(windowTime);
    }

    @Override
    public int increaseOne(String participantId) {
        LocalParticipantSlidingCount count = countCache.putIfAbsent(participantId, new LocalParticipantSlidingCount(TimeUnit.SECONDS, limit),
                windowTime, true);
        return count.increaseOne(participantId);
    }

    @Override
    public void clear() {
        for (LocalParticipantSlidingCount value : countCache.values()) {
            value.clear();
        }
        countCache.clearAll();
    }

    /**
     * Sliding time window impl by {@link TreeSet} to statistics the number of participation
     *
     * @author mdmbct  mdmbct@outlook.com
     * @date 2022/11/25 下午7:27
     * @modified mdmbct
     * @since 1.0
     */
    public static class LocalParticipantSlidingCount extends SlidingWindowCount {

//        @Getter
//        private final String participantId;

        private final TreeSet<Long> counter;

        private final ScheduledFuture<?> clearJob;

        private long expiredTime;


        public LocalParticipantSlidingCount(/*String participantId, */TimeUnit timeUnit, int limit) {
            super(timeUnit, limit);
//            this.participantId = participantId;
            this.counter = new TreeSet<>();
            this.clearJob = CacheClearService.instacne().addClearJob(this::clearExpired, windowTime);
        }

        private void clearExpired() {
            if (expiredTime > 0) {
                counter.removeIf(aLong -> aLong < expiredTime);
                expiredTime = 0;
            }
        }

        @Override
        public synchronized int increaseOne(String participantId) {
            long now = System.currentTimeMillis();
            counter.add(now);
            this.expiredTime = now - windowTime;
            NavigableSet<Long> navigableSet = counter.subSet(expiredTime, true, now, true);
            return navigableSet.size();
        }

        @Override
        public void clear() {
            if (clearJob != null) {
                clearJob.cancel(true);
            }
            counter.clear();
        }
    }
}
