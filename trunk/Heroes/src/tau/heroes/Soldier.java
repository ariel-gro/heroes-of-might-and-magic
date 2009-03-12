package tau.heroes;

public class Soldier extends Creature
{
	/**
	 * Soldier is a creature with the following parameters:
	 * speed = 3
	 * shoots = 0
	 * defense skill = 7
	 * attack skill = 7
	 * hit points = 10
	 * damage = 3
	 * name = "Soldier"
	 * @param numOfUnits
	 */
	public Soldier(int numOfUnits) {
		super(AlignmentType.KNIGHT,3, 0, 7, 7, 10,3, "Soldier",numOfUnits);
	}
}
