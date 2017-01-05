package protocol;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import java.nio.charset.Charset;

/**
 * 16-12-21 上午11:25
 * by ruohan.pan
 */
public class MessageProtocolCodecFactory implements ProtocolCodecFactory
{
	private MsgDecoder decoder;

	private MsgEncoder encoder;

	private static final Charset charset = Charset.forName("UTF-8");

	public MessageProtocolCodecFactory() {
		this.decoder = decoder;
		this.encoder = encoder;
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception
	{
//		return new MessageProtocolEncode(charset);
//		return new MsgEncoder();
		return new ChatServerEncode(charset);
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception
	{
//		return new MessageProtocolDecode(charset);
//		return new MsgDecoder();
		return new ChatServerDecode(charset);
//		return new DecoderTest();
	}
}
