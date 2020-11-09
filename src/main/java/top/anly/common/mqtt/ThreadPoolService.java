package top.anly.common.mqtt;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("all")
public class ThreadPoolService {
    private static final int DEFAULT_CORE_SIZE=10;
    private static final int MAX_QUEUE_SIZE=20;
    private volatile static ThreadPoolExecutor executor;

    // 获取单例的线程池对象
    public static ThreadPoolExecutor getInstance() {
        if (executor == null) {
            synchronized (ThreadPoolService.class) {
                if (executor == null) {
                    executor = new ThreadPoolExecutor(DEFAULT_CORE_SIZE,// 核心线程数
                            MAX_QUEUE_SIZE, // 最大线程数
                            30, // 闲置线程存活时间
                            TimeUnit.MILLISECONDS,// 时间单位
                            //LinkedBlockingQueue
                            new LinkedBlockingQueue<Runnable>(10)
                              //new ArrayBlockingQueue<Runnable>(1)
                    );
                    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
                }
            }
        }
        return executor;
    }

    public void execute(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        executor.execute(runnable);
    }

    // 从线程队列中移除对象
    public void cancel(Runnable runnable) {
        if (executor != null) {
            executor.getQueue().remove(runnable);
        }
    }

}
