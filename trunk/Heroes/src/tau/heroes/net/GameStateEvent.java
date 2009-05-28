package tau.heroes.net;

import tau.heroes.GameState;


public class GameStateEvent {
	private GameStateMessage gameStateMessage;
	
	public GameStateEvent(GameStateMessage gameStateMessage) {
		this.gameStateMessage = gameStateMessage;
	}
	
	public GameStateMessage getGameStateMessage() {
		return gameStateMessage;
	}
}
