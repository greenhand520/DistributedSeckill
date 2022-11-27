package cn.mdmbct.seckill.core.cache;

import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

/**
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/22 下午6:21
 * @modified mdmbct
 * @since 1.0
 */
@NoArgsConstructor
public abstract class BaseLocalCache<K, V> implements Cache<K, V> {

    protected Map<K, CacheObj<V>> cache;

//    @Getter
    private ScheduledFuture<?> clearJob;

    private CacheClearListener<K, V> clearListener = (key, cacheObj) -> {};

    public static <K, V> DefaultLocalCache<K, V> hashMapCache(long defaultTtl) {
        return new DefaultLocalCache<>(defaultTtl, new HashMap<>());
    }

    public static <K, V> DefaultLocalCache<K, V> concurrentHashMapCache(long defaultTtl) {
        return new DefaultLocalCache<>(defaultTtl, new ConcurrentHashMap<>());
    }

    @Setter
    protected long defaultTtl = 0;

    public BaseLocalCache(long defaultTtl) {
        this.defaultTtl = defaultTtl;
    }

    @Override
    public List<V> values() {
        return cache.values().stream().map(CacheObj::getValue).collect(Collectors.toList());
    }

    @Override
    public List<K> keys() {
        return new ArrayList<>(cache.keySet());
    }

    @Override
    public void autoClear(long delay) {
        this.clearJob = CacheClearService.instacne().addClearJob(this::clear, delay);
    }

    @Override
    public void setClearListener(CacheClearListener<K, V> listener) {
        this.clearListener = listener;
    }

    @Override
    public void stopAutoClear() {
        if (clearJob != null) {
            clearJob.cancel(true);
        }
    }

    @Override
    public void clear() {
        for (K k : cache.keySet()) {
            CacheObj<V> vCacheObj = cache.get(k);
            if (vCacheObj.isExpired()) {
                cache.remove(k);
                clearListener.onRemove(k, vCacheObj.getValue());
            }
        }
    }

    @Override
    public void clearAll() {
        cache.clear();
    }

    public Collection<CacheObj<V>> getCacheObjs() {
        return cache.values();
    }


}
