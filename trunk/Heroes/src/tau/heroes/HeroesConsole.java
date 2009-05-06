package tau.heroes;

import java.util.ArrayList;

public class HeroesConsole
{
	private GameController gameController;

	private enum commands
	{
		move("move hero to x,y on the map. Usage: move x y"),
		movesLeft("show haw many moves my hero has left in this turn"),
		endTurn("end the player turn. change turn to other player/s"),
		showDay("show what day is it for current player"),
		daysWithoutCastle("show how many days player is without a castle"),
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

	private enum CastleCommands
	{
		build("Build a creature factory. Usage: build [goblin|soldier|dwarf|archer|dragon]"),
		buildPrices("Get help about creature factory prices"),
		make("Make a new creature. Usage: make [goblin|soldier|dwarf|archer|dragon]"),
		makePrices("Get help about creature prices per unit"),
		split("Split units from the hero's army to the castle's army. Usage: split [goblin|soldier|dwarf|archer|dragon] numberOfUnits"),
		join("Join units to the hero's army from the castle's army. Usage: join [goblin|soldier|dwarf|archer|dragon] numberOfUnits"),
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

	public HeroesConsole(GameController gameController)
	{
		this.gameController = gameController;
	}

	private GameState getGameState()
	{
		return this.gameController.getGameState();
	}

	public void run()
	{
		while (true)
		{
			for (int playerIndex = 0; playerIndex < this.getGameState().getNumberOfPlayers();)
			{
				this.getGameState().setWhosTurn(playerIndex);
				Player player = this.getGameState().getPlayers().get(playerIndex);
				removeDeadPlayers();
				if (this.gameController.isThereAWinner() != null)
					endGame(this.gameController.isThereAWinner());

				if (player.isComputer())
				{
					playerIndex = handleComputerMove(playerIndex, player);
				}
				else
				{
					playerIndex = handleMenu(playerIndex, player);
				}
			}
		}
	}

	/**
	 * @param playerIndex
	 * @param player
	 * @return
	 */
	private int handleMenu(int playerIndex, Player player)
	{
		Hero hero = player.getHero();
		String prompt;

		if (hero == null)
			prompt = player.getName() + " You don't have any heroes to move with!  make your move:";
		else
			prompt = player.getName() + ", You are at " + hero.printLocation()
				+ ", make your move .";

		String[] userInput = MainModule.getCommandAndParameters(prompt);

		if (userInput.length == 0 || !Helper.isValueOf(commands.class, userInput[0]))
			System.out.println("Command not recognized !!!");
		else
			playerIndex = switchCommands(playerIndex, player, userInput);

		return playerIndex;
	}

	private int handleComputerMove(int playerIndex, Player player)
	{
		Hero hero = player.getHero();
		String[] computerMove = new String[3];
		if (hero != null && hero.alive())
		{
			// make a random move:
			computerMove[0] = commands.move.toString();
			computerMove[1] = String.valueOf(hero.getXPos() + (int) (Math.random() * 3) - 1);
			computerMove[2] = String.valueOf(hero.getYPos() + (int) (Math.random() * 3) - 1);
			playerIndex = switchCommands(playerIndex, player, computerMove);
		}
		// end turn:
		computerMove[0] = commands.endTurn.toString();
		playerIndex = switchCommands(playerIndex, player, computerMove);
		return playerIndex;
	}

	/**
	 * @param playerIndex
	 * @param player
	 * @param userInput
	 * @return
	 */
	private int switchCommands(int playerIndex, Player player, String[] userInput)
	{
		switch (commands.valueOf(userInput[0]))
		{
		case move:
			handleMoveCommand(player, userInput);
			break;
		case movesLeft:
			displayMessage(player.getName() + "`s hero has " + player.getMovesLeft()
				+ " moves left in this turn");
			break;
		case endTurn:
			playerIndex = handleEndTurnCommand(playerIndex, player);
			break;
		case showDay:
			displayMessage("Current Day is  :  " + player.getDayAsString());
			break;
		case daysWithoutCastle:
			displayMessage("Days witout castle  :  " + player.getDaysWithoutCastles());
			break;
		case castle:
			castleMenu(player);
			break;
		case highscore:
			handleHighscoreCommand();
			break;
		case clearScore:
			handleClearScoreCommand();
			break;
		case help:
			handleMainHelpCommand();
			break;
		case map:
			handleMapCommand(player);
			break;
		case legend:
			handleLegendCommand();
			break;
		case info:
			handleInfoCommand(player);
			break;
		case quit:
			endGame(null);
			break;
		case save:
			if (!userInput[1].endsWith(".sav"))
				userInput[1] += ".sav";
			handleSaveCommand(userInput);
			break;
		case load:
			playerIndex = handleLoadCommand(userInput);
			break;
		}
		return playerIndex;
	}

