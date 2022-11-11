package cn.mdmbct.seckill.core.repository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 红包
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/19 22:49
 * @modified mdmbct
 * @since 0.1
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class RedPacket extends Award implements Serializable {

    private static final long serialVersionUID = 3931744600494223642L;

    /**
     * 金额 单位：分
     */
    private final int amount;

    /**
     * @param id      id
     * @param amount  金额
     * @param packets 个数
     */
    public RedPacket(String id, int amount, int packets) {
        super(id, packets);
        this.amount = amount;
    }
}
