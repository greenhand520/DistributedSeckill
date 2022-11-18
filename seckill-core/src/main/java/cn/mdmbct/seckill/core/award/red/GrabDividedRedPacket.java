package cn.mdmbct.seckill.core.award.red;

import cn.mdmbct.seckill.core.award.Award;
import cn.mdmbct.seckill.core.award.AwardSeckill;
import com.sun.istack.internal.NotNull;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Grab the divided red packet(already pre-defined every child red packet denomination), just run in single node server.
 */
public class GrabDividedRedPacket extends AwardSeckill {

    @Getter
    private final List<DividedRedPacket> redPackets;


    public GrabDividedRedPacket(@NotNull String id, long ttl, long startTime, Collection<DividedRedPacket> redPackets) {
        super(id, ttl, startTime, new ArrayList<>(redPackets));
        this.redPackets = new ArrayList<>(redPackets);
        int total = redPackets.stream().mapToInt(Award::getTotalCount).sum();

        redPackets.forEach(r -> {
            r.setProbability((double) r.getTotalCount() / total);
        });

    }


}
