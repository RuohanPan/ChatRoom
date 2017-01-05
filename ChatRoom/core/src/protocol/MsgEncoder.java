package protocol;

import model.PackageHead;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 * 16-12-22 上午10:16
 * by ruohan.pan
 */
public class MsgEncoder extends ProtocolEncoderAdapter
{

	private static final int PACKAGE_HEAD = 4;

	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput encoderOutput)
			throws Exception {
		CharsetEncoder ce = Charset.forName("utf-8").newEncoder();
		PackageHead msg = (PackageHead) message;
		//	Mina IoBuffer
		IoBuffer buffer = IoBuffer.allocate(2048).setAutoExpand(true);
//		buffer.order(ByteOrder.LITTLE_ENDIAN).setAutoExpand(true);
		buffer.putInt(msg.getLength());
		buffer.putInt(msg.getSendId());
		buffer.putInt(msg.getRecievedId());
		buffer.putInt(msg.getMessageType());
		buffer.putString(msg.getContent(), ce);
		buffer.flip();
		encoderOutput.write(buffer);
	}

}