package cn.mdmbct.seckill.core.award;

import java.util.*;

/**
 * PDF-Alias算法 <br>
 * 概念分布函数-别名 算法 <br>
 * <pre>
 *     T	    1	    2	    3	    4
 *    PDF   0.1	   0.2	   0.3	   0.4
 *    Alias  3	    4	    4	    NULL
 * </pre>
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/16 上午8:52
 * @modified mdmbct
 * @since 1.0
 */
public class PDFAliasDrawer extends Drawer {

    /**
     * 各奖项的概率数组及其alias数组
     */
    private double[] probabilities;

    private int[] alias;

    private final Random random;

    public PDFAliasDrawer() {
        this.random = new Random();
    }

    /**
     * 初始化
     */
    private void init(List<Double> probabilities) {

        // no check, has checked in method optimiseProbabilities

        this.probabilities = new double[probabilities.size()];
        alias = new int[probabilities.size()];

        // 平均概率
        double average = 1.0 / probabilities.size();

        // 复制概率列表，后面需要对其进行更改
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

        // 在用完large之前用完small
        while (!small.isEmpty() && !large.isEmpty()) {

            // 获取小概率和大概率的索引
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
    public void setProbabilities(Collection<Double> probabilities) {
        init(optimizeProbabilities(probabilities));
    }

    @Override
    public int next() {
        int column = random.nextInt(probabilities.length);
        boolean coinToss = random.nextDouble() < probabilities[column];
        return coinToss ? column : alias[column];
    }
}
