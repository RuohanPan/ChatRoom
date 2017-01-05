package model;

import java.io.Serializable;

/**
 * 16-12-20 下午4:32
 * by ruohan.pan
 */
public class Friends implements Serializable
{
	private static final long serialVersionUID = 1L;

//	private int userId;

	private int friendId;

	private int group;

	private String friendName;

	private String groupName;

	private int isOnline;

/*	public int getUserId()
	{
		return userId;
	}

	public void setUserId(int userId)
	{
		this.userId = userId;
	}*/

	public int getFriendId()
	{
		return friendId;
	}

	public void setFriendId(int friendId)
	{
		this.friendId = friendId;
	}

	public int getGroup()
	{
		return group;
	}

	public void setGroup(int group)
	{
		this.group = group;
	}

	public String getFriendName()
	{
		return friendName;
	}

	public void setFriendName(String friendName)
	{
		this.friendName = friendName;
	}

	public String getGroupName()
	{
		return groupName;
	}

	public void setGroupName(String groupName)
	{
		this.groupName = groupName;
	}

	public int getIsOnline()
	{
		return isOnline;
	}

	public void setIsOnline(int isOnline)
	{
		this.isOnline = isOnline;
	}
}
