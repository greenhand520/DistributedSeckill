package cn.mdmbct.seckill.common.repository.seckill;

import cn.mdmbct.seckill.common.lock.CompeteResult;
import cn.mdmbct.seckill.common.lock.RedissonDistributeLock;
import cn.mdmbct.seckill.common.redis.JedisProperties;
import cn.mdmbct.seckill.common.redis.RedissonConfigFactory;
import cn.mdmbct.seckill.common.redis.RedissonProperties;
import org.junit.Before;
import org.junit.Test;
import org.redisson.Redisson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class RedisGoodsRepositoryTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(RedisGoodsRepositoryTest.class);

    private RedisGoodsRepository repository;

    @Before
    public void setUp() throws Exception {

        final JedisProperties jedisProperties = new JedisProperties();
        jedisProperties.setMaxIdle(-1);

        final JedisPool jedisPool = JedisProperties.getJedisPool(jedisProperties);


        final RedissonDistributeLock lock = new RedissonDistributeLock(Redisson.create(RedissonConfigFactory.createRedissonConfig(new RedissonProperties())),
                3,
                20,
                TimeUnit.SECONDS,
                "seckill:test_lock_"
        );


        final Seckill seckill = new Seckill(
                Arrays.asList(
                        new Goods("1", 1001),
                        new Goods("2", 1001),
                        new Goods("3", 1001),
                        new Goods("4", 1001),
                        new Goods("5", 1001),
                        new Goods("6", 1001),
                        new Goods("7", 1001),
                        new Goods("8", 1001),
                        new Goods("9", 1001),
                        new Goods("10", 1001)
                ),
                1,
                TimeUnit.MINUTES,
                System.currentTimeMillis()
        );


        this.repository = new RedisGoodsRepository(
                jedisPool,
                lock,
                seckill,
                "seckill:test_goods_"
        );
    }

    @Test
    public void incrOne() {

    }

    @Test
    public void decrOne() {

        int corePoolSize = Runtime.getRuntime().availableProcessors();
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, corePoolSize + 1, 10l, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000));


        int killNum = 1000;
        final CountDownLatch latch = new CountDownLatch(killNum);
        for (int i = 0; i < killNum; i++) {
            long userId = i;
            final Runnable task = () -> {
                final CompeteResult competeResult = repository.decrOne("1");
//                LOGGER.info("用户id: " + userId + competeResult);
                System.out.println("用户id: " + userId + competeResult);
                latch.countDown();
            };
            executor.execute(task);
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testDecrOneTimes() throws InterruptedException {

        int corePoolSize = Runtime.getRuntime().availableProcessors();

        for (int j = 1; j < 11; j++) {
            final ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, corePoolSize + 1, 10l, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(1000));

            int killNum = 1000;
            final CountDownLatch latch = new CountDownLatch(killNum);
            for (int i = 0; i < killNum; i++) {
                long userId = i;
                int finalJ = j;
                final Runnable task = () -> {
                    final CompeteResult competeResult = repository.decrOne(String.valueOf(finalJ));
//                LOGGER.info("用户id: " + userId + competeResult);
                    System.out.println("用户id: " + userId + competeResult);
                    latch.countDown();
                };
                executor.execute(task);
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("--------------------------");
            Thread.sleep(1000);
        }
    }

    @Test
    public void updateCount() {
    }
}