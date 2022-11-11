package cn.mdmbct.seckill.starter.properties;

import cn.mdmbct.seckill.core.lock.AwardLock;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * Seckill yml configuration class
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/3 下午7:57
 * @modified mdmbct
 * @since 1.0
 */

@Data
@ConfigurationProperties(prefix = "seckill")
public class SeckillProperties implements Serializable {

    private static final long serialVersionUID = -1365524205210850227L;

    private AwardLock lock;

    private RedisDistributeLockProperties redisDistributeLockProperties;


}
