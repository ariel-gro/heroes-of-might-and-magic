package tau.heroes;
import java.util.HashMap;
import java.util.LinkedList;

public class Player {
	private final String playerName;
	private HashMap<String, Integer> resources;
	private LinkedList<Hero> heroList;

	public Player (String name)
	{
		this.playerName = name;
		resources = new HashMap<String, Integer>(ResourceType.values().length);
		for (int i = 0; i < ResourceType.values().length; i++)
		{
			resources.put(ResourceType.values()[i].getTypeName(), 0);
		}
		heroList = new LinkedList<Hero>();
	}

	public int getQuantity (String type)
	{
		return (this.resources.get(type));
	}
	
	public void incrementQuantity (String type)
	{
		this.resources.put(type, this.resources.get(type) + 1);
	}
	
	public void decrementQuantity (String type)
	{
		this.resources.put(type, this.resources.get(type) - 1);
	}

	public String getName() {
		return this.playerName;
	}
		
		
}
