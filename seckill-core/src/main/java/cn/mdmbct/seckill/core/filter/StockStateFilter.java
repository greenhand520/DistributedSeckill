package cn.mdmbct.seckill.core.filter;

import cn.mdmbct.seckill.core.Participant;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 库存过滤器线程竞争到锁后如果发现库存数（红包数）为0 需反向通知 <br>
 * 该过滤器使用ReentrantReadWriteLock保证在更新“是否有库存”的时候，其他线程不能读取。<br>
 * 注：ReentrantReadWriteLock允许多个读线程同时访问，但不允许写线程和读线程、写线程和写线程同时访问
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/22 8:15
 * @modified mdmbct
 * @since 0.1
 */
public class StockStateFilter extends Filter {

    private boolean haveStock = true;

    private final ReentrantReadWriteLock stateLock;

    public StockStateFilter(int order) {
        super(order);
        this.stateLock = new ReentrantReadWriteLock();
    }

    @Override
    public String notPassMsg() {
        return "没有库存了";
    }

    @Override
    public void doFilter(Participant participant, String productId) {
        try {
            stateLock.readLock().lock();
            if (!haveStock) {
                getFilterContext().setFilterNotPassed(this);
                return;
            }
            doNextFilter(participant, productId);
        } finally {
            stateLock.readLock().unlock();
        }
    }

    public void updateStockState(boolean haveStock) {
        stateLock.writeLock().lock();
        this.haveStock = haveStock;
        stateLock.writeLock().unlock();
    }
}
