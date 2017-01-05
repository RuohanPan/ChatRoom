package model;

/**
 * 16-12-20 下午3:00
 * by ruohan.pan
 */
public class MessageType {
	/**登录验证请求消息类型**/
	public final static int LOGIN_VERIFY = 0x0000;
	/**登录验证响应消息类型**/
	public final static int LOGIN_VERIFY_ACK = 0x0001;
	/**心跳请求消息类型**/
	public final static int HEART_BEAT = 0x0002;
	/**心跳响应消息类型**/
	public final static int HEART_BEAT_ACK = 0x0003;
	/**好友列表请求消息类型**/
	public final static int FRIEND_LIST = 0x0004;
	/**好友列表响应消息类型**/
	public final static int FRIEND_LIST_ACK = 0x0005;
	/**发送消息请求**/
	public final static int SEND_MESSAGE = 0x0006;
	/**发送消息响应**/
	public final static int SEND_MESSAGE_ACK = 0x0007;
	/*添加好友请求*/
	public final static int ADD_FRIEND = 8;
	/*添加好友响应*/
	public final static int ADD_FRIEND_ACK = 9;
	/*删除好友请求*/
	public final static int DEL_FRIEND = 10;
	/*删除好友响应*/
	public final static int DEL_FRIEND_ACK = 11;
	/*添加好友分组*/
	public final static int ADD_FRIEND_GROUP = 12;
	/*添加好友分组响应*/
	public final static int ADD_FRIEND_GROUP_ACK = 	13;
	/*搜索用户请求*/
	public final static int SEARCH_USER = 14;
	/*搜索用户响应响应*/
	public final static int SEARCH_USER_ACK = 15;
	/*好友发消息请求*/
	public final static int SEND_MSG = 16;
	/*好友发消息响应*/
	public final static int SEND_MSG_ACK = 17;
	/*群发消息请求*/
	public final static int BATCH_SEND = 18;
	/*群发消息响应*/
	public final static int BATCH_SEND_ACK = 19;
	/*聊天室请求*/
	public final static int CHATROOM_SEND = 20;
	/*聊天室响应*/
	public final static int CHATROOM_SEND_ACK = 21;
	/*封禁用户登陆请求*/
	public final static int ADMIN_BLOCK_USER_LOGIN = 22;
	/*封禁用户登录响应*/
	public final static int ADMIN_BLOCK_USER_LOGIN_ACK = 23;
	/*封禁用户发送消息请求*/
	public final static int ADMIN_BANNED_USER_POST = 24;
	/*封禁用户发送请求响应*/
	public final static int ADMIN_BANNED_USER_POST_ACK = 25;
	/*将用户加入黑名单*/
	public final static int ADD_TO_BLACKLIST = 26;
	/*用户加入黑名单请求*/
	public final static int ADD_TO_BLACKLIST_ACK = 27;
	/*将用户移除黑名单*/
	public final static int REMOVE_FROM_BLACKLIST = 28;
	/*将用户移除黑名单响应*/
	public final static int REMOVE_FROM_BLACKLIST_ACK = 29;
	/**发送消息通知响应**/
	public final static int SEND_MESSAGE_ACK_NOTICE = 0x1000;
	/**通知用户上下线**/
	public final static int USER_ON_OFF_LINE_NOTICE = 0x1001;

	/**包头大小**/
	public final static int HEAD_LENGTH = 10;
	/**返回的消息类型 0服务端推送**/
	public final static int MESSAGE_TYPE_PUSH = 0;
	/**返回的消息类型 1请求响应**/
	public final static int MESSAGE_TYPE_REQUEST = 1;
	/**返回的内容类型 0 JsonObject**/
	public final static int CONTENT_TYPE_OBJECT = 0;
	/**返回的内容类型 1 JsonArray**/
	public final static int CONTENT_TYPE_ARRAY = 1;

	/*向服务器请求，send和reciver*/
	public final static int SERVER_REV = -1;

	public final static int SERVER_SENT = -2;

	public final static int BRODCAST_SENT = -3;
}