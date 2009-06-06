package tau.heroes.net;

public class GameOverEvent {

	GameOverMessage message;
	public GameOverEvent(GameOverMessage message)
	{
		this.message = message;
	}
	public GameOverMessage getGameOverMessage()
	{
		return message;
	}
}
