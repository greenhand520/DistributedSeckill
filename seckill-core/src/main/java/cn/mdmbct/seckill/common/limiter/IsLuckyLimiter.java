package cn.mdmbct.seckill.common.limiter;

import cn.mdmbct.seckill.common.Participant;

/**
 * 是否中奖 将是否中奖前置到竞争锁之前 提高效率
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/21 9:02
 * @modified mdmbct
 * @since 0.1
 */
public class IsLuckyLimiter extends BaseLimiter {
    public IsLuckyLimiter(int order) {
        super(order);
    }

    @Override
    public void doLimit(Participant participant, LimitContext context) {
        doNextLimit(participant, context);
    }

    @Override
    public String notPassMsg() {
        return "未中奖！";
    }
}
