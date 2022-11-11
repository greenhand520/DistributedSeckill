package cn.mdmbct.seckill.core.lock;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Zookeeper分布式锁  适用于多节点<p>
 * 优点：ZooKeeper分布式锁（这里使用InterProcessMutex），能有效的解决分布式问题，不可重入问题，使用起来也较为简单。 <p>
 * 缺点：ZooKeeper实现的分布式锁，性能并不太高。每次在创建锁和释放锁的过程中，都要动态创建、销毁瞬时节点来实现锁功能。
 * ZK中创建和删除节点只能通过Leader服务器来执行，然后Leader服务器还需要将数据同步到所有的Follower机器上，这样频繁的网络通信，性能的短板是非常突出的。
 * <p>
 * 在高性能，高并发的场景下，不建议使用ZooKeeper的分布式锁。而由于ZooKeeper的高可用特性，所以在并发量不是太高的场景，推荐使用ZooKeeper的分布式锁。
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/19 20:21
 * @modified mdmbct
 * @since 0.1
 */
public class ZkAwardLock implements AwardLock {

    private int baseSleepTimeMs = 1000;

    private int maxRetries = 3;

    private long lockWaitTime = 3;

    private TimeUnit lockWaitTimeTimeUnit = TimeUnit.SECONDS;

    private String address = "localhost:2181";

    private CuratorFramework client;

    private final String lockPath;

    private final Map<String, InterProcessMutex> mutexMap;


    /**
     * 默认 <br>
     * baseSleepTimeMs = 1000 <br>
     * maxRetries = 3 <br>
     * lockWaitTime = 3s <br>
     * address = "localhost:2181"
     *
     * @param lockPath zk分布式锁节点目录，比如：“/curator/lock/seckill“，注意节点最后不能有”/“
     */
    public ZkAwardLock(String lockPath, List<String> productIds) {
        this.lockPath = lockPath;
        this.mutexMap = new HashMap<>(productIds.size());
        init(productIds);
    }

    public ZkAwardLock(String lockPath,
                       int baseSleepTimeMs,
                       int maxRetries,
                       String address,
                       long lockWaitTime,
                       TimeUnit lockWaitTimeTimeUnit,
                       List<String> productIds) {
        this.lockPath = lockPath;
        this.baseSleepTimeMs = baseSleepTimeMs;
        this.maxRetries = maxRetries;
        this.address = address;
        this.lockWaitTime = lockWaitTime;
        this.lockWaitTimeTimeUnit = lockWaitTimeTimeUnit;
        this.mutexMap = new HashMap<>(productIds.size());
        init(productIds);
    }

    private void init(List<String> productIds) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries);
        client = CuratorFrameworkFactory.newClient(address, retryPolicy);
        client.start();
        // 共享可重入锁
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
