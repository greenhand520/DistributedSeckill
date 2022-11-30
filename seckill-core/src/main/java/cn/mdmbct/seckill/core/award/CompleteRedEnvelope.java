package cn.mdmbct.seckill.core.award;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 * Grab a complete red envelope which just define total money and split count <br>
 * this class will auto split the red envelope by {@link CompleteRedEnvelope#doubleMean()} and {@link CompleteRedEnvelope#lineSegmentCutting()} <br>
 * just run in single node server
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/16 下午4:41
 * @modified mdmbct
 * @since 1.0
 */
@Getter
public class CompleteRedEnvelope {

    public enum SplitMethod {
        DOUBLE_MEAN,
        LINE_SEGMENT_CUTTING
    }

    /**
     * unit: Yuan
     */
    private final double totalMoney;

    /**
     * The count of red packet split
     */
    private final int count;

    private int remainCount;

    private double remainMoney;

    private final Random random = new Random();

    /**
     * min denomination participant take, default value is 0.01 Yuan.
     */
    @Setter
    private double minMoney = 0.01;

    public CompleteRedEnvelope(double totalMoney, int count) {
        this.totalMoney = totalMoney;
        this.count = count;
        this.remainCount = count;
        this.remainMoney = totalMoney;
    }

    /**
     * Generate a double between min and max
     **/
    private double nextDouble(double min, double max) {
        return min + ((max - min) * random.nextDouble());
    }

    private double formatMoney(double money) {
        return new BigDecimal(money).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * double mean method, is a fair and square method to split red packet <br>
     * only a thread can execute this method
     * @return The money of red packet grabbed with 2 significant digits , unit：Yuan
     */
    synchronized public double doubleMean() {
        if (remainCount > 1) {
            double money = formatMoney(nextDouble(minMoney, 2 * (remainMoney / remainCount)));
            remainMoney -= money;
            remainCount--;
            return money;
        } else {
            return formatMoney(remainMoney);
        }
    }

    /**
     * line segment cutting, is not a fair and square method to split red packet. <br>
     * on the whole, the faster participants grab speed, the larger the money take, but it not is absolute. <br>
     * attention: what's going to happen is the last few participants may be unable to take money. <br>
     * only a thread can execute this method
     */
    synchronized public double lineSegmentCutting() {
        double begin = totalMoney - remainMoney;
        if (remainCount > 1) {
            double money = formatMoney(nextDouble(begin, totalMoney) - begin);
            remainMoney -= money;
            remainCount--;
            return money;
        } else  {
            return formatMoney(remainMoney);
        }
    }

    synchronized public double grab(SplitMethod splitMethod) {
        switch (splitMethod) {
            case DOUBLE_MEAN:
                return doubleMean();
            case LINE_SEGMENT_CUTTING:
                return lineSegmentCutting();
            default:
                return doubleMean();
        }
    }




}
