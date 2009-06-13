/**
 * 
 */
package tau.heroes;

/**
 * @author Amir
 * 
 */
public class DwarfFactory extends CreatureFactory
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String name = "Dwarf Factory";
	public final static int UNITS_PER_WEEK = 5;

	/**
	 * 
	 */
	public DwarfFactory()
	{
		super(UNITS_PER_WEEK);
		this.setPrice(ResourceType.GOLD.getTypeName(), Constants.DWARF_FACTORY_PRICE_GOLD);
		this.setPrice(ResourceType.WOOD.getTypeName(), Constants.DWARF_FACTORY_PRICE_WOOD);
		this.setPrice(ResourceType.STONE.getTypeName(), Constants.DWARF_FACTORY_PRICE_STONE);
		this.setPricePerUnit(ResourceType.GOLD.getTypeName(), Constants.DWARF_UNIT_PRICE_GOLD);
		this.setPricePerUnit(ResourceType.WOOD.getTypeName(), Constants.DWARF_UNIT_PRICE_WOOD);
		this.setPricePerUnit(ResourceType.STONE.getTypeName(), Constants.DWARF_UNIT_PRICE_STONE);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tau.heroes.CreatureFactory#buildCreatureInternal(int)
	 */
	@Override
	protected Creature buildCreatureInternal(int numberOfUnits)
	{
		return new Dwarf(numberOfUnits);
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
		return Dwarf.CREATURE_NAME;
	}

	@Override
	public Class<? extends Creature> getCreatureClass()
	{
		return Dwarf.class;
	}
}
