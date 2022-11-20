package cn.mdmbct.seckill.core.filter.lottery;

import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * <pre>
 * name           A    B    C    D
 * index          1    2    3    4
 * probability   0.1  0.2  0.3  0.4
 * </pre>
 * <p>
 * Using {@link TreeMap} to build a tree like this:
 * <pre>
 * 　　　　 2(0.3)
 * 　　　   /   \
 *        /     \
 *    1(0.1)  3(0.6)
 *              /
 *             /
 *           4(1)
 * </pre>
 * generate a random number between [0,1). falls in which interval,
 * then the first element after the interval is the element hit by weight
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/16 上午9:15
 * @modified mdmbct
 * @see <a href="https://www.keithschwarz.com/darts-dice-coins/">https://www.keithschwarz.com/darts-dice-coins/
 * @since 1.0
 */
public class WeightRandomLottery implements Lottery {

    private final Random random;

    /**
     * key: probability
     * value: award index
     */
    private final TreeMap<Double, Integer> weightMap;

    public WeightRandomLottery() {
        this.random = new Random();
        this.weightMap = new TreeMap<>();
    }

    @Override
    public void setProbabilities(List<Double> probabilities) {

        for (int i = 0; i < probabilities.size(); i++) {
            double lastWeight = this.weightMap.size() == 0 ? 0 : this.weightMap.lastKey();
            // accumulate
            this.weightMap.put(probabilities.get(i) + lastWeight, i);
        }
    }

    @Override
    public int next() {
        double randomWeight = this.weightMap.lastKey() * random.nextDouble();
        SortedMap<Double, Integer> tailMap = this.weightMap.tailMap(randomWeight, false);
        return tailMap.get(tailMap.firstKey());
    }
}
