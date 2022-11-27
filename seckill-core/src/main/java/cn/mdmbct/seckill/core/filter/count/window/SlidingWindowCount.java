package cn.mdmbct.seckill.core.filter.count.window;

import cn.mdmbct.seckill.core.filter.count.Counter;

import java.util.concurrent.TimeUnit;

/**
 * Sliding time window. Limit the number of participation per user per unit of time
 * ref： https://cloud.tencent.com/developer/news/626730
 * https://juejin.cn/post/7015853473749024776
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/8 下午5:17
 * @modified mdmbct
 * @since 1.0
 */
public abstract class SlidingWindowCount implements Counter {

    /**
     * the count of blocks are divided per unit time, <br>
     * example: 1ms -> 1 times; 1S -> 10 times; 1min -> 1000 times.
     */
//    protected final int slot;

    /**
     * the length of time for each slot(= timeUnit / slot), unit: ms
     */
//    protected final long slotTime;

    /**
     * limit times per unit time
     */
    protected final int limit;

    /**
     * window time, unit: ms
     */
    protected final long windowTime;

    public SlidingWindowCount(/*int slot, */TimeUnit timeUnit, int limit) {
        if (/*slot <= 0 || */limit <= 0) {
            throw new IllegalArgumentException("Both of the param 'limit' must be > 0.");
        }
//        this.slot = slot;
        this.limit = limit;
        this.windowTime = TimeUnit.MILLISECONDS.convert(1, timeUnit);
//        this.slotTime = windowTime / slot;
    }
}
