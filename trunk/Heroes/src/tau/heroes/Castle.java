package tau.heroes;

public class Castle
{
	private Player player;
	private int xPos;
	private int yPos;
	
	public Castle(Player player, Board theBoard, int x, int y) {
		this.player = player;
		this.xPos = x;
		this.yPos = y;
		
		theBoard.placeCastle(this, x, y);
	}
}
