/**
 *
 */
package tau.heroes.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Vector;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tau.heroes.Board;
import tau.heroes.Castle;
import tau.heroes.GameScoreBoard;
import tau.heroes.GameState;
import tau.heroes.Hero;
import tau.heroes.MainModule;
import tau.heroes.Player;
import tau.heroes.Resource;
import tau.heroes.ResourceType;

/**
 * @author Amir
 *
 */
public class MainModuleTest extends TestCase {

	public void testMoveLimits()
	{
		Board board = new Board(20);
		Player player1 = new Player("Liron");
		Hero h1 = new Hero(player1, board, 0, 0);
		player1.setHero(h1);

		//Start your moves:
		assertEquals(player1.getMovesLeft(), 5);
		assertTrue(player1.move(3, 1, board));
		assertEquals(player1.getMovesLeft(), 1);
		assertFalse(player1.move(3, 3, board));
		assertEquals(player1.getMovesLeft(), 1);
		assertTrue(player1.move(3, 2, board));
		assertEquals(player1.getMovesLeft(), 0);
		assertFalse(player1.move(3, 3, board));
		assertEquals(player1.getMovesLeft(), 0);
	}
	
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
		
		MainModule.save("testFile1", players, heroes, castles, resources, theBoard);
		
		hero1.moveTo(x0+1, y0+1, theBoard);
		
		GameState gameState = MainModule.load("testFile1");
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
	
	@Test
	public void testWin() 
	{
		Board theBoard;
		Player player1;
		Player player2;
		Hero hero1;
		Castle castle1;
		Castle castle2;
		
		theBoard = new Board(10);
		player1 = new Player("Jay");
		player2 = new Player("Silent Bob");
		hero1 = new Hero(player1, theBoard, 4, 4);
		castle1 = new Castle(player1, theBoard, 6, 6);
		castle2 = new Castle(player2, theBoard, 4, 6);
		
		GameScoreBoard score = new GameScoreBoard();
		player1.setHero(hero1);
		hero1.player = player1;
		player1.addCastle(castle1);
		player2.addCastle(castle2);
		int armyCount;
		player1.incrementMineQuantity("wood");
		player1.incrementMineQuantity("gold");
		assertEquals(true, player1.isAlive());
		assertEquals(true, player2.isAlive());
		armyCount = player1.getHero().getArmy().getTotalNumberOfUnits();
		
		player1.endTurn();
		player2.endTurn();
		
		hero1.moveTo(4, 6, theBoard);
		assertEquals(player1, castle2.getPlayer());
		assertEquals(true, player1.isAlive());
		assertEquals(false, player2.isAlive());
		assertEquals(null, score.getPlayerAt(0));
	}
		


	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

}
