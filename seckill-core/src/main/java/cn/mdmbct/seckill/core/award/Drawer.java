package cn.mdmbct.seckill.core.award;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Take the award with a probability from award repository
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/16 上午8:34
 * @modified mdmbct
 * @since 1.0
 */
public abstract class Drawer {

    /**
     * 设置奖品/奖项分布概率
     *
     * @param levels 奖项分布概率信息
     */
    public abstract void setProbabilities(Collection<Double> levels);

    /**
     * 返回{@link Drawer#setProbabilities(Collection)}或者{@link Drawer#setProbabilities(Collection)}参数中概率的下标 <br>
     * 如参数：0.0 0.1 0.2 0.3 0.4 0.5 <br>
     * 若返回0，则表示第一个概率0.0对应的奖品/奖项<br>
     * 如返回5，则表示第5个元素0.5对应的奖品/奖项
     *
     * @return 奖品/奖项下标
     */
    public abstract int next();

    /**
     * 优化奖项 如果{@code probability}中所有的概率之和不为0 则自动补全剩下添加到集合中 并返回
     *
     * @param probabilities 奖项概率
     * @throws IllegalArgumentException 当probabilities所有的中奖率超过1时抛出
     */
    protected static List<Double> optimizeProbabilities(Collection<Double> probabilities) {

        List<Double> newLevels = new ArrayList<>(probabilities.size() + 1);

        if (probabilities.size() == 0) {
            throw new IllegalArgumentException("Probabilities must be non-empty.");
        }

        if (!probabilities.stream().allMatch(p -> p > 0)) {
            throw new IllegalArgumentException("All the probability must be great than 0.");
        }

        newLevels.addAll(probabilities);

        double sum = probabilities.stream().mapToDouble(Double::doubleValue).sum();
        if (sum < 1) {
            newLevels.add(1 - sum);
        } else if (sum > 1) {
            throw new IllegalArgumentException("The probability is " + sum + ", is greater than 1.");
        }
        return newLevels;

    }
}
