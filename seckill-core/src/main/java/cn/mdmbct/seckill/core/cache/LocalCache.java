package cn.mdmbct.seckill.core.cache;

import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.StampedLock;

/**
 * a cache impl by hash map, which each element in it has an expired time. <br>
 * you can clear the expired elements manually or use method {@link LocalCache#autoClear(long)} to clear automatically.
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/21 上午9:00
 * @modified mdmbct
 * @since 1.0
 */
public abstract class LocalCache<K, V> implements Cache<K, V> {

    private static final long serialVersionUID = -4912286546402153448L;
    private Map<K, CacheObj<V>> map;

    /**
     * Optimistic reading of StampedLock allows a writing thread to acquire a writable lock,
     * so it will not cause all writing threads to block, that is, when reading more and writing less,
     * the writing thread has the opportunity to acquire a writable lock, reducing the problem of thread
     * starvation and greatly improving throughput.
     *
     * @see <a href="https://www.cnblogs.com/jiagoushijuzi/p/13721319.html">高性能解决线程饥饿的利器 StampedLock</a>
     */
    private final StampedLock lock = new StampedLock();

    private ScheduledFuture<?> clearJob;

    @Setter
    private long ttl;

    public LocalCache() {
        this.map = new HashMap<>();
    }

    public LocalCache(long ttl) {
        this.ttl = ttl;
    }

    /**
     *
     * @param key key
     * @param isUpdateAccessTime is update access time
     * @return value
     */
    @Override
    public V getValue(K key, boolean isUpdateAccessTime) {
        long optimisticReadLockStamp = lock.tryOptimisticRead();
        V value = getValueWithLock(key, isUpdateAccessTime);
        // validate update by writing thread
        if (!lock.validate(optimisticReadLockStamp)) {
            // if a writing thread updated it, use pessimistic lock to re-read
            long readLockStamp = lock.readLock();
            try {
                value = getValueWithLock(key, isUpdateAccessTime);
            } finally {
                lock.unlockRead(readLockStamp);
            }
        }
        return value;
    }

    public V getValueWithLock(K key, boolean isUpdateAccessTime) {
        CacheObj<V> vCacheObj = map.get(key);
        if (vCacheObj.isExpired()) {
            map.remove(key);
            return null;
        }
        return vCacheObj.getValue(isUpdateAccessTime);
    }

    public void putWithoutLock(K key, V value, long ttl) {
        map.put(key, new CacheObj<>(value, ttl));
    }

    /**
     * use new ttl
     * @param key key
     * @param value value
     * @param newTtl time of survival
     * @return is it added successfully
     */
    @Override
    public boolean put(K key, V value, long newTtl) {
        long stamp = lock.writeLock();
        try {
            putWithoutLock(key, value, newTtl);
        } finally {
            lock.unlockRead(stamp);
        }
        return true;
    }

    /**
     * use default ttl
     * @param key key
     * @param value value
     */
    public void put(K key, V value) {
        put(key, value, ttl);
    }

    @Override
    public void autoClear(long delay) {
        this.clearJob = CacheClearService.instacne().addClearJob(this::clear, delay);
    }

    @Override
    public void stopAutoClear() {
        if (clearJob != null) {
            clearJob.cancel(true);
        }
    }

    @Override
    public void clear() {
        long stamp = lock.writeLock();
        try {
            for (K k : map.keySet()) {
                if (map.get(k).isExpired()) {
                    map.remove(k);
                }
            }
        } finally {
            lock.unlockWrite(stamp);
        }
    }
}
