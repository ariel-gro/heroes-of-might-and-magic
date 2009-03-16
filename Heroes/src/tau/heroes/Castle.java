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
		player.addCastle(this);
	}
	
	public void enterHero(Hero hero) {
		if (hero.player == this.player)
			enterHeroIntoOwnCastle(hero);
		else
			if (this.army == null)
				enterHeroIntoEmptyCastle(hero);
	}
	
	private void enterHeroIntoOwnCastle(Hero hero) {
		System.out.println(hero.player.getName() + "'s hero has entered his own castle.");
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
