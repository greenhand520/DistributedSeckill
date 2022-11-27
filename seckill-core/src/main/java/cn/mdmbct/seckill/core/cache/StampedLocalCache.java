package cn.mdmbct.seckill.core.cache;

import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;

/**
 * a cache impl by hash map, which each element in it has an expired time and protected by {@link StampedLock}. <br>
 * you can clear the expired elements manually by method {@link StampedLocalCache#clear()} or
 * use method {@link StampedLocalCache#autoClear(long)} to clear automatically.
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/21 下午7:00
 * @modified mdmbct
 * @since 1.0
 */
public class StampedLocalCache<K, V> extends BaseLocalCache<K, V> {

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

    @Setter
    private long defaultTtl = 0;

    public StampedLocalCache() {
        this.map = new HashMap<>();
    }

    public StampedLocalCache(long defaultTtl) {
        this.defaultTtl = defaultTtl;
    }

    /**
     * @param key                key
     * @param isUpdateAccessTime is update access time
     * @return value
     */
    @Override
    public V getValue(K key, boolean isUpdateAccessTime) {
        long optimisticReadLockStamp = lock.tryOptimisticRead();
        V value = getValueWithoutLock(key, isUpdateAccessTime);
        // validate update by writing thread
        if (!lock.validate(optimisticReadLockStamp)) {
            // if a writing thread updated it, use pessimistic lock to re-read
            long readLockStamp = lock.readLock();
            try {
                value = getValueWithoutLock(key, isUpdateAccessTime);
            } finally {
                lock.unlockRead(readLockStamp);
            }
        }
        return value;
    }

    public V getValueWithoutLock(K key, boolean isUpdateAccessTime) {
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
     *
     * @param key    key
     * @param value  value
     * @param newTtl time of survival
     */
    @Override
    public void put(K key, V value, long newTtl) {
        long stamp = lock.writeLock();
        try {
            putWithoutLock(key, value, newTtl > 0 ? newTtl : defaultTtl);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * use default ttl
     *
     * @param key   key
     * @param value value
     */
    public void put(K key, V value) {
        put(key, value, defaultTtl);
    }

    @Override
    public V putIfAbsent(K key, V value, long ttl, boolean isUpdateAccessTime) {

        long optimisticReadStamp = lock.tryOptimisticRead();
        CacheObj<V> vCacheObj = map.get(key);
        if (lock.validate(optimisticReadStamp)) {
            // no other thread put elements

            if (vCacheObj == null) {
                put(key, value, ttl > 0 ? ttl : defaultTtl);
                return value;
            } else if (ttl > 0) {
                vCacheObj.setTtl(ttl);
            }
            return vCacheObj.getValue();

        } else {
            // another writing thread is putting elements, re-read
            long stamp = lock.readLock();
            try {
                vCacheObj = map.get(key);

                if (vCacheObj == null) {
                    put(key, value, ttl > 0 ? ttl : defaultTtl);
                    return value;
                } else if (ttl > 0) {
                    vCacheObj.setTtl(ttl);
                }
                return vCacheObj.getValue();

            } finally {
                lock.unlockRead(stamp);
            }
        }
    }

    @Override
    public V remove(K key) {
        long stamp = lock.writeLock();
        try {
            return map.remove(key).getValue();
        } finally {
            lock.unlockWrite(stamp);
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
