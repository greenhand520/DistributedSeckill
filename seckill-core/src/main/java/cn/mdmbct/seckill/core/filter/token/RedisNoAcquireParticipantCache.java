package cn.mdmbct.seckill.core.filter.token;

import cn.mdmbct.seckill.core.award.AwardSeckill;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.time.Duration;

/**
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/11 上午11:13
 * @modified mdmbct
 * @since 1.0
 */
public class RedisNoAcquireParticipantCache implements NoAcquireParticipantCache {

    private final RMap<Object, Object> cache;

    public RedisNoAcquireParticipantCache(RedissonClient redissonClient, AwardSeckill seckill) {
        // DSK:${seckillId}:NoAcquireParticipant
        this.cache = redissonClient.getMap("DSK:" + seckill.getId() + ":NoAcquireParticipant");
        this.cache.expire(Duration.ofMillis(seckill.getExpireTime()));
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
