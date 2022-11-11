package cn.mdmbct.seckill.core.lock;

/**
 * 商品、奖品锁接口
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/18 19:27
 * @modified mdmbct
 * @since 0.1
 */
public interface AwardLock {

    /**
     * 尝试加锁
     *
     * @param id 商品、奖品的id
     * @return 是否加锁成功
     */
    boolean tryLock(String id);

    /**
     * 释放锁
     *
     * @param id 商品、奖品的id
     */
    void unLock(String id);

}