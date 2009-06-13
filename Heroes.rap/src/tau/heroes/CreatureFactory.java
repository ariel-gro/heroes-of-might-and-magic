/**
 * 
 */
package tau.heroes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yuval eitan
 * 
 */
public abstract class CreatureFactory implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Map<Class<? extends Creature>, Class<? extends CreatureFactory>> factoryMap;
	private static List<Class<? extends CreatureFactory>> factoryList;
	private Map<String, Integer> prices;
	private Map<String, Integer> pricesPerUnit;
	private final int unitsPerWeek;
	private int unitsAvailable;
	private int dayOfTheWeek = 1;
	/** 1 = Day 1, 2 = Day 2,...., 7 = Day 7 */

	static
	{
		factoryMap = new HashMap<Class<? extends Creature>, Class<? extends CreatureFactory>>();
		factoryMap.put(Goblin.class, GoblinFactory.class);
		factoryMap.put(Soldier.class, SoldierFactory.class);
		factoryMap.put(Dwarf.class, DwarfFactory.class);
		factoryMap.put(Archer.class, ArcherFactory.class);
		factoryMap.put(FireDragon.class, FireDragonFactory.class);

		factoryList = new ArrayList<Class<? extends CreatureFactory>>();
		factoryList.add(GoblinFactory.class);
		factoryList.add(SoldierFactory.class);
		factoryList.add(DwarfFactory.class);
		factoryList.add(ArcherFactory.class);
		factoryList.add(FireDragonFactory.class);
	}

	public static Class<? extends CreatureFactory> getCreatureFactoryClass(
		Class<? extends Creature> creatureClass)
	{
		return factoryMap.get(creatureClass);
	}

	public static CreatureFactory getCreatureFactory(String creatureName)
	{
		CreatureFactory factory = null;
		creatureName = creatureName.toLowerCase();

		if (creatureName.equals("goblin"))
			factory = new GoblinFactory();
		else if (creatureName.equals("soldier"))
			factory = new SoldierFactory();
		else if (creatureName.equals("dwarf"))
			factory = new DwarfFactory();
		else if (creatureName.equals("archer"))
			factory = new ArcherFactory();
		else if (creatureName.equals("dragon"))
			factory = new FireDragonFactory();

		return factory;
	}

	public static List<Class<? extends CreatureFactory>> getCreatureFactoryClasses()
	{
		return factoryList;
	}

	public static CreatureFactory createCreatureFactory(
		Class<? extends CreatureFactory> creatureFactoryClass)
	{
		try
		{
			CreatureFactory factory = creatureFactoryClass.getConstructor().newInstance();
			return factory;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static List<CreatureFactory> getCreatureFactories()
	{
		List<CreatureFactory> creatureFactories = new ArrayList<CreatureFactory>();

		for (Class<? extends CreatureFactory> creatureFactoryClass : getCreatureFactoryClasses())
		{
			CreatureFactory factory = createCreatureFactory(creatureFactoryClass);
			if (factory != null)
				creatureFactories.add(factory);
		}

		return creatureFactories;
	}

	protected CreatureFactory(int unitsPerWeek)
	{
		this.prices = new HashMap<String, Integer>(ResourceType.values().length);
		this.pricesPerUnit = new HashMap<String, Integer>(ResourceType.values().length);
		this.unitsPerWeek = unitsPerWeek;
		this.unitsAvailable = unitsPerWeek;
		this.dayOfTheWeek = 1;
		for (int i = 0; i < ResourceType.values().length; i++)
		{
			prices.put(ResourceType.values()[i].getTypeName(), 0);
			pricesPerUnit.put(ResourceType.values()[i].getTypeName(), 0);
		}
	}

	public int getUnitsPerWeek()
	{
		return this.unitsPerWeek;
	}

	public int getUnitsAvailableToBuild()
	{
		return this.unitsAvailable;
	}

	public int getPrice(String type)
	{
		return this.prices.get(type);
	}

	protected void setPrice(String type, int amount)
	{
		this.prices.put(type, amount);
	}

	public int getPricePerUnit(String type)
	{
		return this.pricesPerUnit.get(type);
	}

	protected void setPricePerUnit(String type, int amount)
	{
		this.pricesPerUnit.put(type, amount);
	}

	public Creature buildCreature(int numberOfUnits)
	{
		this.unitsAvailable -= numberOfUnits;
		return this.buildCreatureInternal(numberOfUnits);
	}

	public abstract String getName();

	public abstract String getUnitName();

	public abstract Class<? extends Creature> getCreatureClass();

	protected abstract Creature buildCreatureInternal(int numberOfUnits);

	public Map<String, Integer> getPrices()
	{
		return this.prices;
	}

	public Map<String, Integer> getPricesPerUnit()
	{
		return this.pricesPerUnit;
	}

	protected void endDay()
	{
		if (this.dayOfTheWeek == 7)
		{
			this.unitsAvailable += this.unitsPerWeek;
			this.dayOfTheWeek = 1;
		}
		else
			this.dayOfTheWeek++;
	}

	public int getDayAsInt()
	{
		return this.dayOfTheWeek;
	}

	public String getDayAsString()
	{
		return "Day " + this.dayOfTheWeek;
	}
}
