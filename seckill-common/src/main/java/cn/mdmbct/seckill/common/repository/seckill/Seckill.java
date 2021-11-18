package cn.mdmbct.seckill.common.repository.seckill;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/18 20:33
 * @modified mdmbct
 * @since 1.0
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class Seckill implements Serializable {

    private static final long serialVersionUID = 5863791314844693335L;

    private final List<Goods> goods;

    /**
     * 秒杀持续时间
     */
    private final long duration;

    /**
     * 持续时间的单位
     */
    private final TimeUnit timeUnit;

    /**
     * 开始时间
     */
    private final long startTime;


}
