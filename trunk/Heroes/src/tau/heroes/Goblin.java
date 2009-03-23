package tau.heroes;

import java.io.Serializable;

public class Goblin extends Creature implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Goblin(int numberOfUnits)
	{
		super(AlignmentType.BARBARIAN, 2, 0, 6, 8, 12, 4, "Goblin", numberOfUnits);
	}
}
