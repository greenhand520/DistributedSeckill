package cn.mdmbct.seckill.core.filter;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.award.Award;
import cn.mdmbct.seckill.core.award.repository.AwardRepository;
import cn.mdmbct.seckill.core.lock.AwardLock;

/**
 * The award (or divided red packet) remain count filter is the last filter.<br>
 * if the stock quantity is > 0, it will reduce one and add the award (or divided red packet) to context,  otherwise not.
 * if the thread get the award lock and the award count is > 0, the award count will decrease one. <br>
 * and {@link AwardQuantityFilter#doFilter(Participant, String)} will return true, otherwise return false.
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/22 8:22
 * @modified mdmbct
 * @since 0.1
 */
public class AwardQuantityFilter extends Filter<Award> {

    private final AwardRepository awardRepository;

    private final AwardLock lock;

    public AwardQuantityFilter(AwardRepository awardRepository, AwardLock awardLock) {
        super(LAST_FILTER_ORDER);
        this.awardRepository = awardRepository;
        this.lock = awardLock;
    }

    @Override
    public String notPassMsg() {
        return "没有了！";
    }

    @Override
    public boolean doFilter(Participant participant, String awardId) {
        try {
            if (lock.tryLock(awardId)) {
                AwardRepository.UpdateRes updateRes = awardRepository.decrOne(awardId);
                if (updateRes.isSuccess()) {
                    getFilterContext().setCompeteRes(new Award(awardId, updateRes.getNewCount()));
                    return true;
                }
            }
        } finally {
            lock.unLock(awardId);
        }
        return false;
    }

}
