package cn.mdmbct.seckill.core.award;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 对商品数量的修改直接在Redis中进行 适用于分布式下使用
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/18 19:56
 * @modified mdmbct
 * @since 0.1
 */
public class RedisAwardRepository implements AwardRepository {

    private final RedissonClient redissonClient;

    private static final String COUNT_CACHE_PREFIX = "AwardCount:AWARD_ID_";

    private final Map<String, String> keyCache;

    public RedisAwardRepository(RedissonClient redissonClient, AwardSeckill seckill) {
        this.redissonClient = redissonClient;
        keyCache = seckill.getAwards().stream().collect(Collectors.toMap(Award::getId, award -> COUNT_CACHE_PREFIX + award.id));

        seckill.getAwards().forEach(award -> {
            RAtomicLong awardCount = redissonClient.getAtomicLong(keyCache.get(award.id));
            // 活动结束后5S过期
            awardCount.expireIfGreater(Duration.ofMillis(seckill.getStartTime() + seckill.getTtl() * 1000 + 5000));
            awardCount.set(award.remainCount.longValue());
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
}