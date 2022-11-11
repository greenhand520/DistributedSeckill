package cn.mdmbct.seckill.core.context;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.filter.Filter;
import cn.mdmbct.seckill.core.lock.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * 过滤器数据上下文 贯穿线程经过的过滤器链
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/21 17:29
 * @modified mdmbct
 * @since 0.1
 */
@ToString
public class FilterContext {

    /**
     * 竞争到的锁 这个锁对象是唯一的 释放锁之后会置空
     */
    @Getter
    private AwardLock competedLock;

    @Getter
    private final Thread thread;

    @Getter
    private final List<Filter> filtersPassed;

    @Setter
    @Getter
    private Filter filterNotPassed;

    @Getter
    private final String productId;

    @Getter
    private final Participant participant;

    public FilterContext(Thread thread, Participant participant, String productId) {
        this.thread = thread;
        this.filtersPassed = new ArrayList<>();
        this.participant = participant;
        this.productId = productId;
    }

    public void addFilterPassed(Filter filter) {
        filtersPassed.add(filter);
    }

    public String getVisualInfo() {

        StringBuilder sb = new StringBuilder();
        sb.append("\n")
                .append("ThreadId: ").append(thread.getId()).append("\n")
                .append("ThreadName: ").append(thread.getName()).append("\n")
                .append("FilterNotPassed: ");

        if (filterNotPassed != null) {
            sb.append(filterNotPassed.getClass()).append("\n");
        } else {
            sb.append("\n");
        }

        sb.append("FiltersPassed: ");
        if (filtersPassed.size() != 0) {
            for (Filter filter : filtersPassed) {
                sb.append(filter.getClass()).append(" ");
            }

        }
        sb.append("\n");


        return sb.toString();
    }

    /**
     * 将竞争到的锁保存到Context中，只在竞争到锁才调用
     * @param lock
     */
    public void setCompetedLock(AwardLock lock) {
        this.competedLock = lock;
    }

    public void unlock() {
        competedLock.unLock(productId);
        this.competedLock = null;
    }
}
