/**
 * 
 */
package tau.heroes;

/**
 * @author yuval eitan
 *
 */
public class GoblinFactory extends CreatureFactory {
	private final static String name = "Goblin Factory";
	
	public GoblinFactory() {
		this.setPrice(ResourceType.GOLD.getTypeName(), 20);
	}
	
	@Override
	public String getName() {
		return name;
	}
}
