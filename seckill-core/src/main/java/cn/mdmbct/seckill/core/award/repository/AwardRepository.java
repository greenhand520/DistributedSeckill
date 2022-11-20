package cn.mdmbct.seckill.core.award.repository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * 商品、奖品管理的库 负责实际存储它们及数量的控制
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/18 19:49
 * @modified mdmbct
 * @since 0.1
 */
public interface AwardRepository {

    /**
     * 增加奖品数量 增加1
     *
     * @param id 奖品id
     * @return 是否增加成功
     */
    UpdateRes incrOne(String id);

    /**
     * 减少奖品数量 减少1
     *
     * @param id 奖品id
     * @return 是否减少成功
     */
    UpdateRes decrOne(String id);

    /**
     * 修改奖品数量
     *
     * @param id       奖品id
     * @param newCount 修改数量
     * @return 是否修改成功
     */
    UpdateRes updateCount(String id, int newCount);

    void clear();

    @Getter
    @ToString
    @RequiredArgsConstructor
    class UpdateRes {

        private final boolean success;

        private final int newCount;

    }

}
