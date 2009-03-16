/**
 * 
 */
package tau.heroes.test;


import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;

import tau.heroes.Board;
import tau.heroes.CanAct;
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
		h1.moveTo(3, 1, board);
		CanAct.reset();
		assertTrue(CanAct.moveUpdate(0, 0, 3, 1));
		CanAct.reset();
		assertFalse(CanAct.moveUpdate(0, 0, 6, 2));
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
