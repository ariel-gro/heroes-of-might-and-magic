/**
 * 
 */
package tau.heroes;

import java.io.Serializable;

/**
 * @author Amir
 * 
 */
public class Archer extends Creature implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String CREATURE_NAME = "Archer";

	/**
	 * @param numberOfUnits
	 */
	public Archer(int numberOfUnits)
	{
		super(AlignmentType.BARBARIAN, 2, 0, 6, 8, 12, 4, CREATURE_NAME, numberOfUnits);
	}
}
