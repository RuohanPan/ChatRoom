package threadPool;

import java.util.concurrent.ExecutorService;

/**
 * 17-1-4 下午3:09
 * by ruohan.pan
 */
public class ExecutorProcessPool {

	private ExecutorService executor;
	private static ExecutorProcessPool pool = new ExecutorProcessPool();
	private final int threadMax = 10;

	private ExecutorProcessPool() {
		System.out.println("threadMax:" + threadMax);
		executor = ExecutorServiceFactory.getInstance().createFixedThreadPool(threadMax);
	}

	public static ExecutorProcessPool getInstance() {
		return pool;
	}

	public void shutdown(){
		executor.shutdown();
	}

	public void execute(Runnable task){
		executor.execute(task);
	}

}
