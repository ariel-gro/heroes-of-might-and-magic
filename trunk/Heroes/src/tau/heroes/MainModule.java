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
	public enum commands {

		move("move hero to x,y on the map. Usage: move x y"),
		endTurn("end the player turn. change turn to other player/s"),
		castle("enter the castle menu"),
		help("get help for the possible commands"),
		info("get information about your player"),
		map("prints the map visible to you"),
		legend("prints the map legend"),
		save("save the current game. Usage: save gameName"),
		load("loads a saved game. Usage: load gameName"),
		quit("quit the game");

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

	public enum CastleCommands {
		build("Build a creature factory. Usage: build [goblin|soldier]"),
		make("Make a new creature. Usage: make [goblin|soldier]"),
		split("Split units from the hero to the castle's army. Usage: split[goblin|soldier, numOfUnits]"),
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

	static final int BOARD_SIZE = 40;
	/**
	 * @param args
	 * @throws IOException
	 */

	public static void save(String fileName, Vector<Player> players, Vector<Hero> heroes, Vector<Castle> castles, Vector<Resource> resources, Board theBoard)
	{
		//GameState gameState = new GameState(players, heroes, castles, resources, theBoard);

		try
		{
			File saveFile = new File(fileName);
			saveFile.createNewFile();
			FileOutputStream fileOut = new FileOutputStream(saveFile);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(new GameState(players, heroes, castles, resources, theBoard));
			out.close();
			fileOut.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}


	public static GameState load(String fileName)
	{
		try
		{
			FileInputStream fileIn = new FileInputStream(fileName);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			GameState gameState = (GameState)in.readObject();
			in.close();
			fileIn.close();
			return gameState;
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Can't find your file!");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args)
	{
		players = new Vector<Player>();
		heroes = new Vector<Hero>();
		castles = new Vector<Castle>();
		resources = new Vector<Resource>();
		String viewSelection = (getCommandAndParameters("Enter visual display method (g for graphical or c for consol):")[0]);
		if(viewSelection.startsWith("g"))
		{
			runGraphicalView();
			return;
		}
		else
		{
			System.out.println("defualt is consol view, enjoy :) ");
		}
		runConsoleView();
	}

	private static void runGraphicalView() {
		Display d = new Display();
		HeroesGui application = new HeroesGui(d);
		Shell shell = application.open();
		while (!shell.isDisposed())
		{
			if (!d.readAndDispatch())
				d.sleep();
		}
		d.dispose();
	}

	private static void runConsoleView() {
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
			castles.add(new Castle(players.get(i), theBoard, randomX, randomY));
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


		while (true)
		{

			for (int player = 0 ; player < numOfPlayers ; )
			{
				removeDeadPlayers(theBoard);
				if (isThereAWinner() != null)
					endGame(isThereAWinner());
				numOfPlayers = players.size();
				Hero h = players.get(player).getHero();
				String temp;

				int oldX = 0;
				int oldY = 0;
				if(h == null)
				{
					temp = players.get(player).getName()+" You don't have any heroes to move with!  make your move:";
				}
				else
				{
					oldX = h.getXPos();
					oldY = h.getYPos();
					temp = players.get(player).getName() + " You are at (" + oldX + ","+ oldY + "), make your move .";
				}
				userInput = getCommandAndParameters(temp);
				if(userInput[0].equals(commands.move.toString()))
				{
					int newX,newY;
					try
					{
						newX = Integer.parseInt(userInput[1]);
						newY = Integer.parseInt(userInput[2]);
					}
					catch(Exception ex)
					{
						System.out.println("Wrong parameters, command not recognized !!!");
						continue;
					}
					if(players.get(player).move(newX,newY, theBoard))
					{
						removeDeadPlayers(theBoard);
						if (isThereAWinner() != null)
							endGame(isThereAWinner());
						numOfPlayers = players.size();
						theBoard.printBoard(players.get(player).getVisibleBoard());
					}
					else
					{
						System.out.println("Illegal move ! You can only move " + players.get(player).getMovesLeft() + " steps more .");
					}
				}
				else if(userInput[0].equals(commands.endTurn.toString()))
				{
					players.get(player).endTurn();

					removeDeadPlayers(theBoard);
					if (isThereAWinner() != null)
						endGame(isThereAWinner());
					numOfPlayers = players.size();
					player = (player + 1)%numOfPlayers;
					continue;
				}
				else if(userInput[0].equals(commands.castle.toString()))
				{
					castleMenu(player);
				}
				else if(userInput[0].equals(commands.help.toString()))
				{
					for (commands cmd : commands.values())
						System.out.println(cmd.toString() + " - " + cmd.description);
				}
				else if(userInput[0].equals(commands.map.toString()))
				{
					theBoard.printBoard(players.get(player).getVisibleBoard());
				}
				else if(userInput[0].equals(commands.legend.toString()))
				{
					theBoard.printLegend();
				}
				else if(userInput[0].equals(commands.info.toString()))
				{
					players.get(player).displayResources();
					players.get(player).displayResourcesAmounts();
					if(h != null)
					{
						System.out.println("You have "+h.getDefenseSkill()+" defense skill and "+h.getAttackSkill()+" attack skill");
						System.out.println("You have an army:");
						System.out.println(h.getArmy().toString());
					}
				}
				else if(userInput[0].equals(commands.quit.toString()))
				{
					System.exit(0);
				}
				else if(userInput[0].equals(commands.save.toString()))
				{
					String fileName = userInput[1];
					save(fileName, players, heroes, castles, resources, theBoard);
				}
				else if(userInput[0].equals(commands.load.toString()))
				{
					String fileName = userInput[1];
					GameState gameState = load(fileName);
					players = gameState.getPlayers();
					heroes = gameState.getHeroes();
					castles = gameState.getCastles();
					resources = gameState.getResources();
					theBoard = gameState.getBoard();
					numOfPlayers = players.size();
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

		if (playerCastles.size() == 0) {
			System.out.println("Sorry, you don't have any castles");
			return;
		}

		Castle theCastle = null;

		if (playerCastles.size() > 1) {
			System.out.println("Please choose one castle:");
			for (int i = 0; i < playerCastles.size(); i++)
				System.out.println((i+1) + ". " + playerCastles.get(i).toLocationString());
			String[] response = getCommandAndParameters("Enter castle number: ");
			int index = Integer.parseInt(response[0]) - 1;
			if (index < 0 || index >= playerCastles.size()) {
				System.out.println("Sorry, bad input");
				return;
			}
			theCastle = playerCastles.get(index);
		}
		else
			theCastle = playerCastles.get(0);

		while (true) {
			System.out.println("Castle menu of " + theCastle.toLocationString());
			String[] response = getCommandAndParameters("Enter a command: ");
			if (response.length > 0)
				if (response[0].equals(CastleCommands.build.toString())) {
					handleBuildCommand(player, theCastle, response);
				}
				else if (response[0].equals(CastleCommands.make.toString())) {
					handleMakeCommand(player, theCastle, response);
				}
				else if (response[0].equals(CastleCommands.help.toString())) {
					for (CastleCommands cmd : CastleCommands.values())
						System.out.println(cmd.toString() + " - " + cmd.getDescription());
				}
				else if (response[0].equals(CastleCommands.split.toString()))
				{
					handleSplitCommand(player, theCastle, response);
				}
				else if (response[0].equals(CastleCommands.exit.toString())) {
					return;
				}
				else
					System.out.println("Unknown command");
		}
	}

	private static void handleBuildCommand(Player player, Castle theCastle,	String[] response) {
		if (response.length > 1) {
			CreatureFactory factory = null;

			if (response[1].equals("goblin"))
				factory = new GoblinFactory();
			else if (response[1].equals("soldier"))
				factory = new SoldierFactory();

			if (factory != null) {
				Class<? extends CreatureFactory> factoryClass = factory.getClass();

				if (theCastle.hasFactory(factoryClass))
					System.out.println("There is already a factory of this type in this castle");
				else
					if (theCastle.canBuildFactory(factoryClass))
						theCastle.addFactory(theCastle.buildFactory(factoryClass));
			}
			else
				System.out.println("Unknown creature type");
		}
	}


	private static void handleSplitCommand(Player player, Castle theCastle, String[] response)
	{
		Creature creature = null;

		if (response.length == 3)
		{
			Hero hero = player.getHero();
			int numOfUnits = Integer.parseInt(response[2]);

			if ((hero.getXPos() != theCastle.getXPos()) || (hero.getYPos() != theCastle.getYPos()))
			{
				System.out.println("You must be in the castle in order to split units !");
				return;
			}

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
				System.out.println("Illegal move !");
				return;
			}
			if (!(hero.removeFromArmy(creature)))
			{
				System.out.println("You dont have enough units to split");
			}
			theCastle.addToArmy(creature);
			return;
		}
		System.out.println("Illegal move !");
	}



	private static void handleMakeCommand(Player player, Castle theCastle, String[] response) {
		if (response.length > 1) {
			Class<? extends Creature> creatureClass = null;

			if (response[1].equals("goblin"))
				creatureClass = Goblin.class;
			else if (response[1].equals("soldier"))
				creatureClass = Soldier.class;

			if (creatureClass != null) {
				int maxUnits = theCastle.getAvailableUnits(creatureClass);

				if (maxUnits > 0) {
					String[] numOfUnitsResponse = getCommandAndParameters("Enter desired number of units (1-" +
							maxUnits + "): ");

					if (numOfUnitsResponse.length > 0) {
						int numberOfUnits = Integer.parseInt(numOfUnitsResponse[0]);
						if (numberOfUnits > 0 && numberOfUnits <= maxUnits) {
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

	/* removes dead players from game and return all of their belongings to the board */
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

				for (int k = 0 ; k < resources.size() ; k++)
					if ((resources.get(k).getOwner() != null) && (resources.get(k).getOwner().equals(myPlayer)))
						resources.get(k).setOwner(null);

				String name = myPlayer.getName();
				players.remove(player);
				System.out.println(name + " is out of the game .");
			}
		}
	}


	private static void endGame(Player winner)
	{
		System.out.println("game ended.");
		System.out.println("winner is: "+winner.getName());
		//TODO: after implementing score, display player's game score
		//and update leader score board if needed
		System.out.println("quitting game");
		System.exit(0);
	}


	private static Player isThereAWinner()
	{
		if (players.size() == 1)
			return players.firstElement();
		else
			return null;
	}
}