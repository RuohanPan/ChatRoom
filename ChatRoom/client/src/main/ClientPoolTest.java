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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientPoolTest {

	public static volatile int count = 0;

	static class MyRunnable implements Runnable {

		@Override
		public void run() {
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
			user.setMessage("From:" + Thread.currentThread().getName() + "   Time:　" + MsgSendUtils.getTime());
			user.setPwd("12345678");
			String message = JSONObject.fromObject(user).toString();
			PackageHead msg = new PackageHead(1, 0 , MessageType.LOGIN_VERIFY, message);
			IoSession ioSession = future.getSession();
			/*用户登陆*/
			ioSession.write(msg);
			try
			{
				Thread.sleep(10000);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			for (int i = 1; i <= count; i++) {
				for (int j = i + 1; j <= count; j++) {
					user.setUserName("Client-" + i);
					user.setFriendName("Client-" + j);
					user.setMessage("msg from:" + user.getUserName() + " to:" + user.getFriendName());
					msg.setMessageType(MessageType.SEND_MSG);
					msg.setSendId(MsgSendUtils.getUserId(user.getUserName()));
					msg.setRecievedId(MsgSendUtils.getUserId(user.getFriendName()));
					msg.setContent(JSONObject.fromObject(user).toString());
					ioSession.write(msg);
					try
					{
						Thread.sleep(1000);
					}
					catch(InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
			/*for (int i = 0; i < count; i++) {
				user.setUserName("Client-" + i);
				user.setMessage("msg from:" + user.getUserName() + " to:" + user.getFriendName());
				msg.setMessageType(MessageType.SEND_MSG);
				msg.setSendId(MsgSendUtils.getUserId(user.getUserName()));
				msg.setRecievedId(MsgSendUtils.getUserId(user.getFriendName()));
				msg.setContent(JSONObject.fromObject(user).toString());
				ioSession.write(msg);
				try
				{
					Thread.sleep(1000);
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			}*/
			future.getSession().getCloseFuture().awaitUninterruptibly();
			connector.dispose();
		}
	}
	public static void main(String[] args) throws InterruptedException
	{
		ExecutorService pool = Executors.newFixedThreadPool(5000);
		for(int i = 0; i < 30; i++) {
			pool.submit(new MyRunnable());
			count++;
			Thread.sleep(100);
		}
		pool.shutdown();
	}
}




