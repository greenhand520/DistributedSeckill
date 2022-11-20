package cn.mdmbct.seckill.core.filter.count;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Sliding time window for single node server
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/8 下午5:21
 * @modified mdmbct
 * @since 1.0
 */
public class LocalSlidingWindowCount extends SlidingWindowCount {

    private final TimeLockCache lockCache;

    private final Map<String, TreeSet<Long>> countMap;

    public LocalSlidingWindowCount(int slot, TimeUnit timeUnit, int limit, long cacheExpireTimeout) {
        super(slot, timeUnit, limit);
        this.lockCache = new TimeLockCache(cacheExpireTimeout);
        this.countMap = new HashMap<>();
    }

    @Override
    public int increaseOne(String participantId) {
        return 0;
    }

    @Override
    public void clear() {
        super.clear();
        lockCache.clear();
        countMap.values().forEach(TreeSet::clear);
        countMap.clear();
    }

    private static class TimeLockCache {
        private final Map<String, CacheLock> awardLocks;

        private ScheduledFuture<?> pruneJobFuture;

        private final long timeout;

        public TimeLockCache(long timeout) {
            this.awardLocks = new ConcurrentHashMap<>();
            if (timeout > 0) {
                this.timeout = timeout;
            } else {
                this.timeout = 60 * 1000;
            }
        }

        public void clear() {
            awardLocks.clear();
        }


    }

    private static class CacheLock {

        /**
         * last visit time
         */
        @Getter
        protected volatile long lastAccess;

        /**
         * object survival time. '<= 0' means permanent survival
         */
        protected final long ttl;
        private final ReentrantLock lock;

        public CacheLock(ReentrantLock lock, long ttl) {
            this.lock = lock;
            this.ttl = ttl;
            this.lastAccess = System.currentTimeMillis();
        }

        public boolean lock() {
            return lock.tryLock();
        }

        public void unLock() {
            lock.unlock();
        }

        public boolean isExpire() {
            if (this.ttl > 0) {
                return (System.currentTimeMillis() - this.lastAccess) > this.ttl;
            }
            return false;
        }




    }

}
