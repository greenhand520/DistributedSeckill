package cn.mdmbct.seckill.core.activity;

import cn.mdmbct.seckill.core.award.CompleteRedEnvelope;
import cn.mdmbct.seckill.core.filter.CompleteRedEnvelopeStateFilter;
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
public class CompleteRedEnvelopeActivity extends Activity {


    /**
     * has already add {@link CompleteRedEnvelopeStateFilter}
     *
     * @param filters             filters to limit participant thread
     * @param activityId          activity id
     * @param duration            the duration of activity. unit: ms
     * @param startTime           the start time of activity. unit: ms
     * @param totalMoney          total money
     * @param redPacketSplitCount split count
     * @param splitMethod         {@link  CompleteRedEnvelope.SplitMethod}
     */
    public CompleteRedEnvelopeActivity(List<Filter> filters,
                                       String activityId, long duration, long startTime,
                                       double totalMoney, int redPacketSplitCount,
                                       CompleteRedEnvelope.SplitMethod splitMethod) {
        super(ActivityConf.completeRedPacketSeckill(activityId, duration, startTime), filters);
        CompleteRedEnvelope redPacket = new CompleteRedEnvelope(totalMoney, redPacketSplitCount);
        CompleteRedEnvelopeStateFilter redPacketStateFilter = new CompleteRedEnvelopeStateFilter(redPacket, splitMethod);
        filters.add(redPacketStateFilter);
    }

}
