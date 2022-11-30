package cn.mdmbct.seckill.core.filter.count.window;

import cn.mdmbct.seckill.core.cache.CacheClearService;

import java.util.TreeSet;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Sliding window count impl by {@link TreeSet}
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/27 下午9:27
 * @modified mdmbct
 * @since 1.0
 */
public abstract class TreeSetSWC extends BaseSlidingWindowCount {

    private final TreeSet<Long> counter;

    private long expiredTime;

    private final ScheduledFuture<?> clearJob;

    public TreeSetSWC(TimeUnit timeUnit) {
        super(timeUnit);
        this.counter = new TreeSet<>();
        this.clearJob = CacheClearService.instance().addClearJob(this::clearExpired, windowTime);
    }

    private void clearExpired() {
        if (expiredTime > 0) {
            counter.removeIf(aLong -> aLong < expiredTime);
            expiredTime = 0;
        }
    }

    protected synchronized int increaseOneToCounter() {
        long now = System.currentTimeMillis();
        counter.add(now);
        this.expiredTime = now - windowTime;
        return counter.subSet(expiredTime, true, now, true).size();
    }

    @Override
    public void clear() {
        if (clearJob != null) {
            clearJob.cancel(true);
        }
        counter.clear();
    }

    public static TreeSetSWC defaultLocalParticipantsSWC(TimeUnit timeUnit) {
        return new TreeSetSWC(timeUnit) {
            @Override
            public synchronized int increaseOne() {
                return increaseOneToCounter();
            }
        };
    }

}
