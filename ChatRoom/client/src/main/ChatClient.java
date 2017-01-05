package main;

import clientHandler.ChatClientHandler;
import model.PackageHead;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import protocol.MessageProtocolCodecFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 16-12-22 上午11:48
 * by ruohan.pan
 */
/*
public class ChatClient {

	private static Logger logger = Logger.getLogger(ChatClient.class);

	public static final int PORT = 8080;
*/
/*	public static void main(String[] args) {
		//创建session
		NioSocketConnector connector = new NioSocketConnector();
		DefaultIoFilterChainBuilder chain = new DefaultIoFilterChainBuilder();
		ProtocolCodecFilter filter = new ProtocolCodecFilter(new MessageProtocolCodecFactory());
		chain.addLast("codec", filter);
		connector.setHandler(new ChatClientHandler());
		connector.setConnectTimeoutCheckInterval(30);
		ConnectFuture cf = connector.connect(new InetSocketAddress("localhost", PORT));
		cf.awaitUninterruptibly();
		PackageHead msg = new PackageHead(1, 0, 0, "来自客户端的请求");
		cf.getSession().write(msg);
		cf.getSession().getCloseFuture().awaitUninterruptibly(30000);
		connector.dispose();
	}*//*


	public static void main(String[] args) {

		NioSocketConnector connector = new NioSocketConnector();
		SocketAddress address = new InetSocketAddress("localhost", PORT);
		DefaultIoFilterChainBuilder chainBuilder = connector.getFilterChain();
		chainBuilder.addLast("logger", new LoggingFilter());
		chainBuilder.addLast("codec", new ProtocolCodecFilter(new MessageProtocolCodecFactory()));
		connector.setHandler(new ChatClientHandler());
		ConnectFuture future = connector.connect(address);
		future.awaitUninterruptibly();
		PackageHead msg = new PackageHead(1, 0, 0, "来自客户端的请求");
		future.getSession().write(msg);
		future.getSession().getCloseFuture().awaitUninterruptibly(30000);
		connector.dispose();
	}

}
*/

public class ChatClient {
	public static void main(String[] args) {

		//	创建客户端连接器 基于tcp/ip
//		NioSocketConnector connector = new NioSocketConnector();
		ExecutorService executor = Executors.newFixedThreadPool(5000);

		IoConnector ioConnector = new NioSocketConnector(1000);

		NioSocketConnector connector = new NioSocketConnector(executor, (IoProcessor<NioSession>)ioConnector);
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
//		PackageHead msg = new PackageHead(1, 0, 14, "{\"userName\":\"yang\", \"pwd\":\"12345678\"}");
//		PackageHead msg = new PackageHead(1, 0, 4,"{}");
		PackageHead msg = new PackageHead(1, 0, 0, "{\"userName\":\"yang\", \"pwd\":\"12345678\", \"friendName\":\"Client-20\",\"groupName\":\"Classmates\", \"message\":\"啦啦啦啦我是yang\"}");

/*		Scanner scanner = new Scanner(System.in);
		System.out.println("SenderId: ");
		msg.setSendId(scanner.nextInt());
		System.out.println("RecieverId: ");
		msg.setRecievedId(scanner.nextInt());
		System.out.println("MessageType: ");
		msg.setMessageType(scanner.nextInt());
		System.out.println("消息: ");
		msg.setContent(scanner.next());*/

		IoSession ioSession = future.getSession();

		ioSession.write(msg);

		/*future.getSession().getCloseFuture().awaitUninterruptibly(300000);

		//	关闭连接
		connector.dispose();*/

	}
}

