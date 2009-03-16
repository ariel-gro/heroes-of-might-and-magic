package tau.heroes;

public final class CanAct 
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
		
		int x = Math.abs(oldX-newX);
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
}