package cn.mdmbct.seckill.core.cache;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author mdmbct  mdmbct@outlook.com
 * @date 2022/11/21 上午9:27
 * @modified mdmbct
 * @since 1.0
 */
public class CacheClearService {

    private static final int CLEAR_POOL_CORE_SIZE = 8;

    private volatile static CacheClearService clearService = null;

    private ScheduledExecutorService executorService;

    private final AtomicInteger jobCount = new AtomicInteger(0);

    private CacheClearService() {
        // init
        this.executorService = new ScheduledThreadPoolExecutor(CLEAR_POOL_CORE_SIZE, r -> {
            Thread thread = new Thread(r, "CacheClearThread-" + jobCount.incrementAndGet());
            if (thread.getPriority() != Thread.NORM_PRIORITY) {
                thread.setPriority(Thread.NORM_PRIORITY);
            }
            return thread;
        });
    }

    public static CacheClearService instacne() {
        if (clearService == null) {
            synchronized (CacheClearService.class) {
                if (clearService == null) {
                    return new CacheClearService();
                }
            }
        }
        return clearService;
    }

    public ScheduledFuture<?> addClearJob(Runnable task, long delay) {
        return this.executorService.scheduleAtFixedRate(task, delay, delay, TimeUnit.MILLISECONDS);
    }

    public void shutdown(boolean isShutdownNow) {
        if (executorService != null) {
            if (isShutdownNow) {
                executorService.shutdownNow();
            } else {
                executorService.shutdown();
            }
        }
    }


}
