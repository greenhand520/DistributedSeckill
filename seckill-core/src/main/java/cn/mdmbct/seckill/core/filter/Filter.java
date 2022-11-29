package cn.mdmbct.seckill.core.filter;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.context.FilterContext;

/**
 * the abstract filter class to filter the lots of compete participant thread. <br>
 * every filter must set its order to tell the {@link FilterChain} what the order it is <br>
 * and filter chain will sort filters to determine filters call order. <br>
 * the smaller the 'order' value, the earlier it is called. <br>
 * default, the first filter's 'order' value is 0, the last filter's 'order' value is the max value of {@link Integer}
 *
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
     * story context to every thread's threadLocal object.
     *
     * @param context {@link FilterContext}
     */
    protected void setFilterContext(FilterContext context) {
        // thread can get the value before remove
//        contextThreadLocal.remove();
        contextThreadLocal.set(context);
    }

    public FilterContext getFilterContext() {
        return contextThreadLocal.get();
    }

    /**
     * get the call order
     *
     * @return call order
     */
    public int getOrder() {
        return this.order;
    }

    /**
     * set the next filter
     *
     * @param filter the next filter
     */
    protected void nextFilter(Filter filter) {
        this.nextFilter = filter;
    }

    /**
     * the filer calling chain.
     * if {@link Filter#doFilter(Participant, String)} return value is true, will execute the next filter and add self to "FilterPassed" in the context, <br>
     * otherwise add self to "FilterNotPassed"
     *
     * @param participant participant
     * @param awardId award id
     */
    public void filter(Participant participant, String awardId) {
        boolean doNextFilter = doFilter(participant, awardId);
        if (doNextFilter) {
            getFilterContext().addFilterPassed(this);
            if (nextFilter != null) {
                nextFilter.filter(participant, awardId);
            }
        } else {
            getFilterContext().setFilterNotPassed(this);
        }
    }

    /**
     * do next filter <br>
     * if "doNextFilter" is true, will execute the next filter and add self to "FilterPassed" in the context, <br>
     * otherwise add self to "FilterNotPassed"
     *
     * @param participant  participant
     * @param awardId      award id
     * @param doNextFilter whether to do the next filter.
     */
    protected void doNextFilter(Participant participant, String awardId, boolean doNextFilter) {
        if (doNextFilter) {
            getFilterContext().addFilterPassed(this);
            if (nextFilter != null) {
                nextFilter.doFilter(participant, awardId);
            }
        } else {
            final FilterContext filterContext = getFilterContext();
            filterContext.setFilterNotPassed(this);
            filterContext.setNotPassMsg(notPassMsg());
        }
    }

    /**
     * clear memory <br>
     * if you must clear memory for your filter, you must overwrite this method and super it.
     */
    protected void clear() {
//        contextThreadLocal.remove();
    }

    /**
     * The concrete filter logic impl
     *
     * @param participant participant
     * @param awardId award id
     * @return whether to do next filer
     */
    public abstract boolean doFilter(Participant participant, String awardId);

    public abstract String notPassMsg();

    @Override
    public int compareTo(Filter filter) {
        return this.getOrder() - filter.getOrder();
    }
}
