package tau.heroes;

import java.io.Serializable;

public class Goblin extends Creature implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public static final String CREATURE_NAME = "Goblin";

	public Goblin(int numberOfUnits)
	{
		super(AlignmentType.BARBARIAN, //type
				2,					//speed
				0, 					//shoots
				4, 					//attack
				4, 					//defense
				10, 				//hit points
				5, 					//damage
				CREATURE_NAME, 		//name
				numberOfUnits);
	}
}
