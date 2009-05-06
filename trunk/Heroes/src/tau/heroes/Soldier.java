package tau.heroes;

public class Soldier extends Creature
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static final String SOLDIER_NAME = "Soldier";

	/**
	 * Soldier is a creature with the following parameters: speed = 3 shoots = 0
	 * defense skill = 7 attack skill = 7 hit points = 10 damage = 3 name =
	 * "Soldier"
	 * 
	 * @param numberOfUnits
	 */

	public Soldier(int numberOfUnits)
	{
		super(AlignmentType.KNIGHT, 3, 0, 7, 7, 10, 3, SOLDIER_NAME, numberOfUnits);
	}
}