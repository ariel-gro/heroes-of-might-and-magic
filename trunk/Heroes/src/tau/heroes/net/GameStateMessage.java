package tau.heroes.net;

import tau.heroes.GameState;

public class GameStateMessage extends AsyncMessage {

	private static final long serialVersionUID = 1L;
	private GameState gs;
	private int index; //your index in the room (the player number in the game)
	//should be zero based - the same as HeroesGUI.currentPlayerIndex
	
	public GameStateMessage(GameState state)
	{
		gs = state;
		this.index = -1;
	}
	
	public GameState getGameState()
	{
		return gs;
	}
	public int getIndex()
	{
		return index;
	}
	public void setIndex(int index)
	{
		this.index = index;
	}
}
