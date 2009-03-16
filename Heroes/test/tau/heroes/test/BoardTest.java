
package tau.heroes.test;
import tau.heroes.*;
import junit.framework.TestCase;
import org.junit.Test;


public class BoardTest extends TestCase
{
	Board board = new Board(20);
	Player player1 = new Player("Liron");
	
	
	Hero h1 = new Hero(player1, board, 0, 0);
	Castle c1 = new Castle(player1, board, 0, 0);
	
	Hero h2 = new Hero(player1, board, 0, 19);
	Resource r1 = new Resource(ResourceType.WOOD, board, 0, 19);
	
	Hero h3 = new Hero(player1, board, 19, 0);
	Castle c2 = new Castle(player1, board, 19, 0);
	
	Hero h4 = new Hero(player1, board, 19, 19);
	Resource r2 = new Resource(ResourceType.GOLD, board, 19, 19);
	
	Hero h5 = new Hero(player1, board, 1, 1);
	Resource r3 = new Resource(ResourceType.STONE, board, 1, 1);
	
	@Test
	public void testGetSize()
	{
		assertEquals(20, board.getSize());
	}

	@Test
	public void testSetWorld()
	{
		assertTrue(board.getBoardState(0, 0).getHero() instanceof Hero);
		assertTrue(board.getBoardState(0, 0).getCastle() instanceof Castle);

		assertTrue(board.getBoardState(0, 19).getHero() instanceof Hero);
		assertTrue(board.getBoardState(0, 19).getResource() instanceof Resource);

		assertTrue(board.getBoardState(19, 0).getHero() instanceof Hero);
		assertTrue(board.getBoardState(19, 0).getCastle() instanceof Castle);

		assertTrue(board.getBoardState(19, 19).getHero() instanceof Hero);
		assertTrue(board.getBoardState(19, 19).getResource() instanceof Resource);

		assertTrue(board.getBoardState(1, 1).getHero() instanceof Hero);
		assertTrue(board.getBoardState(1, 1).getResource() instanceof Resource);
	}
}
