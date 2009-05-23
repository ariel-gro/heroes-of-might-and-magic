package tau.heroes.db;

import java.io.Serializable;


public class UserInfo implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String username;
	private String password;
	private String email;
	private String nickname;
	private int totalScore;
	private GameHistory gameHistory;
	private boolean isGuest = false;
	
	public UserInfo()
	{
		this.totalScore = 0;
		this.gameHistory = null;		
	}
	
	public UserInfo(String username, String password, String email, String nickname)
	{
		this();
		
		this.username = username;
		this.password = password;
		this.email = email;
		this.nickname = nickname;
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
	
	public GameHistory getGameHistory()
	{
		return gameHistory;
	}
	
	public void setGameHistory(GameHistory gameHistory)
	{
		this.gameHistory = gameHistory;
	}
	
	public boolean isGuest()
	{
		return isGuest;
	}
	
	public void setGuest(boolean isGuest)
	{
		this.isGuest = isGuest;
	}
}
