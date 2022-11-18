package cn.mdmbct.seckill.core.context;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.award.Award;
import cn.mdmbct.seckill.core.award.GrabCompleteRedPacket;
import cn.mdmbct.seckill.core.filter.Filter;
import com.sun.istack.internal.NotNull;
import lombok.Getter;
import lombok.Setter;
import reactor.util.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@Getter
public class FilterContext<R> {

    private final Thread thread;

    private final List<Filter<R>> filtersPassed;

    @Setter
    private Filter<R> filterNotPassed;

    @Setter
    private String awardId;

    private final Participant participant;

    /**
     * the result of thread compete. <br>
     * this member is {@link Award} while execute {@link cn.mdmbct.seckill.core.award.AwardSeckill} <br>
     * and while execute {@link GrabCompleteRedPacket}, it's {@link Double}, mean denomination participant get <br>
     *
     * @see Award#Award(String, AtomicInteger)
     */
    @Setter
    private R competeRes;

    @Setter
    private String notPassMsg;


    public FilterContext(Thread thread, @NotNull Participant participant, @Nullable String awardId) {
        this.thread = thread;
        this.filtersPassed = new ArrayList<>();
        this.participant = participant;
        this.awardId = awardId;
    }

    public void addFilterPassed(Filter<R> filter) {
        filtersPassed.add(filter);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("--------------------\n")
                .append("ThreadId: ").append(thread.getId()).append("\n")
                .append("ThreadName: ").append(thread.getName()).append("\n")
                .append("FilterNotPassed: ");

        if (filterNotPassed != null) {
            sb.append(filterNotPassed.getClass().getName()).append("\n");
        } else {
            sb.append("null\n");
        }

        sb.append("FiltersPassed: ");
        if (filtersPassed.size() != 0) {
            for (Filter<R> filter : filtersPassed) {
                sb.append(filter.getClass().getName()).append("\t");
            }

        }
        sb.append("\n");

        sb.append("awardId: ").append(awardId).append("\n")
                .append("CompeteRes: ").append(competeRes).append("\n")
                .append("Participant: ").append(participant).append("\n");

        sb.append("NotPassMsg: ").append(notPassMsg).append("\n");

        sb.append("--------------------\n");

        return sb.toString();
    }


}
