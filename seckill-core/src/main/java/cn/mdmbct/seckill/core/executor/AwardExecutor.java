package cn.mdmbct.seckill.core.executor;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.award.Award;
import cn.mdmbct.seckill.core.award.AwardSeckill;
import cn.mdmbct.seckill.core.award.repository.AwardRepository;
import cn.mdmbct.seckill.core.award.repository.LocalAwardRepository;
import cn.mdmbct.seckill.core.award.repository.RedisAwardRepository;
import cn.mdmbct.seckill.core.filter.AwardQuantityFilter;
import cn.mdmbct.seckill.core.filter.Filter;
import cn.mdmbct.seckill.core.lock.AwardLock;
import cn.mdmbct.seckill.core.lock.LocalAwardLock;
import cn.mdmbct.seckill.core.lock.RedisAwardLock;
import cn.mdmbct.seckill.core.lock.ZkAwardLock;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/20 0:04
 * @modified mdmbct
 * @since 0.1
 */
public class AwardExecutor extends Executor<Award> {
    public AwardExecutor(List<Filter<Award>> filters, AwardRepository awardRepository, AwardLock awardLock) {
        super(filters);
        filters.add(new AwardQuantityFilter(awardRepository, awardLock));
    }

    //// some default constructors ////

    public static AwardExecutor withLocal(List<Filter<Award>> filters, AwardSeckill seckill) {
        return new AwardExecutor(filters, new LocalAwardRepository(seckill), new LocalAwardLock());
    }

    public static AwardExecutor withRedis(List<Filter<Award>> filters,
                                          AwardSeckill seckill,
                                          RedissonClient redissonClient) {
        return new AwardExecutor(filters,
                new RedisAwardRepository(redissonClient, seckill),
                new RedisAwardLock(redissonClient, "REDIS_AWARD_LOCK_"));
    }

    public static AwardExecutor withRedis(List<Filter<Award>> filters,
                                          AwardSeckill seckill,
                                          RedissonClient redissonClient,
                                          RedisAwardLock.RedisAwardLockConfig lockConfig) {
        return new AwardExecutor(filters,
                new RedisAwardRepository(redissonClient, seckill),
                new RedisAwardLock(redissonClient, lockConfig));
    }

    public static AwardExecutor withZooKeeper(List<Filter<Award>> filters,
                                              AwardSeckill seckill,
                                              RedissonClient redissonClient) {
        return new AwardExecutor(filters,
                new RedisAwardRepository(redissonClient, seckill),
                new ZkAwardLock("/curator/lock/seckill/" + seckill.getId(), seckill.getAwards().stream().map(Award::getId).collect(Collectors.toSet())));
    }

    public static AwardExecutor withZooKeeper(List<Filter<Award>> filters,
                                              AwardSeckill seckill,
                                              RedissonClient redissonClient,
                                              ZkAwardLock.ZkLockConfig lockConfig) {
        return new AwardExecutor(filters,
                new RedisAwardRepository(redissonClient, seckill),
                new ZkAwardLock(lockConfig, seckill.getAwards().stream().map(Award::getId).collect(Collectors.toSet())));
    }

    //// some default constructors ////


    @Override
    public Award compete(Participant participant, String awardId) {
        return null;
    }

    @Override
    public void clear() {
        filterChain.clear();
    }
}
