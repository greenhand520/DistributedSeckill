package cn.mdmbct.seckill.core.filter.count;

/**
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/20 上午9:18
 * @modified mdmbct
 * @since 1.0
 */
public interface Counter {

    /**
     * increase one to the participation count of participant.
     * @param participantId participant id
     * @return the number participant in window  time
     */
    default int increaseOne(String participantId) {
        throw new UnsupportedOperationException();
    }

    /**
     * all the number participant increase one.
     * @return the number participant in window  time
     */
    default int increaseOne() {
        throw new UnsupportedOperationException();
    }

    default void clear() {

    }
}
