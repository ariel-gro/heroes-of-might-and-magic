package tau.heroes.net;

import tau.heroes.GameState;

public class GameStateMessage extends AsyncMessage {

	private static final long serialVersionUID = 1L;
	private GameState gs;
	
	public GameStateMessage(GameState state)
	{
		gs = state;
	}
	
	public GameState getGameState()
	{
		return gs;
	}

}
