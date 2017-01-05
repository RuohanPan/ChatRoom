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
 * 16-12-29 下午8:22
 * by ruohan.pan
 */
public class AdminTest
{
	public static void main(String[] args) throws InterruptedException
	{

		NioSocketConnector connector = new NioSocketConnector();

		SocketAddress address = new InetSocketAddress("localhost",8080);

		DefaultIoFilterChainBuilder chain = connector.getFilterChain();

		chain.addLast("logger", new LoggingFilter());
		chain.addLast("mycodec",new ProtocolCodecFilter(new MessageProtocolCodecFactory()));
		//	添加处理器
		connector.setHandler(new ChatClientHandler());
		//　连接到服务器　
		ConnectFuture future = connector.connect(address);
		//	等待连接创建完成
		future.awaitUninterruptibly();
		IoSession ioSession = future.getSession();
		Login user = new Login();
		user.setUserName("yang");
		user.setPwd("12345678");
		user.setMessage("我就试试群发消息行不行");
		//群发消息
//		PackageHead msg = new PackageHead(1, 0, MessageType.BATCH_SEND, "{\"userName\":\"yang\", \"pwd\":\"12345678\",\"message\":\"我就试试群发消息行不行\"}");
		PackageHead msg = new PackageHead();
		msg.setMessageType(MessageType.BATCH_SEND);
		msg.setSendId(MsgSendUtils.getUserId(user.getUserName()));
		msg.setRecievedId(MessageType.BRODCAST_SENT);
		msg.setContent(JSONObject.fromObject(user).toString());
		ioSession.write(msg);
		Thread.sleep(5000);

		//禁止用户登陆
		user.setFriendName("Client-2");
		msg.setRecievedId(MsgSendUtils.getUserId("Client-2"));
		msg.setMessageType(MessageType.ADMIN_BLOCK_USER_LOGIN);
		msg.setContent(JSONObject.fromObject(user).toString());
		ioSession.write(msg);
		Thread.sleep(5000);

        //用户禁言
		user.setFriendName("Client-3");
		msg.setRecievedId(MsgSendUtils.getUserId("Client-2"));
		msg.setMessageType(MessageType.ADMIN_BANNED_USER_POST);
		msg.setContent(JSONObject.fromObject(user).toString());
		ioSession.write(msg);
		Thread.sleep(5000);
		future.getSession().getCloseFuture().awaitUninterruptibly();
		//	关闭连接
		connector.dispose();
	}
}
