/**
 * 
 */
package tau.heroes;

/**
 * @author yuval eitan
 *
 */
public class SoldierFactory extends CreatureFactory {
	private final static String name = "Soldier Factory";
	
	@Override
	public String getName() {
		return name;
	}
}
