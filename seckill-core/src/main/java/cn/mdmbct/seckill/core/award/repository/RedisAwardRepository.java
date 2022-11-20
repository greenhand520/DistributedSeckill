package cn.mdmbct.seckill.core.award.repository;

import cn.mdmbct.seckill.core.award.Award;
import cn.mdmbct.seckill.core.award.AwardSeckill;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Modify the quantity of goods directly in Redis, suitable for multi node servers
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/18 19:56
 * @modified mdmbct
 * @since 0.1
 */
public class RedisAwardRepository implements AwardRepository {

    private final RedissonClient redissonClient;

    private final Map<String, String> keyCache;

    public RedisAwardRepository(RedissonClient redissonClient, AwardSeckill seckill) {
        this.redissonClient = redissonClient;
        // DSK:${seckillId}:AwardCount:{awardId}
        String countCachePrefix = "DSK:" + seckill.getId() + ":AwardCount:";
        keyCache = seckill.getAwards().stream().collect(Collectors.toMap(Award::getId, award -> countCachePrefix + award.getId()));

        seckill.getAwards().forEach(award -> {
            RAtomicLong awardCount = redissonClient.getAtomicLong(keyCache.get(award.getId()));
            awardCount.set(award.getRemainCount().longValue());
            awardCount.expire(Duration.ofMillis(seckill.getExpireTime()));
        });


    }

    @Override
    public UpdateRes incrOne(String id) {
        try {
            return new UpdateRes(true, (int) redissonClient.getAtomicLong(keyCache.get(id)).incrementAndGet());
        } catch (Exception e) {
            e.printStackTrace();
            return new UpdateRes(false, 0);
        }
    }

    @Override
    public UpdateRes decrOne(String id) {
        try {
            return new UpdateRes(true, (int) redissonClient.getAtomicLong(keyCache.get(id)).decrementAndGet());
        } catch (Exception e) {
            e.printStackTrace();
            return new UpdateRes(false, 0);
        }
    }

    @Override
    public UpdateRes updateCount(String id, int newCount) {
        try {
            redissonClient.getAtomicLong(keyCache.get(id)).getAndSet(newCount);
            return new UpdateRes(true, newCount);
        } catch (Exception e) {
            e.printStackTrace();
            return new UpdateRes(false, 0);
        }
    }

    @Override
    public void clear() {
        keyCache.clear();
    }
}
