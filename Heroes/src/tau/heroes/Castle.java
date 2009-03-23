package tau.heroes;

import java.io.Serializable;
import java.util.ArrayList;

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

	public Castle(Player player, Board board, int x, int y) {
		this.player = player;
		this.board = board;
		this.army = null;
		this.factories = new ArrayList<CreatureFactory>();
		this.xPos = x;
		this.yPos = y;

		this.board.placeCastle(this, x, y);
		this.player.addCastle(this);
	}

	public void enterHero(Hero hero) {
		if (hero.player == this.player)
			enterHeroIntoOwnCastle(hero);
		else
		{
			boolean bIsHeroInCastle = false;
			
			if(this.player.getHero() != null)
				bIsHeroInCastle = (this.player.getHero().getXPos() == this.xPos && 
								   this.player.getHero().getYPos() == this.yPos);
			
			if (this.army == null && !bIsHeroInCastle)
				enterHeroIntoEmptyCastle(hero);
			else
				enterHeroIntoOccupiedCastle(hero,bIsHeroInCastle);
		}
	}

	private void enterHeroIntoOwnCastle(Hero hero) {
		System.out.println(hero.player.getName() + "'s hero has entered his own castle.");
	}
	
	private void enterHeroIntoOccupiedCastle(Hero hero, boolean bIsHeroInCastle) {
		System.out.println(hero.player.getName() + "'s hero has entered "
				+ this.player.getName() + "'s castle.");
		
		Hero defendingHero;
		
		// An attack is between two heroes
		// If the castle doesn't have one make a dummy hero...
		if(bIsHeroInCastle)
			defendingHero = this.player.getHero();
		else
			defendingHero = new Hero(0,0,army);
		
		hero.attack(defendingHero);
		if(hero.alive())
		{
			army = null;
			enterHeroIntoEmptyCastle(hero);
		}
		// Dummy hero is dead when we leave this function - no one will point to him.
	}
	
	private void enterHeroIntoEmptyCastle(Hero hero) {
		this.player.removeCastle(this);
		hero.player.addCastle(this);
		this.player = hero.player;
	}

	public Player getPlayer() {
		return this.player;
	}

	public int getXPos() {
		return this.xPos;
	}

	public int getYPos() {
		return this.yPos;
	}

	public Army getArmy() {
		return this.army;
	}

	public void setArmy(Army army) {
		this.army = army;
	}
	
	public CreatureFactory getFactory(Class<? extends CreatureFactory> factoryClass) {
		for (CreatureFactory factory : this.factories)
			if (factory.getClass().equals(factoryClass))
				return factory;
		
		return null;
	}

	public Boolean hasFactory(Class<? extends CreatureFactory> factoryClass) {
		return (this.getFactory(factoryClass) != null);
	}

	public void addFactory(CreatureFactory factory) {
		if (!this.hasFactory(factory.getClass())) {
			this.factories.add(factory);

			System.out.println(this.toLocationString() + ": A new " + factory.getName() + " was added");
		}
	}

	public void removeFactory(CreatureFactory factory) {
		if (this.hasFactory(factory.getClass())) {
			this.factories.remove(factory);

			System.out.println(this.toLocationString() + ": A " + factory.getName() + " was removed");
		}
	}
	
	public boolean canBuildFactory(Class<? extends CreatureFactory> factoryClass) {
		CreatureFactory factory;
		
		try {
			factory = factoryClass.newInstance();
		} 
		catch (InstantiationException e) {
			return false;
		} 
		catch (IllegalAccessException e) {
			return false;
		}
				
		return this.player.hasEnoughResources(factory.getPrices());
	}
	
	public CreatureFactory buildFactory(Class<? extends CreatureFactory> factoryClass) {
		CreatureFactory factory;
		
		try {
			factory = factoryClass.newInstance();
		} 
		catch (InstantiationException e) {
			return null;
		} 
		catch (IllegalAccessException e) {
			return null;
		}
		
		for (ResourceType rType : ResourceType.values()) {
			int price = factory.getPrice(rType.getTypeName());
			this.player.decrementTreasury(rType.getTypeName(), price);
		}
		
		return factory;
	}
	
	public int getAvailableUnits(Class<? extends Creature> creatureClass) {
		Class<? extends CreatureFactory> factoryClass =
			CreatureFactory.getCreatureFactoryClass(creatureClass);
		
		CreatureFactory factory = this.getFactory(factoryClass);
		
		if (factory == null) {
			System.out.println(this.player.getName() + " doesn't have an appropriate factory.");
			return 0;
		}
		
		int unitsLeft = factory.getUnitsLeftToday();
		int unitsCanBuy = player.getMaxUnits(factory.getPricesPerUnit());
		
		if (this.army != null) {
			for (int i = 0; i < Army.MAX_CREATURES; i++)
				if (this.army.getCreature(i) == null || 
						this.army.getCreature(i).getClass().equals(creatureClass))
					return Math.min(unitsLeft, unitsCanBuy);
			
			System.out.println("Army in " + this.toLocationString() + " is full.");
			return 0;
		}
		else
			return Math.min(unitsLeft, unitsCanBuy);
	}

	public String toLocationString() {
		return "Castle at (" + this.xPos + ", " + this.yPos + ")";
	}
	
	public void makeUnits(Class<? extends Creature> creatureClass, int numberOfUnits) {
		Class<? extends CreatureFactory> factoryClass =
			CreatureFactory.getCreatureFactoryClass(creatureClass);
		
		CreatureFactory factory = this.getFactory(factoryClass);
		
		for (int i = 0; i < numberOfUnits; i++)
			this.player.decrementTreasury(factory.getPricesPerUnit());
		Creature creature = factory.buildCreature(numberOfUnits);
		
		this.addToArmy(creature);
	}

	public void addToArmy(Creature creature) {
		if (this.army == null) {
			Creature[] creatures = new Creature[] { creature };
			this.army = new Army(creatures);
		}
		else {
			for (int i = 0; i < Army.MAX_CREATURES; i++)
				if (this.army.getCreature(i) != null && 
						this.army.getCreature(i).getClass().equals(creature.getClass())) {
					Creature existingCreature = this.army.getCreature(i);
					existingCreature.addUnits(creature.get_numberOfUnits());
					return;
				}
			
			for (int i = 0; i < Army.MAX_CREATURES; i++)
				if (this.army.getCreature(i) == null) {
					this.army.setCreature(i, creature);
					return;
				}
		}
	}	
}
