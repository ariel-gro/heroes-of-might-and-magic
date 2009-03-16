package tau.heroes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class MainModule
{
	public enum commands {

		move("move hero to x,y on the map. Usage: move x y"),
		endTurn("end the player turn. change turn to other player/s"),
		help("get help for the possible commands"),
		info("get information about your player"),
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

	static String[] userInput;
	static Vector<Player> players;
	static Vector<Hero> heroes;
	static Vector<Castle> castles;
	static Vector<Resource> resources;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args)
	{
		players = new Vector<Player>();
		heroes = new Vector<Hero>();
		castles = new Vector<Castle>();
		resources = new Vector<Resource>();

		int numOfPlayers = Integer.parseInt(getCommandAndParameters("Enter number of players:")[0]);

		for (int i = 0; i < numOfPlayers; i++)
		{
			userInput = getCommandAndParameters("Enter the Name of player " + (i + 1) + ":");
			players.add(new Player(userInput[0]));
		}

		int boardSize = Integer.parseInt(getCommandAndParameters("Enter board size:")[0]);
		Board theBoard = new Board(boardSize);

		for (int i = 0; i < numOfPlayers; i++)
		{
			int randomX = (int) (Math.random() * (boardSize - 1));
			int randomY = (int) (Math.random() * (boardSize - 1));
			Hero h = new Hero(players.get(i), theBoard, randomX, randomY);
			heroes.add(h);
			castles.add(new Castle(players.get(i), theBoard, randomX, randomY));
			players.get(i).setHero(heroes.get(i));
		}

		for (int i = 0; i < numOfPlayers; i++)
		{
			for (ResourceType rt : ResourceType.values())
			{
				int randomX = (int) (Math.random() * (boardSize - 1));
				int randomY = (int) (Math.random() * (boardSize - 1));

				if (theBoard.getBoardState(randomX, randomY).getIsEmpty())
					resources.add(new Resource(rt, theBoard, randomX, randomY));
			}
		}


		while (true)
		{
			CanAct.reset();
			for (int player = 0; player < numOfPlayers;)
			{

				Hero h = players.get(player).getHero();
				boolean bCanMove = true;
				String temp;
				CanAct.reset();

				theBoard.printBoard();
				int oldX = 0;
				int oldY = 0;
				if(h == null)
				{
					temp = players.get(player).getName()+" You don't have any heroes to move with!  make your move:";
					bCanMove = false;
				}
				else
				{
					oldX = h.getXPos();
					oldY = h.getYPos();
					temp = players.get(player).getName() + " You are at (" + oldX + ", "+ oldY + "), make your move:";
				}
				userInput = getCommandAndParameters(temp);
				if(userInput[0].equals(commands.move.toString()) && bCanMove)
				{
					int newX = Integer.parseInt(userInput[1]);
					int newY = Integer.parseInt(userInput[2]);
					if (CanAct.moveUpdate(oldX, oldY, newX, newY))
					{
						players.get(player).getHero().moveTo(newX, newY, theBoard);
					}
					else
					{
						System.out.println("Illegal move ! You can only move " + CanAct.getMovesLeft() + " steps more .");
					}
				}
				else if(userInput[0].equals(commands.endTurn.toString()))
				{
					players.get(player).endTurn();
					player++;
					CanAct.reset();
					continue;
				}
				else if(userInput[0].equals(commands.help.toString()))
				{
					for (commands cmd : commands.values())
						System.out.println(cmd.toString() + " - " + cmd.description);
				}
				else if(userInput[0].equals(commands.info.toString()))
				{
					players.get(player).displayResources();
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
				else
				{
					System.out.println("Command not recognized !!!");
				}
			}
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
		} catch (IOException ioe)
		{
			System.out.println("IO error trying to read user input !");
			ioe.printStackTrace();
			System.exit(1);
		}

		return userInput.split(" ");
	}
}