package cn.mdmbct.seckill.core.filter.count;

import org.checkerframework.checker.units.qual.C;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Statistics all the participants count impl by {@link AtomicInteger}
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/20 上午9:23
 * @modified mdmbct
 * @since 1.0
 */
public class LocalAllParticipantsCount implements Counter {

    private final AtomicInteger count;

    public LocalAllParticipantsCount() {
        this.count = new AtomicInteger();
    }

    @Override
    public int increaseOne() {
        return count.incrementAndGet();
    }

}
