package tau.heroes;
import java.util.LinkedList;

public class Player {
	String playerName;
	ResourceList resourceList; //TODO change to "map" collection
	LinkedList<Hero> heroList;

	public Player (String name)
	{
		this.playerName = name;
		resourceList = new ResourceList();
		heroList = new LinkedList<Hero>();
	}

	public int getQuantity (String type)
	{
		for (int i = 0; i < resourceList.resources.length; i++)
		{
			if (resourceList.resources[i].getType().equals(type))
				return resourceList.quantity[i];
		}
		return -1;
	}

	protected class ResourceList{
		ResourceType[] resources = ResourceType.values();
		int[] quantity = new int[resources.length];
	}
}
