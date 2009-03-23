package tau.heroes;

import java.io.Serializable;
import java.util.Vector;

public class GameState implements Serializable
{
	private static final long serialVersionUID = 1L;
	private Vector<Player> players;
	private Vector<Hero> heroes;
	private Vector<Castle> castles;
	private Vector<Resource> resources;
	private Board theBoard;
	
	
	public GameState(Vector<Player> players, Vector<Hero> heroes, Vector<Castle> castles, Vector<Resource> resources, Board theBoard)
	{
		this.players = players;
		this.heroes = heroes;
		this.castles = castles;
		this.resources = resources;
		this.theBoard = theBoard;		
	}


	public Vector<Player> getPlayers() 
	{
		return players;
	}
	
	public Vector<Hero> getHeroes() 
	{
		return heroes;
	}
	public Vector<Castle> getCastles() 
	{
		return castles;
	}
	public Vector<Resource> getResources() 
	{
		return resources;
	}
	
	public Board getBoard()
	{
		return theBoard;
	}
}