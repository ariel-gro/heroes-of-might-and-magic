
package tau.heroes.test;
import tau.heroes.*;
import junit.framework.TestCase;
import org.junit.Test;


public class BoardTest extends TestCase
{
	Board board = new Board(20);


	@Test
	public void testGetSize()
	{
		assertEquals(20, board.getSize());
	}

	@Test
	public void testSetWorld()
	{
		assertTrue(board.getBoardState(0, 0).getHero() instanceof Hero);
		assertTrue(board.getBoardState(0, 0).getProperty() instanceof Property);

		assertTrue(board.getBoardState(0, 19).getHero() instanceof Hero);
		assertTrue(board.getBoardState(0, 19).getProperty() instanceof Property);

		assertTrue(board.getBoardState(19, 0).getHero() instanceof Hero);
		assertTrue(board.getBoardState(19, 0).getProperty() instanceof Property);

		assertTrue(board.getBoardState(19, 19).getHero() instanceof Hero);
		assertTrue(board.getBoardState(19, 19).getProperty() instanceof Property);

		assertFalse(board.getBoardState(1, 1).getHero() instanceof Hero);
		assertFalse(board.getBoardState(1, 1).getProperty() instanceof Property);
	}
}
