/**
 *
 */
package tau.heroes.test;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tau.heroes.Board;
import tau.heroes.Castle;
import tau.heroes.GameScoreBoard;
import tau.heroes.Hero;
import tau.heroes.Player;

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

		player1.incrementMineQuantity("wood");
		player1.incrementMineQuantity("gold");
		assertEquals(true, player1.isAlive());
		assertEquals(true, player2.isAlive());
		//int armyCount = player1.getHero().getArmy().getTotalNumberOfUnits();
		
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
