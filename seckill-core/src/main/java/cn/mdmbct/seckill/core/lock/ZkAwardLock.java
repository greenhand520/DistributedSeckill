package cn.mdmbct.seckill.core.lock;

import com.sun.istack.internal.NotNull;
import lombok.Getter;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * ZooKeeper distributed lock is suitable for multi-node servers <br>
 * Advantages: ZooKeeper distributed lock (Inter Process Mutex is used here), which can effectively solve distributed problems,
 * non-reentrant problems, and is relatively simple to use.
 * <br>
 * Disadvantages: The performance of the distributed lock implemented by ZooKeeper is not too high.
 * Every time in the process of creating and releasing the lock, the instantaneous node must be dynamically created
 * and destroyed to realize the lock function.
 * <br>
 * The creation and deletion of nodes in ZooKeeper can only be performed by the Leader server,
 * and then the Leader server also needs to synchronize data to all Follower machines.
 * Such frequent network communication has a very prominent performance shortcoming.
 * <br>
 * In high-performance and high-concurrency scenarios, it is not recommended to use ZooKeeper's distributed locks.
 * Due to the high availability of ZooKeeper, it is recommended to use ZooKeeper's distributed lock in scenarios
 * where the amount of concurrency is not too high
 * <br>
 * 优点：ZooKeeper分布式锁（这里使用InterProcessMutex），能有效的解决分布式问题，不可重入问题，使用起来也较为简单。 <br>
 * 缺点：ZooKeeper实现的分布式锁，性能并不太高。每次在创建锁和释放锁的过程中，都要动态创建、销毁瞬时节点来实现锁功能。<br>
 * ZK中创建和删除节点只能通过Leader服务器来执行，然后Leader服务器还需要将数据同步到所有的Follower机器上，这样频繁的网络通信，性能的短板是非常突出的。 <br>
 * 在高性能，高并发的场景下，不建议使用ZooKeeper的分布式锁。而由于ZooKeeper的高可用特性，所以在并发量不是太高的场景，推荐使用ZooKeeper的分布式锁。
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/19 20:21
 * @modified mdmbct
 * @since 0.1
 */
public class ZkAwardLock implements AwardLock {

    @Getter
    public static class ZkLockConfig implements Serializable {

        /**
         * /DSK/lock/${seckillId}/${awardId}
         */
        private final String lockPathPrefix;

        private int baseSleepTimeMs = 1000;

        private int maxRetries = 3;

        private long lockWaitTime = 3;

        private TimeUnit lockWaitTimeTimeUnit = TimeUnit.SECONDS;

        private String address = "localhost:2181";

        public ZkLockConfig(String seckillId, int baseSleepTimeMs, int maxRetries, long lockWaitTime, TimeUnit lockWaitTimeTimeUnit, String address) {
            this(seckillId);
            this.baseSleepTimeMs = baseSleepTimeMs;
            this.maxRetries = maxRetries;
            this.lockWaitTime = lockWaitTime;
            this.lockWaitTimeTimeUnit = lockWaitTimeTimeUnit;
            this.address = address;
        }

        /**
         * default
         * baseSleepTimeMs = 1000 <br>
         * maxRetries = 3 <br>
         * lockWaitTime = 3s <br>
         * address = "localhost:2181"
         */
        public ZkLockConfig(String seckillId) {
            this.lockPathPrefix = "/DSK/lock/" + seckillId + "/";
        }
    }

    private final Map<String, InterProcessMutex> mutexMap;
    private CuratorFramework client;

    private final ZkLockConfig lockConfig;

    /**
     * default <br>
     */
    public ZkAwardLock(@NotNull String seckillId,
                       @NotNull Set<String>awardIds) {
        this.lockConfig = new ZkLockConfig(seckillId);
        this.mutexMap = new HashMap<>(awardIds.size());
        init(awardIds);
    }

    public ZkAwardLock(ZkLockConfig lockConfig,
                       Set<String> awardIds) {
        this.lockConfig = lockConfig;
        this.mutexMap = new HashMap<>(awardIds.size());
        init(awardIds);
    }

    private void init(Set<String> awardIds) {
        if (awardIds == null || awardIds.size() == 0) {
            throw new IllegalArgumentException("The count of award id must be > 0.");
        }
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(lockConfig.baseSleepTimeMs, lockConfig.maxRetries);
        client = CuratorFrameworkFactory.newClient(lockConfig.address, retryPolicy);
        client.start();
        // shared reentrant lock
        awardIds.forEach(productId -> mutexMap.put(productId, new InterProcessMutex(client, lockConfig.lockPathPrefix + productId)));
    }


    @Override
    public boolean tryLock(String id) {

        try {
            return mutexMap.get(id).acquire(lockConfig.lockWaitTime, lockConfig.lockWaitTimeTimeUnit);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void unLock(String id) {

        try {
            mutexMap.get(id).release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
