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
public class ClientMultiThreadTest
{
	public static volatile int count;

	public static class ClientTest implements Runnable {

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
				Thread.sleep(3000);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}

			/*互发消息*//*
			user.setFriendName("Client-" + (count + 1));
			user.setMessage("HELLO~~~~FROM" + user.getUserName() + "	TIME: " + MsgSendUtils.getTime());
			message = JSONObject.fromObject(user).toString();
			msg = new PackageHead(1, 0, MessageType.SEND_MSG, message);
			ioSession.write(msg);*/

			/*聊天室消息*/
			user.setMessage("我是：" + user.getUserName() + "  发布时间：" + MsgSendUtils.getTime());
			message = JSONObject.fromObject(user).toString();
			msg = new PackageHead(1, 0, MessageType.CHATROOM_SEND, message);
			/*for (int i = 0; i < 3; i++) {
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
			ioSession.write(msg);
			future.getSession().getCloseFuture().awaitUninterruptibly();
			connector.dispose();

		}
	}

	public static void main (String[] args) throws InterruptedException
	{
		for (int i = 0; i < 3; i++) {
			Thread thread = new Thread(new ClientTest());
			thread.start();
			count++;
			Thread.sleep(10000);
		}
	}

}
