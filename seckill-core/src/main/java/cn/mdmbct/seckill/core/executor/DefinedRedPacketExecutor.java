package cn.mdmbct.seckill.core.executor;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.award.red.DefinedRedPacket;
import cn.mdmbct.seckill.core.award.red.GrabDefinedRedPacket;
import cn.mdmbct.seckill.core.filter.Filter;
import cn.mdmbct.seckill.core.filter.FilterChain;

import java.util.List;

/**
 * As the class name
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/16 下午9:29
 * @modified mdmbct
 * @since 1.0
 */
public class DefinedRedPacketExecutor {

    private FilterChain filterChain;

    private GrabDefinedRedPacket grabDefinedRedPacket;


    public DefinedRedPacketExecutor(List<Filter> filters,
                                    String activityId,
                                    long ttl,
                                    long startTime,
                                    List<DefinedRedPacket> redPackets) {
        this.filterChain = new FilterChain(filters);
        this.grabDefinedRedPacket = new GrabDefinedRedPacket(activityId, ttl, startTime, redPackets);
    }

    /**
     *
     * @return if the participant take an award luckily, this method will return it, otherwise return null.
     */
    public DefinedRedPacket compete(Participant participant) {
        filterChain.filter(participant, null);
        // get the competed result form the context


        return null;
    }


}
