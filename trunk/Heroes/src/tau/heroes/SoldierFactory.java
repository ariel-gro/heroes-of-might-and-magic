/**
 * 
 */
package tau.heroes;

/**
 * @author yuval eitan
 *
 */
public class SoldierFactory extends CreatureFactory {
	private static final long serialVersionUID = 1L;
	private final static String name = "Soldier Factory";
	public final static int MAX_UNITS_PER_DAY = 10;
	
	public SoldierFactory() {
		super(MAX_UNITS_PER_DAY);
		this.setPrice(ResourceType.GOLD.getTypeName(), 50);
		this.setPricePerUnit(ResourceType.GOLD.getTypeName(), 10);
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	protected Creature buildCreatureInternal(int numberOfUnits) {
		return new Soldier(numberOfUnits);
	}
}
