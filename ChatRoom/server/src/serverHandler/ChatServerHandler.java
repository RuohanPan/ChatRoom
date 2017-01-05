package serverHandler;

import Utils.MsgSendUtils;
import jdbc.DBUtils;
import main.ChatServer;
import model.*;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.util.CopyOnWriteMap;

import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 16-12-20 下午2:28
 * by ruohan.pan
 */
public class ChatServerHandler extends IoHandlerAdapter {
	private static final Logger logger = Logger.getLogger(ChatServer.class);

	private static ConcurrentHashMap<String, IoSession> sessionMap = new ConcurrentHashMap<String, IoSession>();

	private static ConcurrentHashMap<Integer, Queue<PackageHead>> msgCache = new ConcurrentHashMap<Integer, Queue<PackageHead>>();

//	private static List<IoSession> chatroomList = new ArrayList<IoSession>();

	private static ConcurrentHashMap<String, List<IoSession>> chatroom = new ConcurrentHashMap<String, List<IoSession>>();

	private static CopyOnWriteMap<String, Boolean> blackListMap = new CopyOnWriteMap<String, Boolean>(1000);

	private static CopyOnWriteMap<String, Boolean> bannedPostMap = new CopyOnWriteMap<String, Boolean>(1000);

	public void sessionCreated(IoSession session) throws Exception {
		logger.info("创建连接");
	}

	public void sessionOpened(IoSession session) throws Exception {
		logger.info("打开连接");
	}

