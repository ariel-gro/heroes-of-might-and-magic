/**
 * 
 */
package tau.heroes;

/**
 * @author yuval eitan
 * 
 */
public class SoldierFactory extends CreatureFactory
{
	private static final long serialVersionUID = 1L;
	private final static String name = "Soldier Factory";
	public final static int UNITS_PER_WEEK = 10;

	public SoldierFactory()
	{
		super(UNITS_PER_WEEK);
		this.setPrice(ResourceType.GOLD.getTypeName(), Constants.SOLDIER_FACTORY_PRICE_GOLD);
		this.setPricePerUnit(ResourceType.GOLD.getTypeName(), Constants.SOLDIER_UNIT_PRICE_GOLD);
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	protected Creature buildCreatureInternal(int numberOfUnits)
	{
		return new Soldier(numberOfUnits);
	}

	@Override
	public String getUnitName()
	{
		return Soldier.SOLDIER_NAME;
	}

	@Override
	public Class<? extends Creature> getCreatureClass()
	{
		return Soldier.class;
	}
}
