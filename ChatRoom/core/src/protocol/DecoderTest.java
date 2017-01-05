package protocol;

import model.PackageHead;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * 17-1-3 下午2:44
 * by ruohan.pan
 */
public class DecoderTest extends CumulativeProtocolDecoder
{
	private static final int PACK_HEAD_LEN = 16;

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception
	{
		CharsetDecoder de = Charset.forName("utf-8").newDecoder();

		IoBuffer buffer = IoBuffer.allocate(2048).setAutoExpand(true);
		if (in.remaining() > 1) {
			int length = in.getInt(in.position());
//			in.mark();
			if (length < PACK_HEAD_LEN) {
				return false;
			}
			if (length - PACK_HEAD_LEN > in.remaining()) {
				return false;
			}
/*			if (length > 2048) {
				return false;
			}*/
			byte[] bytes = new byte[length];
			in.get(bytes);
			buffer.put(bytes);
			buffer.flip();
			PackageHead msg = new PackageHead();
			msg.setLength(buffer.getInt());
			msg.setSendId(buffer.getInt());
			msg.setRecievedId(buffer.getInt());
			msg.setMessageType(buffer.getInt());
			msg.setContent(buffer.getString(de));
			out.write(msg);
//			buffer.flip();
			return true;
		}
		return false;
	}
}
