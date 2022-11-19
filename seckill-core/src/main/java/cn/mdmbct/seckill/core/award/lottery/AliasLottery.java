package cn.mdmbct.seckill.core.award.lottery;

import java.util.*;

/**
 * PDF-Alias algorithm <br>
 * 概率分布-别名算法
 * <pre>
 * index          1         2	      3	        4
 * probability   0.1	   0.2	     0.3       0.4
 * alias          3         4	      4	       NULL
 * </pre>
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/16 上午8:52
 * @modified mdmbct
 * @see <a href="https://www.keithschwarz.com/darts-dice-coins/">https://www.keithschwarz.com/darts-dice-coins/
 * @since 1.0
 */
public class AliasLottery implements Lottery {

    /**
     * The probability array of each award and its alias array
     */
    private double[] probabilities;

    private int[] alias;

    private final Random random;

    public AliasLottery() {
        this.random = new Random();
    }

    @Override
    public void setProbabilities(List<Double> probabilities) {
        // init

        // no check, has checked in class AwardSeckill
        this.probabilities = new double[probabilities.size()];
        alias = new int[probabilities.size()];

        // average probability
        double average = 1.0 / probabilities.size();

        // Copy the list of probabilities, need to change it later
        ArrayList<Double> newProbabilities = new ArrayList<>(probabilities);

        Deque<Integer> small = new ArrayDeque<>();
        Deque<Integer> large = new ArrayDeque<>();

        for (int i = 0; i < newProbabilities.size(); ++i) {
            if (newProbabilities.get(i) >= average) {
                large.add(i);
            } else {
                small.add(i);
            }
        }

        // use up small before running out of large
        while (!small.isEmpty() && !large.isEmpty()) {

            // get the index of small and large probabilities
            int less = small.removeLast();
            int more = large.removeLast();

            this.probabilities[less] = probabilities.get(less) * probabilities.size();
            alias[less] = more;

            probabilities.set(more, (probabilities.get(more) + probabilities.get(less)) - average);

            if (probabilities.get(more) >= 1.0 / probabilities.size()) {
                large.add(more);
            } else {
                small.add(more);
            }
        }

        while (!small.isEmpty()) {
            this.probabilities[small.removeLast()] = 1.0;
        }
        while (!large.isEmpty()) {
            this.probabilities[large.removeLast()] = 1.0;
        }
    }

    @Override
    public int next() {
        int column = random.nextInt(probabilities.length);
        boolean coinToss = random.nextDouble() < probabilities[column];
        return coinToss ? column : alias[column];
    }
}
