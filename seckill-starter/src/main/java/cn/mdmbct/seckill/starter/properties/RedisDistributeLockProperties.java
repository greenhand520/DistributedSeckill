package cn.mdmbct.seckill.starter.properties;

import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * Redis distribute lock yml config
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/3 下午8:09
 * @modified mdmbct
 * @since 1.0
 */
@Data
@ConfigurationProperties(prefix = "seckill.redis-lock")
public class RedisDistributeLockProperties implements Serializable {

    private static final long serialVersionUID = -9036777536604727314L;

    public enum RedissonMode {

        /**
         * Redis使用单机模式
         */
        SINGLE,

        /**
         * Redis使用集群模式
         */
        CLUSTER,

        /**
         * 主从模式
         */
        MASTER_SLAVE,

        /**
         * 哨兵模式
         */
        Sentinel

    }

    /**
     * 主机地址(ip:port). Redis url should start with redis:// or rediss://
     */
    @Builder.Default
    private String address = "redis://127.0.0.1:6379";

    /**
     * 密码.
     */
    private String password;

    /**
     * 超时.
     */
    @Builder.Default
    private int timeout = 3000;

    private String username;


    /**
     * 数据库 默认1
     */
    @Builder.Default
    private int database = 0;


    /**
     * Redisson模式 模式单机模式
     */
    private RedissonMode redissonMode = RedissonMode.SINGLE;

    /**
     * 多个redis的host 用于RedissonMode为 <br>
     * {@link RedissonMode#MASTER_SLAVE}、{@link RedissonMode#Sentinel}和{@link RedissonMode#CLUSTER} <br>
     * 其中{@code MASTER_SLAVE}主从模式下为slave的host，master的host为{@code masterHost} <br>
     * 此时{@code host}不用设置
     */
    private String[] hosts;

    /**
     * 用于RedissonMode为{@link RedissonMode#MASTER_SLAVE}时的master redis host
     */
    private String masterHost;

    private int masterMinIdle;

    private int slaveMinIdle;

    /**
     * Redis集群扫描间隔（以毫秒为单位）默认5000
     */
    @Builder.Default
    private int scanInterval = 5000;


}
