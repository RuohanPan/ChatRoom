package model;

import Utils.Base64Utils;

import java.io.Serializable;

/**
 * 16-12-20 下午3:34
 * by ruohan.pan
 */
public class Login implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String userName;

	private String pwd;

	private String friendName;

	private String groupName;

	private String message;

	private String pwdEncypt;


	public String getPwdEncypt() throws Exception
	{
		return Base64Utils.encodeStr(pwd);
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getPwd()
	{
		return pwd;
	}

	public void setPwd(String pwd)
	{
		this.pwd = pwd;
	}


	public String getGroupName()
	{
		return groupName;
	}

	public void setGroupName(String groupName)
	{
		this.groupName = groupName;
	}

	public String getFriendName()
	{
		return friendName;
	}

	public void setFriendName(String friendName)
	{
		this.friendName = friendName;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}
}
