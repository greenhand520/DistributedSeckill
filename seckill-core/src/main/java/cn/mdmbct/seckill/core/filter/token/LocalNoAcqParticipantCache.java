package cn.mdmbct.seckill.core.filter.token;

import cn.mdmbct.seckill.core.utils.MapUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 单机下无令牌用户缓存 使用Map做缓存
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/11 上午10:04
 * @modified mdmbct
 * @since 1.0
 */
public class LocalNoAcqParticipantCache implements NoAcquireParticipantCache {


    /**
     * 依然是每个key一个锁 且只能单线程读或者写 key:用户id
     */
//    private final Map<String, ReentrantLock> lockMap;

    private final Map<String, Object> cache;

    private static final Object V = new Object();

    public LocalNoAcqParticipantCache() {
//        this.lockMap = new ConcurrentHashMap<>();
        this.cache = new HashMap<>();
    }

    @Override
    public void add(String participantId) {
        cache.put(participantId, V);
    }

//    @Override
//    public boolean check(String participantId) {
//
//        final ReentrantLock lock = MapUtils.computeIfAbsent(lockMap, participantId, key -> new ReentrantLock(true));
//
//        try {
//            if (lock.tryLock(1, TimeUnit.SECONDS)) {
//                return cache.remove(participantId) != null;
//            }
//            return false;
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            return false;
//        } finally {
//            lock.unlock();
//            lockMap.remove(participantId);
//        }
//    }


    @Override
    public boolean check(String participantId) {
        // 不加锁觉得没有关系 同一用户不同线程操作同一个缓存 不管有没有检查到 总会有一个线程能把缓存删掉
        return cache.remove(participantId) != null;
    }

    @Override
    public void clear() {
        cache.clear();
    }
}
