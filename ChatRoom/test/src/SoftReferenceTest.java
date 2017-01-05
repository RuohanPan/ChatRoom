import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

/**
 * 16-12-27 下午3:01
 * by ruohan.pan
 */
public class SoftReferenceTest {

	/**
	 * @param args
	 */

	public static void main(String[] args) {
//		SoftReference<String> softRef = new SoftReference<String>(str);\
		ReferenceQueue queue = new ReferenceQueue();
		PhantomReference phantomReference = new PhantomReference(new Referent(), queue);
		System.out.println(phantomReference.get());
		Object obj = queue.poll();
		System.out.println(obj);
		/*第一次GC*/
		System.gc();
		System.out.println(queue.poll());

		try
		{
			Thread.sleep(5000);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		System.gc();
		System.out.println(String.format("PhantomReference string: %s", phantomReference.get()));
		obj = queue.poll();
		System.out.println(obj);
	}


}
