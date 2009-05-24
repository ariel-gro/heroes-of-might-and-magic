package tau.heroes.test;

import tau.heroes.*;
import junit.framework.TestCase;

public class ScoreBoardTest extends TestCase
{

	Player player1 = new Player("Ido", PlayerColor.BLUE);
	Player player2 = new Player("Ariel", PlayerColor.BLUE);
	GameScoreBoard board = new GameScoreBoard();

	public void testScoreBoard()
	{
		board.addToScoreBoard(player1, 15);
		board.addToScoreBoard(player2, 17);

		assertEquals(17, board.getScoreAt(0));
		assertEquals(15, board.getScoreAt(1));
		assertEquals(player2, board.getPlayerAt(0));
		assertEquals(player1, board.getPlayerAt(1));
		System.out.println(board.print());
	}

	public void testSaveScoreBoard()
	{
		board.addToScoreBoard(player1, 15);
		board.addToScoreBoard(player2, 17);
		board.save();

		GameScoreBoard otherBoard = new GameScoreBoard();
		otherBoard.load();

		assertEquals(17, board.getScoreAt(0));
		assertEquals(15, board.getScoreAt(1));
		assertEquals(player2, board.getPlayerAt(0));
		assertEquals(player1, board.getPlayerAt(1));
		System.out.println(board.print());
	}

}
