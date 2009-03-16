package tau.heroes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.HashMap;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

public class Player {
	private final String playerName;
	private Hero hero;
	private HashMap<String, Integer> resources;
	private HashMap<String, Integer> resourceAmount;
	private ArrayList<Castle> castles;
	private int daysWithoutCastles;

	public Player (String name)
	{
		this.playerName = name;
		resources = new HashMap<String, Integer>(ResourceType.values().length);
		resourceAmount = new HashMap<String, Integer>(ResourceType.values().length);
		castles = new ArrayList<Castle>();
		daysWithoutCastles = 0;
		for (int i = 0; i < ResourceType.values().length; i++)
		{
			resources.put(ResourceType.values()[i].getTypeName(), 0);
			resourceAmount.put(ResourceType.values()[i].getTypeName(), 0);
		}
	}

	public void setHero(Hero theHero)
	{
		this.hero = theHero;
	}

	public Hero getHero()
	{
		return hero;
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

	public String getName()
	{
		return this.playerName;
	}

	public int getCurrentAmount(String type)
	{
		return this.resourceAmount.get(type);
	}

	public void incrementAmount (String type, int amount)
	{
		this.resourceAmount.put(type, this.resourceAmount.get(type) + amount);
	}

	public void decrementAmount (String type, int amount)
	{
		this.resourceAmount.put(type, this.resourceAmount.get(type) - amount);
	}
	
	public void addCastle(Castle castle) {
		if (!this.castles.contains(castle)) {
			this.castles.add(castle);
			
			System.out.println(this.playerName + " now has the castle at (" + 
					castle.getXPos() + ", " + castle.getYPos() + ")");
		}
	}
	
	public void removeCastle(Castle castle) {
		if (this.castles.contains(castle)) {
			this.castles.remove(castle);
			
			System.out.println(this.playerName + " lost the castle at (" + 
					castle.getXPos() + ", " + castle.getYPos() + ")");
		}
	}

	public void endTurn()
	{
		ResourceType tempType;
		int amount;

		for (int i = 0; i < ResourceType.values().length; i++)
		{
			tempType = ResourceType.values()[i];
			amount = (this.resources.get(tempType.getTypeName()))*tempType.getPerDay();
			this.incrementAmount(tempType.getTypeName(), amount);
		}

		if(hero != null && !hero.alive())
		{
			hero = null;
		}

		System.out.println("Player "+this.playerName+" ended his turn\n");
	}

	public void displayResourcesAmounts()
	{
		String tempTypeName;

		System.out.println("Player "+this.playerName+" resource amounts list:");
		System.out.println();
		System.out.println("Resource \t Amount");
		for (int i = 0; i < ResourceType.values().length; i++)
		{
			tempTypeName = ResourceType.values()[i].getTypeName();
			System.out.println(tempTypeName+" \t\t "+this.resourceAmount.get(tempTypeName)+"\n");
		}
	}

	public void displayResources()
	{
		String tempTypeName;

		System.out.println("Player "+this.playerName+" resource mines list:\n");
		System.out.println("Resource \t Quantity");
		for (int i = 0; i < ResourceType.values().length; i++)
		{
			tempTypeName = ResourceType.values()[i].getTypeName();
			System.out.println(tempTypeName+" \t\t "+this.resources.get(tempTypeName)+"\n");
		}
	}
	
	public boolean isAlive()
	{
		if (castles.isEmpty())
		{
			daysWithoutCastles++;
			if (daysWithoutCastles == 7)
			{
				return false;
			}
		}
		else
		{
			daysWithoutCastles = 0;
		}
		return true;
	}
}