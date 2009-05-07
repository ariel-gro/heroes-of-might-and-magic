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
		super(AlignmentType.KNIGHT, 2, 10, 12, 12, 25, 15, CREATURE_NAME, numberOfUnits);
	}
}
