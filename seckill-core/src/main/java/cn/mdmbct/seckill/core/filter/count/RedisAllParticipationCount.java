package cn.mdmbct.seckill.core.filter.count;

/**
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/27 下午10:51
 * @modified mdmbct
 * @since 1.0
 */
public class RedisAllParticipationCount implements Counter {

    @Override
    public int increaseOne(String participantId) {
        return Counter.super.increaseOne(participantId);
    }

    @Override
    public void clear() {
        Counter.super.clear();
    }
}
