package cn.mdmbct.seckill.core.activity;

import cn.mdmbct.seckill.core.Participant;
import cn.mdmbct.seckill.core.cache.BaseLocalCache;
import cn.mdmbct.seckill.core.cache.Cache;
import cn.mdmbct.seckill.core.cache.CacheClearService;
import cn.mdmbct.seckill.core.context.FilterContext;

/**
 * Second kill, lottery, grab red envelope activity manager
 *
 * @author mdmbct  mdmbct@outlook.com
 * @date 2021/11/20 0:01
 * @modified mdmbct
 * @since 0.1
 */
public class ActivityManager {

    private final Cache<String, Activity> activityCache;

    private volatile static ActivityManager activityManager = null;

    private ActivityManager() {
        this.activityCache = BaseLocalCache.concurrentHashMapCache(0);
        activityCache.setClearListener(this::clear);
    }

    public static ActivityManager instance() {
        if (activityManager == null) {
            synchronized (CacheClearService.class) {
                if (activityManager == null) {
                    activityManager = new ActivityManager();
                    return activityManager;
                }
            }
        }
        return activityManager;
    }

    public void addActivity(Activity activity) {
        activityCache.put(activity.getActivityConf().getId(), activity,
                activity.activityConf.getExpireTime() - System.currentTimeMillis());
    }

    public void autoClear(long delay) {
        activityCache.autoClear(delay);
    }

    public void clearManually() {
        activityCache.clear();
        // will call cn.mdmbct.seckill.core.activity.ActivityManager.clear by ClearListener
    }

    public void clearAll() {
        for (Activity activity : activityCache.values()) {
            activity.clear();
        }
        activityCache.clearAll();
    }

    public FilterContext getFilterContext(String activityId) {
        return activityCache.getValue(activityId, false).getFilterContext();
    }

    private void clear(String activityId, Activity activity) {
        activity.clear();
    }

    /**
     * @param activityId  activity id
     * @param participant participant
     * @param awardId     award id
     * @param awardClass  {@code Award.class} or {@code Double.class}
     * @param <R>         Award class
     * @return Award
     * @see Activity#compete(Participant, String, Class)
     */
    public <R> R compete(String activityId, Participant participant, String awardId, Class<R> awardClass) {
        Activity activity = activityCache.getValue(activityId, false);
        if (activity.isExpired()) {
            throw new UnsupportedOperationException("活动已过期或者不存在！");
        }
        return activity.compete(participant, awardId, awardClass);
    }
}
