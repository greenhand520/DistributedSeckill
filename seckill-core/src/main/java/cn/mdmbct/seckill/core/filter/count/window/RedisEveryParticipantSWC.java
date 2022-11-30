package cn.mdmbct.seckill.core.filter.count.window;

import org.redisson.api.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * For mul servers nodes, statistics every participant count impl by sliding time window
 * which impl by redis zset <br>
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/8 下午5:19
 * @modified mdmbct
 * @since 1.0
 */
public class RedisEveryParticipantSWC extends BaseSlidingWindowCount {

    private final RedissonClient redissonClient;

    private final String keyPrefix;

    public RedisEveryParticipantSWC(RedissonClient redissonClient, /*int slot, */TimeUnit timeUnit, int limit, String seckillId) {
        super(/*slot,*/ timeUnit);
        this.redissonClient = redissonClient;
        this.keyPrefix = "DSK:" + seckillId + ":EveryParticipantSWC:";
    }

    @Override
    public int increaseOne(String participantId) {
        try {
            BatchOptions batchOptions = BatchOptions.defaults();
            batchOptions.executionMode(BatchOptions.ExecutionMode.IN_MEMORY_ATOMIC);
            RBatch batch = redissonClient.createBatch(batchOptions);
            long now = System.currentTimeMillis();
            RScoredSortedSetAsync<String> set = batch.getScoredSortedSet(keyPrefix + participantId);
            set.addAsync(now, String.valueOf(now));
            set.removeRangeByScoreAsync(0, true, now - windowTime, false);
            RFuture<Integer> countRFuture = set.sizeAsync();
            batch.execute();
            return countRFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
