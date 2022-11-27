package cn.mdmbct.seckill.core.filter.count.window;

import org.redisson.api.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * For mul servers nodes, statistics all participants count impl by sliding time window
 * which impl by redis zset <br>
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/27 下午9:18
 * @modified mdmbct
 * @since 1.0
 */
public class RedisAllParticipantsSWC extends SlidingWindowCount{

    private final RedissonClient redissonClient;

    private final String key;

    public RedisAllParticipantsSWC(RedissonClient redissonClient, TimeUnit timeUnit, int limit, String seckillId) {
        super(timeUnit, limit);
        this.redissonClient = redissonClient;
        this.key = "DSK:" + seckillId + ":AllParticipantSWC";
    }

    @Override
    public int increaseOne() {
        try {
            BatchOptions batchOptions = BatchOptions.defaults();
            batchOptions.executionMode(BatchOptions.ExecutionMode.IN_MEMORY_ATOMIC);
            RBatch batch = redissonClient.createBatch(batchOptions);
            long now = System.currentTimeMillis();
            RScoredSortedSetAsync<String> set = batch.getScoredSortedSet(key);
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
