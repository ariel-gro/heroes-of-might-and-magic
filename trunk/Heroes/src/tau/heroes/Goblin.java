package tau.heroes;

import java.io.Serializable;

public class Goblin extends Creature implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public static final String GOBLIN_NAME = "Goblin";

	public Goblin(int numberOfUnits)
	{
		super(AlignmentType.BARBARIAN, 2, 0, 4, 4, 10, 5, GOBLIN_NAME, numberOfUnits);
	}
}
