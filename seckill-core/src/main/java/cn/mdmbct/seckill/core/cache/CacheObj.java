package cn.mdmbct.seckill.core.cache;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/21 下午6:24
 * @modified mdmbct
 * @since 1.0
 */
@ToString
public class CacheObj<V> implements Serializable {

    private static final long serialVersionUID = -9200483212932452755L;
    private final V value;

    /**
     * time of survival after of accessed。
     */
    @Getter
    @Setter
    private long ttl;

    /**
     * last access time. new or get operational will update it.
     * @see CacheObj#getValue(boolean)
     */
    @Getter
    private long lastAccess;

    public CacheObj(V value, long ttl) {
        this.value = value;
        this.ttl = ttl;
        this.lastAccess = System.currentTimeMillis();
    }

    public boolean isExpired() {
        if (ttl > 0) {
            return System.currentTimeMillis() > lastAccess + ttl;
        }
        return false;
    }

    /**
     *
     * @param isUpdateAccessTime whether to update accessTime.if update it, the new expired time is ttl + newAccessTime
     * @return value
     */
    public V getValue(boolean isUpdateAccessTime) {
        if (isUpdateAccessTime) {
            lastAccess = System.currentTimeMillis();
        }
        return value;
    }

    /**
     * do not update the access time.
     * @return value
     */
    public V getValue() {
        return value;
    }


}
