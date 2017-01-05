import java.lang.ref.SoftReference;

/**
 * 16-12-27 下午2:52
 * by ruohan.pan
 */
public class WeakReferenceTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String str = new String("hello");
		//		String ref = str;
		SoftReference<String> ref = new SoftReference<String> (str);
		str = null;
		if (ref != null) {
			System.gc();
			System.out.println(ref.get());
		}
	}

}