	/**
	 * @param player
	 * @param userInput
	 */
	private void handleMoveCommand(Player player, String[] userInput)
	{
		if (userInput.length < 3)
			System.out.println("Wrong parameters.");
		else
		{
			int newX, newY;
			newX = Helper.tryParseInt(userInput[1]);
			newY = Helper.tryParseInt(userInput[2]);

			if (!Helper.isIntBetween(newX, 0, Constants.BOARD_SIZE - 1)
				|| !Helper.isIntBetween(newY, 0, Constants.BOARD_SIZE - 1))
				System.out.println("Wrong parameters.");
			else if (player.move(newX, newY, this.gameController.getGameState().getBoard()))
				handleMapCommand(player);
			else
			{
				System.out.println("Illegal move ! You can only move " + player.getMovesLeft()
					+ " steps more .");
			}
		}
	}

	/**
	 * @param playerIndex
	 * @param player
	 * @return
	 */
	private int handleEndTurnCommand(int playerIndex, Player player)
	{
		player.endTurn();

		removeDeadPlayers();
		if (this.gameController.isThereAWinner() != null)
			endGame(this.gameController.isThereAWinner());

		playerIndex = (playerIndex + 1) % this.gameController.getGameState().getNumberOfPlayers();
		return playerIndex;
	}

	/**
	 *
	 */
	private void handleHighscoreCommand()
	{
		System.out.print(Helper.getScoreBoard().print());
	}

	/**
	 *
	 */
	private void handleClearScoreCommand()
	{
		String message = "Yes or No?\n";
		String[] responses;

		System.out.println("Are you sure yo want to clear the highscore table?");
		System.out.println("this action can not be reversed");
		responses = MainModule.getCommandAndParameters(message);
		if (responses.length > 0
			&& (responses[0].startsWith("y") || (responses[0].startsWith("Y"))))
		{
			Helper.getScoreBoard().clearScoreBoard();
		}
	}

	/**
	 *
	 */
	private void handleMainHelpCommand()
	{
		for (commands cmd : commands.values())
			System.out.println(cmd.toString() + " - " + cmd.description);
	}

	/**
	 * @param player
	 */
	private void handleMapCommand(Player player)
	{
		this.gameController.getGameState().getBoard().printBoard(player.getVisibleBoard());
	}

	/**
	 *
	 */
	private void handleLegendCommand()
	{
		this.gameController.getGameState().getBoard().printLegend();
	}

	/**
	 * @param player
	 * @param hero
	 */
	private void handleInfoCommand(Player player)
	{
		player.displayMines();
		player.displayTreasury();

		Hero hero = player.getHero();
		if (hero != null)
		{
			System.out.println("You have " + hero.getDefenseSkill() + " defense skill and "
				+ hero.getAttackSkill() + " attack skill");
			System.out.println("You have an army:");
			System.out.println(hero.getArmy().toString());
		}

		for (int i = 0; i < player.getCastles().size(); i++)
			System.out.println(player.getCastles().get(i).toInfoString());
	}

	/**
	 * @param userInput
	 */
	private int handleLoadCommand(String[] userInput)
	{
		if (userInput.length < 2)
			System.out.println("Illegal input.");
		else
		{
			String fileName = userInput[1];
			if (!fileName.endsWith(".sav"))
			{
				displayMessage("Not a valid Heroes *.sav file.\nTry again or start a new game");
			}
			if (this.gameController.loadGame(fileName))
				System.out.println("Game has been load from '" + fileName + "'.");
		}
		return this.gameController.getGameState().getWhosTurn();
	}

	/**
	 * @param userInput
	 */
	private void handleSaveCommand(String[] userInput)
	{
		if (userInput.length < 2)
			System.out.println("Illegal input.");
		else
		{
			String fileName = userInput[1];
			if (this.gameController.saveGame(fileName))
				System.out.println("Game has been save on '" + fileName + "'.");
		}
	}

	private void castleMenu(Player player)
	{
		ArrayList<Castle> playerCastles = player.getCastles();
		if (playerCastles.size() == 0)
		{
			System.out.println("Sorry, you don't have any castles");
			return;
		}

		Castle theCastle = handleCastleChoice(playerCastles);
		if (theCastle == null)
		{
			System.out.println("Sorry, bad input");
			return;
		}

		while (true)
		{
			System.out.println("Castle menu of " + theCastle.printLocation());
			String[] userInput = MainModule.getCommandAndParameters("Enter a command: ");
			if (userInput.length == 0 || !Helper.isValueOf(CastleCommands.class, userInput[0]))
				System.out.println("Command not recognized !!!");
			else if (userInput.length > 0)
			{
				if (switchCastleCommands(player, theCastle, userInput))
					return;
			}
		}
	}

