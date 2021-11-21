package cn.mdmbct.seckill.common.filter;

import cn.mdmbct.seckill.common.Participant;
import lombok.RequiredArgsConstructor;

/**
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/20 0:12
 * @modified mdmbct
 * @since 0.1
 */
@RequiredArgsConstructor
public abstract class BaseFilter implements Filter {

    protected Filter nextFilter;

    protected final int order;

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public void nextFilter(Filter filter) {
        this.nextFilter = filter;
    }

    @Override
    public void doNextFilter(Participant participant, FilterRes res, Filter curFilter) {
        if (nextFilter != null) {
            res.addFilterPassed(curFilter);
           nextFilter.doFilter(participant, res);
        }
    }
}
