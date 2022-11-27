package cn.mdmbct.seckill.core.activity;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.award.Award;
import cn.mdmbct.seckill.core.award.repository.AwardRepository;
import cn.mdmbct.seckill.core.filter.AwardQuantityFilter;
import cn.mdmbct.seckill.core.filter.Filter;
import cn.mdmbct.seckill.core.lock.AwardLock;

import java.util.List;

/**
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/20 0:04
 * @modified mdmbct
 * @since 0.1
 */
public class AwardActivity extends Activity<Award> {
    public AwardActivity(List<Filter<Award>> filters, AwardRepository awardRepository, AwardLock awardLock) {
        super(filters);
        filters.add(new AwardQuantityFilter(awardRepository, awardLock));
    }

    @Override
    public Award compete(Participant participant, String awardId) {
        filterChain.filter(participant, awardId);
        return filterChain.getFilterContext().getCompeteRes();
    }

    @Override
    public void clear() {
        super.clear();
        // todo: delete redis cache by key prefix
        // ref: https://cloud.tencent.com/developer/article/1447147
    }
}
