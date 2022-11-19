package cn.mdmbct.seckill.core.award.lottery;

import java.util.List;

/**
 * Take the award with a probability from award repository <br>
 * for more lottery algorithms refer to this <a href="https://www.keithschwarz.com/darts-dice-coins/">website.
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/16 上午8:34
 * @modified mdmbct
 * @since 1.0
 */
public interface Lottery {

    /**
     * set the award distribution probability information
     *
     * @param probabilities award distribution probability information
     */
    void setProbabilities(List<Double> probabilities);

    /**
     * return the probability index of param 'probabilities' in method {@link Lottery#setProbabilities(List)} <br>
     * example params：0.0 0.1 0.2 0.3 0.4 0.5 <br>
     * if return 0, means the award corresponding to the first probability 0.0<br>
     * if return 5，means the award corresponding to the fifth probability 0.5
     *
     * @return award index
     */
    int next();

}
