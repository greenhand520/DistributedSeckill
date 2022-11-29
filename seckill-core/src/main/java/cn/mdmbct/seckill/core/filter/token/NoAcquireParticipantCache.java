package cn.mdmbct.seckill.core.filter.token;

import cn.mdmbct.seckill.core.activity.ActivityConf;
import org.redisson.api.RedissonClient;

/**
 * 没有获取到令牌的参加者缓存
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/11 上午10:02
 * @modified mdmbct
 * @since 1.0
 */
public interface NoAcquireParticipantCache {


    /**
     * 将没有拿到令牌的人id放入其中
     *
     * @param participantId 用户id
     */
    void add(String participantId);

    /**
     * 检查是否在缓存里
     *
     * @param participantId 用户id
     * @return 是否在缓存里
     */
    boolean check(String participantId);

    void clear();

    static LocalNoAcqParticipantCache localCache() {
        return new LocalNoAcqParticipantCache();
    }

    static RedisNoAcquireParticipantCache redisCache(RedissonClient redissonClient, ActivityConf conf) {
        return new RedisNoAcquireParticipantCache(redissonClient, conf);
    }

}
