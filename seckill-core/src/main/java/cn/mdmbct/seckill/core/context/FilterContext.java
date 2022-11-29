package cn.mdmbct.seckill.core.context;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.award.Award;
import cn.mdmbct.seckill.core.award.CompleteRedEnvelope;
import cn.mdmbct.seckill.core.activity.ActivityConf;
import cn.mdmbct.seckill.core.filter.Filter;
import com.sun.istack.internal.NotNull;
import lombok.Getter;
import lombok.Setter;
import reactor.util.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@Getter
public class FilterContext {
    private final Thread thread;
    private final List<Filter> filtersPassed;
    @Setter
    private Filter filterNotPassed;
    @Setter
    private String awardId;
    private final Participant participant;

//    private final AwardSeckill seckill;

    /**
     * the result of thread compete. <br>
     * this member is {@link Award} while execute {@link ActivityConf} <br>
     * and while execute {@link CompleteRedEnvelope}, it's {@link Double}, mean denomination participant get <br>
     *
     * @see Award#Award(String, AtomicInteger)
     */
    @Setter
    private Object competeRes;
    @Setter
    private String notPassMsg;
    public FilterContext(Thread thread, @NotNull Participant participant, @Nullable String awardId) {
        this.thread = thread;
        this.filtersPassed = new ArrayList<>();
        this.participant = participant;
        this.awardId = awardId;
    }
    public void addFilterPassed(Filter filter) {
        filtersPassed.add(filter);
    }
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("-----------FilterContext-----------\n")
                .append("ThreadId: ").append(thread.getId()).append("\n")
                .append("ThreadName: ").append(thread.getName()).append("\n")
                .append("FilterNotPassed: ");

        if (filterNotPassed != null) {
            sb.append(filterNotPassed.getClass().getName()).append("\n");
        } else {
            sb.append("null\n");
        }

        sb.append("FiltersPassed:\n");
        if (filtersPassed.size() != 0) {
            for (Filter filter : filtersPassed) {
                sb.append(filter.getClass().getName()).append("\n");
            }

        }
        sb.append("\n");

        sb.append("awardId: ").append(awardId).append("\n")
                .append("CompeteRes: ").append(competeRes).append("\n")
                .append("Participant: ").append(participant).append("\n");

        sb.append("NotPassMsg: ").append(notPassMsg).append("\n");

        sb.append("--------------------------------\n");

        return sb.toString();
    }


}
