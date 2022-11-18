package cn.mdmbct.seckill.core.filter;

import cn.mdmbct.seckill.core.award.Award;
import cn.mdmbct.seckill.core.award.repository.LocalAwardRepository;
import cn.mdmbct.seckill.core.award.repository.RedisAwardRepository;
import cn.mdmbct.seckill.core.award.red.GrabDividedRedPacket;
import cn.mdmbct.seckill.core.lock.LocalAwardLock;
import cn.mdmbct.seckill.core.lock.RedissonAwardLock;
import cn.mdmbct.seckill.core.lock.ZkAwardLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Divided red packet quantity filter
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/17 上午10:41
 * @modified mdmbct
 * @since 1.0
 */
public class DividedRedPacketQuantityFilter {

    public static class LocalDividedRedPacketQuantityFilter extends AwardQuantityFilter {

        public LocalDividedRedPacketQuantityFilter(GrabDividedRedPacket grabDividedRedPacket) {
            super(new LocalAwardRepository(grabDividedRedPacket), new LocalAwardLock());
        }
    }

    public static class RedisDividedRedPacketQuantityFilter extends AwardQuantityFilter {

        public RedisDividedRedPacketQuantityFilter(GrabDividedRedPacket grabDividedRedPacket, RedissonClient redissonClient) {
            super(new RedisAwardRepository(redissonClient, grabDividedRedPacket),
                    new RedissonAwardLock(redissonClient, "REDIS_AWARD_LOCK_" + grabDividedRedPacket.getId()));
        }

        public RedisDividedRedPacketQuantityFilter(GrabDividedRedPacket grabDividedRedPacket,
                                                   RedissonClient redissonClient,
                                                   int lockWaitTime,
                                                   int lockExpireTime,
                                                   TimeUnit timeUnit) {
            super(new RedisAwardRepository(redissonClient, grabDividedRedPacket),
                    new RedissonAwardLock(redissonClient,
                            lockWaitTime,
                            lockExpireTime,
                            timeUnit,
                            "REDIS_AWARD_LOCK_" + grabDividedRedPacket.getId()));
        }
    }

    public static class ZKDividedRedPacketQuantityFilter extends AwardQuantityFilter {


        public ZKDividedRedPacketQuantityFilter(GrabDividedRedPacket grabDividedRedPacket, RedissonClient redissonClient) {
            super(new RedisAwardRepository(redissonClient, grabDividedRedPacket),
                    new ZkAwardLock("/curator/lock/seckill/" + grabDividedRedPacket.getId(),
                            grabDividedRedPacket.getRedPackets().stream().map(Award::getId).collect(Collectors.toSet())
                    )
            );
        }

        public ZKDividedRedPacketQuantityFilter(GrabDividedRedPacket grabDividedRedPacket,
                                                RedissonClient redissonClient,
                                                int baseSleepTimeMs,
                                                int maxRetries,
                                                String address,
                                                long lockWaitTime,
                                                TimeUnit lockWaitTimeTimeUnit) {
            super(new RedisAwardRepository(redissonClient, grabDividedRedPacket),
                    new ZkAwardLock("/curator/lock/seckill/" + grabDividedRedPacket.getId(),
                            baseSleepTimeMs,
                            maxRetries,
                            address,
                            lockWaitTime,
                            lockWaitTimeTimeUnit,
                            grabDividedRedPacket.getRedPackets().stream().map(Award::getId).collect(Collectors.toSet())
                    )
            );
        }
    }


}
