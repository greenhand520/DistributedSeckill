package cn.mdmbct.seckill.core.executor;

import cn.mdmbct.seckill.core.filter.Filter;
import cn.mdmbct.seckill.core.filter.FilterChain;

import java.util.List;

/**
 * Abstract activity executor class <br>
 * you should create an object of this class to manager a seckill or grab red packet activity <br>
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/16 下午9:29
 * @modified mdmbct
 * @since 1.0
 */
public abstract class Executor {

    private FilterChain filterChain;



    public Executor(List<Filter> filters) {
        this.filterChain = new FilterChain(filters);
    }
}
