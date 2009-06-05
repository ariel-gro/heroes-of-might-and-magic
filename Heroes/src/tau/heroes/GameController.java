package tau.heroes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import tau.heroes.db.UserInfo;
import tau.heroes.net.ChatListener;
import tau.heroes.net.ChatMessage;
import tau.heroes.net.GameStateListener;
import tau.heroes.net.GameStateMessage;
import tau.heroes.net.HeroesClientPeer;
import tau.heroes.net.HeroesServer;
import tau.heroes.net.NetworkResult;
import tau.heroes.net.RoomInfo;
import tau.heroes.net.RoomUpdateListener;

public class GameController
{
	private GameState gameState;
	private HeroesClientPeer serverProxy;
	private int networkIndex;

	public static final int LOACL_GAME_INDEX = -1;
	public static boolean isNetwork;

	public GameController(boolean isGUI)
	{
		this.gameState = new GameState(isGUI);
		serverProxy = HeroesClientPeer.instance();
		networkIndex = LOACL_GAME_INDEX;
		isNetwork = this.isNetworkGame();
	}

	public void initNewGame(Vector<Player> players)
	{
		this.gameState.setPlayers(players);

		initBoard();
		placeHeroes(players);
		placeCastles(players);
		placeResources(players.size());
	}
	
	public boolean isNetworkGame()
	{
		return (this.getNetworkIndex() != LOACL_GAME_INDEX);
	}

	private void initBoard()
	{
		this.gameState.setBoard(new Board(Constants.BOARD_SIZE));
		int[] point;
		for (int i = 0; i < 2; i++)
		{
			for (int j = 0; j < Constants.MAX_PLAYERS; j++)
			{
				point = randomizeByZone(j);
				this.gameState.getBoard().placeMapObject(MapObject.FIRE, point[0], point[1]);
				point = randomizeByZone(j);
				this.gameState.getBoard().placeMapObject(MapObject.ROCK, point[0], point[1]);
				point = randomizeByZone(j);
				this.gameState.getBoard().placeMapObject(MapObject.TREESTOMP, point[0], point[1]);

			}

		}
	}

