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
	private final int maxUnitsPerDay;
	private int unitsBuiltToday;
	
	static {
		factoryMap = new HashMap<Class<? extends Creature>, Class<? extends CreatureFactory>>();
		factoryMap.put(Goblin.class, GoblinFactory.class);
		factoryMap.put(Soldier.class, SoldierFactory.class);
	}
	
	public static Class<? extends CreatureFactory> getCreatureFactoryClass(
			Class<? extends Creature> creatureClass) {
		return factoryMap.get(creatureClass);
	}
	
	protected CreatureFactory(int maxUnitsPerDay) {
		this.prices = new HashMap<String, Integer>(ResourceType.values().length);
		this.pricesPerUnit = new HashMap<String, Integer>(ResourceType.values().length);
		this.maxUnitsPerDay = maxUnitsPerDay;
		this.unitsBuiltToday = 0;
		for (int i = 0; i < ResourceType.values().length; i++) {
			prices.put(ResourceType.values()[i].getTypeName(), 0);
			pricesPerUnit.put(ResourceType.values()[i].getTypeName(), 0);
		}
	}
	
	public int getMaxUnitsPerDay() {
		return this.maxUnitsPerDay;
	}
	
	public int getUnitsBuildToday() {
		return this.unitsBuiltToday;
	}
	
	public int getUnitsLeftToday() {
		return this.maxUnitsPerDay - this.unitsBuiltToday;
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
		this.unitsBuiltToday += numberOfUnits;
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
}
