package cn.mdmbct.seckill.core.award;

import com.sun.istack.internal.NotNull;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/16 上午10:18
 * @modified mdmbct
 * @since 1.0
 */
public class AwardSeckill extends Seckill {

    private static final long serialVersionUID = 2409022301649566580L;
    @Getter
    private List<Award> awards;

    public AwardSeckill(@NotNull String id, long ttl, long startTime, Collection<Award> awards) {
        super(id, ttl, startTime);
        setAwards(awards);
    }

    public void setAwards(Collection<Award> awards) {
        if (!awards.stream().allMatch(a -> a.getProbability() > 0)) {
            throw new IllegalArgumentException("The probability of the awards must > 0");
        }

        this.awards = new ArrayList<>(awards);

        double sum = awards.stream().mapToDouble(Award::getProbability).sum();
        double p = 1 - sum;
        if (p > 0) {
            awards.add(new Award.NoAward(p));
        }

    }
}
