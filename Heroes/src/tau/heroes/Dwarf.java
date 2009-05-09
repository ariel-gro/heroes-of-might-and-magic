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
		super(AlignmentType.SORCERESS, //type
				2,					//speed
				0, 					//shoots
				8, 					//attack
				8, 					//defense
				20, 				//hit points
				9, 					//damage
				CREATURE_NAME, 		//name
				numberOfUnits);
	}
}