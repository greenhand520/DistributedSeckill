package cn.mdmbct.seckill.core.cache;

/**
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/23 下午6:17
 * @modified mdmbct
 * @since 1.0
 */
public interface CacheClearListener<K, V> {

    void onRemove(K key, V v);

}
