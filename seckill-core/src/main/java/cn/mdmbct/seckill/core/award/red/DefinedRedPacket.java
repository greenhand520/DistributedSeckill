package cn.mdmbct.seckill.core.award.red;

import cn.mdmbct.seckill.core.award.Award;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * A red packet which has pre-defined denomination and count.
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/19 22:49
 * @modified mdmbct
 * @since 0.1
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class DefinedRedPacket extends Award implements Serializable {

    private static final long serialVersionUID = 3931744600494223642L;

    /**
     * denomination unit: Yuan
     */
    private final double money;

    /**
     * @param id     id
     * @param money  denomination
     * @param count the count of red packet of this money
     */
    public DefinedRedPacket(String id, double money, int count) {
        super(id, count);
        if (money < 0) {
            throw new IllegalArgumentException("The denomination of red packet must be more than zero.");
        }
        this.money = money;
    }
}
