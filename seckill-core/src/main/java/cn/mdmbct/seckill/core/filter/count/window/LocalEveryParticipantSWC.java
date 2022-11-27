package cn.mdmbct.seckill.core.filter.count.window;

import cn.mdmbct.seckill.core.cache.BaseLocalCache;
import cn.mdmbct.seckill.core.cache.Cache;

import java.util.TreeSet;
import java.util.concurrent.TimeUnit;


/**
 * For single node server, statistics every participant count impl by sliding time window
 * which impl by {@link TreeSet}  <br>
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/8 下午5:21
 * @modified mdmbct
 * @since 1.0
 */
public class LocalEveryParticipantSWC extends SlidingWindowCount {

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

    private final Cache<String, TreeSetSWC> countCache;

    public LocalEveryParticipantSWC(TimeUnit timeUnit, int limit) {
        super(timeUnit, limit);
        this.countCache = BaseLocalCache.concurrentHashMapCache(windowTime);
    }

    @Override
    public int increaseOne(String participantId) {
        TreeSetSWC count = countCache.putIfAbsent(participantId, TreeSetSWC.defaultLocalParticipantsSWC(TimeUnit.SECONDS, limit),
                windowTime, true);
        return count.increaseOne(participantId);
    }

    @Override
    public void clear() {
        for (TreeSetSWC value : countCache.values()) {
            value.clear();
        }
        countCache.clearAll();
    }

    /**
     * Sliding time window impl by {@link TreeSet} to statistics the number of participant
     *
     * @author mdmbct  mdmbct@outlook.com
     * @date 2022/11/25 下午7:27
     * @modified mdmbct
     * @since 1.0
     */
//    public static class LocalParticipantSlidingCount extends TreeSetSWC {
//
////        @Getter
////        private final String participantId;
//
//        public LocalParticipantSlidingCount(/*String participantId, */TimeUnit timeUnit, int limit) {
//            super(timeUnit, limit);
////            this.participantId = participantId;
//        }
//
//        @Override
//        public synchronized int increaseOne(String participantId) {
//            return increaseOneToCounter();
//        }
//
//    }
}
