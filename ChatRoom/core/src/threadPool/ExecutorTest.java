package threadPool;

/**
 * 17-1-4 下午3:10
 * by ruohan.pan
 */
public class ExecutorTest {

	public static void main(String[] args) {

		ExecutorProcessPool pool = ExecutorProcessPool.getInstance();
		for (int i = 0; i < 200; i++) {
			pool.execute(new ExcuteTask2(i+""));
		}
//		pool.shutdown();
	}

	static class ExcuteTask2 implements Runnable {
		private String taskName;

		public ExcuteTask2(String taskName) {
			this.taskName = taskName;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("-------------这里执行业务逻辑，Runnable TaskName = " + taskName + "-------------");
		}

	}
}