package threadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 17-1-4 下午3:08
 * by ruohan.pan
 */
public class ExecutorServiceFactory {
	private static ExecutorServiceFactory executorFactory = new ExecutorServiceFactory();

	private ExecutorService executors;

	private ExecutorServiceFactory() {
	}

	public static ExecutorServiceFactory getInstance() {
		return executorFactory;
	}

	public ExecutorService createFixedThreadPool(int count) {
		// 创建
		executors = Executors.newFixedThreadPool(count, getThreadFactory());
		return executors;
	}

	private ThreadFactory getThreadFactory() {
		return new ThreadFactory() {
			AtomicInteger sn = new AtomicInteger();
			public Thread newThread(Runnable r) {
				SecurityManager s = System.getSecurityManager();
				ThreadGroup group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
				Thread t = new Thread(group, r);
				t.setName("任务线程 - " + sn.incrementAndGet());
				return t;
			}
		};
	}
}
