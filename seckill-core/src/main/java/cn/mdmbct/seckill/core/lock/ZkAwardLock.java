package cn.mdmbct.seckill.core.lock;

import com.sun.istack.internal.NotNull;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * ZooKeeper distributed lock is suitable for multi-node servers <br>
 * Advantages: ZooKeeper distributed lock (Inter Process Mutex is used here), which can effectively solve distributed problems,
 * non-reentrant problems, and is relatively simple to use.
 *  <br>
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

    private final String lockPath;
    private final Map<String, InterProcessMutex> mutexMap;
    private int baseSleepTimeMs = 1000;
    private int maxRetries = 3;
    private long lockWaitTime = 3;
    private TimeUnit lockWaitTimeTimeUnit = TimeUnit.SECONDS;
    private String address = "localhost:2181";
    private CuratorFramework client;


    /**
     * default <br>
     * baseSleepTimeMs = 1000 <br>
     * maxRetries = 3 <br>
     * lockWaitTime = 3s <br>
     * address = "localhost:2181"
     *
     * @param lockPath zk distributed lock node directory, such as："/curator/lock/seckill" <br>
     *                 ⚠⚠⚠ Note that the node cannot have '/' at the end
     */
    public ZkAwardLock(@NotNull String lockPath, @NotNull Set<String> awardIds) {

        if (lockPath == null || lockPath.trim().length() <= 1 || lockPath.endsWith("/")) {
            throw new IllegalArgumentException("The value of param 'lockPath' " + lockPath + " is illegal.");
        }

        if (awardIds == null || awardIds.size() == 0) {
            throw new IllegalArgumentException("The count of award id must be > 0.");
        }

        this.lockPath = lockPath;
        this.mutexMap = new HashMap<>(awardIds.size());
        init(awardIds);
    }

    public ZkAwardLock(String lockPath,
                       int baseSleepTimeMs,
                       int maxRetries,
                       String address,
                       long lockWaitTime,
                       TimeUnit lockWaitTimeTimeUnit,
                       Set<String> productIds) {
        this.lockPath = lockPath;
        this.baseSleepTimeMs = baseSleepTimeMs;
        this.maxRetries = maxRetries;
        this.address = address;
        this.lockWaitTime = lockWaitTime;
        this.lockWaitTimeTimeUnit = lockWaitTimeTimeUnit;
        this.mutexMap = new HashMap<>(productIds.size());
        init(productIds);
    }

    private void init(Set<String> productIds) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries);
        client = CuratorFrameworkFactory.newClient(address, retryPolicy);
        client.start();
        // shared reentrant lock
        productIds.forEach(productId -> mutexMap.put(productId, new InterProcessMutex(client, lockPath + "/" + productId)));
    }


    @Override
    public boolean tryLock(String id) {

        try {
            return mutexMap.get(id).acquire(lockWaitTime, lockWaitTimeTimeUnit);
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
