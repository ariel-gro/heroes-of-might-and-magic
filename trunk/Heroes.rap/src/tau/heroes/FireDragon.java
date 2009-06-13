/**
 * 
 */
package tau.heroes;

import java.io.Serializable;

/**
 * @author Amir
 * 
 */
public class FireDragon extends Creature implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String CREATURE_NAME = "Fire Dragon";

	/**
	 * @param numberOfUnits
	 */
	public FireDragon(int numberOfUnits)
	{
		super(AlignmentType.WARLOCK, //type
				2,					//speed
				0, 				//shoots
				30, 				//attack
				30, 				//defense
				100, 				//hit points
				25, 				//damage
				CREATURE_NAME, 		//name
				numberOfUnits);
	}
}
