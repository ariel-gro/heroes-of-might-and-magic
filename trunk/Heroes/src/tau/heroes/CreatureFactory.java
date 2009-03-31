/**
 * 
 */
package tau.heroes;

import java.io.Serializable;
import java.util.HashMap;

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
	private static HashMap<Class<? extends Creature>, Class<? extends CreatureFactory>> factoryMap;
	private HashMap<String, Integer> prices;
	private HashMap<String, Integer> pricesPerUnit;
	private final int unitsPerWeek;
	private int unitsAvailable;
	private int dayOfTheWeek; /** 1 = Sunday, 2 = Monday,...., 7 = Saturday */

	static 
	{
		factoryMap = new HashMap<Class<? extends Creature>, Class<? extends CreatureFactory>>();
		factoryMap.put(Goblin.class, GoblinFactory.class);
		factoryMap.put(Soldier.class, SoldierFactory.class);
	}

	public static Class<? extends CreatureFactory> getCreatureFactoryClass(
			Class<? extends Creature> creatureClass) {
		return factoryMap.get(creatureClass);
	}

	protected CreatureFactory(int unitsPerWeek) {
		this.prices = new HashMap<String, Integer>(ResourceType.values().length);
		this.pricesPerUnit = new HashMap<String, Integer>(ResourceType.values().length);
		this.unitsPerWeek = unitsPerWeek;
		this.unitsAvailable = unitsPerWeek;
		this.dayOfTheWeek = 1;
		for (int i = 0; i < ResourceType.values().length; i++) {
			prices.put(ResourceType.values()[i].getTypeName(), 0);
			pricesPerUnit.put(ResourceType.values()[i].getTypeName(), 0);
		}
	}

	public int getUnitsPerWeek() {
		return this.unitsPerWeek;
	}

	public int getUnitsAvailableToBuild() {
		return this.unitsAvailable;
	}

	public int getPrice(String type) {
		return this.prices.get(type);
	}

	protected void setPrice(String type, int amount) {
		this.prices.put(type, amount);
	}

	public int getPricePerUnit(String type) {
		return this.pricesPerUnit.get(type);
	}

	protected void setPricePerUnit(String type, int amount) {
		this.pricesPerUnit.put(type, amount);
	}

	public Creature buildCreature(int numberOfUnits) {
		numberOfUnits = (numberOfUnits > this.unitsAvailable)?
				this.unitsAvailable : numberOfUnits;
		this.unitsAvailable -= numberOfUnits;
		return this.buildCreatureInternal(numberOfUnits);
	}

	public abstract String getName();
	protected abstract Creature buildCreatureInternal(int numberOfUnits);

	public HashMap<String, Integer> getPrices() {
		return this.prices;
	}

	public HashMap<String, Integer> getPricesPerUnit() {
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

	public String getDay()
	{
		switch (this.dayOfTheWeek)
		{
			case 1: return "Sunday";
			case 2: return "Monday";
			case 3: return "Tuesday";
			case 4: return "Wednesday";
			case 5: return "Thursday";
			case 6: return "Friday";
			case 7: return "Saturday";
			default:
				return null;
		}
	}
}
