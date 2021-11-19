package cn.mdmbct.seckill.common.filter;

import cn.mdmbct.seckill.common.CompeteRes;
import cn.mdmbct.seckill.common.Participant;

import java.util.Collections;
import java.util.List;

/**
 * 过滤器链
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/19 23:50
 * @modified mdmbct
 * @since 0.1
 */
public class FilterChain {

    private final List<Filter> filters;

    public FilterChain(List<Filter> filters) {
        this.filters = filters;
        init();
    }

    public CompeteRes doFilter(Participant participant) {
        if (filters.size() != 0) {
            return filters.get(0).doFilter(participant);
        }
        return null;
    }


    private void init() {
        Collections.sort(filters);
        for (int i = 0; i < filters.size(); i++) {

            Filter filter = filters.get(i);
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
       filters.forEach(Filter::clear);
    }

}