package cn.mdmbct.seckill.core.filter.count;


import cn.mdmbct.seckill.core.filter.Filter;

/**
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/27 下午11:04
 * @modified mdmbct
 * @since 1.0
 */
public abstract class CountFilter extends Filter {

    protected final int countLimit;

    protected final Counter counter;

    protected CountFilter(int order, int countLimit, Counter counter) {
        super(order);
        this.countLimit = countLimit;
        this.counter = counter;
    }

    @Override
    public void clear() {
        counter.clear();
    }


}
