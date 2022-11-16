package cn.mdmbct.seckill.core.award;

import com.sun.istack.internal.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * The award level
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/16 上午9:32
 * @modified mdmbct
 * @since 1.0
 */
@Getter
public class Level implements Serializable {

    private static final long serialVersionUID = 7207602425549069538L;

    /**
     * 奖项/奖品等级id 必须有一个空的构造函数
     */
    protected final String id;

    /**
     * 等级名 一等奖 二等奖 ...
     */
    @Setter
    protected String name;

    /**
     * 奖品等级对应排序 值越小等级越高 <br>
     * 比如：<br>
     * 特等奖 ---> 0 <br>
     * 一等奖 ---> 1 <br>
     * 二等奖 ---> 2
     */
    @Setter
    private int level;

    /**
     * 概率 可另修改
     */
    private double probability;

    /**
     * 奖品等级下奖品 可单独设置
     */
    private Set<Award> awards;

    public Level(String id, int level, double probability, @NotNull Collection<Award> awards) {

        checkAwards(id, level, awards);

        this.id = id;
        this.level = level;
        this.probability = probability;
        this.awards = new HashSet<>(awards);
        awards.forEach(a -> a.probability = probability);
//        awards.forEach(award -> award.setLevel(this.level));
    }

    /**
     * Just has one award
     *
     * @param id
     * @param level
     * @param probability
     * @param award
     */
    public Level(String id, int level, double probability, @NotNull Award award) {

        checkAward(id, level, award);

        this.id = id;
        this.level = level;
        this.probability = probability;
        this.awards = new HashSet<>(1);
        awards.add(award);
        award.probability = probability;
    }

    private static void checkAward(String id, int level, Award award) {
        // 除开未中奖奖项 其余下面都需要有奖品
        if (award == null) {
            throw new IllegalArgumentException("The award must not be null, Level ID: " + id + "Level: " + level);
        }

        if (level < 0) {
            throw new IllegalArgumentException("The param of 'level' must > 0");
        }
    }

    private static void checkAwards(String id, int level, Collection<Award> awards) {
        // 除开未中奖奖项 其余下面都需要有奖品
        if (awards == null || awards.size() == 0) {
            throw new IllegalArgumentException("The awards must not be null or empty, Level ID: " + id + "Level: " + level);
        }

        if (level < 0) {
            throw new IllegalArgumentException("The param of 'level' must > 0");
        }
    }

    public void setAwards(Collection<Award> awards) {
        checkAwards(id, level, awards);
        this.awards = new HashSet<>(awards);
        awards.forEach(a -> a.probability = probability);
    }

    public void addAwards(Collection<Award> awards) {
        checkAwards(id, level, awards);
        this.awards.addAll(awards);
        awards.forEach(a -> a.probability = probability);
    }

    public void setAward(Award award) {
        checkAward(id, level, award);
        awards.clear();
        awards.add(award);
        award.probability = probability;
    }

    public void removeAwards(Collection<Award> awardsRemoved) {
        awards.removeAll(awardsRemoved);
    }

    public void setProbability(double probability) {
        if (probability < 0 || probability > 1) {
            throw new IllegalArgumentException("The probability must between 0 and 1");
        }
        this.probability = probability;
        awards.forEach(a -> a.probability = probability);
    }

    public static class LevelUnlucky extends Level {

        /**
         * 未中奖奖项信息
         *
         * @param id          id
         * @param probability 未中奖概率
         */
        public LevelUnlucky(String id, double probability) {
            super(id, Integer.MAX_VALUE, probability, new ArrayList<>());
            this.name = "Level_Unlucky";
        }

    }
}
