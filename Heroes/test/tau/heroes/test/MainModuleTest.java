/**
 *
 */
package tau.heroes.test;


import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;

import tau.heroes.Board;
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
