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
	public final static int UNITS_PER_WEEK = 10;
	
	public SoldierFactory() {
		super(UNITS_PER_WEEK);
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
