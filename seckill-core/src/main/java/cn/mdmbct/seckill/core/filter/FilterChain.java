package cn.mdmbct.seckill.core.filter;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.context.FilterContext;

import java.util.Collections;
import java.util.List;

/**
 * 过滤器链
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/21 17:37
 * @modified mdmbct
 * @since 0.1
 */
public class FilterChain {

    private final List<Filter> filters;

    private final ThreadLocal<FilterContext> contextThreadLocal;

    public FilterChain(List<Filter> filters) {
        this.filters = filters;
        this.contextThreadLocal = new ThreadLocal<>();
        init();
    }

    /**
     * all the participant thread execute this method
     * @param participant participant who compete the award
     * @param awardId the id of award which the participant want to take. <br>
     *                if the param value is null, it will random take an award.
     *
     */
    public void doFilter(Participant participant, String awardId) {
        if (filters.size() != 0) {
            final Filter firstFilter = filters.get(0);
            firstFilter.setFilterContext(new FilterContext(Thread.currentThread(), participant, awardId));
            firstFilter.doFilter(participant, awardId);
        }
    }


    private void init() {
        Collections.sort(filters);
        for (int i = 0; i < filters.size(); i++) {

            Filter filter = filters.get(i);
            filter.setContextThreadLocal(contextThreadLocal);
            if (i == filters.size() - 1) {
                filter.nextFilter(null);
                return;
            }
            filter.nextFilter(filters.get(i + 1));
        }
    }

    /**
     * 清理工作
     */
    public void clear() {
        contextThreadLocal.remove();
        filters.forEach(Filter::clear);
    }

    public FilterContext getFilterContext() {
        return filters.get(0).getFilterContext();
    }
}
