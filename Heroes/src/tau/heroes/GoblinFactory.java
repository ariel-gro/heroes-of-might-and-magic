/**
 * 
 */
package tau.heroes;

/**
 * @author yuval eitan
 *
 */
public class GoblinFactory extends CreatureFactory 
{
	private static final long serialVersionUID = 1L;
	private final static String name = "Goblin Factory";
	public final static int UNITS_PER_WEEK = 5;
	
	public GoblinFactory() {
		super(UNITS_PER_WEEK);
		this.setPrice(ResourceType.GOLD.getTypeName(), Constants.GOBLIN_FACTORY_PRICE_GOLD);
		this.setPricePerUnit(ResourceType.GOLD.getTypeName(), Constants.GOBLIN_UNIT_PRICE_GOLD);
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
