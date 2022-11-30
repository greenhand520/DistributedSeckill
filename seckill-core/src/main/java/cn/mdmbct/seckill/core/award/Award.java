package cn.mdmbct.seckill.core.award;

import cn.mdmbct.seckill.core.context.FilterContext;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Award
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/19 20:05
 * @modified mdmbct
 * @since 0.1
 */
@Getter
@ToString
@EqualsAndHashCode
public class Award implements Serializable, Comparable<Award> {

    private static final long serialVersionUID = -93160346279104104L;

    private final String id;

    /**
     * 剩余数量
     */
    private final AtomicInteger remainCount;

    private int totalCount = 0;

    @Setter
    protected double probability = 0;

    public Award(String id, int totalCount) {
        this.id = id;
        this.totalCount = totalCount;
        this.remainCount = new AtomicInteger(totalCount);
    }

    /**
     * created for filter context
     * @param id award id
     * @param remainCount remain count
     * @see FilterContext#getCompeteRes()
     */
    public Award(String id, AtomicInteger remainCount) {
        this.id = id;
        this.remainCount = remainCount;
    }

    /**
     * 增加1并返回新值
     *
     * @return 新的数量
     */
    public int incrOne() {
        return remainCount.incrementAndGet();
    }

    /**
     * 减少1并返回新值
     *
     * @return 新的数量
     */
    public int decrOne() {
        return remainCount.decrementAndGet();
    }

    public void update(int newCount) {
        remainCount.getAndSet(newCount);
    }

    @Override
    public int compareTo(Award o) {
        // sort from smallest to largest
        return Double.compare(probability, o.probability);
    }

    public static class NoAward extends Award {
        
        public NoAward(double probability) {
            super("no_award", 0);
            this.probability = probability;
        }

    }
}
