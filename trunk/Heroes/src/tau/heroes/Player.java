package tau.heroes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player implements Serializable
{
	public static final String COMPUTER_NAME = "computer";
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final String playerName;
	private Hero hero;
	private Map<String, Integer> mines;
	private Map<String, Integer> treasury;
	private List<Castle> castles;
	private int daysWithoutCastles;
	private final static int MAX_MOVES_ALLOWED = 5;
	private int movesLeft;
	private boolean[][] visibleBoard;
	private int dayOfTheWeek = 1;	/** 1 = Day 1, 2 = Day 2,...., 7 = Day 7 */
	private int computerLevel = 0; /** 0 = Human, 1 = Novice, 2 = Expert **/
	
	public Player(String name)
	{
		this.playerName = name;
		mines = new HashMap<String, Integer>(ResourceType.values().length);
		treasury = new HashMap<String, Integer>(ResourceType.values().length);
		castles = new ArrayList<Castle>();
		daysWithoutCastles = 0;
		movesLeft = MAX_MOVES_ALLOWED;
		// init to false:
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
		if (hero != null)
			setVisibleBoard(hero.getXPos(), hero.getYPos(), 1);
	}

	public Hero getHero()
	{
		if (hero != null && !hero.alive())
		{
			hero = null;
		}
		return hero;
	}

	public int getMineQuantity(String type)
	{
		return (this.mines.get(type));
	}

	public void incrementMineQuantity(String type)
	{
		this.mines.put(type, this.mines.get(type) + 1);
	}

	public void decrementMineQuantity(String type)
	{
		this.mines.put(type, this.mines.get(type) - 1);
	}

	public int getMovesLeft()
	{
		if (hero == null)
			return 0;
		return movesLeft;
	}

	public String getName()
	{
		return this.playerName;
	}

	public int getDaysWithoutCastles()
	{
		return this.daysWithoutCastles;
	}
	
	public Map<String, Integer> getCurrentTreasury()
	{
		return this.treasury;
	}

	public int getCurrentTreasuryAmount(String type)
	{
		return this.treasury.get(type);
	}

	public void incrementTreasury(String type, int amount)
	{
		this.treasury.put(type, this.treasury.get(type) + amount);
	}

	public void incrementTreasury(Map<String, Integer> neededResources)
	{
		for (ResourceType rType : ResourceType.values())
		{
			int price = neededResources.get(rType.getTypeName());
			this.incrementTreasury(rType.getTypeName(), price);
		}
	}

	public void decrementTreasury(String type, int amount)
	{
		this.treasury.put(type, this.treasury.get(type) - amount);
	}

	public void decrementTreasury(Map<String, Integer> neededResources)
	{
		for (ResourceType rType : ResourceType.values())
		{
			int price = neededResources.get(rType.getTypeName());
			this.decrementTreasury(rType.getTypeName(), price);
		}
	}

	public void addCastle(Castle castle)
	{
		if (!this.castles.contains(castle))
		{
			this.castles.add(castle);
		}
	}

	public void removeCastle(Castle castle)
	{
		if (this.castles.contains(castle))
		{
			this.castles.remove(castle);

			if (!(this.isComputer()))
			{
				if (GameState.isGUI())
					HeroesGui.displayMessage(this.playerName + " lost the castle at ("
						+ castle.getXPos() + ", " + castle.getYPos() + ")");
				else
					HeroesConsole.displayMessage(this.playerName + " lost the castle at ("
						+ castle.getXPos() + ", " + castle.getYPos() + ")");
			}
		}
	}

	public boolean hasEnoughResources(Map<String, Integer> neededResources)
	{
		boolean hasEnough = true;

		for (ResourceType rType : ResourceType.values())
		{
			int price = neededResources.get(rType.getTypeName());
			int amount = this.getCurrentTreasuryAmount(rType.getTypeName());
			if (price > amount)
			{
				hasEnough = false;
			}
		}

		return hasEnough;
	}

	public int getMaxUnits(Map<String, Integer> neededResources)
	{
		int maxUnits = Integer.MAX_VALUE;

		for (ResourceType rType : ResourceType.values())
		{
			int price = neededResources.get(rType.getTypeName());
			int amount = this.getCurrentTreasuryAmount(rType.getTypeName());
			if (price > 0)
			{
				int units = amount / price;
				maxUnits = Math.min(maxUnits, units);
			}
		}

		return maxUnits;
	}

	public Map<String, Integer> getMines()
	{
		return this.mines;
	}

	public List<Castle> getCastles()
	{
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
			amount = (this.mines.get(tempType.getTypeName())) * tempType.getPerDay();
			this.incrementTreasury(tempType.getTypeName(), amount);
		}
		if (castles.isEmpty())
			daysWithoutCastles++;
		else
			daysWithoutCastles = 0;
		if (daysWithoutCastles >= 7)
			hero = null;
		// will make sure that the hero is alive, if not then null the hero.
		getHero();

		for (int i = 0; i < this.castles.size(); i++)
		{
			this.castles.get(i).endDay();
			this.incrementTreasury(ResourceType.GOLD.getTypeName(), Constants.GOLD_PER_CASTLE);
		}

		if (this.dayOfTheWeek == 7)
		{
			this.dayOfTheWeek = 1;
			if (!(this.isComputer()))
			{
				if (GameState.isGUI())
					HeroesGui.displayMessage("New week started !\n"
						+ "All factories increase creature population");
				else
					HeroesConsole.displayMessage("New week started !\n"
						+ "All factories increase creature population");
			}
		}
		else
			this.dayOfTheWeek++;

		System.out.println("Player " + this.playerName + " ended his turn\n");
	}

	public int getDayAsInt()
	{
		return this.dayOfTheWeek;
	}

	public String getDayAsString()
	{
		return "Day " + this.dayOfTheWeek;
	}

	public void displayTreasury()
	{
		String tempTypeName;

		System.out.println("Player " + this.playerName + " treasury list:");
		System.out.println();
		System.out.println("Resource \t Amount");
		for (int i = 0; i < ResourceType.values().length; i++)
		{
			tempTypeName = ResourceType.values()[i].getTypeName();
			System.out.println(tempTypeName + " \t\t " + this.treasury.get(tempTypeName));
		}
	}

	public void displayMines()
	{
		String tempTypeName;

		System.out.println("Player " + this.playerName + " mines list:\n");
		System.out.println("Mine \t Quantity");
		for (int i = 0; i < ResourceType.values().length; i++)
		{
			tempTypeName = ResourceType.values()[i].getTypeName();
			System.out.println(tempTypeName + " \t\t " + this.mines.get(tempTypeName));
		}
	}

	public boolean isAlive()
	{
		return (getHero() != null) || !this.castles.isEmpty();
	}

	public boolean move(int x, int y, Board board)
	{
		if (hero == null)
			return false;

		int oldX = hero.getXPos();
		int oldY = hero.getYPos();

		int counter = Math.abs(oldX - x) + Math.abs(oldY - y);
		if (counter > movesLeft)
		{
			return false;
		}

		boolean retVal = hero.moveTo(x, y, board);
		if (retVal)
		{
			movesLeft -= counter;
			setVisiblePath(oldX, oldY, x, y);
		}

		return retVal;
	}

	public boolean checkMove(int x, int y, Board board)
	{
		if (hero == null)
			return false;

		int oldX = hero.getXPos();
		int oldY = hero.getYPos();

		int counter = Math.abs(oldX - x) + Math.abs(oldY - y);
		if (counter > movesLeft)
			return false;

		if (hero.checkStepsAllowed(x, y, board) == false)
			return false;

		return true;
	}

	private void setVisiblePath(int xSource, int ySource, int xDest, int yDest)
	{
		int bigX = Math.max(xSource, xDest);
		int smallX = Math.min(xSource, xDest);
		int BigY = Math.max(ySource, yDest);
		int smallY = Math.min(ySource, yDest);

		// start walking on the axes first go on the horizontal
		for (int i = smallX; i <= bigX; i++)
		{
			setVisibleBoard(i, ySource, 1);
		}
		// then on the vertical:
		for (int i = smallY; i <= BigY; i++)
		{
			setVisibleBoard(xDest, i, 1);
		}
	}

	private void setVisibleBoard(int x, int y, int radius)
	{
		for (int i = x - radius; i <= x + radius; i++)
		{
			for (int j = y - radius; j <= y + radius; j++)
			{
				try
				{
					visibleBoard[i][j] = true;
				}
				catch (IndexOutOfBoundsException iobe)
				{
					// This means we are on the edge of the board...
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
		return (computerLevel != 0);
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

		for (int i = 0; i < this.castles.size() - 1; i++)
		{
			army = this.castles.get(i).getArmy();
			if (army != null)
				ret += army.getTotalNumberOfUnits();
		}
		return ret;
	}
	
	//This is the AI of the computer.
	//Not related to moves only for buying creatures and factories.
	public void AIMove()
	{
		if((computerLevel<2) || castles == null || castles.isEmpty())
			return;
		//buy things only in the first day of the week (to allow accumulation of the resources)
		if(dayOfTheWeek != 1)
			return;
		
		Castle theCastle = castles.get(0);
		//The logic is first try to buy Factories
		for( CreatureFactory factory : CreatureFactory.getCreatureFactories())
		{			
			Class<? extends CreatureFactory> factoryClass = factory.getClass();	
			if (theCastle.canBuildFactory(factoryClass))
				theCastle.addFactory(theCastle.buildFactory(factoryClass));
		}
		//Then if you still have resources buy creatures
		for( CreatureFactory factory : CreatureFactory.getCreatureFactories())
		{			
			Class<? extends Creature> creatureClass = factory.getCreatureClass();
			int availableUnits = theCastle.getAvailableUnits(creatureClass);
			if (availableUnits > 0)
				theCastle.makeUnits(creatureClass, availableUnits);	
		}		
	}

	public void setComputerLevel(int computerLevel) {
		this.computerLevel = computerLevel;
	}

	public int getComputerLevel() {
		return computerLevel;
	}
}