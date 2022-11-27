package cn.mdmbct.seckill.core.cache;

import java.io.Serializable;
import java.util.List;

/**
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/21 下午6:19
 * @modified mdmbct
 * @since 1.0
 */
public interface Cache<K, V> extends Serializable {

    V getValue(K key, boolean isUpdateAccessTime);

    /**
     * @param key   key
     * @param value value
     * @param ttl   if ttl is > 0, update the ttl, is < 0, never expire, is = 0, use default ttl.
     */
    void put(K key, V value, long ttl);

    /**
     * if the key is not existed, put it
     *
     * @param key                key
     * @param value              value
     * @param ttl                if ttl is > 0, update the ttl, is < 0, never expire, is = 0, use default ttl.
     * @param isUpdateAccessTime is update access time
     * @return if put value successfully, return it, otherwise return null.
     */
    V putIfAbsent(K key, V value, long ttl, boolean isUpdateAccessTime);

    /**
     *
     * @param key key
     * @return the removed obj
     */
    V remove(K key);

    /**
     * get all the value
     * @return collections of all the value
     */
    List<V> values();

    List<K> keys();

    /**
     * auto clear the expired obj
     * @param delay scheduled cleanup interval
     */
    void autoClear(long delay);

    void stopAutoClear();

    /**
     * clear the expired obj immediately.
     */
    void clear();

    void setClearListener(CacheClearListener<K, V> listener);

    void clearAll();

}