	private void placeHeroes(Vector<Player> players)
	{
		Vector<Hero> heroes = new Vector<Hero>();

		for (int i = 0; i < players.size(); i++)
		{
			Player player = players.elementAt(i);
			int[] point = randomizeByZone(i);
			Hero hero = new Hero(player, this.gameState.getBoard(), point[0], point[1]);
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
				.getYPos(), CastleType.values()[players.indexOf(player)]));
		}

		this.gameState.setCastles(castles);
	}

	private void placeResources(int numberOfResources)
	{
		Vector<Resource> resources = new Vector<Resource>();

		for (int i = 0; i < numberOfResources; i++)
		{
			for (int ind = 0; ind < ResourceType.values().length;)
			{
				ResourceType rt = ResourceType.values()[ind];
				int[] point = randomizeByZone(i);
				// make sure board is empty at (randomX, randomY)
				// and only if empty place mine and increment ind.
				if (this.gameState.getBoard().getBoardState(point[0], point[1]).getIsEmpty())
				{
					resources.add(new Resource(rt, this.gameState.getBoard(), point[0], point[1]));
					ind++;
				}
			}
		}

		this.gameState.setResources(resources);
	}

	/**
	 * Removes dead players from game and return all of their belongings to the
	 * board.
	 * 
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
		return this.gameState.isWinner();
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

	private int[] randomizeByZone(int playerNum)
	{
		int[] rt = new int[2];

		double randomX = Math.random();
		double randomY = Math.random();

		switch (playerNum)
		{
		case 0:
			rt[0] = (int) (randomX * (Constants.BOARD_SIZE / 2 - 3)) + 1;
			rt[1] = (int) (randomY * (Constants.BOARD_SIZE / 2 - 3)) + 1;
			break;

		case 1:
			rt[0] = (int) (randomX * (Constants.BOARD_SIZE / 2 - 2)) + Constants.BOARD_SIZE / 2;
			rt[1] = (int) (randomY * (Constants.BOARD_SIZE / 2 - 2)) + Constants.BOARD_SIZE / 2;
			break;

		case 2:
			rt[0] = (int) (randomX * (Constants.BOARD_SIZE / 2 - 3)) + 1;
			rt[1] = (int) (randomY * (Constants.BOARD_SIZE / 2 - 2)) + Constants.BOARD_SIZE / 2;
			break;

		case 3:
			rt[0] = (int) (randomX * (Constants.BOARD_SIZE / 2 - 2)) + Constants.BOARD_SIZE / 2;
			rt[1] = (int) (randomY * (Constants.BOARD_SIZE / 2 - 3)) + 1;
			break;

		default:
			rt[0] = (int) (randomX * (Constants.BOARD_SIZE / 2 - 3)) + 1;
			rt[1] = (int) (randomY * (Constants.BOARD_SIZE / 2 - 3)) + 1;
			break;
		}
		return rt;
	}

	public static String handleBuildPricesCommand()
	{
		String msg = "Prices:\n\n";

		for (CreatureFactory factory : CreatureFactory.getCreatureFactories())
		{
			msg += factory.getName() + ":\n";
			for (ResourceType rType : ResourceType.values())
			{
				msg += rType.getTypeName() + "\t-\t " + factory.getPrice(rType.getTypeName())
					+ "\n";
			}
			msg += "\n";
		}

		return msg;
	}

	public static String handleMakePricesCommand()
	{
		String msg = "Prices per creature:\n\n";

		for (CreatureFactory factory : CreatureFactory.getCreatureFactories())
		{
			msg += factory.getUnitName() + ":\n";
			for (ResourceType rType : ResourceType.values())
			{
				msg += rType.getTypeName() + "\t-\t "
					+ factory.getPricePerUnit(rType.getTypeName()) + "\n";
			}
			msg += "\n";
		}

		return msg;
	}
	
	public static void handleMessage (String msg)
	{
		if (GameState.isGUI())
		{
			HeroesGui.displayMessage(msg);
		}
		else
		{
			HeroesConsole.displayMessage(msg);
		}
		if (GameController.isNetwork)
		{
			HeroesClientPeer.instance().asyncSendMessage(new ChatMessage("serever: " + msg));
		}
	}

	public int getNetworkIndex()
	{
		return networkIndex;
	}

	public void setNetworkIndex(int networkIndex)
	{
		this.networkIndex = networkIndex;
		isNetwork = this.isNetworkGame();
	}

	// Network controler
	public NetworkResult<Boolean> Login(String ip, String username, String password, boolean asGuest)
	{
		if (!serverProxy.isConnected())
		{
			if (!serverProxy.connect(ip, HeroesServer.SERVER_PORT))
				return new NetworkResult<Boolean>(false, "Error opening connection to server on "
					+ ip + ":" + HeroesServer.SERVER_PORT);
		}
		return serverProxy.Login(username, password, asGuest);
	}

	public NetworkResult<Boolean> Register(String ip, String username, String password,
		String email, String nickname)
	{
		if (!serverProxy.isConnected())
		{
			if (!serverProxy.connect(ip, HeroesServer.SERVER_PORT))
				return new NetworkResult<Boolean>(false, "Error opening connection to server on "
					+ ip + ":" + HeroesServer.SERVER_PORT);
		}
		return serverProxy.Register(username, password, email, nickname);
	}

	public void Disconnect()
	{
		if (serverProxy != null && serverProxy.isConnected())
			serverProxy.disconnect();
	}

	public NetworkResult<List<RoomInfo>> getRoomsFromServer()
	{
		if (!serverProxy.isConnected())
			return null;

		return serverProxy.getRooms();
	}

	public void sendChat(String message)
	{
		ChatMessage chatMessage = new ChatMessage(message);
		serverProxy.asyncSendMessage(chatMessage);
	}

	public void sendGameState()
	{
		GameStateMessage gsMessage = new GameStateMessage(gameState);
		serverProxy.asyncSendMessage(gsMessage);
	}

	public void addChatListener(ChatListener listener)
	{
		serverProxy.addChatListener(listener);
	}

	public void addGameStateListener(GameStateListener listener)
	{
		serverProxy.addGameStateListener(listener);
	}
	
	public void addRoomUpdateListener(RoomUpdateListener listener)
	{
		serverProxy.addRoomUpdateListener(listener);
	}
	
	public void removeRoomUpdateListener(RoomUpdateListener listener)
	{
		serverProxy.removeRoomUpdateListener(listener);
	}
	
	public List<RoomInfo> roomsList()
	{
		return serverProxy.getRoomsList();
	}
	
	public NetworkResult<List<UserInfo>> getRoomsMemebrs(UUID roomID)
	{
		return serverProxy.getRoomMembers(roomID);
	}
	
	public UserInfo getUserInfo()
	{
		return serverProxy.getUserInfo();
	}

	public NetworkResult<Boolean> startNewNetworkGame() {
		 return serverProxy.startNewNetworkGame();
	}

	public NetworkResult<Boolean> createRoom(String name)
	{
		return serverProxy.createRoom(name);
	}
	
	public NetworkResult<Boolean> joinRoom(UUID id)
	{
		return serverProxy.joinRoom(id);
	}
}