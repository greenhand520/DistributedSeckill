package cn.mdmbct.seckill.core.award;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 奖品
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/19 20:05
 * @modified mdmbct
 * @since 0.1
 */
@Getter
@EqualsAndHashCode
public class Award implements Serializable {

    private static final long serialVersionUID = -93160346279104104L;

    protected final String id;

    /**
     * 剩余数量
     */
    protected final AtomicInteger remainCount;

    private final int totalCount;

    @Setter
    protected double probability = 0;

    public Award(String id, int totalCount) {
        this.id = id;
        this.totalCount = totalCount;
        this.remainCount = new AtomicInteger(totalCount);
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

    public static class NoAward extends Award {
        
        public NoAward(double probability) {
            super("no_award", 0);
            this.probability = probability;
        }

    }
}
