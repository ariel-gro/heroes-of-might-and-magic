package tau.heroes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class MainModule
{
	public static void main(String[] args)
	{
		boolean isGUI = selectView(args);
		GameController gameController = new GameController(isGUI);
		if (isGUI)
			runGraphicalView(gameController);
		else
			runConsoleView(gameController);
	}

	private static boolean selectView(String[] args)
	{
		if (args != null && args.length > 0)
			if (args[0].toLowerCase().equals("gui"))
				return true;
			else if (args[0].toLowerCase().equals("console"))
				return false;				
		
		String message = "Whould you like to play in graphical mode or console mode (g for graphical or c for console): ";
		String[] responses = getCommandAndParameters(message);

		if (responses.length > 0 && responses[0].startsWith("g"))
		{
			return true;
		}
		else
		{
			System.out.println("Defualt is console view, enjoy :) ");
			return false;
		}
	}

	private static void runGraphicalView(GameController gameController)
	{
		Display d = new Display();
		int numberOfPlayers = HeroesGui.getNumberOfPlayers();
		Vector<Player> players = HeroesGui.getPlayers(numberOfPlayers);
		if (players == null || players.isEmpty() || players.size() < 2
			|| players.get(0).isComputer())
		{
			Shell s = new Shell(d);
			MessageBox box = new MessageBox(s, SWT.ICON_ERROR);
			box
				.setMessage("Players' names are ilegal! make sure you follow the rules\n\t\tBye-Bye");
			box.open();
			return;
		}
		gameController.initNewGame(players);

		HeroesGui application = new HeroesGui(d, gameController);
		Shell shell = application.open();
		while (!shell.isDisposed())
		{
			if (!d.readAndDispatch())
				d.sleep();
		}
		d.dispose();
	}

	private static void runConsoleView(GameController gameController)
	{
		int numberOfPlayers = getNumberOfPlayers();
		Vector<Player> players = getPlayers(numberOfPlayers);
		gameController.initNewGame(players);
		HeroesConsole application = new HeroesConsole(gameController);
		application.run();
	}

	/**
	 * @return Number of players from user input
	 */
	private static int getNumberOfPlayers()
	{
		String message = "Enter number of players (" + Constants.MIN_PLAYERS + "-"
			+ Constants.MAX_PLAYERS + "): ";
		int numberOfPlayers = Integer.MIN_VALUE;
		String[] responses;

		do
		{
			responses = getCommandAndParameters(message);
			if (responses.length > 0)
				numberOfPlayers = Helper.tryParseInt(responses[0]);
		} while (!Helper
			.isIntBetween(numberOfPlayers, Constants.MIN_PLAYERS, Constants.MAX_PLAYERS));

		return numberOfPlayers;
	}

	private static Vector<Player> getPlayers(int numberOfPlayers)
	{
		String message;
		String[] responses;
		Vector<Player> players = new Vector<Player>();
		System.out.println("If you want one of the players will be the computer, enter "
			+ Player.COMPUTER_NAME + " as his name.");
		for (int i = 0; i < numberOfPlayers;)
		{
			message = "Please enter player " + (i + 1) + "'s name: ";
			responses = getCommandAndParameters(message);
			if (responses.length > 0 && responses[0].length() > 0)
			{
				players.add(new Player(responses[0]));
				i++;
			}
		}

		return players;
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
}