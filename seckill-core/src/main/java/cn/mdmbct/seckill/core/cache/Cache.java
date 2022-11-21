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
     * auto clear the expired obj
     * @param delay scheduled cleanup interval
     */
    void autoClear(long delay);

    void stopAutoClear();

    /**
     * clear the expired obj immediately.
     */
    void clear();

}
