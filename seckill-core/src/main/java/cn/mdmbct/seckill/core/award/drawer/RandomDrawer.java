package cn.mdmbct.seckill.core.award.drawer;

import java.util.Collection;
import java.util.Random;

/**
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/16 上午9:15
 * @modified mdmbct
 * @since 1.0
 */
public class RandomDrawer extends Drawer {

    private final Random random;

    public RandomDrawer(Random random) {
        this.random = random;
    }

    @Override
    public void setProbabilities(Collection<Double> levels) {

    }

    @Override
    public int next() {
        return 0;
    }
}
