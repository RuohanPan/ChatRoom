package main;

import org.apache.log4j.Logger;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LogLevel;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import protocol.MessageProtocolCodecFactory;
import serverHandler.ChatServerHandler;

import java.net.InetSocketAddress;

/**
 * 16-12-20 下午2:08
 * by ruohan.pan
 */
public class ChatServer{
	private static Logger logger = Logger.getLogger(ChatServer.class);

	public static final int PORT = 8080;
	private static final int IDLETIMEOUT = 30;
	private static final int HEARTBEATRATE = 15;
	public static void main(String[] args) {
		IoAcceptor acceptor = new NioSocketAcceptor();
		/*IoThreadPoolFilter*/
		//添加日志过滤器
		try {
		DefaultIoFilterChainBuilder filterChain = acceptor.getFilterChain();
//		filterChain.addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"),
//				LineDelimiter.WINDOWS.getValue(), LineDelimiter.WINDOWS.getValue())));
		filterChain.addLast("codec", new ProtocolCodecFilter(new MessageProtocolCodecFactory()));
//		filterChain.addLast("executor", new ExecutorFilter(executor));
		LoggingFilter loggingFilter = new LoggingFilter();
		loggingFilter.setMessageReceivedLogLevel(LogLevel.INFO);
		loggingFilter.setMessageSentLogLevel(LogLevel.INFO);
		filterChain.addLast("logger", loggingFilter);
		acceptor.setHandler(new ChatServerHandler());
		acceptor.bind(new InetSocketAddress(PORT));
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 30);
		logger.info("Server启动成功，端口号" + PORT);
		} catch(Exception e) {
			logger.info("Server启动失败" + e);
			e.printStackTrace();
		}
	}
}

