/**
 * 
 */
package tau.heroes.test;

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
	Player player2;
	Hero hero1;
	Castle castle1;
	Castle castle2;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		theBoard = new Board(10);
		player1 = new Player("Jay");
		player2 = new Player("Silent Bob");
		hero1 = new Hero(player1, theBoard, 4, 4);
		castle1 = new Castle(player1, theBoard, 6, 6);
		castle2 = new Castle(player2, theBoard, 4, 6);
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
		assertEquals(4, castle2.getXPos());
		assertEquals(6, castle2.getYPos());
		assertEquals(castle2, theBoard.getBoardState(4, 6).getCastle());
		assertEquals(player1, castle1.getPlayer());
		assertEquals(player2, castle2.getPlayer());
		assertEquals(1, player1.getCastles().size());
		assertEquals(1, player2.getCastles().size());
		assertTrue(player1.getCastles().contains(castle1));
		assertTrue(player2.getCastles().contains(castle2));
	}
	
	/**
	 * Test method for {@link tau.heroes.Castle#enterHero(tau.heroes.Hero)}.
	 */
	@Test
	public void testEnterHeroOwnCastle() {
		Boolean result = hero1.moveTo(6, 6, theBoard);
		assertTrue(result);
		assertEquals(6, hero1.getXPos());
		assertEquals(6, hero1.getYPos());
		assertEquals(hero1, theBoard.getBoardState(6, 6).getHero());
		assertEquals(6, castle1.getXPos());
		assertEquals(6, castle1.getYPos());
		assertEquals(castle1, theBoard.getBoardState(6, 6).getCastle());
		assertEquals(player1, castle1.getPlayer());
		assertEquals(player2, castle2.getPlayer());
		assertEquals(1, player1.getCastles().size());
		assertEquals(1, player2.getCastles().size());
		assertTrue(player1.getCastles().contains(castle1));
		assertTrue(player2.getCastles().contains(castle2));
	}

	public void testEnterHeroEmptyCastle() {
		Boolean result = hero1.moveTo(4, 6, theBoard);
		assertTrue(result);
		assertEquals(4, hero1.getXPos());
		assertEquals(6, hero1.getYPos());
		assertEquals(hero1, theBoard.getBoardState(4, 6).getHero());
		assertEquals(4, castle2.getXPos());
		assertEquals(6, castle2.getYPos());
		assertEquals(castle2, theBoard.getBoardState(4, 6).getCastle());
		assertEquals(player1, castle1.getPlayer());
		assertEquals(player1, castle2.getPlayer());
		assertEquals(2, player1.getCastles().size());
		assertEquals(0, player2.getCastles().size());
		assertTrue(player1.getCastles().contains(castle1));
		assertTrue(player1.getCastles().contains(castle2));
	}
	
	public void testAddRemoveFactory() {
		SoldierFactory sFactory = new SoldierFactory();
		GoblinFactory gFactory = new GoblinFactory();
		
		Class<? extends CreatureFactory> sFactoryClass = sFactory.getClass();
		Class<? extends CreatureFactory> gFactoryClass = gFactory.getClass();
		
		assertTrue(!castle1.hasFactory(sFactoryClass));
		assertTrue(!castle1.hasFactory(gFactoryClass));
		
		castle1.addFactory(sFactory);
		
		assertTrue(castle1.hasFactory(sFactoryClass));
		assertTrue(!castle1.hasFactory(gFactoryClass));
		
		castle1.addFactory(gFactory);
		
		assertTrue(castle1.hasFactory(sFactoryClass));
		assertTrue(castle1.hasFactory(gFactoryClass));
		
		castle1.removeFactory(sFactory);
		
		assertTrue(!castle1.hasFactory(sFactoryClass));
		assertTrue(castle1.hasFactory(gFactoryClass));
		
		castle1.removeFactory(gFactory);
		
		assertTrue(!castle1.hasFactory(sFactoryClass));
		assertTrue(!castle1.hasFactory(gFactoryClass));
	}
}
