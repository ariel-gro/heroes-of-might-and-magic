package tau.heroes;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Player implements Serializable
{
	public static final String COMPUTER_NAME = "computer";
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final String playerName;
	private Hero hero;
	private HashMap<String, Integer> mines;
	private HashMap<String, Integer> treasury;
	private ArrayList<Castle> castles;
	private int daysWithoutCastles;
	private final static int MAX_MOVES_ALLOWED = 5;
	private static int movesLeft;
	private boolean[][] visibleBoard;



	public Player (String name)
	{
		this.playerName = name;
		mines = new HashMap<String, Integer>(ResourceType.values().length);
		treasury = new HashMap<String, Integer>(ResourceType.values().length);
		castles = new ArrayList<Castle>();
		daysWithoutCastles = 0;
		movesLeft = MAX_MOVES_ALLOWED;
		//init to false:
		visibleBoard = new boolean[40][40];

		for (int i = 0; i < ResourceType.values().length; i++)
		{
			mines.put(ResourceType.values()[i].getTypeName(), 0);
			treasury.put(ResourceType.values()[i].getTypeName(), 0);
		}
	}

	public void setHero(Hero theHero)
	{
		this.hero = theHero;
		if(hero != null)
			setVisibleBoard(hero.getXPos(),hero.getYPos(),1);
	}

	public Hero getHero()
	{
		if(hero != null && !hero.alive())
		{
			hero = null;
		}
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

	public int getMovesLeft()
	{
		if(hero == null)
			return 0;
		return movesLeft;
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
		movesLeft = MAX_MOVES_ALLOWED;

		for (int i = 0; i < ResourceType.values().length; i++)
		{
			tempType = ResourceType.values()[i];
			amount = (this.mines.get(tempType.getTypeName()))*tempType.getPerDay();
			this.incrementTreasury(tempType.getTypeName(), amount);
		}
		if (castles.isEmpty())
			daysWithoutCastles++;
		else
			daysWithoutCastles = 0;
		if(daysWithoutCastles >= 7)
			hero = null;
		//will make sure that the hero is alive, if not then null the hero.
		getHero();
		
		for (int i = 0; i < this.castles.size(); i++)
		{
			this.castles.get(i).endDay();
			this.incrementTreasury("gold", Constants.GOLD_PER_CASTLE);
		}

		System.out.println("Player "+this.playerName+" ended his turn\n");
	}

	public void displayTreasury()
	{
		String tempTypeName;

		System.out.println("Player "+this.playerName+" treasury list:");
		System.out.println();
		System.out.println("Resource \t Amount");
		for (int i = 0; i < ResourceType.values().length; i++)
		{
			tempTypeName = ResourceType.values()[i].getTypeName();
			System.out.println(tempTypeName+" \t\t "+this.treasury.get(tempTypeName));
		}
	}

	public void displayMines()
	{
		String tempTypeName;

		System.out.println("Player "+this.playerName+" mines list:\n");
		System.out.println("Mine \t Quantity");
		for (int i = 0; i < ResourceType.values().length; i++)
		{
			tempTypeName = ResourceType.values()[i].getTypeName();
			System.out.println(tempTypeName+" \t\t "+this.mines.get(tempTypeName));
		}
	}

	public boolean isAlive()
	{
		return (getHero() != null) || !this.castles.isEmpty();
	}
	public boolean move(int x,int y, Board board)
	{
		if(hero == null)
			return false;

		int oldX = hero.getXPos();
		int oldY = hero.getYPos();

		int counter = Math.abs(oldX - x)+Math.abs(oldY-y);
		if (counter > movesLeft)
		{
			return false;
		}

		boolean retVal = hero.moveTo(x, y, board);
		if(retVal)
		{
			movesLeft -= counter;
			setVisiblePath(oldX , oldY , x , y);
		}

		return retVal;
	}
	private void setVisiblePath(int xSource,int ySource,int xDest,int yDest)
	{
		int bigX = Math.max(xSource, xDest);
		int smallX = Math.min(xSource, xDest);
		int BigY= Math.max(ySource, yDest);
		int smallY = Math.min(ySource, yDest);

		//start walking on the axes first go on the horizontal
		for(int i =smallX ;i<=bigX;i++)
		{
			setVisibleBoard(i, ySource, 1);
		}
		//then on the vertical:
		for(int i =smallY ;i<=BigY;i++)
		{
			setVisibleBoard(xDest,i , 1);
		}
	}
	private void setVisibleBoard(int x,int y,int radius)
	{
		for(int i = x-radius;i<=x+radius;i++)
		{
			for(int j = y-radius;j<=y+radius;j++)
			{
				try
				{
					visibleBoard[i][j] = true;
				}
				catch(IndexOutOfBoundsException iobe)
				{
					//This means we are on the edge of the board...
				}
			}
		}
	}
	public boolean[][] getVisibleBoard()
	{
		return visibleBoard;
	}
	public boolean isComputer()
	{
		return (getName().toLowerCase().equals(COMPUTER_NAME));
	}

	public int finalScore()
	{
		int ret = 0;
		Army army;

		for (int i = 0; i < ResourceType.values().length; i++)
		{
			ret += this.getMineQuantity(ResourceType.values()[i].getTypeName());
			ret += this.getCurrentTreasuryAmount(ResourceType.values()[i].getTypeName());
		}

		army = this.hero.getArmy();
		ret += army.getTotalNumberOfUnits();

		for (int i = 0; i< this.castles.size()-1; i++)
		{
			army = this.castles.get(i).getArmy();
			if (army != null)
				ret += army.getTotalNumberOfUnits();
		}
		return ret;
	}
}