package Utils;

import org.apache.commons.codec.binary.Base64;

/**
 * 16-12-29 下午2:36
 * by ruohan.pan
 */
public class Base64Utils
{
	public static String encodeStr(String plainText){
		byte[] b=plainText.getBytes();
		Base64 base64=new Base64();
		b=base64.encode(b);
		String s=new String(b);
		return s;
	}

	public static String decodeStr(String encodeStr){
		byte[] b=encodeStr.getBytes();
		Base64 base64=new Base64();
		b=base64.decode(b);
		String s=new String(b);
		return s;
	}

	public static void main(String[] args) {
		String s = "123456";
		System.out.println(encodeStr(s));
		System.out.println(decodeStr(encodeStr(s)));
	}

}
