package model;

import java.io.Serializable;

/**
 * 16-12-20 下午4:56
 * by zhenxiong.zhao
 */
public class User implements Serializable
{
	private static final long serialVersionUID = 1L;
	private int userId;
	private String name;
//	private String userNum;
	private String password;
	private int isOnline;


	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getIsOnline()
	{
		return isOnline;
	}

	public void setIsOnline(int isOnline)
	{
		this.isOnline = isOnline;
	}

	public int getUserId()
	{
		return userId;
	}

	public void setUserId(int userId)
	{
		this.userId = userId;
	}
}
