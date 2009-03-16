package tau.heroes;

public class Castle
{
	private Board board;
	private Player player;
	private Army army;
	private int xPos;
	private int yPos;
	
	public Castle(Player player, Board board, int x, int y) {
		this.player = player;
		this.board = board;
		this.army = null;
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
}
