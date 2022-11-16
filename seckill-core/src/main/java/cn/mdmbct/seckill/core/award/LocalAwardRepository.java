package cn.mdmbct.seckill.core.award;

import java.util.HashMap;
import java.util.Map;

/**
 * 使用map存储产品 适用于单体下使用
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/11 上午11:51
 * @modified mdmbct
 * @since 1.0
 */
public class LocalAwardRepository implements AwardRepository {

    private final Map<String, Award> cache;
    // 加不加读写锁效果没什么区别 按理来说是不需要读写锁的 因为某线程更新商品信息前必须拥有该商品的锁
//    private final ReentrantReadWriteLock readWriteLock;

    public LocalAwardRepository(AwardSeckill seckill) {
        this.cache = new HashMap<>(seckill.getAwards().size());
        seckill.getAwards().forEach(award -> cache.put(award.id, award));
    }

    @Override
    public UpdateRes incrOne(String id) {
        try {
            return new UpdateRes(true, cache.get(id).incrOne());
        } catch (Exception e) {
            e.printStackTrace();
            // 应该只有一种可能 id不在map中
            return new UpdateRes(false, 0);
        }
    }

    @Override
    public UpdateRes decrOne(String id) {
        try {
            return new UpdateRes(true, cache.get(id).decrOne());
        } catch (Exception e) {
            e.printStackTrace();
            return new UpdateRes(false, 0);
        }
    }

    @Override
    public UpdateRes updateCount(String id, int newCount) {
        try {
            cache.get(id).update(newCount);
            return new UpdateRes(true, newCount);
        } catch (Exception e) {
            e.printStackTrace();
            return new UpdateRes(false, 0);
        }
    }
}
