package cn.mdmbct.seckill.core.cache;

import java.io.Serializable;

/**
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/21 上午12:19
 * @modified mdmbct
 * @since 1.0
 */
public interface Cache<K, V> extends Serializable {

    V getValue(K key, boolean isUpdateAccessTime);

    /**
     *
     * @param key key
     * @param value value
     * @param ttl time of survival
     * @return is it added successfully
     */
    boolean put(K key, V value, long ttl);

    /**
     * if the key is not existed, put it
     * @param key key
     * @param value value
     * @param ttl if ttl is > 0, update the ttl.
     * @return is it added successfully
     */
    boolean putIfAbsent(K key, V value, long ttl);

    /**
     *
     * @param key key
     * @return the removed obj
     */
    V remove(K key);

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

    void clearAll();

}
