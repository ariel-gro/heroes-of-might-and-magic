package tau.heroes;

import java.io.Serializable;
import java.util.Vector;

public class GameState implements Serializable
{
	private static final long serialVersionUID = 1L;
	private int whosTurn = 0;
	private Vector<Player> players;
	private Vector<Hero> heroes;
	private Vector<Castle> castles;
	private Vector<Resource> resources;
	private Board board;
	private final boolean isGUI;
	
	public GameState(boolean isGUI)
	{
		this.isGUI = isGUI;
	}
	
	public int getWhosTurn()
	{
		return this.whosTurn;
	}
	
	public void setWhosTurn(int whosTurn)
	{
		this.whosTurn = whosTurn;
	}
	
	public Vector<Player> getPlayers() 
	{
		return this.players;
	}
	
	public void setPlayers(Vector<Player> players)
	{
		this.players = players;
	}
	
	public Vector<Hero> getHeroes() 
	{
		return this.heroes;
	}
	
	public void setHeroes(Vector<Hero> heroes)
	{
		this.heroes = heroes;
	}
	
	public Vector<Castle> getCastles() 
	{
		return this.castles;
	}
	
	public void setCastles(Vector<Castle> castles)
	{
		this.castles = castles;
	}
	
	public Vector<Resource> getResources() 
	{
		return this.resources;
	}
	
	public void setResources(Vector<Resource> resources)
	{
		this.resources = resources;
	}
	
	public Board getBoard()
	{
		return this.board;
	}
	
	public void setBoard(Board board)
	{
		this.board = board;
	}
	
	public int getNumberOfPlayers()
	{
		return this.players.size();
	}
	
	public boolean isGUI()
	{
		return this.isGUI;
	}
}