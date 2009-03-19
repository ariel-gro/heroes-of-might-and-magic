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
	public final static int MAX_UNITS_PER_DAY = 5;
	
	public GoblinFactory() {
		super(5);
		this.setPrice(ResourceType.GOLD.getTypeName(), 100);
		this.setPricePerUnit(ResourceType.GOLD.getTypeName(), 30);
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	protected Creature buildCreatureInternal(int numberOfUnits) {
		return new Goblin(numberOfUnits);
	}
}
