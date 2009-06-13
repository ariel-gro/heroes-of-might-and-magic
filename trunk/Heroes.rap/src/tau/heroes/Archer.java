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
		super(AlignmentType.KNIGHT, //type
				2,					//speed
				10, 				//shoots
				10, 				//attack
				10, 				//defense
				25, 				//hit points
				12, 				//damage
				CREATURE_NAME, 		//name
				numberOfUnits);
	}
}
