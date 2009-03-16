package tau.heroes;

public class Castle
{
	private Board board;
	private Player player;
	private int xPos;
	private int yPos;
	
	public Castle(Player player, Board board, int x, int y) {
		this.player = player;
		this.board = board;
		this.xPos = x;
		this.yPos = y;
		
		this.board.placeCastle(this, x, y);
	}
	
	public void enterHero(Hero hero) {
		if (hero.player == this.player)
			enterHeroIntoOwnCastle(hero);
	}
	
	private void enterHeroIntoOwnCastle(Hero hero) {
		System.out.println(hero.player.getName() + "'s hero has entered his own castle.\n");
	}

	public int getXPos() {
		return this.xPos;
	}

	public int getYPos() {
		return this.yPos;
	}
}