	/**
	 * @param player
	 * @param theCastle
	 * @param userInput
	 * @return true if needs to exit from castle menu
	 */
	private boolean switchCastleCommands(Player player, Castle theCastle, String[] userInput)
	{
		switch (CastleCommands.valueOf(userInput[0]))
		{
		case build:
			handleBuildCommand(player, theCastle, userInput);
			break;
		case buildPrices:
			displayMessage(GameController.handleBuildPricesCommand());
			break;
		case make:
			handleMakeCommand(player, theCastle, userInput);
			break;
		case makePrices:
			displayMessage(GameController.handleMakePricesCommand());
			break;
		case help:
			handleCastleHelpCommand();
			break;
		case split:
			handleSplitCommand(player, theCastle, userInput);
			break;
		case join:
			handleJoinCommand(player, theCastle, userInput);
			break;
		case exit:
			return true;
		}

		return false;
	}

	/**
	 *
	 */
	private void handleCastleHelpCommand()
	{
		for (CastleCommands cmd : CastleCommands.values())
			System.out.println(cmd.toString() + " - " + cmd.getDescription());
	}

	/**
	 * @param playerCastles
	 * @return
	 */
	private Castle handleCastleChoice(ArrayList<Castle> playerCastles)
	{
		Castle theCastle = null;

		if (playerCastles.size() > 1)
		{
			System.out.println("Please choose one castle:");
			for (int i = 0; i < playerCastles.size(); i++)
				System.out.println((i + 1) + ". " + playerCastles.get(i).printLocation());
			String[] response = MainModule.getCommandAndParameters("Please enter castle number: ");
			if (response.length > 0)
			{
				int index = Helper.tryParseInt(response[0]);
				if (Helper.isIntBetween(index, 1, playerCastles.size()))
					theCastle = playerCastles.get(index - 1);
			}
		}
		else
			theCastle = playerCastles.get(0);
		return theCastle;
	}

	private static void handleBuildCommand(Player player, Castle theCastle, String[] response)
	{
		if (response.length > 1)
		{
			CreatureFactory factory = CreatureFactory.getCreatureFactory(response[1]);

			if (factory != null)
			{
				Class<? extends CreatureFactory> factoryClass = factory.getClass();

				if (theCastle.hasFactory(factoryClass))
					System.out.println("There is already a factory of this type in this castle");
				else
				{
					if (theCastle.canBuildFactory(factoryClass))
						theCastle.addFactory(theCastle.buildFactory(factoryClass));
					else
					{
						String msg = theCastle.getPlayer().getName()
							+ " doesn't have enough resources.\n\n" + "Need:\n";

						for (ResourceType rType : ResourceType.values())
						{
							msg += rType.getTypeName() + ":\t "
								+ factory.getPrice(rType.getTypeName()) + "\n";
						}
						msg += "\nHas only:\n";
						for (ResourceType rType : ResourceType.values())
						{
							msg += rType.getTypeName()
								+ ":\t "
								+ theCastle.getPlayer().getCurrentTreasuryAmount(rType
									.getTypeName()) + "\n";
						}
						System.out.println(msg);
					}
				}
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

			if ((hero.getXPos() != theCastle.getXPos()) || (hero.getYPos() != theCastle.getYPos()))
			{
				System.out.println("You must be in the castle in order to split units !");
				return;
			}

			int numberOfUnits = Helper.tryParseInt(response[2]);
			if (numberOfUnits < 1)
			{
				System.out.println("Illegal input.");
				return;
			}

			CreatureFactory factory = CreatureFactory.getCreatureFactory(response[1]);
			if (factory == null)
			{
				System.out.println("Unknown creature type!");
				return;
			}

			Creature creature = factory.buildCreature(numberOfUnits);

			if (!theCastle.canAddToArmy(creature.getClass()))
			{
				System.out.println("Army in " + theCastle.printLocation() + " is full.");
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

			int numberOfUnits = Helper.tryParseInt(response[2]);
			if (numberOfUnits < 1)
			{
				System.out.println("Illegal input.");
				return;
			}

			CreatureFactory factory = CreatureFactory.getCreatureFactory(response[1]);
			if (factory == null)
			{
				System.out.println("Unknown creature type!");
				return;
			}

			Creature creature = factory.buildCreature(numberOfUnits);

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
			Class<? extends Creature> creatureClass = Creature.getCreatureClass(response[1]);

			if (creatureClass != null)
			{
				int maxUnits = theCastle.getAvailableUnits(creatureClass);

				if (maxUnits > 0)
				{
					String[] numOfUnitsResponse = MainModule
						.getCommandAndParameters("Enter desired number of units (1-" + maxUnits
							+ "): ");

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

	private void removeDeadPlayers()
	{
		for (Player player : this.gameController.removeDeadPlayers())
			System.out.println(player.getName() + " is out of the game .");
	}

	private void endGame(Player winner)
	{
		System.out.println("game ended.");
		if (winner != null)
		{
			System.out.println("winner is: " + winner.getName() + " with a score of: "
				+ winner.finalScore());
			Helper.getScoreBoard().addToScoreBoard(winner, winner.finalScore());
		}
		Helper.getScoreBoard().save();
		handleHighscoreCommand();
		System.exit(0);
	}

	public static void displayMessage(String message)
	{
		System.out.println(message + "\n");
	}
}
