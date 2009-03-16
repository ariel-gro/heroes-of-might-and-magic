/**
 * 
 */
package tau.heroes.test;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tau.heroes.*;

/**
 * @author yuval eitan
 *
 */
public class CastleTest extends TestCase {
	Board theBoard;
	Player player1;
	Hero hero1;
	Castle castle1;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		theBoard = new Board(10);
		player1 = new Player("Player1");
		hero1 = new Hero(player1, theBoard, 4, 4);
		castle1 = new Castle(player1, theBoard, 6, 6);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test that setup is correct
	 */
	@Test
	public void testSetup() {
		assertEquals(4, hero1.getXPos());
		assertEquals(4, hero1.getYPos());
		assertEquals(hero1, theBoard.getBoardState(4, 4).getHero());
		assertEquals(6, castle1.getXPos());
		assertEquals(6, castle1.getYPos());
		assertEquals(castle1, theBoard.getBoardState(6, 6).getCastle());
	}
	
	/**
	 * Test method for {@link tau.heroes.Castle#enterHero(tau.heroes.Hero)}.
	 */
	@Test
	public void testEnterHero() {
		Boolean result = hero1.moveTo(6, 6, theBoard);
		assertTrue(result);
		assertEquals(6, hero1.getXPos());
		assertEquals(6, hero1.getYPos());
		assertEquals(hero1, theBoard.getBoardState(6, 6).getHero());
		assertEquals(6, castle1.getXPos());
		assertEquals(6, castle1.getYPos());
		assertEquals(castle1, theBoard.getBoardState(6, 6).getCastle());
	}

}
