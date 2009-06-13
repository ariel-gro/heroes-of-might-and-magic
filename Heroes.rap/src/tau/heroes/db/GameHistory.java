package tau.heroes.db;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

public class GameHistory implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Date gameDate;
	private int gameScore;
	private List<String> opponentPlayersNames;
	
	public GameHistory()
	{
		this.gameDate = null;
		this.gameScore = 0;
		this.opponentPlayersNames = null;
	}
	
	public Date getGameDate()
	{
		return gameDate;
	}
	
	public void setGameDate(Date gameDate)
	{
		this.gameDate = gameDate;
	}
	
	public int getGameScore()
	{
		return gameScore;
	}
	
	public void setGameScore(int gameScore)
	{
		this.gameScore = gameScore;
	}
	
	public List<String> getOpponentPlayersNames()
	{
		return opponentPlayersNames;
	}
	
	public void setOpponentPlayersNames(List<String> opponentPlayersNames)
	{
		this.opponentPlayersNames = opponentPlayersNames;
	}
}
