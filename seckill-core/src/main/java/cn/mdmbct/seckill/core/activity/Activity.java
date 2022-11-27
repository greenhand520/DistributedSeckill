package cn.mdmbct.seckill.core.activity;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.award.Award;
import cn.mdmbct.seckill.core.award.AwardSeckill;
import cn.mdmbct.seckill.core.award.CompleteRedPacket;
import cn.mdmbct.seckill.core.award.repository.LocalAwardRepository;
import cn.mdmbct.seckill.core.award.repository.RedisAwardRepository;
import cn.mdmbct.seckill.core.filter.Filter;
import cn.mdmbct.seckill.core.filter.FilterChain;
import cn.mdmbct.seckill.core.lock.LocalAwardLock;
import cn.mdmbct.seckill.core.lock.RedisAwardLock;
import cn.mdmbct.seckill.core.lock.ZkAwardLock;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract activity executor class <br>
 * you should create an object of this class to execute a seckill or grab red packet activity <br>
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/16 下午9:29
 * @modified mdmbct
 * @since 1.0
 */
public abstract class Activity<R> {

    protected final FilterChain<R> filterChain;

    public Activity(List<Filter<R>> filters) {
        this.filterChain = new FilterChain<>(filters);
    }

    /**
     * if the participant take an award(red packet) luckily, this method will return it, otherwise return null.
     *
     * @param participant participant
     * @param awardId     awardId. when grab a red
     * @return award or the denomination of red packet that the participant take
     */
    public abstract R compete(Participant participant, String awardId);

    /**
     * clear memory
     */
    public void clear() {
        filterChain.clear();
    }

    //// some default constructors ////

    public static AwardActivity awardWithLocal(List<Filter<Award>> filters, AwardSeckill seckill) {
        return new AwardActivity(filters, new LocalAwardRepository(seckill), new LocalAwardLock());
    }

    public static AwardActivity awardWithRedis(List<Filter<Award>> filters,
                                               AwardSeckill seckill,
                                               RedissonClient redissonClient) {
        return new AwardActivity(filters,
                new RedisAwardRepository(redissonClient, seckill),
                new RedisAwardLock(seckill.getId(), redissonClient));
    }

    public static AwardActivity awardWithRedis(List<Filter<Award>> filters,
                                               AwardSeckill seckill,
                                               RedissonClient redissonClient,
                                               RedisAwardLock.RedisAwardLockConfig lockConfig) {
        return new AwardActivity(filters,
                new RedisAwardRepository(redissonClient, seckill),
                new RedisAwardLock(redissonClient, lockConfig));
    }

    public static AwardActivity awardWithZooKeeper(List<Filter<Award>> filters,
                                                   AwardSeckill seckill,
                                                   RedissonClient redissonClient) {
        return new AwardActivity(filters,
                new RedisAwardRepository(redissonClient, seckill),
                new ZkAwardLock(seckill.getId(), seckill.getAwards().stream().map(Award::getId).collect(Collectors.toSet())));
    }

    public static AwardActivity awardWithZooKeeper(List<Filter<Award>> filters,
                                                   AwardSeckill seckill,
                                                   RedissonClient redissonClient,
                                                   ZkAwardLock.ZkLockConfig lockConfig) {
        return new AwardActivity(filters,
                new RedisAwardRepository(redissonClient, seckill),
                new ZkAwardLock(lockConfig, seckill.getAwards().stream().map(Award::getId).collect(Collectors.toSet())));
    }

    public static CompleteRedPacketActivity redPacketActivity(List<Filter<Double>> filters, double totalMoney, int redPacketSplitCount,
                                                              CompleteRedPacket.SplitMethod splitMethod) {
        return new CompleteRedPacketActivity(filters, totalMoney, redPacketSplitCount, splitMethod);
    }

    //// some default constructors ////


}
