package cn.mdmbct.seckill.core.filter;

import cn.mdmbct.seckill.core.Participant;

/**
 * The award (or pre-defined red packet) remain count filter is the last filter.<br>
 * if the stock quantity is > 0, it will reduce one and add the award (or pre-defined red packet) to context,  otherwise not.
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/17 上午10:41
 * @modified mdmbct
 * @since 1.0
 */
public class AwardStockQuantityFilter<R> extends Filter<R>{

    public AwardStockQuantityFilter() {
        super(LAST_FILTER_ORDER);
    }

    @Override
    public boolean doFilter(Participant participant, String awardId) {
        return true;
    }

    @Override
    public String notPassMsg() {
        return "没有了！";
    }
}
