package main;

import Utils.MsgSendUtils;
import clientHandler.ChatClientHandler;
import model.Login;
import model.MessageType;
import model.PackageHead;
import net.sf.json.JSONObject;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import protocol.MessageProtocolCodecFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * 16-12-29 下午8:51
 * by ruohan.pan
 */
public class ClientFriendsFuncTest
{
	public static  volatile int count;

	public static class FriendsFuncTest implements Runnable {

		@Override
		public void run()
		{
			NioSocketConnector connector = new NioSocketConnector();
			SocketAddress address = new InetSocketAddress("localhost",8080);
			DefaultIoFilterChainBuilder chain = connector.getFilterChain();
			chain.addLast("logger", new LoggingFilter());
			chain.addLast("mycodec",new ProtocolCodecFilter(new MessageProtocolCodecFactory()));
			connector.setHandler(new ChatClientHandler());
			ConnectFuture future = connector.connect(address);
			future.awaitUninterruptibly();
			Login user = new Login();
			user.setUserName("Client-" + count);
			user.setPwd("12345678");
			String message = JSONObject.fromObject(user).toString();
			PackageHead msg = new PackageHead(1, 0 , MessageType.LOGIN_VERIFY, message);
			IoSession ioSession = future.getSession();
			/*登陆*/
			ioSession.write(msg);
			try
			{
				Thread.sleep(5000);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			/*添加好友和分组*/
			for (int i = 1; i <= count; i++) {
				for (int j = i + 1; j <= count; j++) {
					/*添加好友*/
					user.setUserName("Client-" + i);
					user.setFriendName("Client-" + j);
					user.setMessage("Msg from " + user.getUserName() + " to " + user.getFriendName() + " " + MsgSendUtils.getTime());
					user.setGroupName("Friends");
					message = JSONObject.fromObject(user).toString();
					msg.setMessageType(MessageType.ADD_FRIEND);
					msg.setSendId(MsgSendUtils.getUserId(user.getUserName()));
					msg.setRecievedId(MessageType.SERVER_REV);
					msg.setContent(message);
					ioSession.write(msg);
					msg.setMessageType(MessageType.ADD_FRIEND_GROUP);
					ioSession.write(msg);
					try
					{
						Thread.sleep(5000);
					}
					catch(InterruptedException e)
					{
						e.printStackTrace();
					}
					/*user.setGroupName(null);
					msg.setContent(JSONObject.fromObject(user).toString());
					ioSession.write(msg);*/
				}
			}

			/*查看好友列表*/
			for (int i = 1; i <= count; i++) {
				msg.setMessageType(MessageType.FRIEND_LIST);
				ioSession.write(msg);
			}
			try
			{
				Thread.sleep(5000);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}

			/*搜索存在用户*/
			user.setFriendName("Client-1000");
			msg.setMessageType(MessageType.SEARCH_USER);
			msg.setSendId(MsgSendUtils.getUserId(user.getUserName()));
			msg.setRecievedId(MessageType.SERVER_REV);
			msg.setContent(JSONObject.fromObject(user).toString());
			ioSession.write(msg);
			try
			{
				Thread.sleep(5000);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			/*搜索不存在用户*/
			user.setFriendName("sss");
			msg.setMessageType(MessageType.SEARCH_USER);
			msg.setSendId(MsgSendUtils.getUserId(user.getUserName()));
			msg.setRecievedId(MessageType.SERVER_REV);
			msg.setContent(JSONObject.fromObject(user).toString());
			ioSession.write(msg);
			try
			{
				Thread.sleep(5000);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			/*删除好友*/
			/*for (int i = 1; i <= count; i++) {
				for (int j = i + 1; j <= count; j++) {
					*//*添加好友*//*
					user.setUserName("Client-" + i);
					user.setFriendName("Client-" + j);
					msg.setContent(JSONObject.fromObject(user).toString());
					msg.setMessageType(MessageType.DEL_FRIEND);
					ioSession.write(msg);
				}
			}
			try
			{
				Thread.sleep(5000);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}*/
			future.getSession().getCloseFuture().awaitUninterruptibly();
			connector.dispose();
		}
	}

	public static void main (String[] args) throws InterruptedException
	{
		for (int i = 0; i < 3; i++) {
			Thread thread = new Thread(new FriendsFuncTest());
			thread.start();
			count++;
			Thread.sleep(10000);
		}
	}

}
