package cn.mdmbct.seckill.core.lock;

import cn.mdmbct.seckill.core.utils.MapUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用{@link ReentrantLock} 适用于单机情况下
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/19 10:21
 * @modified mdmbct
 * @since 0.1
 */
public class LocalAwardLock implements AwardLock {


    /**
     * 每个商品一个锁，提高并发度 key:商品id
     * 这里使用公平锁（非公平锁：随机获取的过程，谁运气好，cpu时间片轮询到哪个线程，哪个线程就能获取锁） 17100454981
     */
    private final Map<String, ReentrantLock> idLocks;

//    private final Set<String>

    private int lockWaitTime = 3;

    private TimeUnit timeUnit = TimeUnit.SECONDS;

    /**
     * 默认锁等待时间为3s
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
            // 加这个会导致总有几个倒霉线程获取不到锁
//            idLocks.remove(id);
        }
    }

}
