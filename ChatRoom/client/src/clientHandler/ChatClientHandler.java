package clientHandler;

import main.ChatClient;
import model.PackageHead;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

//import com.xd.nms.util.ByteAndStr16;

/**
 * 16-12-20 下午2:28
 * by ruohan.pan
 */
public class ChatClientHandler extends IoHandlerAdapter{

	private static final Logger logger = Logger.getLogger(ChatClient.class);

	/**
	 * 	接收消息
	 */
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		System.out.println("客户端接收到的消息：" + (PackageHead)message);
	}

	/**
	 * 	发送消息
	 */
	public void messageSent(IoSession session, Object message) throws Exception {
		System.out.println("客户端发送消息...");
		super.messageSent(session, message);

	}

	/**
	 * 	会话创建
	 */
	public void sessionOpened(IoSession session) throws Exception {

		System.out.println("客户端已经连接到了服务器...");

	}

	/**
	 * 	会话关闭
	 */
	public void sessionClosed(IoSession session) throws Exception {
		logger.info("关闭连接");
		System.out.println("连接关闭...");
		super.sessionClosed(session);
	}

}