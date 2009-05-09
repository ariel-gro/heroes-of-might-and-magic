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
	public final static int UNITS_PER_WEEK = 10;

	public GoblinFactory()
	{
		super(UNITS_PER_WEEK);
		this.setPrice(ResourceType.GOLD.getTypeName(), Constants.GOBLIN_FACTORY_PRICE_GOLD);
		this.setPrice(ResourceType.WOOD.getTypeName(), Constants.GOBLIN_FACTORY_PRICE_WOOD);
		this.setPrice(ResourceType.STONE.getTypeName(), Constants.GOBLIN_FACTORY_PRICE_STONE);
		this.setPricePerUnit(ResourceType.GOLD.getTypeName(), Constants.GOBLIN_UNIT_PRICE_GOLD);
		this.setPricePerUnit(ResourceType.WOOD.getTypeName(), Constants.GOBLIN_UNIT_PRICE_WOOD);
		this.setPricePerUnit(ResourceType.STONE.getTypeName(), Constants.GOBLIN_UNIT_PRICE_STONE);
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	protected Creature buildCreatureInternal(int numberOfUnits)
	{
		return new Goblin(numberOfUnits);
	}

	@Override
	public String getUnitName()
	{
		return Goblin.CREATURE_NAME;
	}

	@Override
	public Class<? extends Creature> getCreatureClass()
	{
		return Goblin.class;
	}
}
