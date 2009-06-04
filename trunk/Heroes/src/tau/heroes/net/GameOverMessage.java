package tau.heroes.net;

import java.util.List;
import java.util.Vector;

import tau.heroes.Player;

public class GameOverMessage extends AsyncMessage {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Player winner;
	
	
	public GameOverMessage(Player winner)
	{
		this.winner = winner;
	}
	
	
	public Player getWinner()
	{
		return winner;
	}
}
