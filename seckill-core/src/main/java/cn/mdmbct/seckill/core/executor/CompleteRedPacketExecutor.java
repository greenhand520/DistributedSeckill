package cn.mdmbct.seckill.core.executor;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.award.GrabCompleteRedPacket;
import cn.mdmbct.seckill.core.filter.CompleteRedPacketStateFilter;
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
public class CompleteRedPacketExecutor<R> extends Executor<Double> {


    /**
     * has already add {@link CompleteRedPacketStateFilter}
     *
     * @param filters             filters to limit participant thread
     * @param totalMoney          total money
     * @param redPacketSplitCount split count
     * @param splitMethod         {@link  GrabCompleteRedPacket.SplitMethod}
     */
    public CompleteRedPacketExecutor(List<Filter<Double>> filters, double totalMoney, int redPacketSplitCount,
                                     GrabCompleteRedPacket.SplitMethod splitMethod) {
        super(filters);
        final GrabCompleteRedPacket grabARedPacket = new GrabCompleteRedPacket(totalMoney, redPacketSplitCount);
        final CompleteRedPacketStateFilter aRedPacketStateFilter = new CompleteRedPacketStateFilter(grabARedPacket, splitMethod);
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
