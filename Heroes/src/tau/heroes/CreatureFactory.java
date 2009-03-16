/**
 * 
 */
package tau.heroes;

import java.util.HashMap;

/**
 * @author yuval eitan
 *
 */
public abstract class CreatureFactory {
	private HashMap<String, Integer> prices;
	
	protected CreatureFactory() {
		this.prices = new HashMap<String, Integer>(ResourceType.values().length);
		for (int i = 0; i < ResourceType.values().length; i++)
			prices.put(ResourceType.values()[i].getTypeName(), 0);
	}
	
	public int getPrice(String type) {
		return this.prices.get(type);
	}
	
	protected void setPrice(String type, int amount) {
		this.prices.put(type, amount);
	}
	
	public abstract String getName();
}
