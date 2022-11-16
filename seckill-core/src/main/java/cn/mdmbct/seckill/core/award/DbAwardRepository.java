package cn.mdmbct.seckill.core.award;

import cn.mdmbct.seckill.core.lock.AwardLock;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 对商品数量修改在数据库中进行
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/18 21:35
 * @modified mdmbct
 * @since 0.1
 */
@RequiredArgsConstructor
public class DbAwardRepository implements AwardRepository {

    private final AwardLock lock;

    /**
     * 数量加1 并返回新的数量
     * 输入: 产品id
     * 输出: 产品新的数量
     */
    private final Function<String, Integer> dbDecrOneOp;

    /**
     * 数量减1 并返回新的数量
     * 输入: 产品id
     * 输出: 产品新的数量
     */
    private final Function<String, Integer> dbIncrOneOp;

    private final Consumer<CountUpdateParams> dbUpdateOp;

    @Override
    public UpdateRes incrOne(String id) {
        try {
            return new UpdateRes(true, dbIncrOneOp.apply(id));
        } catch (Exception e) {
            e.printStackTrace();
            return new UpdateRes(false, 0);
        }
    }

    @Override
    public UpdateRes decrOne(String id) {
        try {
            return new UpdateRes(true, dbDecrOneOp.apply(id));
        } catch (Exception e) {
            e.printStackTrace();
            return new UpdateRes(false, 0);
        }
    }

    @Override
    public UpdateRes updateCount(String id, int newCount) {
        try {
            dbUpdateOp.accept(new CountUpdateParams(id, newCount));
            return new UpdateRes(true, newCount);
        } catch (Exception e) {
            e.printStackTrace();
            return new UpdateRes(false, 0);
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class CountUpdateParams {

        /**
         * 产品id
         */
        private final String id;

        /**
         * 新的数量
         */
        private final int newCount;
    }
}
