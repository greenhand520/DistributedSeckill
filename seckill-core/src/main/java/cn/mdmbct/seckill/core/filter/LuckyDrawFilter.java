package cn.mdmbct.seckill.core.filter;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.award.Award;
import cn.mdmbct.seckill.core.award.AwardSeckill;
import cn.mdmbct.seckill.core.award.lottery.AliasLottery;
import cn.mdmbct.seckill.core.award.lottery.Lottery;
import cn.mdmbct.seckill.core.award.lottery.WeightRandomLottery;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Lucky draw filter, draw award for the thread that competed a lock. <br>
 * if the thread take a luck award, filter chain will do the next {@link AwardQuantityFilter} filter.
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/22 8:14
 * @modified mdmbct
 * @since 0.1
 */
public class LuckyDrawFilter extends Filter<Award> {

    private final Lottery lottery;

    private final List<Award> awards;

    public LuckyDrawFilter(Lottery lottery, AwardSeckill awardSeckill) {
        super(LAST_FILTER_ORDER - 1);
        this.lottery = lottery;
        lottery.setProbabilities(awardSeckill.getAwards().stream().map(Award::getProbability).collect(Collectors.toList()));
        this.awards = awardSeckill.getAwards();
    }

    public static LuckyDrawFilter aliasLottery(AwardSeckill seckill) {
        return new LuckyDrawFilter(new AliasLottery(), seckill);
    }

    public static LuckyDrawFilter weightRandomLottery(AwardSeckill seckill) {
        return new LuckyDrawFilter(new WeightRandomLottery(), seckill);
    }

    @Override
    public String notPassMsg() {
        return "未中奖！";
    }

    @Override
    public boolean doFilter(Participant participant, String awardId) {
        Award award = awards.get(lottery.next());
        if (award instanceof Award.NoAward) {
            getFilterContext().setCompeteRes(null);
            return false;
        } else {
            getFilterContext().setCompeteRes(award);
            return true;
        }
    }
}
