package cn.mdmbct.seckill.core.filter;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.context.FilterContext;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * filter chain to filter the lots of participant
 * R: the result of participant compete
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/21 17:37
 * @modified mdmbct
 * @since 0.1
 */
public class FilterChain<R> {

    private final List<Filter<R>> filters;

    private final ThreadLocal<FilterContext<R>> contextThreadLocal;

    public FilterChain(List<Filter<R>> filters) {
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
    public void filter(Participant participant, String awardId) {
        if (filters.size() != 0) {
            final Filter<R> firstFilter = filters.get(0);
            firstFilter.setFilterContext(new FilterContext<>(Thread.currentThread(), participant, awardId));
            firstFilter.filter(participant, awardId);
        }
    }


    private void init() {
        checkOrder(filters);
        Collections.sort(filters);
        for (int i = 0; i < filters.size(); i++) {
            Filter<R> filter = filters.get(i);
            filter.setContextThreadLocal(contextThreadLocal);
            if (i == filters.size() - 1) {
                filter.nextFilter(null);
                return;
            }
            filter.nextFilter(filters.get(i + 1));
        }
    }

    private void checkOrder(List<Filter<R>> filters) {
        final List<Integer> orderList = filters.stream().map(Filter::getOrder).collect(Collectors.toList());
        final Set<Integer> orderSet = new HashSet<>(orderList);
        if (orderSet.size() < filters.size()) {
            //  cal the same order
            for (Integer i : orderSet) {
                orderList.remove(i);
            }
            throw new IllegalArgumentException("There are the same order filter in the filter chain, orders are " + orderList);
        }
    }

    /**
     * 清理工作
     */
    public void clear() {
        contextThreadLocal.remove();
        filters.forEach(Filter::clear);
    }

    public FilterContext<R> getFilterContext() {
        return filters.get(0).getFilterContext();
    }
}
