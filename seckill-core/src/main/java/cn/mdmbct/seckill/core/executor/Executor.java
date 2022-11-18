package cn.mdmbct.seckill.core.executor;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.filter.Filter;
import cn.mdmbct.seckill.core.filter.FilterChain;

import java.util.List;

/**
 * Abstract activity executor class <br>
 * you should create an object of this class to execute a seckill or grab red packet activity <br>
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/16 下午9:29
 * @modified mdmbct
 * @since 1.0
 */
public abstract class Executor<R> {

    protected final FilterChain<R> filterChain;

    public Executor(List<Filter<R>> filters) {
        this.filterChain = new FilterChain<>(filters);
    }

    public abstract R compete(Participant participant, String awardId);


}