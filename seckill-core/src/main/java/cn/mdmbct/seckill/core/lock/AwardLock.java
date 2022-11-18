package cn.mdmbct.seckill.core.lock;

/**
 * prize lock interface
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/18 19:27
 * @modified mdmbct
 * @since 0.1
 */
public interface AwardLock {

    /**
     * try to lock
     *
     * @param id award (divided red packet id) id
     * @return whether the lock is successful
     */
    boolean tryLock(String id);

    /**
     * release lock
     *
     * @param id award (divided red packet id) id
     */
    void unLock(String id);

}
