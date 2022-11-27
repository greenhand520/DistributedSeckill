package cn.mdmbct.seckill.core.filter.token;

import cn.mdmbct.seckill.core.cache.BaseLocalCache;
import cn.mdmbct.seckill.core.cache.Cache;

/**
 * no token user cache under single node server
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/11 上午10:04
 * @modified mdmbct
 * @since 1.0
 */
public class LocalNoAcqParticipantCache implements NoAcquireParticipantCache {



    private final Cache<String, Object> cache;

    private static final Object V = new Object();

    /**
     * the expired time of elements in cache is 1 second.
     */
    public LocalNoAcqParticipantCache() {
        this.cache = BaseLocalCache.hashMapCache(1000);
    }

    @Override
    public void add(String participantId) {
        cache.put(participantId, V, 0);
    }

    @Override
    public boolean check(String participantId) {
        return cache.remove(participantId) != null;
    }

    @Override
    public void clear() {
        cache.clear();
    }
}
