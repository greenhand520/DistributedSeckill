package cn.mdmbct.seckill.core.filter;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.context.FilterContext;

/**
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/21 17:26
 * @modified mdmbct
 * @since 0.1
 */
public abstract class Filter implements Comparable<Filter> {


    public static final int FIRST_FILTER_ORDER = 0;
    public static final int LAST_FILTER_ORDER = Integer.MAX_VALUE;

    protected ThreadLocal<FilterContext> contextThreadLocal;

    protected Filter nextFilter;

    protected final int order;

    public Filter(int order) {

        if (order < 0) {
            throw new IllegalArgumentException("The param 'order' must more than zero");
        }
        this.order = order;
    }

    protected void setContextThreadLocal(ThreadLocal<FilterContext> contextThreadLocal) {
        this.contextThreadLocal = contextThreadLocal;
    }

    /**
     * 将context存储到ThreadLocal中 每个过滤器都有同一个ThreadLocal对象来存储context
     *
     * @param context
     */
    protected void setFilterContext(FilterContext context) {
        // 线程只要在remove之前去get，都能拿到之前set的值
//        contextThreadLocal.remove();
        contextThreadLocal.set(context);
    }

    public FilterContext getFilterContext() {
        return contextThreadLocal.get();
    }

    /**
     * 获取调用顺序
     *
     * @return 调用顺序
     */
    public int getOrder() {
        return this.order;
    }

    /**
     * 设置下一个过滤器 getOrder()获取的到值越小 过滤器越靠前
     *
     * @param filter 下一个过滤器
     */
    protected void nextFilter(Filter filter) {
        this.nextFilter = filter;
    }

    /**
     * 设置下一个过滤器
     *
     * @param participant
     * @param productId
     */
    protected void doNextFilter(Participant participant, String productId) {
        getFilterContext().addFilterPassed(this);
        if (nextFilter != null) {
            nextFilter.doFilter(participant, productId);
        }
    }

    /**
     * 清理工作 如果需要做清理工作必须重写此方法
     */
    protected void clear() {
        contextThreadLocal.remove();
    }

    /**
     * 过滤逻辑
     *
     * @param participant
     * @param productId
     */
    public abstract void doFilter(Participant participant, String productId);

    public abstract String notPassMsg();

    @Override
    public int compareTo(Filter filter) {
        return this.getOrder() - filter.getOrder();
    }
}
