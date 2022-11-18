package cn.mdmbct.seckill.core.executor;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.award.red.GrabARedPacket;
import cn.mdmbct.seckill.core.filter.ARedPacketStateFilter;
import cn.mdmbct.seckill.core.filter.Filter;

import java.util.List;

/**
 * As the class name
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/17 上午9:27
 * @modified mdmbct
 * @since 1.0
 */
public class ARedPacketExecutor<Double> extends Executor<Double> {


    /**
     * has already add {@link ARedPacketStateFilter}
     *
     * @param filters             filters to limit participant thread
     * @param totalMoney          total money
     * @param redPacketSplitCount split count
     * @param splitMethod         {@link  GrabARedPacket.SplitMethod}
     */
    public ARedPacketExecutor(List<Filter<Double>> filters, double totalMoney, int redPacketSplitCount,
                              GrabARedPacket.SplitMethod splitMethod) {
        super(filters);
        final GrabARedPacket grabARedPacket = new GrabARedPacket(totalMoney, redPacketSplitCount);
        final ARedPacketStateFilter<Double> aRedPacketStateFilter = new ARedPacketStateFilter<>(grabARedPacket, splitMethod);
        filters.add(aRedPacketStateFilter);
    }

    /**
     * participant thread compete red packet
     *
     * @param participant participant
     * @param awardId     set null
     * @return the denomination of red packet participant take
     */
    @Override
    public Double compete(Participant participant, String awardId) {
        filterChain.filter(participant, awardId);
        return filterChain.getFilterContext().getCompeteRes();
        // todo: may todo something else.
    }


}
