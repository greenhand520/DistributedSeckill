package cn.mdmbct.seckill.core.lock;

import cn.mdmbct.seckill.core.utils.MapUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * use{@link ReentrantLock} to impl {@link AwardLock}, suitable for single node servers
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/19 10:21
 * @modified mdmbct
 * @since 0.1
 */
public class LocalAwardLock implements AwardLock {


    /**
     * Every award has its own lock to improve concurrency.key:award id
     * A fair lock is used here (unfair lock: the process of random acquisition, who is lucky, which thread is polled by the cpu time slice, which thread can acquire the lock)
     */
    private final Map<String, ReentrantLock> idLocks;

//    private final Set<String>

    private int lockWaitTime = 3;

    private TimeUnit timeUnit = TimeUnit.SECONDS;

    /**
     * The default lock waiting time is 3s
     */
    public LocalAwardLock() {
        this.idLocks = new ConcurrentHashMap<>();
    }

    public LocalAwardLock(int lockWaitTime, TimeUnit timeUnit) {
        this.idLocks = new ConcurrentHashMap<>();
        this.lockWaitTime = lockWaitTime;
        this.timeUnit = timeUnit;
    }

    @Override
    public boolean tryLock(String id) {
        try {
            final ReentrantLock lock = MapUtils.computeIfAbsent(idLocks, id, key -> new ReentrantLock(true));
            return lock.tryLock(lockWaitTime, timeUnit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void unLock(String id) {
        final ReentrantLock lock = idLocks.get(id);
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
            // Adding this will cause a few unlucky threads to fail to acquire the lock
//            idLocks.remove(id);
        }
    }

}
