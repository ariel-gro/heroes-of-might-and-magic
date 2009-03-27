/**
 * 
 */
package tau.heroes.test;


import java.util.Vector;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tau.heroes.Board;
import tau.heroes.Castle;
import tau.heroes.GameController;
import tau.heroes.GameState;
import tau.heroes.Hero;
import tau.heroes.Player;
import tau.heroes.Resource;
import tau.heroes.ResourceType;

/**
 * @author Amir
 *
 */
public class GameControllerTest extends TestCase
{

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testSaveAndLoad()
	{
		String[] userInput = {"player a", "player b"};
		Vector<Player> players = new Vector<Player>();
		Vector<Hero> heroes = new Vector<Hero>();
		Vector<Castle> castles = new Vector<Castle>();
		Vector<Resource> resources = new Vector<Resource>();
		int numOfPlayers = 2;

		for (int i = 0 ; i < numOfPlayers ; i++)
		{
			players.add(new Player(userInput[0]));
		}

		int boardSize = 10;
		Board theBoard = new Board(boardSize);

		for (int i = 0; i < numOfPlayers; i++)
		{
			int randomX = (int) (Math.random() * (boardSize - 1));
			int randomY = (int) (Math.random() * (boardSize - 1));
			Hero h = new Hero(players.get(i), theBoard, randomX, randomY);
			heroes.add(h);
			castles.add(new Castle(players.get(i), theBoard, randomX, randomY));
			players.get(i).setHero(heroes.get(i));
		}

		for (int i = 0 ; i < numOfPlayers ; i++)
		{
			for (ResourceType rt : ResourceType.values())
			{
				int X = (int) (Math.random() * (boardSize - 1));
				int Y = (int) (Math.random() * (boardSize - 1));

				if (theBoard.getBoardState(X, Y).getIsEmpty())
					resources.add(new Resource(rt, theBoard, X, Y));
			}
		}

		//Start your moves:
		Hero hero0 = players.get(0).getHero();
		Hero hero1 = players.get(1).getHero();
		int x0 = hero0.getXPos();
		int y0 = hero0.getYPos();
		int x1 = hero1.getXPos();
		int y1 = hero1.getYPos();
		
		GameController gameController = new GameController();
		gameController.getGameState().setPlayers(players);
		gameController.getGameState().setHeroes(heroes);
		gameController.getGameState().setCastles(castles);
		gameController.getGameState().getResources();
		gameController.getGameState().setBoard(theBoard);
		gameController.saveGame("testFile1");
		
		hero1.moveTo(x0+1, y0+1, theBoard);
		
		gameController.loadGame("testFile1");
		GameState gameState = gameController.getGameState();
		players = gameState.getPlayers();
		heroes = gameState.getHeroes();
		castles = gameState.getCastles();
		resources = gameState.getResources();
		theBoard = gameState.getBoard();
		numOfPlayers = players.size();
		boardSize = theBoard.getSize();
		Hero newHero0 = players.get(0).getHero();
		Hero newHero1 = players.get(1).getHero();
		
		assertEquals(x0, newHero0.getXPos());
		assertEquals(y0, newHero0.getYPos());
		assertEquals(x1, newHero1.getXPos());
		assertEquals(y1, newHero1.getYPos());
	}
}
