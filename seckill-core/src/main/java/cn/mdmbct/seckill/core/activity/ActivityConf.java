package cn.mdmbct.seckill.core.activity;

import cn.mdmbct.seckill.core.award.Award;
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
public class ActivityConf implements Serializable {

    private static final long serialVersionUID = 2409022301649566580L;

    /**
     * seckill activity id
     */
    private final String id;

    /**
     * activity start time
     */
    private final long startTime;

    private List<Award> awards;

    private final long expireTime;

    private ActivityConf(@NotNull String id, long duration, long startTime, Collection<Award> awards) {
        this(id, duration, startTime);
        setAwards(awards);
    }

    private ActivityConf(@NotNull String id, long duration, long startTime) {
        if (id == null) {
            throw new IllegalArgumentException("The param 'id' must not be mull");
        }

        if (duration <= 0 || startTime <= 0) {
            throw new IllegalArgumentException("The parma 'ttl' and 'startTime' both must be > 0, the illegal parma are " + duration + " and " + startTime);
        }

        this.id = id;
        this.startTime = startTime;
        this.expireTime = duration * 1000;
    }

    public long getCacheExpiredTime() {
        // Expires in 5 seconds after the activity ends
        return expireTime + 5000;
    }

    public long getEndTime() {
        return startTime + expireTime;
    }

    public static ActivityConf awardSeckill(@NotNull String id, long duration, long startTime, Collection<Award> awards) {
        return new ActivityConf(id, duration, startTime, awards);
    }

    public static ActivityConf completeRedPacketSeckill(@NotNull String id, long duration, long startTime) {
        return new ActivityConf(id, duration, startTime);
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

        if (awards.stream().allMatch(a -> a.getProbability() > 0 && a.getProbability() < 1)) {
            this.awards = new ArrayList<>(awards);

            double sum = awards.stream().mapToDouble(Award::getProbability).sum();
            double p = 1 - sum;
            if (p > 0) {
                awards.add(new Award.NoAward(p));
            } else if (p < 0) {
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
