package tau.heroes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

public class GameController
{
	private GameState gameState;

	public GameController()
	{
		this.gameState = new GameState();
	}

	public void initNewGame(Vector<Player> players)
	{
		this.gameState.setPlayers(players);

		initBoard();
		placeHeroes(players);
		placeCastles(players);
		placeResources(players.size());
	}

	private void initBoard()
	{
		this.gameState.setBoard(new Board(Constants.BOARD_SIZE));
	}

	private void placeHeroes(Vector<Player> players)
	{
		Vector<Hero> heroes = new Vector<Hero>();

		for (Player player : players)
		{
			int randomX = (int) (Math.random() * (Constants.BOARD_SIZE - 1));
			int randomY = (int) (Math.random() * (Constants.BOARD_SIZE - 1));
			Hero hero = new Hero(player, this.gameState.getBoard(), randomX, randomY);
			heroes.add(hero);
			player.setHero(hero);
		}

		this.gameState.setHeroes(heroes);
	}

	private void placeCastles(Vector<Player> players)
	{
		Vector<Castle> castles = new Vector<Castle>();

		for (Player player : players)
		{
			Hero hero = player.getHero();
			castles.add(new Castle(player, this.gameState.getBoard(), hero.getXPos() + 1, hero
				.getYPos()));
		}

		this.gameState.setCastles(castles);
	}

	private void placeResources(int numberOfResources)
	{
		Vector<Resource> resources = new Vector<Resource>();

		for (int i = 0; i < numberOfResources; i++)
		{
			for (ResourceType rt : ResourceType.values())
			{
				int randomX = (int) (Math.random() * (Constants.BOARD_SIZE - 1));
				int randomY = (int) (Math.random() * (Constants.BOARD_SIZE - 1));

				if (this.gameState.getBoard().getBoardState(randomX, randomY).getIsEmpty())
					resources.add(new Resource(rt, this.gameState.getBoard(), randomX, randomY));
			}
		}

		this.gameState.setResources(resources);
	}

	/**
	 * Removes dead players from game and return all of their belongings to the
	 * board.
	 * @return vector of players that were removed
	 */
	public Vector<Player> removeDeadPlayers()
	{
		Vector<Player> deadPlayers = new Vector<Player>();

		for (Player player : this.gameState.getPlayers())
		{
			if (!player.isAlive())
			{
				removePlayerHero(player);
				removePlayerResources(player);
				deadPlayers.add(player);
			}
		}
		this.gameState.getPlayers().removeAll(deadPlayers);
		return deadPlayers;
	}

	/**
	 * @param player
	 */
	private void removePlayerHero(Player player)
	{
		if (player.getHero() != null)
		{
			int x = player.getHero().getXPos();
			int y = player.getHero().getYPos();
			this.gameState.getBoard().getBoardState(x, y).setHero(null);
			player.getHero().kill();
		}
	}

	/**
	 * @param player
	 */
	private void removePlayerResources(Player player)
	{
		for (Resource resource : this.gameState.getResources())
			if (resource.getOwner() != null && resource.getOwner().equals(player))
				resource.setOwner(null);
	}

	public Player isThereAWinner()
	{
		if (this.gameState.getPlayers().size() == 1)
			return this.gameState.getPlayers().firstElement();
		else
			return null;
	}

	public boolean saveGame(String fileName)
	{
		try
		{
			File saveFile = new File(fileName);
			saveFile.createNewFile();
			FileOutputStream fileOut = new FileOutputStream(saveFile);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(this.gameState);
			out.close();
			fileOut.close();
			return true;
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Not enough disk space ! \nCant save the game");
		}
		catch (IOException e)
		{
			System.out.println("Illegal file name ! \nPlease give another name");
		}
		return false;
	}

	public boolean loadGame(String fileName)
	{
		try
		{
			FileInputStream fileIn = new FileInputStream(fileName);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			this.gameState = (GameState) in.readObject();
			in.close();
			fileIn.close();
			return true;
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Can't find the requested file !");
		}
		catch (IOException e)
		{
			System.out.println("Cant open the requested file ! \nPlease give another name");
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("Can't find the requested file !");
		}

		return false;
	}

	public GameState getGameState()
	{
		return this.gameState;
	}

	public void setGameState(GameState gameState)
	{
		this.gameState = gameState;
	}
}
