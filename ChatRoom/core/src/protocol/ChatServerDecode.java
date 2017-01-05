package protocol;

import model.PackageHead;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * 17-1-4 下午7:41
 * by ruohan.pan
 */
public class ChatServerDecode extends CumulativeProtocolDecoder
{

	private final AttributeKey CONTEXT = new AttributeKey(getClass(), "context");
	private final Charset charset;
	private int maxPackLength = 1024;
	public ChatServerDecode(Charset charset) {
		this.charset = charset;
	}

	private Context getContext(IoSession session) {
		Context ctx;
		ctx = (Context) session.getAttribute(CONTEXT);
		if (ctx == null) {
			ctx = new Context(charset);
			session.setAttribute(CONTEXT, ctx);
		}
		return ctx;
	}


	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception
	{
		final int packHeadLength = 16;
		in.order(ByteOrder.LITTLE_ENDIAN);
		Context ctx = getContext(session);
		// 先把当前buffer中的数据追加到Context的buffer当中
		ctx.append(in);
		// 把position指向0位置，把limit指向原来的position位置
		IoBuffer buf = ctx.getBuffer();
		buf.flip();
		// 然后按数据包的协议进行读取
		if (buf.remaining() >= packHeadLength) {
			buf.mark();
			// 读取消息头部分
			PackageHead msg = new PackageHead();
			int length = buf.getInt();
			msg.setLength(length);
			msg.setSendId(buf.getInt());
			msg.setRecievedId(buf.getInt());
			msg.setMessageType(buf.getInt());
			int bodyLen = length - packHeadLength;
			// 读取正常的消息包，并写入输出流中，以便IoHandler进行处理
			if (bodyLen > 0 && buf.remaining() >= bodyLen) {
				msg.setContent(buf.getString(bodyLen, charset.newDecoder()));
			} else {
				buf.clear();
			}
			out.write(msg);
		}
		if (buf.hasRemaining()) {
			return true;
		}
		buf.clear();
		return false;
	}

	@Override
	public void finishDecode(IoSession session, ProtocolDecoderOutput out)
			throws Exception {

	}

	@Override
	public void dispose(IoSession session) throws Exception {
		Context ctx = (Context) session.getAttribute(CONTEXT);
		if (ctx != null) {
			session.removeAttribute(CONTEXT);
		}

	}

	public class Context
	{
		private final CharsetDecoder decoder;
		private IoBuffer buf;

		public Context(Charset charset) {
			decoder = charset.newDecoder();
			buf = IoBuffer.allocate(1024).setAutoExpand(true);
			buf.order(ByteOrder.LITTLE_ENDIAN);
		}

		public IoBuffer getBuffer() {
			return buf;
		}

		public void append(IoBuffer in) {
			getBuffer().put(in);
		}

	}


}

