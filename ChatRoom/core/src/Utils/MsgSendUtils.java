package Utils;

import jdbc.DBUtils;
import model.PackageHead;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.util.CopyOnWriteMap;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 16-12-28 下午4:00
 * by ruohan.pan
 */
public class MsgSendUtils
{
	/*public static String getDigest(String pwd) {
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(pwd.getBytes());
			byte[] pwdBytes = md.digest();
			pwd = byte2hex(pwdBytes);
		}
		catch(NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return pwd;
	}*/
	/*对密码进行Base64编码*/
	public static String getBase64Encode(String key) throws Exception {
		return (new BASE64Encoder()).encodeBuffer(key.getBytes());
	}
	/*解密*/
	public static byte[] getBase64Decoder(String key) throws Exception {
		return (new BASE64Decoder()).decodeBuffer(key);
	}

	public static String byte2hex(byte[] b) //二进制
	{
		String hs="";
		String stmp="";
		for (int n=0;n<b.length;n++)
		{
			stmp=(java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length()==1) hs=hs+"0"+stmp;
			else hs=hs+stmp;
			if (n<b.length-1)  hs=hs+":";
		}
		return hs.toUpperCase();
	}
	/*获取用户的id*/
	public static int getUserId (String userName) {
		ResultSet resultSet = DBUtils.getInstance().getUserInfo(userName);
		int userid = 0;
		try
		{
			while (resultSet.next()) {
				userid = resultSet.getInt("id");
				break;
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return userid;
	}
	/*发送离线消息*/
	public static void offlineMsgSend(PackageHead packageHead, ConcurrentHashMap<Integer, Queue<PackageHead>> msgCache, Integer friendId) {
		if (msgCache.containsKey(friendId)) {
			msgCache.get(friendId).offer(packageHead);
		} else {
			Queue<PackageHead> phQueue = new LinkedList<PackageHead>();
			phQueue.offer(packageHead);
			msgCache.put(friendId, phQueue);
		}
	}
	/*广播离线消息*/
	public static void offlineMsgSendToAll(PackageHead packageHead, Map<String, IoSession> sessionMap,
										   ConcurrentHashMap<Integer, Queue<PackageHead>> msgCache) throws SQLException
	{
		ResultSet users = DBUtils.getInstance().getAllusers();
		while(users.next()) {
			String userName = users.getString("name");
			int userId = users.getInt("id");
			if (sessionMap.get(userName) == null) {
				MsgSendUtils.offlineMsgSend(packageHead, msgCache, userId);
			}
		}
	}
	/*管理员加黑名单*/
//	private static CopyOnWriteMap<String, Boolean> blackListMap = new CopyOnWriteMap<String, Boolean>(1000);

	public  static boolean isInBlackList(String userName, CopyOnWriteMap<String, Boolean> blackListMap) {
		return blackListMap.get(userName) == null ? false : true;
	}

	public static void addBlackList (String userName, CopyOnWriteMap<String, Boolean> blackListMap) {
		blackListMap.put(userName, Boolean.TRUE);
	}

	public static void batchAddBlackList(Map<String, Boolean> ids, CopyOnWriteMap<String, Boolean> blackListMap) {
		blackListMap.putAll(ids);
	}

	/*时间*/
	public static String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return sdf.format(new Date());
	}


}
