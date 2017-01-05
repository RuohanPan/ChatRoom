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
import threadPool.ExecutorProcessPool;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * 17-1-3 下午3:53
 * by ruohan.pan
 */
public class ThreadPoolTest {

	private static PackageHead msg = new PackageHead();

	private static NioSocketConnector connector = new NioSocketConnector();

	private static SocketAddress address = new InetSocketAddress("localhost",8080);

	private static int count = 1;

	public static class ClientTest implements Runnable {

//		private String taskName;

		@Override
		public void run()
		{

			ConnectFuture future = connector.connect(address);
			future.awaitUninterruptibly();
			IoSession ioSession = future.getSession();
			ioSession.write(msg);
//			Login user = new Login();
//			user.setUserName("Client-" + count);
//			user.setPwd("12345678");
//			user.setMessage("Msg from: " + user.getUserName());
//			msg.setMessageType(MessageType.BATCH_SEND);
//			msg.setSendId(MsgSendUtils.getUserId(user.getUserName()));
//			for (int i = 0; i < 10; i++) {
//				user.setFriendName("Client-" + i);
//				user.setMessage("Msg from :" + user.getUserName() + " to: " + user.getFriendName() + " time:" + MsgSendUtils.getTime());
//				String message = JSONObject.fromObject(user).toString();
//				msg.setMessageType(MessageType.SEND_MSG);
//				msg.setSendId(MsgSendUtils.getUserId(user.getUserName()));
//				msg.setRecievedId(MsgSendUtils.getUserId(user.getFriendName()));
//				msg.setContent(message);
//				ioSession.write(msg);
//				try
//				{
//					Thread.sleep(100);
//				}
//				catch(InterruptedException e)
//				{
//					e.printStackTrace();
//				}
//			}
		}
	}

	public static void main (String[] args) throws InterruptedException
	{

		DefaultIoFilterChainBuilder chain = connector.getFilterChain();
		chain.addLast("logger", new LoggingFilter());
		chain.addLast("mycodec",new ProtocolCodecFilter(new MessageProtocolCodecFactory()));
		connector.setHandler(new ChatClientHandler());
		ExecutorProcessPool pool = ExecutorProcessPool.getInstance();
		Login user = new Login();
		/*一个connector生成5000个session，分配给10个线程*/
		for (int i = 0; i < 5000; i++) {
//			登陆
			user.setUserName("Client-" + i);
			user.setPwd("12345678");
			String message = JSONObject.fromObject(user).toString();
			msg.setSendId(MsgSendUtils.getUserId(user.getUserName()));
			msg.setRecievedId(MessageType.SERVER_REV);
			msg.setMessageType(MessageType.LOGIN_VERIFY);
			msg.setContent(message);
			pool.execute(new ClientTest());
			count++;
			Thread.sleep(100);
		}

		/*互发消息*/
		for (int j = 0; j < 5000; j++) {
			for (int k = 0; k < 5000; k++) {
				if (j == k) {
					continue;
				} else {
					user.setUserName("Client-" + j);
					user.setFriendName("Client" + k);
					user.setPwd("12345678");
					user.setMessage("Msg from :" + user.getUserName() + " to: " + user.getFriendName() + " time:" + MsgSendUtils.getTime());
					String message = JSONObject.fromObject(user).toString();
					msg.setSendId(MsgSendUtils.getUserId(user.getUserName()));
					msg.setRecievedId(MsgSendUtils.getUserId(user.getFriendName()));
					msg.setMessageType(MessageType.SEND_MSG);
					msg.setContent(message);
					pool.execute(new ClientTest());
				}
					Thread.sleep(100);
			}
		}
	}
}
