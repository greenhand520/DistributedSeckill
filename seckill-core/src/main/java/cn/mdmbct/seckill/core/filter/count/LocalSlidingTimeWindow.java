package cn.mdmbct.seckill.core.filter.count;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.concurrent.TimeUnit;


/**
 * Sliding time window for single node server
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/8 下午5:21
 * @modified mdmbct
 * @since 1.0
 */
public class LocalSlidingTimeWindow extends SlidingTimeWindowCount {

    /**
     * 记录窗口滑动的Node
     */
    private Node lastNode;

    public LocalSlidingTimeWindow(int slot, TimeUnit timeUnit, int limit) {
        super(slot, timeUnit, limit);

        // init
        Node curNode = null;
        final long curMs = System.currentTimeMillis();
        for (int i = 0; i < slot; i++) {
            if (lastNode == null) {
                lastNode = new Node(curMs, 0, i);
                curNode = lastNode;
            } else {
                lastNode.next = new Node(curMs, 0, i);
                lastNode = lastNode.getNext();
            }
        }
        lastNode.next = curNode;
    }


    /**
     * 获取当前窗口总数
     *
     * @return
     */
    private long getSum() {
        long sum = 0L;
        Node temp = lastNode;
        for (int i = 0; i < slot; i++) {
            sum += temp.count;
            temp = temp.next;
        }
        return sum;
    }

    /**
     * 重设位于index的Node数据 其中count = 0
     *
     * @param index 位置
     * @param curMs 当前毫秒时间
     */
    private void reset(int index, long curMs) {
        if (index < 0) {
            return;
        }

        Node temp = lastNode;
        for (int i = 0; i < index; i++) {
            temp = temp.next;
        }
        temp.time = curMs;
        temp.count = 0;
        lastNode = temp;
    }

    private void reset() {
        final long curMs = System.currentTimeMillis();
        int index = (int) ((curMs - lastNode.time) / slotTime);
        if (index > slot) {
            index = slot;
        }
        reset(index, curMs);
    }

    /**
     * 检查是否超限 如果未超限 次数加1
     *
     * @return 是否超限
     */
    public synchronized boolean checkAndAdd() {
        reset();
        final long sum = getSum();
        if (sum > limit) {
            return false;
        }
        lastNode.addCount();
        return true;
    }

    @Override
    public int increaseOne(String participantId) {
        return 0;
    }

    @Override
    public void clear() {

    }

    /**
     * 用滑动窗口来限流时，设置的单位时间越小，分割的时间越多，统计就会越准确。
     *
     * @author mdmbct  mdmbct@outlook.com
     * @date 2022/11/8 下午5:29
     * @modified mdmbct
     * @since 1.0
     */
    @Getter
    @ToString
    public static class Node {

        @Setter
        private long time;

        @Setter
        private int count;

        @Setter
        private Node next;

        private final int id;

        public Node(long time, int count, int id) {
            this.time = time;
            this.count = count;
            this.id = id;
        }

        private void addCount() {
            this.count += 1;
        }
    }
}
