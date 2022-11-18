package cn.mdmbct.seckill.core.filter;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.lock.AwardLock;

/**
 * 竞争锁过滤，倒数第二个过滤器 <br>
 * 只能通过竞争到锁的线程
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/22 8:21
 * @modified mdmbct
 * @since 0.1
 */
public class CompeteLockFilter<R> extends Filter<R> {

    private final AwardLock lock;

    public CompeteLockFilter(AwardLock lock) {
        super(LAST_FILTER_ORDER - 1);
        this.lock = lock;
    }

    @Override
    public String notPassMsg() {
        return "下次加油！";
    }

    @Override
    public boolean doFilter(Participant participant, String awardId) {
        return lock.tryLock(awardId);
    }
}
