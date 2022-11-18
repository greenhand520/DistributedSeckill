package cn.mdmbct.seckill.core.executor;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.award.Award;
import cn.mdmbct.seckill.core.filter.Filter;

import java.util.List;

/**
 * As the class name
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/16 下午9:29
 * @modified mdmbct
 * @since 1.0
 */
public class DividedRedPacketExecutor<R> extends Executor<Award>{



    public DividedRedPacketExecutor(List<Filter<Award>> filters) {
        super(filters);
    }

    @Override
    public Award compete(Participant participant, String awardId) {
        return null;
    }

}
