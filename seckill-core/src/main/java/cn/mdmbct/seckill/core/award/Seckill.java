package cn.mdmbct.seckill.core.award;

import com.sun.istack.internal.NotNull;
import lombok.Getter;

import java.io.Serializable;

/**
 * 秒杀配置
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/18 20:33
 * @modified mdmbct
 * @since 0.1
 */
@Getter
public class Seckill implements Serializable {

    private static final long serialVersionUID = 5863791314844693335L;

    /**
     * 秒杀活动id
     */
    private final String id;

    /**
     * 秒杀持续时间 单位：秒
     */
    private final long ttl;

    /**
     * 开始时间
     */
    private final long startTime;

    public Seckill(@NotNull String id, long ttl, long startTime) {

        if (id == null) {
            throw new IllegalArgumentException("The param 'id' must not be mull");
        }

        if (ttl <= 0 || startTime <= 0) {
            throw new IllegalArgumentException("The parma 'ttl' and 'startTime' both must be > 0, the illegal parma are "
                    + ttl + " and " + startTime);
        }

        this.id = id;
        this.ttl = ttl;
        this.startTime = startTime;
    }
}
