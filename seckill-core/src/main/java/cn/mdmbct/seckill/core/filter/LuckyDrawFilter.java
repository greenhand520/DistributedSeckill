package cn.mdmbct.seckill.core.filter;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.award.Drawer;

/**
 * Lucky draw filter, draw award for the thread that competed a lock. <br>
 * if the thread take a luck award, filter chain will do the next {@link StockStateFilter} filter.
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/22 8:14
 * @modified mdmbct
 * @since 0.1
 */
public class LuckyDrawFilter<R> extends Filter<R> {

    private final Drawer drawer;
    private final int probabilitiesSize;


    public LuckyDrawFilter(int order, Drawer drawer, int probabilitiesSize) {
        super(order);
        this.drawer = drawer;
        this.probabilitiesSize = probabilitiesSize;
    }

    @Override
    public String notPassMsg() {
        return "未中奖！";
    }

    @Override
    public boolean doFilter(Participant participant, String awardId) {

        return true;
    }
}
