package cn.mdmbct.seckill.core.filter.count;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Statistics the number of times each user participated in the entire activity
 * impl by {@link HashMap} and {@link ReentrantLock}
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/27 下午10:45
 * @modified mdmbct
 * @since 1.0
 */
public class LocalAllParticipationCount implements Counter{

    @Override
    public int increaseOne(String participantId) {
        return Counter.super.increaseOne(participantId);
    }

    @Override
    public void clear() {
        Counter.super.clear();
    }
}
