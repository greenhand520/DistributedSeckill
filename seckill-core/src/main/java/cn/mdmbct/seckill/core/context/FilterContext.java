package cn.mdmbct.seckill.core.context;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.award.Award;
import cn.mdmbct.seckill.core.filter.Filter;
import com.sun.istack.internal.NotNull;
import lombok.Getter;
import lombok.Setter;
import reactor.util.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;


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
     * this member is {@link Award} while execute {@link cn.mdmbct.seckill.core.award.Seckill} or execute {@link cn.mdmbct.seckill.core.award.red.GrabDefinedRedPacket} <br>
     * and while execute {@link cn.mdmbct.seckill.core.award.red.GrabARedPacket}, it's {@link Double}, mean denomination participant get
     */
    @Setter
    private R competeRes;


    public FilterContext(Thread thread, @NotNull Participant participant, @Nullable String awardId) {
        this.thread = thread;
        this.filtersPassed = new ArrayList<>();
        this.participant = participant;
        this.awardId = awardId;
    }

    public void addFilterPassed(Filter<R> filter) {
        filtersPassed.add(filter);
    }

    public String getVisualInfo() {

        StringBuilder sb = new StringBuilder();
        sb.append("\n")
                .append("ThreadId: ").append(thread.getId()).append("\n")
                .append("ThreadName: ").append(thread.getName()).append("\n");

        sb.append("FiltersPassed: ");
        if (filtersPassed.size() != 0) {
            for (Filter filter : filtersPassed) {
                sb.append(filter.getClass()).append(" ");
            }

        }
        sb.append("\n");


        return sb.toString();
    }


}
