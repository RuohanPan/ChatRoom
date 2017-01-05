/**
 * 16-12-27 下午5:20
 * by ruohan.pan
 */
public class ThreadDeadLockTest implements Runnable {
	public int flag = 1;
	private static Object o1 = new Object(), o2 = new Object();
	@Override
	public void run() {
		System.out.println("flag=" + flag);
		if (flag == 1) {
			synchronized (o1) {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				synchronized (o2) {
					System.out.println("1");
				}
			}
		}
		if (flag == 0) {
			synchronized (o2) {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				synchronized (o1) {
					System.out.println("0");
				}
			}
		}
	}

	public static void main(String[] args) {

		ThreadDeadLockTest td1 = new ThreadDeadLockTest();
		ThreadDeadLockTest td2 = new ThreadDeadLockTest();
		td1.flag = 1;
		td2.flag = 0;
		new Thread(td1).start();
		new Thread(td2).start();

	}
}