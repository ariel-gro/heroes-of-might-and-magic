/**
 * 
 */
package tau.heroes;

import java.io.Serializable;

/**
 * @author Amir
 * 
 */
public class Dwarf extends Creature implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String CREATURE_NAME = "Dwarf";

	/**
	 * @param numberOfUnits
	 */
	public Dwarf(int numberOfUnits)
	{
		super(AlignmentType.BARBARIAN, 2, 0, 6, 8, 12, 4, CREATURE_NAME, numberOfUnits);
	}
}
