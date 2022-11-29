package cn.mdmbct.seckill.core.activity;

import cn.mdmbct.seckill.core.award.repository.AwardRepository;
import cn.mdmbct.seckill.core.filter.AwardQuantityFilter;
import cn.mdmbct.seckill.core.filter.Filter;
import cn.mdmbct.seckill.core.lock.AwardLock;

import java.util.Collection;
import java.util.List;

/**
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/20 0:04
 * @modified mdmbct
 * @since 0.1
 */
public class CompleteAwardActivity extends Activity {

    private final AwardRepository awardRepository;

    /**
     * @param filters         filters to filter the competed thread
     * @param conf            {@link ActivityConf#awardSeckill(String, long, long, Collection)}
     * @param awardRepository {@link AwardRepository}
     * @param awardLock       {@link  AwardLock}
     */
    public CompleteAwardActivity(List<Filter> filters, ActivityConf conf, AwardRepository awardRepository, AwardLock awardLock) {
        super(conf, filters);
        this.awardRepository = awardRepository;
        filters.add(new AwardQuantityFilter(awardRepository, awardLock));
    }

    /**
     * Update the award stock
     *
     * @param awardId  award id
     * @param newStock new stock quantity
     * @return
     */
    @Override
    public boolean updateStock(String awardId, int newStock) {
        return awardRepository.updateCount(awardId, newStock).isSuccess();
    }

    @Override
    public void clear() {
        super.clear();
        // todo: delete redis cache by key prefix
        // ref: https://cloud.tencent.com/developer/article/1447147
    }
}
