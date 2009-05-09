package tau.heroes;

public class Soldier extends Creature
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static final String CREATURE_NAME = "Soldier";

	/**
	 * Soldier is a creature with the following parameters: speed = 3 shoots = 0
	 * defense skill = 7 attack skill = 7 hit points = 10 damage = 3 name =
	 * "Soldier"
	 * 
	 * @param numberOfUnits
	 */

	public Soldier(int numberOfUnits)
	{
		super(AlignmentType.KNIGHT, //type
				3,					//speed
				0, 					//shoots
				6, 					//attack
				6, 					//defense
				15, 				//hit points
				6, 					//damage
				CREATURE_NAME, 		//name
				numberOfUnits);
	}
}