	public void sessionClosed(IoSession session) throws Exception {
		logger.info("下线成功");
		logger.info("关闭连接");
		String currentUserName = (String)session.getAttribute("userName");
		PackageHead ph = new PackageHead();
		ph.setContent("下线通知");
		session.write(ph);
		Collection<IoSession> allSessions = session.getService().getManagedSessions().values();
		for (IoSession is : allSessions) {
			if (DBUtils.getInstance().isAlreadyFriend(currentUserName, (String)is.getAttribute("name"))) {
				ph.setMessageType(MessageType.USER_ON_OFF_LINE_NOTICE);
				ph.setSendId(MsgSendUtils.getUserId(currentUserName));
				ph.setContent("好友" + currentUserName + "下线了！");
				is.write(ph);
			} else {
				break;
			}
		}
		sessionMap.remove(currentUserName);
	}

	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		logger.info("连接空闲中");
	}

	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		logger.warn("异常", cause);
	}

	public void messageReceived(IoSession session, Object message) throws Exception {
		Object msg = message;
		System.out.println(message);
		PackageHead packageHead = (PackageHead)message;
		JSONObject jsonObject = JSONObject.fromObject(packageHead.getContent());
		Login login = (Login)jsonObject.toBean(jsonObject, Login.class);
		String currentUserName = login.getUserName();
		/*BASE64加密*/
		String password = login.getPwdEncypt();
		String ackContent;
		IoSession ioSession = sessionMap.get(login.getUserName());
		Map<String, Object> resultMap = new HashMap<String, Object>();
		switch(packageHead.getMessageType()) {
			/*用户登陆请求，请求格式(length,sendId,reciverId,messageType,content)*/
			case MessageType.LOGIN_VERIFY:
				packageHead.setMessageType(MessageType.LOGIN_VERIFY_ACK);
				if (blackListMap.containsKey(currentUserName) && blackListMap.get(currentUserName)) {
					System.out.println("已被管理员封禁，禁止登陆");
					resultMap.put("info", "已被管理员封禁，禁止登陆");
					ackContent = JSONObject.fromObject(resultMap).toString();
					packageHead.setLength(ackContent.length());
					packageHead.setSendId(MessageType.SERVER_SENT);
					packageHead.setMessageType(MessageType.LOGIN_VERIFY_ACK);
					packageHead.setContent(JSONObject.fromObject(resultMap).toString());
					session.write(packageHead);/*登录确认*/
				} else if (ioSession != null && ioSession.isConnected()) {
					resultMap.put("info", "用户已登录");
					resultMap.put("userName", currentUserName);
					ackContent = JSONObject.fromObject(resultMap).toString();
					packageHead.setContent(ackContent);
					session.write(packageHead);/*登录确认*/
				}else if (!DBUtils.getInstance().isExit(currentUserName)) {
					resultMap.put("info", "用户名不存在");
					resultMap.put("userName", currentUserName);
				} else if (!DBUtils.getInstance().isMatch("user", "name", currentUserName, "password", password)) {
					resultMap.put("info", "密码错误");
					resultMap.put("userName", currentUserName);
				} else {
					ResultSet result = DBUtils.getInstance().getUserInfo(currentUserName);
					User user = new User();
					while(result.next()) {
						user.setUserId(result.getInt("id"));
						user.setName(result.getString("name"));
						user.setPassword(login.getPwdEncypt());
						user.setIsOnline(result.getInt("is_online"));
						break;
					}
						DBUtils.getInstance().setOnOrOff(currentUserName);
						resultMap.put("status", 0);
						resultMap.put("info", "登陆成功");
						resultMap.put("user", user);
						session.setAttribute("name", currentUserName);
						sessionMap.put(currentUserName, session);
					/*List<Friends> friendsList = DBUtils.getInstance().getFriendsList(currentUserName);
					if (friendsList != null && friendsList.size() != 0) {*//*好友上线通知*//*
						packageHead.setMessageType(MessageType.USER_ON_OFF_LINE_NOTICE);
						packageHead.setSendId(1);
						packageHead.setContent("好友" + currentUserName + "上线了！");
						for (Friends friend : friendsList) {
							packageHead.setRecievedId(friend.getFriendId());
							IoSession iso = sessionMap.get(friend.getFriendName());
							if (iso != null) {
								iso.write(packageHead);
							}
						}
					}*/
						Collection<IoSession> allSessions = session.getService().getManagedSessions().values();
						for (IoSession is : allSessions) {
							if (DBUtils.getInstance().isAlreadyFriend(currentUserName, (String)is.getAttribute("name"))) {
								packageHead.setMessageType(MessageType.USER_ON_OFF_LINE_NOTICE);
								packageHead.setSendId(1);
								packageHead.setContent("好友" + currentUserName + "上线了！");
								is.write(packageHead);
							} else {
								break;
							}
						}
						ackContent = JSONObject.fromObject(resultMap).toString();
						packageHead.setLength(ackContent.length());
						packageHead.setSendId(MessageType.SERVER_SENT);
						packageHead.setRecievedId(1);
						packageHead.setMessageType(MessageType.LOGIN_VERIFY_ACK);
						packageHead.setContent(ackContent);
						session.write(packageHead);/*登录确认*/
					/*是否有发给当前用户的离线消息*/
						if (msgCache.containsKey(user.getUserId()) && !msgCache.get(user.getUserId()).isEmpty()) {
							while (!msgCache.get(user.getUserId()).isEmpty()) {
								Map<String, Object> tmpRstMap = new HashMap<String, Object>();
								packageHead = msgCache.get(user.getUserId()).poll();
								tmpRstMap.put("info", "收到消息");
								tmpRstMap.put("message", packageHead.getContent());
								ResultSet rst = DBUtils.getInstance().getUserInfo(packageHead.getSendId());
								String sendUser = null;
								while (rst.next()) {
									sendUser = rst.getString("name");
								}
								tmpRstMap.put("sendUser", sendUser);
								ackContent = JSONObject.fromObject(tmpRstMap).toString();
								packageHead.setMessageType(MessageType.SEND_MESSAGE_ACK);
								packageHead.setContent(ackContent);
								session.write(packageHead);
							}
						}
						break;
					}
				break;
			/*获取当前用户好友列表*/
			case MessageType.FRIEND_LIST:
				sessionMap.put(currentUserName, session);
				List<Friends> friendsList = DBUtils.getInstance().getFriendsList(currentUserName);
				resultMap.put("info", "获取好友列表成功");
				resultMap.put("friendsList", friendsList);
				ackContent = JSONObject.fromObject(resultMap).toString();
				packageHead.setLength(ackContent.length());
				packageHead.setSendId(MessageType.SERVER_SENT);
				packageHead.setRecievedId(MsgSendUtils.getUserId(currentUserName));
				packageHead.setMessageType(MessageType.FRIEND_LIST_ACK);
				packageHead.setContent(JSONObject.fromObject(resultMap).toString());
				session.write(packageHead);
				break;
			/*通过用户名搜索用户*/
			case MessageType.SEARCH_USER:
				sessionMap.put(currentUserName, session);
				String searchUserName = login.getFriendName();
				if (DBUtils.getInstance().isExit(searchUserName)) {
					ResultSet rst = DBUtils.getInstance().getUserInfo(searchUserName);
					User searchUser = new User();
					while(rst.next()) {
						searchUser.setUserId(rst.getInt("id"));
						searchUser.setName(rst.getString("name"));
						searchUser.setIsOnline(rst.getInt("is_online"));
						break;
					}
					resultMap.put("info", "查找" + searchUserName + "用户成功！");
					resultMap.put("user", searchUser);
				} else {
					resultMap.put("info", searchUserName + "查找不存在！");
				}
				ackContent = JSONObject.fromObject(resultMap).toString();
				packageHead.setLength(ackContent.length());
				packageHead.setSendId(MessageType.SERVER_SENT);
				packageHead.setRecievedId(1);
				packageHead.setMessageType(MessageType.SEARCH_USER_ACK);
				packageHead.setContent(ackContent);
				session.write(packageHead);
				break;
			/*添加好友,汉字有问题*/
			case MessageType.ADD_FRIEND:
				sessionMap.put(currentUserName, session);
				String friendName = login.getFriendName();
				if (!DBUtils.getInstance().isExit(friendName)) {
					resultMap.put("info", "用户不存在，添加好友失败");
				} else if (DBUtils.getInstance().isAlreadyInBlackList(currentUserName, friendName)
						|| DBUtils.getInstance().isAlreadyInBlackList(friendName, currentUserName)) {
					resultMap.put("info", "添加好友失败, 已在黑名单中");
				} else if (friendName.equals(currentUserName)) {
					resultMap.put("info", "无法添加自己为好友");
				} else if (DBUtils.getInstance().isAlreadyFriend(currentUserName, friendName)) {
					resultMap.put("info", "已经为好友");
				} else if (DBUtils.getInstance().addFriend(currentUserName, friendName) == -1){
					resultMap.put("info", "添加好友失败，异常");
				} else {
					Friends friend = new Friends();
					ResultSet rst = DBUtils.getInstance().getUserInfo(friendName);
					while(rst.next()) {
						friend.setFriendId(rst.getInt("id"));
						friend.setFriendName(friendName);
						friend.setIsOnline(rst.getInt("is_online"));
						break;
					}
					resultMap.put("info", "添加好友成功");
					resultMap.put("friend", friend);
				}
				ackContent = JSONObject.fromObject(resultMap).toString();
				packageHead.setLength(ackContent.length());
				packageHead.setSendId(MessageType.SERVER_SENT);
				packageHead.setRecievedId(1);
				packageHead.setMessageType(MessageType.ADD_FRIEND_ACK);
				packageHead.setContent(ackContent);
				session.write(packageHead);
				break;
			/*将好友加入黑名单*/
			case MessageType.ADD_TO_BLACKLIST:
				sessionMap.put(currentUserName, session);
				String blockedFriend = login.getFriendName();
				if (!DBUtils.getInstance().isExit(blockedFriend)
						|| DBUtils.getInstance().isAlreadyInBlackList(currentUserName, blockedFriend)
						|| DBUtils.getInstance().insertToBlackList(currentUserName, blockedFriend) == -1) {
					resultMap.put("info", "将"+ blockedFriend + "加入黑名单失败");
				} else {
					resultMap.put("info", "将"+ blockedFriend + "加入黑名单成功");
				}
				packageHead.setLength(JSONObject.fromObject(resultMap).toString().length());
				packageHead.setSendId(MessageType.SERVER_SENT);
				packageHead.setRecievedId(MsgSendUtils.getUserId(currentUserName));
				packageHead.setMessageType(MessageType.ADD_TO_BLACKLIST_ACK);
				packageHead.setContent(JSONObject.fromObject(resultMap).toString());
				session.write(packageHead);
				break;
			/*将用户移除黑名单但不是好友*/
			case MessageType.REMOVE_FROM_BLACKLIST:
				sessionMap.put(currentUserName, session);
				String unblockedFriend = login.getFriendName();
				if (!DBUtils.getInstance().isExit(unblockedFriend)
						|| DBUtils.getInstance().removeFromBlackList(currentUserName, unblockedFriend) == -1) {
					resultMap.put("info", "从黑名单移除"+ unblockedFriend + "失败");
				} else {
					resultMap.put("info", "从黑名单移除"+ unblockedFriend + "成功");
				}
				packageHead.setLength(JSONObject.fromObject(resultMap).toString().length());
				packageHead.setSendId(MessageType.SERVER_SENT);
				packageHead.setRecievedId(MsgSendUtils.getUserId(currentUserName));
				packageHead.setMessageType(MessageType.REMOVE_FROM_BLACKLIST_ACK);
				packageHead.setContent(JSONObject.fromObject(resultMap).toString());
				session.write(packageHead);
				break;
			/*删除好友*/
			case MessageType.DEL_FRIEND:
				sessionMap.put(currentUserName, session);
				String friendDelName = login.getFriendName();
				if (!DBUtils.getInstance().isExit(friendDelName)
						|| DBUtils.getInstance().delFriend(currentUserName, friendDelName) == -1) {
					resultMap.put("info", currentUserName + "删除用户" + friendDelName + "失败");
				} else {
					resultMap.put("info", currentUserName + "删除好友" + friendDelName + "成功");
				}
				packageHead.setLength(JSONObject.fromObject(resultMap).toString().length());
				packageHead.setSendId(MessageType.SERVER_SENT);
				packageHead.setRecievedId(MsgSendUtils.getUserId(currentUserName));
				packageHead.setMessageType(MessageType.DEL_FRIEND_ACK);
				packageHead.setContent(JSONObject.fromObject(resultMap).toString());
				session.write(packageHead);
				break;
			/*好友分组*/
			case MessageType.ADD_FRIEND_GROUP:
				sessionMap.put(currentUserName, session);
				friendName = login.getFriendName();
				String groupName = login.getGroupName();
				if (StringUtils.isEmpty(groupName)) {
					resultMap.put("info", "分组名不能为空！");
				}else if (DBUtils.getInstance().friendGroupUpdate(currentUserName, friendName, groupName) != -1) {
					resultMap.put("info", "添加分组成功！");
					resultMap.put("friendName", friendName);
					resultMap.put("groupName", groupName);
				} else {
					resultMap.put("info", "添加分组失败！");
				}
				packageHead.setSendId(MessageType.SERVER_SENT);
				packageHead.setRecievedId(MsgSendUtils.getUserId(currentUserName));
				packageHead.setMessageType(MessageType.ADD_FRIEND_GROUP_ACK);
				packageHead.setContent(JSONObject.fromObject(resultMap).toString());
				session.write(packageHead);
				break;
			/*发送消息*/
			case MessageType.SEND_MSG:
				/*判断用户是否已被封禁发言*/
				sessionMap.put(currentUserName, session);
				if (bannedPostMap.containsKey(currentUserName) && bannedPostMap.get(currentUserName)) {
					packageHead.setContent("您已经被禁言！");
					session.write(packageHead);
					break;
				}
				sessionMap.put(currentUserName, session);
				IoSession iso = sessionMap.get(login.getFriendName());
				int currentUserId = MsgSendUtils.getUserId(currentUserName);
				int friendId = MsgSendUtils.getUserId(login.getFriendName());
				if (DBUtils.getInstance().isAlreadyInBlackList(currentUserName, login.getFriendName())
					|| DBUtils.getInstance().isAlreadyInBlackList(login.getFriendName(), currentUserName)) {
					resultMap.put("info", "发送消息失败, 已在黑名单中");
					packageHead.setRecievedId(MessageType.SEND_MESSAGE_ACK);
					ackContent = JSONObject.fromObject(resultMap).toString();
					packageHead.setMessageType(MessageType.SEND_MESSAGE_ACK);
					packageHead.setContent(ackContent);
					session.write(packageHead);
				}else if (iso == null) {/*如果好友未登录，则消息写入缓存map中*/
					packageHead.setSendId(currentUserId);
					packageHead.setRecievedId(friendId);
					packageHead.setContent(login.getMessage());
					MsgSendUtils.offlineMsgSend(packageHead, msgCache, friendId);
				} else {/*如果好友已登录则直接发送消息*/
					packageHead.setRecievedId(MessageType.SEND_MESSAGE_ACK);
					resultMap.put("info", "收到消息");
					resultMap.put("message", login.getMessage());
					resultMap.put("sendUser", currentUserName);
					ackContent = JSONObject.fromObject(resultMap).toString();
					packageHead.setMessageType(MessageType.SEND_MESSAGE_ACK);
					packageHead.setContent(ackContent);
					iso.write(packageHead);
				}
				break;
			/*聊天室*/
			case MessageType.CHATROOM_SEND:
				sessionMap.put(currentUserName, session);
				/*判断用户是否已被封禁发言*/
				if (bannedPostMap.containsKey(currentUserName)
						&& bannedPostMap.get(currentUserName)
						||blackListMap.containsKey(currentUserName)
						&& blackListMap.get(currentUserName)) {
					packageHead.setContent("您已经被禁言！");
					session.write(packageHead);
					break;
				}
				if (!chatroom.containsKey("chatRoom")) {
					List<IoSession> sessionList = new ArrayList<IoSession>();
					Collection<IoSession> onlineSessions = session.getService().getManagedSessions().values();
					sessionList.addAll(onlineSessions);
					chatroom.put("chatRoom", sessionList);
				} else {
					chatroom.get("chatRoom").add(session);
				}
				packageHead.setMessageType(MessageType.CHATROOM_SEND_ACK);
				packageHead.setSendId(MsgSendUtils.getUserId(currentUserName));
				packageHead.setContent("【聊天室】"+ currentUserName + ": " + login.getMessage());
				for (IoSession ios : chatroom.get("chatRoom")) {
//					packageHead.setRecievedId(MsgSendUtils.getUserId(ios.getAttribute("name").toString()));
					ios.write(packageHead);
				}
				break;
			/*管理员群发消息*/
			case MessageType.BATCH_SEND:
				sessionMap.put(currentUserName, session);
				packageHead.setSendId(MsgSendUtils.getUserId(currentUserName));
				packageHead.setMessageType(MessageType.SEND_MESSAGE_ACK);
				if (!DBUtils.getInstance().isAdmin(currentUserName)) {
					packageHead.setSendId(MessageType.SERVER_SENT);
					packageHead.setRecievedId(MsgSendUtils.getUserId(currentUserName));
					packageHead.setContent("操作失败，没有管理员权限！");
					session.write(packageHead);
				} else {
					packageHead.setSendId(MessageType.BRODCAST_SENT);
					packageHead.setContent("群发消息：" + login.getMessage() + MsgSendUtils.getTime());
					Collection<IoSession> sessions = session.getService().getManagedSessions().values();
					for (IoSession is : sessions) {
						if (is == session) {
							break;
						}
						is.write(packageHead);
					}
					/*未登录用户接收离线群发消息*/
					MsgSendUtils.offlineMsgSendToAll(packageHead, sessionMap, msgCache);
				}
				break;
			/*封禁用户登录*/
			case MessageType.ADMIN_BLOCK_USER_LOGIN:
				sessionMap.put(currentUserName, session);
				packageHead.setSendId(MsgSendUtils.getUserId(currentUserName));
				packageHead.setMessageType(MessageType.ADMIN_BLOCK_USER_LOGIN_ACK);
				packageHead.setRecievedId(MessageType.SERVER_REV);
				if (!DBUtils.getInstance().isAdmin(currentUserName)) {
					packageHead.setContent("操作失败，没有管理员权限！");
				} else {
					String blockedUserName = login.getFriendName();
					blackListMap.put(blockedUserName, true);
					IoSession ios = sessionMap.get(blockedUserName);
					if (ios != null) {
						packageHead.setContent("已被管理员封禁！");
						ios.write(packageHead);
						ios.closeNow();
						sessionMap.remove(login.getFriendName(), ios);
					}
					packageHead.setContent("封禁" + blockedUserName + "成功！");
					session.write(packageHead);
				}
				break;
			/*封禁用户发言*/
			case MessageType.ADMIN_BANNED_USER_POST:
				sessionMap.put(currentUserName, session);
				packageHead.setSendId(MsgSendUtils.getUserId(currentUserName));
				packageHead.setMessageType(MessageType.ADMIN_BANNED_USER_POST_ACK);
				packageHead.setRecievedId(MessageType.SERVER_REV);
				if (!DBUtils.getInstance().isAdmin(currentUserName)) {
					packageHead.setContent("操作失败，没有管理员权限！");
				} else {
					String bannedUserName = login.getFriendName();
					IoSession ios = sessionMap.get(bannedUserName);
					if (ios != null) {
						bannedPostMap.put(bannedUserName, true);
						packageHead.setContent("您已被管理员禁言！");
						packageHead.setRecievedId(MsgSendUtils.getUserId(bannedUserName));
						ios.write(packageHead);
					}
				}
				break;
		}
		logger.info("接受消息成功");
	}

	public void messageSent(org.apache.mina.core.session.IoSession session, java.lang.Object message) throws java.lang.Exception { /* compiled code */ }

	public void inputClosed(org.apache.mina.core.session.IoSession session) throws java.lang.Exception { /* compiled code */ }

}

