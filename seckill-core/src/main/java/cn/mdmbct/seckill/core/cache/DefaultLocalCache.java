package cn.mdmbct.seckill.core.cache;

import cn.mdmbct.seckill.core.utils.MapUtils;

import java.util.Map;

/**
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/22 下午6:49
 * @modified mdmbct
 * @since 1.0
 */
public class DefaultLocalCache<K, V> extends BaseLocalCache<K, V> {

    DefaultLocalCache(long defaultTtl, Map<K, CacheObj<V>> map) {
        super(defaultTtl);
        this.cache = map;
    }

    @Override
    public V getValue(K key, boolean isUpdateAccessTime) {
        CacheObj<V> vCacheObj = cache.get(key);
        if (vCacheObj != null) {
            return vCacheObj.getValue(isUpdateAccessTime);
        }
        return null;
    }

    @Override
    public void put(K key, V value, long ttl) {
        cache.put(key, new CacheObj<>(value, ttl == 0 ? defaultTtl : ttl));
    }

    @Override
    public V putIfAbsent(K key, V value, long ttl, boolean isUpdateAccessTime) {
        return MapUtils.computeIfAbsent(cache, key, k -> new CacheObj<>(value, ttl == 0 ? defaultTtl : ttl)).getValue();
    }

    @Override
    public V remove(K key) {
        CacheObj<V> vCacheObj = cache.remove(key);
        return vCacheObj == null ? null : vCacheObj.getValue(false);
    }
}
