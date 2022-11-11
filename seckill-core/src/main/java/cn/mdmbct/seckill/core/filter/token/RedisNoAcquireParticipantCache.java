package cn.mdmbct.seckill.core.filter.token;

import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

/**
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/11 上午11:13
 * @modified mdmbct
 * @since 1.0
 */
public class RedisNoAcquireParticipantCache implements NoAcquireParticipantCache {

    private final RMap<Object, Object> cache;

    private static final String CACHE_KEY = "RS_NO_TOKEN_PARTICIPANT";

    public RedisNoAcquireParticipantCache(RedissonClient redissonClient) {
        this.cache = redissonClient.getMap(CACHE_KEY);
    }

    @Override
    public void add(String participantId) {
        cache.put(participantId, participantId);
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
