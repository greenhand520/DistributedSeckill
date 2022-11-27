package cn.mdmbct.seckill.core.filter.count.window;

import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

/**
 * For single node server, statistics all participants count impl by sliding time window
 * which impl by {@link TreeSet}  <br>
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/27 下午9:17
 * @modified mdmbct
 * @since 1.0
 */
public class LocalAllParticipantsSWC extends TreeSetSWC {

    public LocalAllParticipantsSWC(TimeUnit timeUnit) {
        super(timeUnit);
    }

    @Override
    public synchronized int increaseOne() {
        return increaseOneToCounter();
    }
}
