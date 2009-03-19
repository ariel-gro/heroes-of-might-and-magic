package tau.heroes;

import java.util.ArrayList;

public class Castle
{
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

	public Boolean hasFactory(Class<? extends CreatureFactory> factoryClass) {
		for (CreatureFactory factory : this.factories)
			if (factory.getClass().equals(factoryClass))
				return true;

		return false;
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

	public String toLocationString() {
		return "Castle at (" + xPos + ", " + this.yPos + ")";
	}
}
