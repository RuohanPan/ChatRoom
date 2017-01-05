package protocol;

import model.PackageHead;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * 16-12-22 上午10:17
 * by ruohan.pan
 */
public class MsgDecoder extends CumulativeProtocolDecoder
{
//	private final static AttributeKey leftBuffer = new AttributeKey(MsgDecoder.class, "leftByteBuffer");

	@Override
	protected boolean doDecode(IoSession session, IoBuffer buffer, ProtocolDecoderOutput decoderOutput)
			throws Exception {
//		buffer = IoBuffer.allocate(2048).setAutoExpand(true);
		CharsetDecoder de = Charset.forName("utf-8").newDecoder();
		if(buffer.remaining() < 16 ){
			return false;
		}else{
			int length = buffer.getInt();
			PackageHead msg = new PackageHead();
			msg.setSendId(buffer.getInt());
			msg.setRecievedId(buffer.getInt());
			msg.setMessageType(buffer.getInt());
			msg.setContent(buffer.getString(length - 16, de));
			decoderOutput.write(msg);
			return true;
		}
	}
}
