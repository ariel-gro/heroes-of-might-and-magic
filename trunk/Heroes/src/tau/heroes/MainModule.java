package tau.heroes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class MainModule
{
	public enum commands
	{
		move("move hero to x,y on the map. Usage: move x y"),
		endTurn("end the player turn. change turn to other player/s"),
		castle("enter the castle menu"),
		help("get help for the possible commands"),
		info("get information about your player"),
		map("prints the map visible to you"),
		legend("prints the map legend"),
		save("save the current game. Usage: save gameName"),
		load("loads a saved game. Usage: load gameName"),
		quit("quit the game"),
		highscore("prints the 10 highest scores"),
		clearScore("clears the highscore table");

		private final String description;

		private commands(String theDescription)
		{
			this.description = theDescription;
		}

		public String getDescription()
		{
			return this.description;
		}
	}

	public enum CastleCommands
	{
		build("Build a creature factory. Usage: build [goblin|soldier]"),
		make("Make a new creature. Usage: make [goblin|soldier]"),
		split("Split units from the hero's army to the castle's army. Usage: split [goblin|soldier] numberOfUnits"),
		join("Join units to the hero's army from the castle's army. Usage: join [goblin|soldier] numberOfUnits"),
		help("Get help for the possible commands"),
		exit("Exit castle menu");

		private final String description;

		private CastleCommands(String theDescription)
		{
			this.description = theDescription;
		}

		public String getDescription()
		{
			return this.description;
		}
	}

	static String[] userInput;
	static Vector<Player> players;
	static Vector<Hero> heroes;
	static Vector<Castle> castles;
	static Vector<Resource> resources;
	static GameScoreBoard scoreBoard;

	static final int BOARD_SIZE = 40;

	/**
	 * @param args
	 * @throws IOException
	 */

	public static boolean save(String fileName, Vector<Player> players, Vector<Hero> heroes,
		Vector<Castle> castles, Vector<Resource> resources, Board theBoard)
	{
		try
		{
			File saveFile = new File(fileName);
			saveFile.createNewFile();
			FileOutputStream fileOut = new FileOutputStream(saveFile);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(new GameState(players, heroes, castles, resources, theBoard));
			out.close();
			fileOut.close();
			return true;
		}
		catch (FileNotFoundException e)
		{
			//e.printStackTrace();
			System.out.println("Not enough disk space ! \nCant save the game");
		}
		catch (IOException e)
		{
			//e.printStackTrace();
			System.out.println("Illegal file name ! \nPlease give another name");
		}
		return false;
	}

	public static GameState load(String fileName)
	{
		try
		{
			FileInputStream fileIn = new FileInputStream(fileName);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			GameState gameState = (GameState) in.readObject();
			in.close();
			fileIn.close();
			return gameState;
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Can't find the requested file !");
			//e.printStackTrace();
		}
		catch (IOException e)
		{
			//e.printStackTrace();
			System.out.println("Cant open the requested file ! \nPlease give another name");
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("Can't find the requested file !");
			//e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args)
	{
		players = new Vector<Player>();
		heroes = new Vector<Hero>();
		castles = new Vector<Castle>();
		resources = new Vector<Resource>();
		scoreBoard = new GameScoreBoard();
		scoreBoard.load();
		//if(viewSelection.startsWith("g"))
		//{
		//	runGraphicalView();
		//	return;
		//}
		//else
		//{
		//	System.out.println("defualt is consol view, enjoy :) ");
		//}
		runConsoleView();
	}

	protected static void runGraphicalView(GameState gs) 
	{
		Display d = new Display();
		HeroesGui application = new HeroesGui(d, gs);
		Shell shell = application.open();
		while (!shell.isDisposed())
		{
			if (!d.readAndDispatch())
				d.sleep();
		}
		d.dispose();
	}

	private static void runConsoleView()
	{
		int numOfPlayers = Integer.parseInt(getCommandAndParameters("Enter number of players:")[0]);

		for (int i = 0; i < numOfPlayers; i++)
		{
			userInput = getCommandAndParameters("Enter the Name of player " + (i + 1) + ":");
			players.add(new Player(userInput[0]));
		}

		Board theBoard = new Board(BOARD_SIZE);

		for (int i = 0; i < numOfPlayers; i++)
		{
			int randomX = (int) (Math.random() * (BOARD_SIZE - 1));
			int randomY = (int) (Math.random() * (BOARD_SIZE - 1));
			Hero h = new Hero(players.get(i), theBoard, randomX, randomY);
			heroes.add(h);
			castles.add(new Castle(players.get(i), theBoard, randomX+1, randomY));
			players.get(i).setHero(heroes.get(i));
		}

		for (int i = 0; i < numOfPlayers; i++)
		{
			for (ResourceType rt : ResourceType.values())
			{
				int randomX = (int) (Math.random() * (BOARD_SIZE - 1));
				int randomY = (int) (Math.random() * (BOARD_SIZE - 1));

				if (theBoard.getBoardState(randomX, randomY).getIsEmpty())
					resources.add(new Resource(rt, theBoard, randomX, randomY));
			}
		}		
		
		String viewSelection = (getCommandAndParameters("Whould you like to play in graphical mode or consol mode(g for graphical or c for consol):")[0]);
		if(viewSelection.startsWith("g"))
		{
			GameState gs = new GameState(players, heroes, castles, resources, theBoard);
			runGraphicalView(gs);
			return;
		}
		else
		{
			System.out.println("defualt is consol view, enjoy :) ");
		}

		
		
		
		while (true)
		{
			for (int player = 0; player < numOfPlayers;)
			{
				removeDeadPlayers(theBoard);
				if (isThereAWinner() != null)
					endGame(isThereAWinner());
				numOfPlayers = players.size();
				Hero h = players.get(player).getHero();
				String temp;

				int oldX = 0;
				int oldY = 0;
				if (h == null)
				{
					temp = players.get(player).getName()
						+ " You don't have any heroes to move with!  make your move:";
				}
				else
				{
					oldX = h.getXPos();
					oldY = h.getYPos();
					temp = players.get(player).getName() + " You are at (" + oldX + "," + oldY
						+ "), make your move .";
				}
				userInput = getCommandAndParameters(temp);
				if (userInput[0].equals(commands.move.toString()))
				{
					int newX, newY;
					try
					{
						newX = Integer.parseInt(userInput[1]);
						newY = Integer.parseInt(userInput[2]);
					}
					catch (Exception ex)
					{
						System.out.println("Wrong parameters, command not recognized !!!");
						continue;
					}
					if (players.get(player).move(newX, newY, theBoard))
					{
						removeDeadPlayers(theBoard);
						if (isThereAWinner() != null)
							endGame(isThereAWinner());
						numOfPlayers = players.size();
						theBoard.printBoard(players.get(player).getVisibleBoard());
					}
					else
					{
						System.out.println("Illegal move ! You can only move "
							+ players.get(player).getMovesLeft() + " steps more .");
					}
				}
				else if (userInput[0].equals(commands.endTurn.toString()))
				{
					players.get(player).endTurn();

					removeDeadPlayers(theBoard);
					if (isThereAWinner() != null)
						endGame(isThereAWinner());
					numOfPlayers = players.size();
					player = (player + 1) % numOfPlayers;
					continue;
				}
				else if (userInput[0].equals(commands.castle.toString()))
				{
					castleMenu(player);
				}
				else if (userInput[0].equals(commands.highscore.toString()))
				{
					scoreBoard.print();
				}
				else if (userInput[0].equals(commands.clearScore.toString()))
				{
					scoreBoard.clearScoreBoard();
				}
				else if (userInput[0].equals(commands.help.toString()))
				{
					for (commands cmd : commands.values())
						System.out.println(cmd.toString() + " - " + cmd.description);
				}
				else if (userInput[0].equals(commands.map.toString()))
				{
					theBoard.printBoard(players.get(player).getVisibleBoard());
				}
				else if (userInput[0].equals(commands.legend.toString()))
				{
					theBoard.printLegend();
				}
				else if (userInput[0].equals(commands.info.toString()))
				{
					players.get(player).displayMines();
					players.get(player).displayTreasury();
					if (h != null)
					{
						System.out.println("You have " + h.getDefenseSkill()
							+ " defense skill and " + h.getAttackSkill() + " attack skill");
						System.out.println("You have an army:");
						System.out.println(h.getArmy().toString());
						for (int i = 0; i < players.get(player).getCastles().size(); i++)
							System.out.println(players.get(player).getCastles().get(i).toInfoString());
					}
				}
				else if (userInput[0].equals(commands.quit.toString()))
				{
					endGame(null);
				}
				else if (userInput[0].equals(commands.save.toString()))
				{
					String fileName = userInput[1];
					if (save(fileName, players, heroes, castles, resources, theBoard) == true)
					{
						System.out.println("Game has been save on '" + fileName + "'.");
					}
				}
				else if (userInput[0].equals(commands.load.toString()))
				{
					String fileName = userInput[1];
					GameState gameState = load(fileName);
					if (gameState != null)
					{
						players = gameState.getPlayers();
						heroes = gameState.getHeroes();
						castles = gameState.getCastles();
						resources = gameState.getResources();
						theBoard = gameState.getBoard();
						numOfPlayers = players.size();
						System.out.println("Game has been load from '" + fileName + "'.");
					}
				}
				else
				{
					System.out.println("Command not recognized !!!");
				}
			}
		}
	}

	private static void castleMenu(int player_index)
	{
		Player player = players.get(player_index);
		ArrayList<Castle> playerCastles = player.getCastles();

		if (playerCastles.size() == 0)
		{
			System.out.println("Sorry, you don't have any castles");
			return;
		}

		Castle theCastle = null;

		if (playerCastles.size() > 1)
		{
			System.out.println("Please choose one castle:");
			for (int i = 0; i < playerCastles.size(); i++)
				System.out.println((i + 1) + ". " + playerCastles.get(i).toLocationString());
			String[] response = getCommandAndParameters("Enter castle number: ");
			int index = Integer.parseInt(response[0]) - 1;
			if (index < 0 || index >= playerCastles.size())
			{
				System.out.println("Sorry, bad input");
				return;
			}
			theCastle = playerCastles.get(index);
		}
		else
			theCastle = playerCastles.get(0);

		while (true)
		{
			System.out.println("Castle menu of " + theCastle.toLocationString());
			String[] response = getCommandAndParameters("Enter a command: ");
			if (response.length > 0)
				if (response[0].equals(CastleCommands.build.toString()))
				{
					handleBuildCommand(player, theCastle, response);
				}
				else if (response[0].equals(CastleCommands.make.toString()))
				{
					handleMakeCommand(player, theCastle, response);
				}
				else if (response[0].equals(CastleCommands.help.toString()))
				{
					for (CastleCommands cmd : CastleCommands.values())
						System.out.println(cmd.toString() + " - " + cmd.getDescription());
				}
				else if (response[0].equals(CastleCommands.split.toString()))
				{
					handleSplitCommand(player, theCastle, response);
				}
				else if (response[0].equals(CastleCommands.join.toString()))
				{
					handleJoinCommand(player, theCastle, response);
				}
				else if (response[0].equals(CastleCommands.exit.toString()))
				{
					return;
				}
				else
					System.out.println("Unknown command");
		}
	}

	private static void handleBuildCommand(Player player, Castle theCastle, String[] response)
	{
		if (response.length > 1)
		{
			CreatureFactory factory = null;

			if (response[1].equals("goblin"))
				factory = new GoblinFactory();
			else if (response[1].equals("soldier"))
				factory = new SoldierFactory();

			if (factory != null)
			{
				Class<? extends CreatureFactory> factoryClass = factory.getClass();

				if (theCastle.hasFactory(factoryClass))
					System.out.println("There is already a factory of this type in this castle");
				else if (theCastle.canBuildFactory(factoryClass))
					theCastle.addFactory(theCastle.buildFactory(factoryClass));
			}
			else
				System.out.println("Unknown creature type!");
		}
	}

	private static void handleSplitCommand(Player player, Castle theCastle, String[] response)
	{
		if (response.length == 3)
		{
			Hero hero = player.getHero();
			if (hero == null)
			{
				System.out.println("Sorry, but you don't have a hero.");
				return;
			}

			int numOfUnits = Integer.parseInt(response[2]);

			if ((hero.getXPos() != theCastle.getXPos()) || (hero.getYPos() != theCastle.getYPos()))
			{
				System.out.println("You must be in the castle in order to split units !");
				return;
			}

			Creature creature = null;
			if (response[1].equals("goblin"))
			{
				creature = new Goblin(numOfUnits);
			}
			else if (response[1].equals("soldier"))
			{
				creature = new Soldier(numOfUnits);
			}
			else
			{
				System.out.println("Unknown creature type!");
				return;
			}

			if (!theCastle.canAddToArmy(creature.getClass()))
			{
				System.out.println("Army in " + theCastle.toLocationString() + " is full.");
				return;
			}
			else if (!hero.removeFromArmy(creature))
			{
				System.out.println("You dont have enough units to split");
				return;
			}
			else
			{
				theCastle.addToArmy(creature);
				return;
			}
		}
		System.out.println("Illegal move !");
	}
	
	private static void handleJoinCommand(Player player, Castle theCastle, String[] response)
	{
		if (response.length == 3)
		{
			Hero hero = player.getHero();
			if (hero == null)
			{
				System.out.println("Sorry, but you don't have a hero.");
				return;
			}

			if ((hero.getXPos() != theCastle.getXPos()) || (hero.getYPos() != theCastle.getYPos()))
			{
				System.out.println("You must be in the castle in order to join units !");
				return;
			}

			int numOfUnits = Integer.parseInt(response[2]);

			Creature creature = null;
			if (response[1].equals("goblin"))
			{
				creature = new Goblin(numOfUnits);
			}
			else if (response[1].equals("soldier"))
			{
				creature = new Soldier(numOfUnits);
			}
			else
			{
				System.out.println("Unknown creature type!");
				return;
			}

			if (!hero.canAddToArmy(creature.getClass()))
			{
				System.out.println("Army of " + hero.toString() + " is full.");
				return;
			}
			else if (!theCastle.canRemoveFromArmy(creature))
			{
				System.out.println("You dont have enough units to join.");
				return;
			}
			else
			{
				theCastle.removeFromArmy(creature);
				hero.addToArmy(creature);
				return;
			}
		}
		System.out.println("Illegal move !");
	}

	private static void handleMakeCommand(Player player, Castle theCastle, String[] response)
	{
		if (response.length > 1)
		{
			Class<? extends Creature> creatureClass = null;

			if (response[1].equals("goblin"))
				creatureClass = Goblin.class;
			else if (response[1].equals("soldier"))
				creatureClass = Soldier.class;

			if (creatureClass != null)
			{
				int maxUnits = theCastle.getAvailableUnits(creatureClass);

				if (maxUnits > 0)
				{
					String[] numOfUnitsResponse = getCommandAndParameters("Enter desired number of units (1-"
						+ maxUnits + "): ");

					if (numOfUnitsResponse.length > 0)
					{
						int numberOfUnits = Integer.parseInt(numOfUnitsResponse[0]);
						if (numberOfUnits > 0 && numberOfUnits <= maxUnits)
						{
							theCastle.makeUnits(creatureClass, numberOfUnits);
						}
						else
							System.out.println("Number of units is our of range.");
					}
					else
						System.out.println("Bad input.");
				}
				else
					System.out.println("Sorry, but you can't build units.");
			}
			else
				System.out.println("Unknown creature type.");
		}
	}

	public static String[] getCommandAndParameters(String cliPrompt)
	{
		String userInput = null;

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.print(cliPrompt);

		try
		{
			userInput = br.readLine();
		}
		catch (IOException ioe)
		{
			System.out.println("IO error trying to read user input !");
			ioe.printStackTrace();
			System.exit(1);
		}

		return userInput.split(" ");
	}

	/*
	 * removes dead players from game and return all of their belongings to the
	 * board
	 */
	/* side effect: if only one player is left playing - ends the game */
	private static void removeDeadPlayers(Board theBoard)
	{
		for (int player = 0; player < players.size(); player++)
		{
			if (!(players.get(player).isAlive()))
			{
				Player myPlayer = players.get(player);
				if (myPlayer.getHero() != null)
				{
					int x = myPlayer.getHero().getXPos();
					int y = myPlayer.getHero().getYPos();
					theBoard.getBoardState(x, y).setHero(null);
					myPlayer.getHero().kill();
				}

				for (int k = 0; k < resources.size(); k++)
					if ((resources.get(k).getOwner() != null)
						&& (resources.get(k).getOwner().equals(myPlayer)))
						resources.get(k).setOwner(null);

				String name = myPlayer.getName();
				players.remove(player);
				System.out.println(name + " is out of the game .");
			}
		}
	}

	public static void endGame(Player winner)
	{
		System.out.println("game ended.");
		if (winner!= null)
		{
			System.out.println("winner is: " + winner.getName() + " with a score of: " + winner.finalScore());
			scoreBoard.addToScoreBoard(winner, winner.finalScore());
		}
		scoreBoard.save();
		scoreBoard.print();
		System.exit(0);
	}

	public static Player isThereAWinner()
	{
		if (players.size() == 1)
			return players.firstElement();
		else
			return null;
	}
}