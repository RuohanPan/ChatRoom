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
 * 16-12-30 下午6:52
 * by ruohan.pan
 */
public class KeepSendingMsgTest2
{
	public static void main(String[] args) throws InterruptedException
	{

		//	创建客户端连接器 基于tcp/ip
		NioSocketConnector connector = new NioSocketConnector();

		//	连接的地址和端口
		SocketAddress address = new InetSocketAddress("localhost",8080);

		//	获取过滤器链
		DefaultIoFilterChainBuilder chain = connector.getFilterChain();

		//	配置日志过滤器和自定义编解码器
		chain.addLast("logger", new LoggingFilter());
		chain.addLast("mycodec",new ProtocolCodecFilter(new MessageProtocolCodecFactory()));

		//	添加处理器
		connector.setHandler(new ChatClientHandler());

		//　连接到服务器　
		ConnectFuture future = connector.connect(address);

		//	等待连接创建完成
		future.awaitUninterruptibly();

		//	会话创建后发送消息到服务器
		//		PackageHead msg = new PackageHead(1, 0, 0, "{\"userName\":\"cesc\", \"pwd\":\"12345\"}");
		//		PackageHead msg = new PackageHead(1, 0, 16, "{\"userName\":\"cesc\", \"pwd\":\"12345\", \"friendName\":\"yang\", \"message\":\"我是yang\"}");
		//		PackageHead msg = new PackageHead(1, 0, 4,"{}");
		//		PackageHead msg = new PackageHead(1, 0, 20, "{\"userName\":\"yang\", \"pwd\":\"123456\",\"message\":\"我就看看\"}");
		IoSession ioSession = future.getSession();
		Login user = new Login();
		user.setUserName("Client-3");
		user.setFriendName("Client-1");
		user.setPwd("12345678");
		user.setMessage("cacaca消息来自" + user.getUserName());
		PackageHead msg = new PackageHead();
		msg.setSendId(MsgSendUtils.getUserId(user.getUserName()));
		msg.setRecievedId(MsgSendUtils.getUserId(user.getFriendName()));
		msg.setContent(JSONObject.fromObject(user).toString());
		msg.setMessageType(MessageType.LOGIN_VERIFY);
		ioSession.write(msg);
		Thread.sleep(5000);
		msg.setMessageType(MessageType.SEND_MSG);

		//		PackageHead msg = new PackageHead(1, 0, 16, "{\"userName\":\"Client-1\", \"friendName\":\"Client-3\", \"pwd\":\"12345678\",\"message\":\"我就发个消息试试\"}");
/*		Scanner scanner = new Scanner(System.in);
		System.out.println("SenderId: ");
		msg.setSendId(scanner.nextInt());
		System.out.println("RecieverId: ");
		msg.setRecievedId(scanner.nextInt());
		System.out.println("MessageType: ");
		msg.setMessageType(scanner.nextInt());
		System.out.println("消息: ");
		msg.setContent(scanner.next());*/


		//		ioSession.write(msg);
		/*for (int i = 0 ; i < 100; i++) {
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
*/
		while (true) {
			ioSession.write(msg);

			Thread.sleep(1000);
		}
		/*future.getSession().getCloseFuture().awaitUninterruptibly(3000);

		//	关闭连接
		connector.dispose();*/
	}

}
