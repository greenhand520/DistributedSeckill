package cn.mdmbct.seckill.core.filter;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.award.red.GrabARedPacket;

/**
 * As the class name mean, is the last filter while you execute grab a red packet activity <br>
 * it will add the denomination participant competed to the context if red packet has remained money, <br>
 * otherwise, the denomination in context is "0.00"
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/17 上午10:49
 * @modified mdmbct
 * @since 1.0
 */
public class ARedPacketStateFilter<Double> extends Filter<Double> {

    private final GrabARedPacket grabARedPacket;

    private final GrabARedPacket.SplitMethod splitMethod;

    public ARedPacketStateFilter(GrabARedPacket grabARedPacket, GrabARedPacket.SplitMethod splitMethod) {
        super(LAST_FILTER_ORDER);
        this.grabARedPacket = grabARedPacket;
        this.splitMethod = splitMethod;
    }

    @Override
    public boolean doFilter(Participant participant, String awardId) {
        return true;
    }

    @Override
    public String notPassMsg() {
        return null;
    }
}
