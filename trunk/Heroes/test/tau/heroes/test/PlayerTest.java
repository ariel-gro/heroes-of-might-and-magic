/**
 *
 */
package tau.heroes.test;

import tau.heroes.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author ran
 *
 */
public class PlayerTest {

	Player p;
	Hero h;
	Board board;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		p = new Player("Test");
		board = new Board(20);
		h = new Hero(p,board,0,0);

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link tau.heroes.Player#Player(java.lang.String)}.
	 */
	@Test
	public void testPlayer()
	{

	}

	/**
	 * Test method for {@link tau.heroes.Player#setHero(tau.heroes.Hero)}.
	 */
	@Test
	public void testSetHero()
	{
		assertNull(p.getHero());
		p.setHero(h);
		assertNotNull(p.getHero());
		assertEquals(h,p.getHero());
		Hero h1 = new Hero(1,1,null);
		p.setHero(h1);
		//hero with no army is dead...
		assertNull(p.getHero());
	}

	/**
	 * Test method for {@link tau.heroes.Player#getMovesLeft()}.
	 */
	@Test
	public void testGetMovesLeft() {
		assertEquals(0,p.getMovesLeft());
		p.setHero(h);
		assertEquals(5,p.getMovesLeft());
	}

	/**
	 * Test method for {@link tau.heroes.Player#getName()}.
	 */
	@Test
	public void testGetName() {
		assertEquals("Test",p.getName());
	}



	/**
	 * Test method for {@link tau.heroes.Player#isAlive()}.
	 */
	@Test
	public void testIsAlive() {
		//the player has no castle and no heroes - so it dies
		assertEquals(false, p.isAlive());
		
		//now we give the player a dummy hero
		p.setHero(h);
		//for the first six days the hero is alive
		for(int i = 0; i< 6;i++)
			assertEquals(true,p.isAlive());
		//now it should be dead.
		assertEquals(false,p.isAlive());
	}

	/**
	 * Test method for {@link tau.heroes.Player#move(int, int, tau.heroes.Board)}.
	 */
	@Test
	public void testMove1()
	{
		assertFalse(p.move(0, 3, board));
		p.setHero(h);
		assertTrue(p.move(0, 3, board));
		boolean[][] b =  p.getVisibleBoard();
		//radius 1:
		assertTrue(b[0][0]);
		assertTrue(b[0][1]);
		assertTrue(b[0][2]);
		assertTrue(b[0][3]);
		assertTrue(b[0][4]);
		assertTrue(b[1][0]);
		assertTrue(b[1][1]);
		assertTrue(b[1][2]);
		assertTrue(b[1][3]);
		assertTrue(b[1][4]);
		//radius 2:
		assertFalse(b[2][0]);
		assertFalse(b[2][1]);
		assertFalse(b[2][2]);
		assertFalse(b[2][3]);
		assertFalse(b[2][4]);
		assertFalse(b[1][5]);
		assertFalse(b[0][5]);

	}
	@Test
	public void testMove2()
	{
		assertFalse(p.move(1, 1, board));
		p.setHero(h);
		assertTrue(p.move(1, 1, board));
		boolean[][] b =  p.getVisibleBoard();
		//radius 1:
		assertTrue(b[0][0]);
		assertTrue(b[0][1]);
		assertTrue(b[0][2]);
		assertTrue(b[1][0]);
		assertTrue(b[1][1]);
		assertTrue(b[1][2]);
		assertTrue(b[2][0]);
		assertTrue(b[2][1]);
		assertTrue(b[2][2]);
		//radius 2:
		assertFalse(b[0][3]);
		assertFalse(b[1][3]);
		assertFalse(b[2][3]);
		assertFalse(b[3][0]);
		assertFalse(b[3][1]);
		assertFalse(b[3][2]);
		assertFalse(b[3][3]);
	}
}
