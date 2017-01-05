package protocol;

import model.PackageHead;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.nio.ByteOrder;
import java.nio.charset.Charset;

/**
 * 17-1-4 下午7:42
 * by ruohan.pan
 */
public class ChatServerEncode extends ProtocolEncoderAdapter
{
	private Charset charset = null;

	public ChatServerEncode(Charset charset) {
		this.charset = charset;
	}

	@Override
	public void encode(IoSession session, Object message,
					   ProtocolEncoderOutput out) throws Exception {
		final int packHeadLength = 16;
		if (message instanceof PackageHead) {
			PackageHead msg = (PackageHead) message;
			IoBuffer buffer = IoBuffer.allocate(msg.getLength());
			buffer.order(ByteOrder.LITTLE_ENDIAN).setAutoExpand(true);
			buffer.putInt(msg.getLength());
			buffer.putInt(msg.getSendId());
			buffer.putInt(msg.getRecievedId());
			buffer.putInt(msg.getMessageType());
			if (msg.getLength() > packHeadLength) {
				buffer.putString(msg.getContent(), charset.newEncoder());
			}
			buffer.flip();
			out.write(buffer);
			out.flush();
			buffer.free();
		}
	}
}