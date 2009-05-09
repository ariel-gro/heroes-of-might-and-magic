package tau.heroes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Castle implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Board board;
	private Player player;
	private Army army;
	private ArrayList<CreatureFactory> factories;
	private int xPos;
	private int yPos;

	public Castle(Player player, Board board, int x, int y)
	{
		this.player = player;
		this.board = board;
		this.army = null;
		this.factories = new ArrayList<CreatureFactory>();
		this.xPos = x;
		this.yPos = y;

		this.board.placeCastle(this, x, y);
		this.player.addCastle(this);
	}

	public void enterHero(Hero hero)
	{
		if (hero.player == this.player)
			enterHeroIntoOwnCastle(hero);
		else
		{
			boolean bIsHeroInCastle = false;

			if (this.player.getHero() != null)
				bIsHeroInCastle = (this.player.getHero().getXPos() == this.xPos && this.player
					.getHero().getYPos() == this.yPos);

			if (this.army == null && !bIsHeroInCastle)
				enterHeroIntoEmptyCastle(hero);
			else
				enterHeroIntoOccupiedCastle(hero, bIsHeroInCastle);
		}
	}

	private void enterHeroIntoOwnCastle(Hero hero)
	{
		if (!(player.isComputer()))
		{
			if (GameState.isGUI())
				HeroesGui.displayMessage(hero.player.getName()
					+ "'s hero has entered his own castle.");
			else
				HeroesConsole.displayMessage(hero.player.getName()
					+ "'s hero has entered his own castle.");
		}
	}

	private void enterHeroIntoOccupiedCastle(Hero hero, boolean bIsHeroInCastle)
	{
		boolean isGui = GameState.isGUI();
		if (!(player.isComputer()))
		{
			if (isGui)
			{
				HeroesGui.displayMessage(hero.player.getName() + "'s hero has entered "
					+ this.player.getName() + "'s castle.");
			}
			else
			{
				HeroesConsole.displayMessage(hero.player.getName() + "'s hero has entered "
					+ this.player.getName() + "'s castle.");
			}
		}

		Hero defendingHero;

		// An attack is between two heroes
		// If the castle doesn't have one make a dummy hero...
		if (bIsHeroInCastle)
			defendingHero = this.player.getHero();
		else
		{
			defendingHero = new Hero(0, 0, army);
			defendingHero.player = this.player;
		}
		hero.attack(defendingHero);
		System.out.println("attacker = " + hero.alive() + " defender = " + defendingHero.alive());
		if (hero.alive() && !defendingHero.alive())
		{
			army = null;
			enterHeroIntoEmptyCastle(hero);
		}
		else
		{
			board.removeHero(xPos, yPos);
		}

		// Dummy hero is dead when we leave this function - no one will point to
		// him.
		if (!bIsHeroInCastle)
		{
			defendingHero.kill();
			defendingHero = null;
		}
	}

	private void enterHeroIntoEmptyCastle(Hero hero)
	{
		this.player.removeCastle(this);
		hero.player.addCastle(this);
		this.player = hero.player;
	}

	public Player getPlayer()
	{
		return this.player;
	}

	public int getXPos()
	{
		return this.xPos;
	}

	public int getYPos()
	{
		return this.yPos;
	}

	public Army getArmy()
	{
		return this.army;
	}

	public void setArmy(Army army)
	{
		this.army = army;
	}
	
	public List<CreatureFactory> getFactories()
	{
		return this.factories;
	}

	public CreatureFactory getFactory(Class<? extends CreatureFactory> factoryClass)
	{
		for (CreatureFactory factory : this.factories)
			if (factory.getClass().equals(factoryClass))
				return factory;

		return null;
	}

	public Boolean hasFactory(Class<? extends CreatureFactory> factoryClass)
	{
		return (this.getFactory(factoryClass) != null);
	}

	public void addFactory(CreatureFactory factory)
	{
		if (!this.hasFactory(factory.getClass()))
		{
			this.factories.add(factory);

			if (!(player.isComputer()))
			{
				if (GameState.isGUI())
					HeroesGui.displayMessage(this.printLocation() + ": A new " + factory.getName()
						+ " was added");
				else
					HeroesConsole.displayMessage(this.printLocation() + ": A new "
						+ factory.getName() + " was added");
			}
		}
	}

	public void removeFactory(CreatureFactory factory)
	{
		if (this.hasFactory(factory.getClass()))
		{
			this.factories.remove(factory);

			System.out.println(this.printLocation() + ": A " + factory.getName() + " was removed");
		}
	}

	public boolean canBuildFactory(Class<? extends CreatureFactory> factoryClass)
	{
		CreatureFactory factory;

		try
		{
			factory = factoryClass.newInstance();
		}
		catch (InstantiationException e)
		{
			return false;
		}
		catch (IllegalAccessException e)
		{
			return false;
		}

		return this.player.hasEnoughResources(factory.getPrices());
	}

	public CreatureFactory buildFactory(Class<? extends CreatureFactory> factoryClass)
	{
		CreatureFactory factory;

		try
		{
			factory = factoryClass.newInstance();
		}
		catch (InstantiationException e)
		{
			return null;
		}
		catch (IllegalAccessException e)
		{
			return null;
		}

		for (ResourceType rType : ResourceType.values())
		{
			int price = factory.getPrice(rType.getTypeName());
			this.player.decrementTreasury(rType.getTypeName(), price);
		}

		return factory;
	}

	public int getAvailableUnits(Class<? extends Creature> creatureClass)
	{
		Class<? extends CreatureFactory> factoryClass = CreatureFactory
			.getCreatureFactoryClass(creatureClass);

		CreatureFactory factory = this.getFactory(factoryClass);

		if (factory == null)
		{
			System.out.println(this.player.getName() + " doesn't have an appropriate factory.");
			return 0;
		}

		int unitsLeft = factory.getUnitsAvailableToBuild();
		int unitsCanBuy = player.getMaxUnits(factory.getPricesPerUnit());

		if (this.army != null)
			if (this.canAddToArmy(creatureClass))
				return Math.min(unitsLeft, unitsCanBuy);
			else
			{
				System.out.println("Army in " + this.printLocation() + " is full.");
				return 0;
			}
		else
			return Math.min(unitsLeft, unitsCanBuy);
	}

	public String printLocation()
	{
		return "Castle at (" + this.xPos + ", " + this.yPos + ")";
	}

	public void makeUnits(Class<? extends Creature> creatureClass, int numberOfUnits)
	{
		Class<? extends CreatureFactory> factoryClass = CreatureFactory
			.getCreatureFactoryClass(creatureClass);

		CreatureFactory factory = this.getFactory(factoryClass);

		for (int i = 0; i < numberOfUnits; i++)
			this.player.decrementTreasury(factory.getPricesPerUnit());
		Creature creature = factory.buildCreature(numberOfUnits);

		this.addToArmy(creature);
	}

	public boolean canAddToArmy(Class<? extends Creature> creatureClass)
	{
		return (this.army == null || this.army.canAdd(creatureClass));
	}

	public void addToArmy(Creature creature)
	{
		if (this.army == null)
		{
			Creature[] creatures = new Creature[] { creature };
			this.army = new Army(creatures);
		}
		else
			this.army.add(creature);
	}

	public boolean canRemoveFromArmy(Creature creature)
	{
		return (this.army != null && this.army.canRemove(creature));
	}

	public void removeFromArmy(Creature creature)
	{
		this.army.remove(creature);
	}

	public String toInfoString()
	{
		String s = this.printLocation() + ":\n";
		if (this.factories.size() > 0)
			for (CreatureFactory factory : this.factories)
				s += factory.getName() + "\n";
		else
			s += "No factories.\n";
		s += "\n";
		s += "Castle's Army: \n";
		if (army != null)
			s += this.army.toString();
		else
			s += "No army.";

		return s;
	}

	protected void endDay()
	{
		for (int i = 0; i < this.factories.size(); i++)
			this.factories.get(i).endDay();
	}
}