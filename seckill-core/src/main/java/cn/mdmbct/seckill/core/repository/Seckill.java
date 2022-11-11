package cn.mdmbct.seckill.core.repository;

import cn.mdmbct.seckill.core.repository.Award;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀配置
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/18 20:33
 * @modified mdmbct
 * @since 0.1
 */
@Getter
@RequiredArgsConstructor
public class Seckill implements Serializable {

    private static final long serialVersionUID = 5863791314844693335L;

    /**
     * 秒杀活动id
     */
    private final String id;

    private final List<Award> awards;

    /**
     * 秒杀持续时间 单位：秒
     */
    private final long ttl;

    /**
     * 开始时间
     */
    private final long startTime;


}
