package cn.mdmbct.seckill.core.award;

import com.sun.istack.internal.NotNull;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/16 上午10:18
 * @modified mdmbct
 * @since 1.0
 */
@Getter
public class AwardSeckill implements Serializable {

    private static final long serialVersionUID = 2409022301649566580L;

    /**
     * seckill activity id
     */
    protected final String id;

    /**
     * seckill duration, unit: second
     */
    protected final long ttl;

    /**
     * activity start time
     */
    protected final long startTime;

    private List<Award> awards;

    private final long expireTime;

    public AwardSeckill(@NotNull String id, long ttl, long startTime, Collection<Award> awards) {
        if (id == null) {
            throw new IllegalArgumentException("The param 'id' must not be mull");
        }

        if (ttl <= 0 || startTime <= 0) {
            throw new IllegalArgumentException("The parma 'ttl' and 'startTime' both must be > 0, the illegal parma are "
                    + ttl + " and " + startTime);
        }

        this.id = id;
        this.ttl = ttl;
        this.startTime = startTime;
        // Expires in 5 seconds after the activity ends
        this.expireTime = ttl * 1000 + 5000;
        setAwards(awards);
    }

    /**
     * if all the award probability is > 0 and < 1, and the sum of all award probability is <= 1, this method will use the
     * value of 1 subtract the sum of all probabilities as the {@link Award.NoAward} probability. <br>
     * otherwise, will re-cal all the award probability by the count of each award divided by the total count <br>
     * sort award from smallest to largest by its probability.
     *
     * @param awards awards
     */
    public void setAwards(Collection<Award> awards) {

        boolean isIllegal = false;

        if (awards.stream().allMatch(a -> a.probability > 0 && a.probability < 1)) {
            this.awards = new ArrayList<>(awards);

            double sum = awards.stream().mapToDouble(Award::getProbability).sum();
            double p = 1 - sum;
            if (p > 0) {
                awards.add(new Award.NoAward(p));
            } else if (p < 0){
                isIllegal = true;
            }
        } else {
            isIllegal = true;
        }

        if (isIllegal) {
            ArrayList<Award> temp = new ArrayList<>(awards);
            int total = awards.stream().mapToInt(Award::getTotalCount).sum();
            temp.forEach(r -> {
                r.setProbability((double) r.getTotalCount() / total);
            });
            this.awards = temp;
        }
        // sort from smallest to largest
        Collections.sort(this.awards);

    }

}
