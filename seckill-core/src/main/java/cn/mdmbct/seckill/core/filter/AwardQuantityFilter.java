package cn.mdmbct.seckill.core.filter;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.award.Award;
import cn.mdmbct.seckill.core.award.AwardSeckill;
import cn.mdmbct.seckill.core.award.repository.AwardRepository;
import cn.mdmbct.seckill.core.award.repository.LocalAwardRepository;
import cn.mdmbct.seckill.core.award.repository.RedisAwardRepository;
import cn.mdmbct.seckill.core.lock.AwardLock;
import cn.mdmbct.seckill.core.lock.LocalAwardLock;
import cn.mdmbct.seckill.core.lock.RedissonAwardLock;
import cn.mdmbct.seckill.core.lock.ZkAwardLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * The award (or divided red packet) remain count filter is the last filter.<br>
 * if the stock quantity is > 0, it will reduce one and add the award (or divided red packet) to context,  otherwise not.
 * if the thread get the award lock and the award count is > 0, the award count will decrease one. <br>
 * and {@link AwardQuantityFilter#doFilter(Participant, String)} will return true, otherwise return false.
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/22 8:22
 * @modified mdmbct
 * @since 0.1
 */
public class AwardQuantityFilter extends Filter<Award> {

    private final AwardRepository awardRepository;

    private final AwardLock lock;

    public AwardQuantityFilter(AwardRepository awardRepository, AwardLock awardLock) {
        super(LAST_FILTER_ORDER);
        this.awardRepository = awardRepository;
        this.lock = awardLock;
    }

    @Override
    public String notPassMsg() {
        return "没有了！";
    }

    @Override
    public boolean doFilter(Participant participant, String awardId) {
        try {
            if (lock.tryLock(awardId)) {
                AwardRepository.UpdateRes updateRes = awardRepository.decrOne(awardId);
                if (updateRes.isSuccess()) {
                    getFilterContext().setCompeteRes(new Award(awardId, updateRes.getNewCount()));
                    return true;
                }
            }
        } finally {
            lock.unLock(awardId);
        }
        return false;
    }


    public static class LocalAwardQuantityFilter extends AwardQuantityFilter {

        public LocalAwardQuantityFilter(AwardSeckill seckill) {
            super(new LocalAwardRepository(seckill), new LocalAwardLock());
        }
    }

    public static class RedisAwardQuantityFilter extends AwardQuantityFilter {

        public RedisAwardQuantityFilter(AwardSeckill seckill, RedissonClient redissonClient) {
            super(new RedisAwardRepository(redissonClient, seckill),
                    new RedissonAwardLock(redissonClient, "REDIS_AWARD_LOCK_" + seckill.getId()));
        }

        public RedisAwardQuantityFilter(AwardSeckill seckill,
                                                   RedissonClient redissonClient,
                                                   int lockWaitTime,
                                                   int lockExpireTime,
                                                   TimeUnit timeUnit) {
            super(new RedisAwardRepository(redissonClient, seckill),
                    new RedissonAwardLock(redissonClient,
                            lockWaitTime,
                            lockExpireTime,
                            timeUnit,
                            "REDIS_AWARD_LOCK_" + seckill.getId()));
        }
    }

    public static class ZKAwardQuantityFilter extends AwardQuantityFilter {


        public ZKAwardQuantityFilter(AwardSeckill seckill, RedissonClient redissonClient) {
            super(new RedisAwardRepository(redissonClient, seckill),
                    new ZkAwardLock("/curator/lock/seckill/" + seckill.getId(),
                            seckill.getAwards().stream().map(Award::getId).collect(Collectors.toSet())
                    )
            );
        }

        public ZKAwardQuantityFilter(AwardSeckill seckill,
                                                RedissonClient redissonClient,
                                                int baseSleepTimeMs,
                                                int maxRetries,
                                                String address,
                                                long lockWaitTime,
                                                TimeUnit lockWaitTimeTimeUnit) {
            super(new RedisAwardRepository(redissonClient, seckill),
                    new ZkAwardLock("/curator/lock/seckill/" + seckill.getId(),
                            baseSleepTimeMs,
                            maxRetries,
                            address,
                            lockWaitTime,
                            lockWaitTimeTimeUnit,
                            seckill.getAwards().stream().map(Award::getId).collect(Collectors.toSet())
                    )
            );
        }
    }
}
