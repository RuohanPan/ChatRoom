package jdbc;

import Utils.Base64Utils;
import model.Friends;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 16-12-20 下午4:21
 * by ruohan.pan
 */
public class DBUtils
{
	private static final DBUtils instance = new DBUtils();
	public static DBUtils getInstance() {
		return instance;
	}
	private Connection conn = null;
	/*创建连接*/
	private Connection getConnection() {
		if(conn == null)
		{
			try
			{
				conn=DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/chatroom"
						,"root"
						,"");
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
		return conn;
	}

	public ResultSet executeQuery(String sql)
	{
		try
		{
			Statement statement = getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			ResultSet result = statement.executeQuery(sql);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public int executeUpdate(String sql)
	{
		try
		{
			Statement statement = getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			int result = statement.executeUpdate(sql);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}

	/*获取所有用户*/
	public ResultSet getAllusers() {
		String sql = "select * from user";
		return this.executeQuery(sql);
	}
	/*获取用户信息*/
	public ResultSet getUserInfo(String value) {
		String sql = "select * from user where name = '" + value + "'";
		ResultSet result = this.executeQuery(sql);
		return result;
	}

	/*通过用户id获取信息*/
	public ResultSet getUserInfo(Integer userId) {
		String sql = "select * from user where id = " + userId;
		ResultSet result = this.executeQuery(sql);
		return result;
	}

	/*用户是否存在*/
	public boolean isExit(String value){
		String sql = "select * from user where name = '" + value + "'";
		ResultSet result = this.executeQuery(sql);
		boolean flag = false;
		try {
			while (result.next()) {
				flag = true;
				break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}
	/*是否已是好友*/
	public boolean isAlreadyFriend(String userName, String friendName) {
		String sql1 = "select * from friends where user_id = (select id from user where name = '" + userName +"') " +
				"and friend_id = (select id from user where name = '"+friendName+"')";
		String sql2 = "select * from friends where user_id = (select id from user where name = '" + friendName +"') " +
				"and friend_id = (select id from user where name = '"+userName+"')";
		ResultSet resultSet1 = this.executeQuery(sql1);
		ResultSet resultSet2 = this.executeQuery(sql2);
		boolean flag1 = false;
		boolean flag2 = false;
		try {
			while (resultSet1.next()) {
				flag1 = true;
				break;
			}
			while (resultSet2.next()) {
				flag2 = true;
				break;
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return flag1 || flag2;
	}
	/*添加好友和好友分组*/
	public int addFriend(String userName, String friendName) {
		String sql1 = "insert into friends (user_id, friend_id) values ((select id from user where name = '"+userName+"')," +
				"(select id from user where name = '"+friendName+"'))";
		String sql2 = "insert into friends (user_id, friend_id) values ((select id from user where name = '"+friendName+"')," +
				"(select id from user where name = '"+userName+"'))";
		return this.executeUpdate(sql1) + this.executeUpdate(sql2);
	}
	/*删除好友*/
	public int delFriend(String userName, String friendName) throws SQLException
	{
		String friendsInfo = "select * from friends where user_id = (select id from user where name = '"+userName+"') and " +
				" friend_id = (select id from user where name = '"+friendName+"')";

		String friendDelSql1 = "delete from friends where user_id = (select id from user where name = '"+userName+"') and " +
				" friend_id = (select id from user where name = '"+friendName+"')";

		String friendDelSql2 = "delete from friends where user_id = (select id from user where name = '"+friendName+"') and " +
				" friend_id = (select id from user where name = '"+userName+"')";

		if (!isAlreadyFriend(userName, friendName)) {
			return -1;
		} else return this.executeUpdate(friendDelSql1) + this.executeUpdate(friendDelSql2);
	}
	/*是否已经在黑名单*/
	public boolean isAlreadyInBlackList(String userName, String friendName) throws SQLException
	{
		String sql = "select * from blacklist where user_id = (select id from user where name ='"+userName+"')" +
				"and friend_id = (select id from user where name ='"+friendName+"')";
		ResultSet resultSet = this.executeQuery(sql);
		Boolean flag = false;
		while(resultSet.next()) {
			flag = true;
			break;
		}
		return flag;
	}
	/*将好友加入黑名单*/
	public int insertToBlackList(String userName, String friendName) throws SQLException
	{
		if (!this.isAlreadyFriend(userName, friendName) || this.isAlreadyInBlackList(userName, friendName)) {
			return -1;
		}
		String sql1 = "insert into blacklist (user_id, friend_id) values ((select id from user where name = '"+userName+"')," +
				"(select id from user where name = '"+friendName+"'))";
		return this.executeUpdate(sql1) + this.delFriend(userName, friendName);
	}
	/*移除黑名单*/
	public int removeFromBlackList(String userName, String friendName) throws SQLException
	{
		String sql = "delete from blacklist where user_id = (select id from user where name = '"+userName+"') and " +
				" friend_id = (select id from user where name = '"+friendName+"')";
		if (!this.isAlreadyInBlackList(userName, friendName)) {
			return -1;
		} else {
			return this.executeUpdate(sql);
		}
	}
	/*添加好友分组*/
	public int friendGroupUpdate (String userName, String friendName, String groupName) {
		String insertGroupSql = "update friends set group_name = '" + groupName + "' where user_id = (select id from user where " +
				"name = '"+ userName +"') and friend_id = (select id from user where name ='" + friendName +"')";
		return this.executeUpdate(insertGroupSql);
	}

	/*用户密码是否匹配*/
	public boolean isMatch(String table, String name, String nameValue, String password, String pwdValue) throws Exception
	{
		String pwdDecypt = Base64Utils.decodeStr(pwdValue);
		String sql = "select * from " +  table + " where " + name + " = '" + nameValue + "'" + " and " + password + " = '" + pwdDecypt + "'" ;
		ResultSet result = this.executeQuery(sql);
		boolean flag = false;
		try {
			while (result.next()) {
				flag = true;
				break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}

	/*用户上线或下线*/
	public int setOnOrOff(String value) {
		String queryIsOnlineSql = "select is_online from user where name = '" + value + "' ";
		int online = 0;//初始未登录
		try
		{
			ResultSet rst = this.executeQuery(queryIsOnlineSql);
			while (rst.next()) {
				online = rst.getInt("is_online");
				break;
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		String sql;
		if (online == 1) {
			sql = "update user set is_online=0 where name ='" + value + "'";
		} else {
			sql = "update user set is_online=1 where name ='" + value + "'";
		}
		return this.executeUpdate(sql);
	}

	/*获取用户好友列表*/
	public List<Friends> getFriendsList(String userName) {
		String sql = "select friend_id from friends where user_id = (select id from user where name = '" + userName + "')";
		ResultSet friends = null;
		List<Friends> friendsList = new ArrayList<Friends>();
		try {
			friends = this.executeQuery(sql);
			while (friends.next()) {
				Friends friendInfo = new Friends();
				Integer friendId = friends.getInt("friend_id");
				String friendInfoSql = "select name from user where id = " + friendId;
				String groupInfoSql = "select group_name from friends where user_id = (select id from user where name = '" + userName + "')"
						+ " and friend_id = " + friendId;
				friendInfo.setFriendId(friendId);
				ResultSet rst1 = this.executeQuery(friendInfoSql);
				while (rst1.next()) {
					friendInfo.setFriendName(rst1.getString("name"));
					break;
				}
				ResultSet rst2 = this.executeQuery(groupInfoSql);
				while (rst2.next()) {
					friendInfo.setGroupName(rst2.getString("group_name"));
					break;
				}
//				friendInfo.setGroup(friends.getInt("group_id"));
				friendsList.add(friendInfo);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return friendsList;
	}

	/*添加用户*/
	public int inserNewUser(String userName, String pwd, int isOnline) {
		String sql = "insert into user (name, password, is_online) values ('" +userName+"', '"+pwd+"'," + isOnline+")";
		return this.executeUpdate(sql);
	}
	/*添加好友*/
	public int insertFriend(Integer userId, Integer friendId, String groupName) {
		String sql = "insert into friends (user_id, friend_id, group_name) values (" + userId + ", " + friendId + ",'" + groupName +"')";
		return this.executeUpdate(sql);
	}
	/*是否为管理员*/
	public boolean isAdmin(String userName) {
		String sql = "select is_admin from user where name = '" + userName + "'";
		ResultSet rst = this.executeQuery(sql);
		boolean flag = false;
		try
		{
			if (rst.next()) {
				if (rst.getBoolean("is_admin")) {
					flag = true;
				}
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return flag;
	}

}
