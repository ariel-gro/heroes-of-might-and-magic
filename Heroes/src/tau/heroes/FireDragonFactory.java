/**
 * 
 */
package tau.heroes;

/**
 * @author Amir
 * 
 */
public class FireDragonFactory extends CreatureFactory
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String name = "Fire Dragon Factory";
	public final static int UNITS_PER_WEEK = 5;

	/**
	 *
	 */
	public FireDragonFactory()
	{
		super(UNITS_PER_WEEK);
		this.setPrice(ResourceType.GOLD.getTypeName(), Constants.FIREDRAGON_FACTORY_PRICE_GOLD);
		this.setPricePerUnit(ResourceType.GOLD.getTypeName(), Constants.FIREDRAGON_UNIT_PRICE_GOLD);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tau.heroes.CreatureFactory#buildCreatureInternal(int)
	 */
	@Override
	protected Creature buildCreatureInternal(int numberOfUnits)
	{
		return new FireDragon(numberOfUnits);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tau.heroes.CreatureFactory#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getUnitName()
	{
		return FireDragon.CREATURE_NAME;
	}

	@Override
	public Class<? extends Creature> getCreatureClass()
	{
		return FireDragon.class;
	}
}
