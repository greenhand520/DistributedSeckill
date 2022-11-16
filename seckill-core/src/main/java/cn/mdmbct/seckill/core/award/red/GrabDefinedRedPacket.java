package cn.mdmbct.seckill.core.award.red;

import cn.mdmbct.seckill.core.award.Award;
import cn.mdmbct.seckill.core.award.Seckill;
import com.sun.istack.internal.NotNull;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


/**
 * Grab the pre-defined red packet, just run in single node server.
 */
public class GrabDefinedRedPacket extends Seckill {

    @Getter
    private final List<DefinedRedPacket> redPackets;


    public GrabDefinedRedPacket(@NotNull String id, long ttl, long startTime, List<DefinedRedPacket> redPackets) {
        super(id, ttl, startTime);
        this.redPackets = new ArrayList<>(redPackets);
        int total = redPackets.stream().mapToInt(Award::getTotalCount).sum();

        redPackets.forEach(r -> {
            r.setProbability((double) r.getTotalCount() / total);
        });

    }


}
