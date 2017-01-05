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
 * 16-12-30 下午4:06
 * by ruohan.pan
 */
public class BlackListTest
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
			ioSession.write(msg);
			/*将好友添加到黑名单*/
			/*for (int i = 1; i <= count; i++) {
				user.setFriendName("Client-" + i);
				msg.setMessageType(MessageType.ADD_TO_BLACKLIST);
				msg.setSendId(MsgSendUtils.getUserId(user.getUserName()));
				msg.setRecievedId(MessageType.SERVER_REV);
				msg.setContent(JSONObject.fromObject(user).toString());
				ioSession.write(msg);
			}*/
			user.setFriendName("Client-3");
			msg.setMessageType(MessageType.ADD_TO_BLACKLIST);
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

			/*向黑名单中的用户发消息*/
			user.setMessage("Msg from :" + user.getUserName() + " to :" + user.getFriendName());
			msg.setMessageType(MessageType.SEND_MSG);
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
			/*将用户从黑名单中移除*/
			msg.setMessageType(MessageType.REMOVE_FROM_BLACKLIST);
			ioSession.write(msg);
			try
			{
				Thread.sleep(5000);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			/*再次发消息*/
			user.setMessage("移除黑名单后 Msg from : " + user.getUserName() + "to:" + user.getFriendName());
			msg.setMessageType(MessageType.SEND_MSG);
			msg.setContent(JSONObject.fromObject(user).toString());
			ioSession.write(msg);
			/*加移除的用户为好友*/
			msg.setMessageType(MessageType.ADD_FRIEND);
			ioSession.write(msg);
			future.getSession().getCloseFuture().awaitUninterruptibly();
			connector.dispose();

		}
	}

	public static void main (String[] args) throws InterruptedException
	{
		for (int i = 0; i < 2; i++) {
			Thread thread = new Thread(new FriendsFuncTest());
			thread.start();
			count++;
			Thread.sleep(5000);
		}
	}

}

