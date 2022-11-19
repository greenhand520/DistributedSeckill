package cn.mdmbct.seckill.core.activity;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.award.CompleteRedPacket;
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
public class CompleteRedPacketActivity extends Activity<Double> {


    /**
     * has already add {@link CompleteRedPacketStateFilter}
     *
     * @param filters             filters to limit participant thread
     * @param totalMoney          total money
     * @param redPacketSplitCount split count
     * @param splitMethod         {@link  CompleteRedPacket.SplitMethod}
     */
    public CompleteRedPacketActivity(List<Filter<Double>> filters, double totalMoney, int redPacketSplitCount,
                                     CompleteRedPacket.SplitMethod splitMethod) {
        super(filters);
        final CompleteRedPacket redPacket = new CompleteRedPacket(totalMoney, redPacketSplitCount);
        final CompleteRedPacketStateFilter redPacketStateFilter = new CompleteRedPacketStateFilter(redPacket, splitMethod);
        filters.add(redPacketStateFilter);
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

    @Override
    public void clear() {
        filterChain.clear();
    }
}
