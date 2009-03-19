package tau.heroes;
import java.util.ArrayList;
import java.util.HashMap;

public class Player {
	private final String playerName;
	private Hero hero;
	private HashMap<String, Integer> mines;
	private HashMap<String, Integer> treasury;
	private ArrayList<Castle> castles;
	private int daysWithoutCastles;

	public Player (String name)
	{
		this.playerName = name;
		mines = new HashMap<String, Integer>(ResourceType.values().length);
		treasury = new HashMap<String, Integer>(ResourceType.values().length);
		castles = new ArrayList<Castle>();
		daysWithoutCastles = 0;
		for (int i = 0; i < ResourceType.values().length; i++)
		{
			mines.put(ResourceType.values()[i].getTypeName(), 0);
			treasury.put(ResourceType.values()[i].getTypeName(), 0);
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

	public int getMineQuantity (String type)
	{
		return (this.mines.get(type));
	}

	public void incrementMineQuantity (String type)
	{
		this.mines.put(type, this.mines.get(type) + 1);
	}

	public void decrementMineQuantity (String type)
	{
		this.mines.put(type, this.mines.get(type) - 1);
	}

	public String getName()
	{
		return this.playerName;
	}

	public int getCurrentTreasuryAmount(String type)
	{
		return this.treasury.get(type);
	}

	public void incrementTreasury (String type, int amount)
	{
		this.treasury.put(type, this.treasury.get(type) + amount);
	}

	public void incrementTreasury(HashMap<String, Integer> neededResources) {
		for (ResourceType rType : ResourceType.values()) {
			int price = neededResources.get(rType.getTypeName());
			this.incrementTreasury(rType.getTypeName(), price);
		}
	}
	
	public void decrementTreasury (String type, int amount)
	{
		this.treasury.put(type, this.treasury.get(type) - amount);
	}
	
	public void decrementTreasury(HashMap<String, Integer> neededResources) {
		for (ResourceType rType : ResourceType.values()) {
			int price = neededResources.get(rType.getTypeName());
			this.decrementTreasury(rType.getTypeName(), price);
		}
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
	
	public boolean hasEnoughResources(HashMap<String, Integer> neededResources) {
		boolean hasEnough = true;
		
		for (ResourceType rType : ResourceType.values()) {
			int price = neededResources.get(rType.getTypeName());
			int amount = this.getCurrentTreasuryAmount(rType.getTypeName());
			if (price > amount) {
				System.out.println(this.getName() + " doesn't have enough resources of type " +
						rType.getTypeName() + ". Needs " + price + 
						" and has only " + amount + ".");
				hasEnough = false;
			}
		}
		
		return hasEnough;
	}
	
	public int getMaxUnits(HashMap<String, Integer> neededResources) {
		int maxUnits = Integer.MAX_VALUE;
		
		for (ResourceType rType : ResourceType.values()) {
			int price = neededResources.get(rType.getTypeName());
			int amount = this.getCurrentTreasuryAmount(rType.getTypeName());
			if (price > 0) {
				int units = amount / price;
				maxUnits = Math.min(maxUnits, units);
			}
		}
		
		return maxUnits;
	}
		
	public ArrayList<Castle> getCastles() {
		return this.castles;
	}

	public void endTurn()
	{
		ResourceType tempType;
		int amount;

		for (int i = 0; i < ResourceType.values().length; i++)
		{
			tempType = ResourceType.values()[i];
			amount = (this.mines.get(tempType.getTypeName()))*tempType.getPerDay();
			this.incrementTreasury(tempType.getTypeName(), amount);
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
			System.out.println(tempTypeName+" \t\t "+this.treasury.get(tempTypeName)+"\n");
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
			System.out.println(tempTypeName+" \t\t "+this.mines.get(tempTypeName)+"\n");
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