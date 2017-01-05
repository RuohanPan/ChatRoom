package model;

import java.io.Serializable;
import java.nio.charset.Charset;

public class PackageHead implements Serializable
{
	public PackageHead() {

	}

	private static final long serialVersionUID = 1;

	private Integer length;

	private Integer sendId;

	private Integer recievedId;

	private Integer messageType;

	private String content;

	public int getLength() {
		this.length = 4 * 4 + this.content.getBytes(Charset.forName("utf-8")).length;
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public PackageHead(Integer sendId, Integer recievedId, Integer messageType, String content) {
		this.sendId = sendId;
		this.recievedId = recievedId;
		this.messageType = messageType;
		this.content = content;
	}

	public Integer getSendId()
	{
		return sendId;
	}

	public void setSendId(Integer sendId)
	{
		this.sendId = sendId;
	}

	public Integer getRecievedId()
	{
		return recievedId;
	}

	public void setRecievedId(Integer recievedId)
	{
		this.recievedId = recievedId;
	}

	public Integer getMessageType()
	{
		return messageType;
	}

	public void setMessageType(Integer messageType)
	{
		this.messageType = messageType;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	@Override
	public String toString() {
		return "Message [Length = " + this.getLength() + ", sendId = " + sendId + ", receiverId = " + recievedId
				+ ", messageType = " + messageType + ", content = " + content + "]";
	}

}

