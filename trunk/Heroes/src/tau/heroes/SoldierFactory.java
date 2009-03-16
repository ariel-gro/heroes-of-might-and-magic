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
	
	public SoldierFactory() {
		this.setPrice(ResourceType.GOLD.getTypeName(), 50);
	}
	
	@Override
	public String getName() {
		return name;
	}
}
