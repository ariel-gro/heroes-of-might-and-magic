package tau.heroes;

public class Move 
{	
	private final static int movesAllowed = 5;
	private static int movesLeft; 
		
	public static void reset()
	{
		movesLeft = movesAllowed;
	}
		
	public static boolean moveUpdate(int oldX, int oldY, int newX, int newY)
	{
		int counter;

		int x = Math.abs(oldX - newX);
		int y =  Math.abs(oldY-newY);
		counter = x+y;
		if (counter > movesLeft)
		{
			return false;
		}
		else
		{
			movesLeft -= counter;
			return true;
		}
	}

	public static int getMovesLeft()
	{
		return movesLeft;
	}
	
	
	public static void makeMove(String[] parameters, Hero hero, Board board)
	{
		int oldX = hero.getXPos();
		int oldY = hero.getYPos();
		int newX = oldX;
		int newY = oldY;

		try 
		{
			newX = Integer.parseInt(parameters[1]);
			newY = Integer.parseInt(parameters[2]);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			System.out.println("Please enter x and y parameters . Check Help for more info .");
		}

		if (moveUpdate(oldX, oldY, newX, newY))
		{
			hero.moveTo(newX, newY, board);
			board.printBoard();
		}
		else
		{
			System.out.println("Illegal move ! You can only move " + getMovesLeft() + " steps more .");
		}
	}
}