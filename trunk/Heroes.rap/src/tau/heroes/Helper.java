package tau.heroes;

public class Helper
{
	private static final GameScoreBoard scoreBoard;

	static
	{
		scoreBoard = new GameScoreBoard();
		scoreBoard.load();
	}

	public static GameScoreBoard getScoreBoard()
	{
		return scoreBoard;
	}

	/**
	 * Tries to parse a string into integer.
	 * 
	 * @param s
	 *            - a String containing the integer representation to be parsed.
	 * @return the integer value represented by the argument in decimal, or
	 *         Integer.MIN_VALUE in case of bad input format.
	 */
	public static int tryParseInt(String s)
	{
		try
		{
			return Integer.parseInt(s);
		}
		catch (NumberFormatException e)
		{
			return Integer.MIN_VALUE;
		}
	}

	public static boolean isIntBetween(int value, int min, int max)
	{
		return (value >= min && value <= max);
	}

	public static <T extends Enum<T>> boolean isValueOf(Class<T> enumType, String name)
	{
		try
		{
			Enum.valueOf(enumType, name);
			return true;
		}
		catch (IllegalArgumentException e)
		{
			return false;
		}
	}
}
