package tau.heroes.db;

import java.sql.Date;
import java.util.List;

public class GameHistory
{
	private Date gameDate;
	private int gameScore;
	private List<String> opponentPlayersNames;
	
	public GameHistory(Date gameDate, int gameScore, List<String> opponentPlayersNames)
	{
		this.gameDate = gameDate;
		this.gameScore = gameScore;
		this.opponentPlayersNames = opponentPlayersNames;
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
