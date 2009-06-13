package tau.heroes.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserInfo implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int userID;
	private String username;
	private String password;
	private String email;
	private String nickname;
	private int totalScore;
	private List<GameHistory> gameHistoryList;
	private boolean isGuest = false;

	public UserInfo()
	{
		this.userID = 0;
		this.totalScore = 0;
		this.gameHistoryList = new ArrayList<GameHistory>();
	}

	public UserInfo(String username, String password, String email, String nickname)
	{
		this();

		this.username = username;
		this.password = password;
		this.email = email;
		this.nickname = nickname;
	}

	public int getUserID()
	{
		return userID;
	}

	public void setUserID(int userID)
	{
		this.userID = userID;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getNickname()
	{
		return nickname;
	}

	public void setNickname(String nickname)
	{
		this.nickname = nickname;
	}

	public int getTotalScore()
	{
		return totalScore;
	}

	public void setTotalScore(int totalScore)
	{
		this.totalScore = totalScore;
	}

	public List<GameHistory> getGameHistory()
	{
		return gameHistoryList;
	}

	public boolean addGameHistory(GameHistory gameHistory)
	{
		return (this.gameHistoryList.add(gameHistory));
	}

	public boolean isGuest()
	{
		return isGuest;
	}

	public void setGuest(boolean isGuest)
	{
		this.isGuest = isGuest;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof UserInfo)
			return userID == ((UserInfo) obj).userID;
		else
			return super.equals(obj);
	}
}
