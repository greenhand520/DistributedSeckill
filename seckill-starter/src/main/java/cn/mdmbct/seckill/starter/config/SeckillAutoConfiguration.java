package cn.mdmbct.seckill.starter.config;

import cn.mdmbct.seckill.starter.properties.RedisDistributeLockProperties;
import cn.mdmbct.seckill.starter.properties.SeckillProperties;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/3 下午8:04
 * @modified mdmbct
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties({SeckillProperties.class, RedisDistributeLockProperties.class})
public class SeckillAutoConfiguration {

    private static RedissonClient redissonClient(RedisDistributeLockProperties properties) {
        Config redissonConfig = RedissonConfigFactory.createRedissonConfig(properties);
        return Redisson.create(redissonConfig);
    }
}
