package cn.mdmbct.seckill.core.filter.count;

/**
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/20 上午9:18
 * @modified mdmbct
 * @since 1.0
 */
public interface ParticipationCount {

    /**
     * increase one to the participation count of participant.
     * @param participantId participant id
     * @return cur participation count
     */
    int increaseOne(String participantId);

    default void clear() {

    }
}
