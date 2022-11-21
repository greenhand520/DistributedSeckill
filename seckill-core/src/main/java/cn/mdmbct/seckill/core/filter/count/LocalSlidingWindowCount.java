package cn.mdmbct.seckill.core.filter.count;

import cn.mdmbct.seckill.core.cache.Cache;
import cn.mdmbct.seckill.core.cache.LocalCache;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
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

    private final Cache<String, ReentrantLock> lockCache;
    private final Map<String, TreeSet<Long>> countMap;

    public LocalSlidingWindowCount(int slot, TimeUnit timeUnit, int limit, long cacheExpireTimeout) {
        super(slot, timeUnit, limit);
        this.countMap = new HashMap<>();
        this.lockCache = new LocalCache<>(cacheExpireTimeout);
        // 30s
        lockCache.autoClear(30 * 1000);
    }

    @Override
    public int increaseOne(String participantId) {
        return 0;
    }

    @Override
    public void clear() {
        super.clear();
        lockCache.clearAll();
        countMap.values().forEach(TreeSet::clear);
        countMap.clear();
    }




}
