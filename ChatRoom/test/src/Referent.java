/**
 * 16-12-27 下午4:36
 * by ruohan.pan
 */
public class Referent
{
	static Referent referent;
	public void finalize() {
		referent = this;
	}
}

