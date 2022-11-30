package cn.mdmbct.seckill.core.activity;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.award.Award;
import cn.mdmbct.seckill.core.award.CompleteRedEnvelope;
import cn.mdmbct.seckill.core.award.repository.LocalAwardRepository;
import cn.mdmbct.seckill.core.award.repository.RedisAwardRepository;
import cn.mdmbct.seckill.core.context.FilterContext;
import cn.mdmbct.seckill.core.filter.Filter;
import cn.mdmbct.seckill.core.filter.FilterChain;
import cn.mdmbct.seckill.core.lock.LocalAwardLock;
import cn.mdmbct.seckill.core.lock.RedisAwardLock;
import cn.mdmbct.seckill.core.lock.ZkAwardLock;
import lombok.Getter;
import lombok.Setter;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract activity executor class <br>
 * you should create an object of this class to execute a seckill or grab red packet activity <br>
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/16 下午9:29
 * @modified mdmbct
 * @since 1.0
 */
public abstract class Activity {

    protected final FilterChain filterChain;

    @Getter
    protected final ActivityConf activityConf;

    protected final String id;

    @Setter
    @Getter
    protected RedissonClient redissonClient;

    public Activity(ActivityConf conf, List<Filter> filters) {
        this.activityConf = conf;
        this.id = conf.getId();
        this.filterChain = new FilterChain(filters);
    }

    /**
     * if the participant take an award(red packet) luckily, this method will return it, otherwise return null.
     *
     * @param participant     participant
     * @param awardId         awardId. when grab a red
     * @param competeResClass Award.class or Double.class
     * @return award or the denomination of red packet that the participant take
     */
    public <R> R compete(Participant participant, String awardId, Class<R> competeResClass) {
        filterChain.filter(participant, awardId);
        Object competeRes = filterChain.getFilterContext().getCompeteRes();
        // todo: may todo something else.
        return competeResClass.cast(competeRes);
    }

    /**
     * clear memory
     */
    public void clear() {
        filterChain.clear();
        if (redissonClient != null) {
            clearRedisCache();
        }
    }

    /**
     * batch delete redis keys by prefix(DSK:${id}) <br>
     * if use {@code redis-cli --raw keys "ops-coffee-*" | xargs redis-cli del},
     * while a large number of keys are matched, which may cause redis to crash. <br>
     * so here use redis command {@code SCAN}, base usage: <br>
     * {@code SCAN cursor [MATCH pattern] [COUNT count]}
     *
     * @see <a href="https://cloud.tencent.com/developer/article/1447147">Redis删除特定前缀key的优雅实现</a> <br>
     * <a href="https://www.jianshu.com/p/5a95a8209e5b">使用scan配合lua脚本进行删除key</a>
     */
    private void clearRedisCache() {
        RScript script = redissonClient.getScript();
        // see src/main/resources/redisScanDel.lua
        String scanDelLua = "local function scan(key)\n" +
                "    local cursor = 0\n" +
                "    local keyNum = 0\n" +
                "\n" +
                "    repeat\n" +
                "        local res = redis.call(\"scan\", cursor, \"match\", key, 'COUNT', 1000)\n" +
                "        if (res ~= nil and #res >= 0) then\n" +
                "            redis.replicate_commands()\n" +
                "            cursor = tonumber(res[1])\n" +
                "            local keys = res[2]\n" +
                "            keyNum = #keys\n" +
                "            for i = 1, keyNum, 1 do\n" +
                "                local k = tostring(keys[i])\n" +
                "                redis.call(\"del\", k)\n" +
                "            end\n" +
                "        end\n" +
                "    until (cursor <= 0)\n" +
                "\n" +
                "    return keyNum\n" +
                "end\n" +
                "\n" +
                "local len = #KEYS\n" +
                "local i = 1\n" +
                "while (i <= len)\n" +
                "do\n" +
                "    scan(KEYS[i])\n" +
                "    i = i + 1\n" +
                "end";
        script.eval(RScript.Mode.READ_WRITE,
                scanDelLua,
                RScript.ReturnType.INTEGER,
                Arrays.asList("DSK:" + id + ":*", "{DSK:" + id + ":*"));
    }

    public FilterContext getFilterContext() {
        return filterChain.getFilterContext();
    }

    /**
     * Update the award stock
     *
     * @param awardId  award id
     * @param newStock new stock quantity
     * @return
     */
    public boolean updateStock(String awardId, int newStock) {
        throw new UnsupportedOperationException();
    }

    public boolean isExpired() {
        return activityConf.getEndTime() <= System.currentTimeMillis();
    }

    //// some default constructors ////

    public static CompleteAwardActivity awardWithLocal(List<Filter> filters, ActivityConf conf) {
        return new CompleteAwardActivity(filters, conf, new LocalAwardRepository(conf), new LocalAwardLock());
    }

    public static CompleteAwardActivity awardWithRedis(List<Filter> filters, RedissonClient redissonClient,
                                                       ActivityConf conf) {
        CompleteAwardActivity activity = new CompleteAwardActivity(filters, conf,
                new RedisAwardRepository(redissonClient, conf),
                new RedisAwardLock(conf.getId(), redissonClient));
        activity.redissonClient = redissonClient;
        return activity;
    }

    public static CompleteAwardActivity awardWithRedis(List<Filter> filters,
                                                       RedissonClient redissonClient, ActivityConf conf,
                                                       RedisAwardLock.RedisAwardLockConfig lockConfig) {
        CompleteAwardActivity activity = new CompleteAwardActivity(filters, conf,
                new RedisAwardRepository(redissonClient, conf),
                new RedisAwardLock(redissonClient, lockConfig));
        activity.redissonClient = redissonClient;
        return activity;
    }

    public static CompleteAwardActivity awardWithZooKeeper(List<Filter> filters,
                                                           RedissonClient redissonClient, ActivityConf seckill) {
        CompleteAwardActivity activity = new CompleteAwardActivity(filters, seckill,
                new RedisAwardRepository(redissonClient, seckill),
                new ZkAwardLock(seckill.getId(), seckill.getAwards().stream().map(Award::getId).collect(Collectors.toSet())));
        activity.redissonClient = redissonClient;
        return activity;
    }

    public static CompleteAwardActivity awardWithZooKeeper(List<Filter> filters,
                                                           RedissonClient redissonClient, ActivityConf seckill,
                                                           ZkAwardLock.ZkLockConfig lockConfig) {
        CompleteAwardActivity activity = new CompleteAwardActivity(filters, seckill,
                new RedisAwardRepository(redissonClient, seckill),
                new ZkAwardLock(lockConfig, seckill.getAwards().stream().map(Award::getId).collect(Collectors.toSet())));
        activity.redissonClient = redissonClient;
        return activity;
    }

    /**
     * @see CompleteRedEnvelopeActivity#CompleteRedEnvelopeActivity(List, String, long, long, double, int, CompleteRedEnvelope.SplitMethod)
     */
    public static CompleteRedEnvelopeActivity redPacketActivity(List<Filter> filters,
                                                                String activityId, long duration, long startTime,
                                                                double totalMoney, int redPacketSplitCount,
                                                                CompleteRedEnvelope.SplitMethod splitMethod) {
        return new CompleteRedEnvelopeActivity(filters,
                activityId, duration, startTime,
                totalMoney, redPacketSplitCount, splitMethod);
    }

    //// some default constructors ////


}
