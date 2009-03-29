package tau.heroes;

import java.io.File;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class HeroesGui
{
	private Shell shell;

	private String file = null;

	private boolean isModified = false;

	private static IconCache iconCache = new IconCache();

	final int numOfCells;

	private Color black;

	private Color green;

	private Display display;

	static Control currentChild = null;

	private GameController gameController;

	private static int currentPlayerIndex = 0;

	private Composite boardComposite;

	private Composite statusComposite;

	private ScrolledComposite sc;

	public Display getDisplay()
	{
		return display;
	}

	public HeroesGui(Display d, GameController gameController)
	{
		this.display = d;
		this.gameController = gameController;
		this.numOfCells = gameController.getGameState().getBoard().getSize();
		iconCache.initResources(display);
	}

	public Shell open()
	{
		shell = new Shell(display/*, SWT.APPLICATION_MODAL*/);
		shell.setLayout(new FillLayout());
		shell.setImage(iconCache.stockImages[iconCache.appIcon]);
		shell.setText("Heroes of Might and Magic");
		shell.setMaximized(true);
		black = display.getSystemColor(SWT.COLOR_BLACK);
		green = display.getSystemColor(SWT.COLOR_GREEN);
		shell.setBackground(black);
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e)
			{
				e.doit = close();
			}
		});

		createMenuBar();

		GridLayout layout = new GridLayout();
		shell.setLayout(layout);

		GridData sashData = new GridData();
		sashData.grabExcessHorizontalSpace = true;
		sashData.grabExcessVerticalSpace = true;
		sashData.horizontalAlignment = GridData.FILL;
		sashData.verticalAlignment = GridData.FILL;
		SashForm sash = new SashForm(shell, SWT.BORDER);
		sash.setOrientation(SWT.HORIZONTAL);
		sash.setLayoutData(sashData);

		sc = new ScrolledComposite(sash, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		createBoardWindow();

		createStatusWindow(sash);

		sash.setWeights(new int[] { 85, 15 });

		shell.open();
		return shell;
	}

	private boolean close()
	{
		if (isModified)
		{
			// ask user if they want to save current address book
			MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
			box.setText(shell.getText());
			box.setMessage("Close_save");

			int choice = box.open();
			if (choice == SWT.CANCEL)
			{
				return false;
			} else if (choice == SWT.YES)
			{
				if (!save())
					return false;
			}
		}

		iconCache.freeResources();

		return true;
	}

	private int fromBoardToDisplayIcons(int x, int y)
	{
		BoardState bs = gameController.getGameState().getBoard().getBoardState(x, y);

		if ((bs.getHero()) != null)
		{
			return iconCache.heroIcon;
		} else if ((bs.getCastle()) != null)
		{
			return iconCache.castleIcon;
		} else if ((bs.getResource()) != null)
		{
			if (bs.getResource().getType().getTypeName().equals("wood"))
			{
				return iconCache.woodIcon;
			} else if (bs.getResource().getType().getTypeName().equals("gold"))
			{
				return iconCache.goldMineIcon;
			} else if (bs.getResource().getType().getTypeName().equals("stone"))
			{
				return iconCache.stoneIcon;
			}
		}

		return iconCache.grassIcon;
	}

	private String fromBoardToDisplayDecription(int x, int y)
	{
		BoardState bs = gameController.getGameState().getBoard().getBoardState(x, y);

		if ((bs.getHero()) != null)
		{
			return bs.getHero().player.getName() + "'s Hero";
		} else if ((bs.getCastle()) != null)
		{
			return bs.getCastle().getPlayer().getName() + "'s Castle";
		} else if ((bs.getResource()) != null)
		{
			if (bs.getResource().getType().getTypeName().equals("wood"))
			{
				return bs.getResource().getType().getTypeName() + " owned by "
						+ (bs.getResource().getOwner() == null ? "none" : bs.getResource().getOwner().getName());
			} else if (bs.getResource().getType().getTypeName().equals("gold"))
			{
				return bs.getResource().getType().getTypeName() + " owned by "
						+ (bs.getResource().getOwner() == null ? "none" : bs.getResource().getOwner().getName());
			} else if (bs.getResource().getType().getTypeName().equals("stone"))
			{
				return bs.getResource().getType().getTypeName() + " owned by "
						+ (bs.getResource().getOwner() == null ? "none" : bs.getResource().getOwner().getName());
			}
		}

		return "";
	}

	private void createBoardWindow()
	{
		boolean[][] isVisible;

		if (boardComposite != null && boardComposite.isDisposed() == false)
		{
			boardComposite.dispose();
			//iconCache.freeResources();
			//iconCache.initResources(display);
		}

		boardComposite = new Composite(sc, SWT.NONE);
		//boardComposite.setEnabled(false);
		boardComposite.setBackground(black);
		GridData d = new GridData(GridData.FILL_BOTH);
		boardComposite.setLayoutData(d);

		GridLayout tableLayout = new GridLayout();
		tableLayout.numColumns = numOfCells;
		tableLayout.makeColumnsEqualWidth = true;
		tableLayout.horizontalSpacing = 0;
		tableLayout.verticalSpacing = 0;
		boardComposite.setLayout(tableLayout);

		isVisible = gameController.getGameState().getPlayers().elementAt(currentPlayerIndex).getVisibleBoard();

		for (int y = 0; y < numOfCells; y++)
		{
			for (int x = 0; x < numOfCells; x++)
			{

				Label b = new Label(boardComposite, SWT.NONE);
				int t = fromBoardToDisplayIcons(x, y);

				if (isVisible[x][y])
					b.setImage(iconCache.stockImages[t]);
				else
					b.setImage(iconCache.stockImages[iconCache.grassIcon]);

				String description;
				if (t != iconCache.grassIcon)
				{
					description = fromBoardToDisplayDecription(x, y);
					b.setToolTipText(description);
				}

				if (t == iconCache.heroIcon)
					b.setMenu(createHeroPopUpMenu());

				if (t == iconCache.castleIcon)
					b.setMenu(createCastlePopUpMenu());
			}
		}

		sc.setContent(boardComposite);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setMinSize(boardComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		Control[] controls = boardComposite.getChildren();
		for (int i = 0; i < controls.length; i++)
		{
			int x = i % numOfCells;
			int y = i / numOfCells;
			int t = fromBoardToDisplayIcons(x, y);

			if (isVisible[x][y] && t == iconCache.heroIcon)
			{
				Rectangle bounds = controls[i].getBounds();
				Rectangle area = sc.getClientArea();
				Point origin = sc.getOrigin();
				if (origin.x > bounds.x)
					origin.x = Math.max(0, bounds.x);
				if (origin.y > bounds.y)
					origin.y = Math.max(0, bounds.y);
				if (origin.x + area.width < bounds.x + bounds.width)
					origin.x = Math.max(0, bounds.x + bounds.width - area.width / 2);
				if (origin.y + area.height < bounds.y + bounds.height)
					origin.y = Math.max(0, bounds.y + bounds.height - area.height / 2);

				sc.setOrigin(origin);
			}
		}
	}

	private void createStatusWindow(Composite parent)
	{
		statusComposite = new Composite(parent, SWT.BORDER);
		statusComposite.setBackground(black);
		GridData d = new GridData(GridData.FILL_BOTH);
		statusComposite.setLayoutData(d);

		Label tempLabel = new Label(statusComposite, SWT.NONE);
		tempLabel.setText("THIS IS WHERE ALL \n THE STATUS WILL BE");
		tempLabel.setBackground(green);
		GridLayout tempLayout = new GridLayout();
		statusComposite.setLayout(tempLayout);
	}

	/**
	 * Creates the menu at the top of the shell where most of the programs
	 * functionality is accessed.
	 *
	 * @return The <code>Menu</code> widget that was created
	 */
	private Menu createMenuBar()
	{
		Menu menuBar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menuBar);

		// create each header and subMenu for the menuBar
		createFileMenu(menuBar);
		createHelpMenu(menuBar);

		return menuBar;
	}

	private void displayError(String msg)
	{
		MessageBox box = new MessageBox(shell, SWT.ICON_ERROR);
		box.setMessage(msg);
		box.open();
	}

	private void openFileDlg()
	{
		FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);

		fileDialog.setFilterExtensions(new String[] { "*.sav;", "*.*" });
		fileDialog.setFilterNames(new String[] { "Saved Games" + " (*.sav)", "All Files" + " (*.*)" });
		String name = fileDialog.open();

		if (name == null)
			return;
		File file = new File(name);
		if (!file.exists())
		{
			displayError("File " + file.getName() + " " + "Does_not_exist");
			return;
		}

		Cursor waitCursor = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
		shell.setCursor(waitCursor);

		this.gameController.loadGame(name);

		createBoardWindow();

		shell.setCursor(null);
		waitCursor.dispose();

	}

	private boolean save()
	{
		if (file == null)
			return saveAs();

		Cursor waitCursor = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
		shell.setCursor(waitCursor);

		System.out.println("Saving game to: " + file);
		//MainModule.save(file, gameState.getPlayers(), gameState.getHeroes(), gameState.getCastles(), gameState.getResources(), gameState.getBoard());
		this.gameController.saveGame(file);

		shell.setCursor(null);
		waitCursor.dispose();

		return true;
	}

	private boolean saveAs()
	{

		FileDialog saveDialog = new FileDialog(shell, SWT.SAVE);
		saveDialog.setFilterExtensions(new String[] { "*.sav;", "*.*" });
		saveDialog.setFilterNames(new String[] { "Saved Games" + " (*.sav)", "All Files" + " (*.*)" });

		saveDialog.open();
		String name = saveDialog.getFileName();

		if (name.equals(""))
			return false;

		if (name.indexOf(".sav") != name.length() - 4)
		{
			name += ".sav";
		}

		String file = saveDialog.getFilterPath() + "\\" + name;
		if (new File(file).exists())
		{
			MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
			box.setText("Overite Existing File");
			box.setMessage("File " + name + " " + "Already exists, would you like to overwrite?");
			if (box.open() != SWT.YES)
			{
				return false;
			}
		}
		this.file = file;
		return save();
	}

	/**
	 * Creates all the items located in the File submenu and associate all the
	 * menu items with their appropriate functions.
	 *
	 * @param menuBar
	 *            Menu the <code>Menu</code> that file contain the File
	 *            submenu.
	 */
	private void createFileMenu(Menu menuBar)
	{
		// File menu.
		MenuItem item = new MenuItem(menuBar, SWT.CASCADE);
		item.setText("File");
		Menu menu = new Menu(shell, SWT.DROP_DOWN);
		item.setMenu(menu);
		/**
		 * Adds a listener to handle enabling and disabling some items in the
		 * Edit submenu.
		 */
		menu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e)
			{
				//Menu menu = (Menu) e.widget;
				//MenuItem[] items = menu.getItems();

				//////// ************ TBD ********** //////////////////
			}
		});

		// File -> New Contact
		MenuItem subItem = new MenuItem(menu, SWT.NULL);
		subItem.setText("New Game");
		subItem.setAccelerator(SWT.MOD1 + 'N');
		subItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				//newEntry();
			}
		});

		// File -> Open
		subItem = new MenuItem(menu, SWT.NULL);
		subItem.setText("Open Saved Game");
		subItem.setAccelerator(SWT.MOD1 + 'O');
		subItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				if (close())
				{
					openFileDlg();
				}
			}
		});

		// File -> Save.
		subItem = new MenuItem(menu, SWT.NULL);
		subItem.setText("Save Game");
		subItem.setAccelerator(SWT.MOD1 + 'S');
		subItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				save();
			}
		});

		// File -> Save As.
		subItem = new MenuItem(menu, SWT.NULL);
		subItem.setText("Save Game as");
		subItem.setAccelerator(SWT.MOD1 + 'A');
		subItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				saveAs();
			}
		});

		new MenuItem(menu, SWT.SEPARATOR);

		// File -> Exit.
		subItem = new MenuItem(menu, SWT.NULL);
		subItem.setText("Exit");
		subItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				shell.close();
			}
		});
	}

	/**
	   * Creates all items located in the popup menu and associates all the menu
	   * items with their appropriate functions.
	   *
	   * @return Menu The created popup menu.
	   */
	private Menu createHeroPopUpMenu()
	{
		Menu popUpMenu = new Menu(shell, SWT.POP_UP);

		/**
		 * Adds a listener to handle enabling and disabling some items in the
		 * Edit submenu.
		 */
		popUpMenu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e)
			{
				//Menu menu = (Menu) e.widget;
				//MenuItem[] items = menu.getItems();
				//int count = table.getSelectionCount();

			}
		});

		MenuItem item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Move");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				InputDialog numberInput = new InputDialog(Display.getCurrent().getActiveShell(), "", "Enter 5-8 characters", "", null);
				
				 if (numberInput.open() == Window.OK) {
			          // User clicked OK; update the label with the input
			          //label.setText(numberInput.getValue());
			        }
			}
		});

		//new MenuItem(popUpMenu, SWT.SEPARATOR);

		item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("End Turn");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				handleEndTurnCommand();
			}
		});

		return popUpMenu;
	}

	/**
	 * Creates all items located in the popup menu and associates all the menu
	 * items with their appropriate functions.
	 *
	 * @return Menu The created popup menu.
	 */
	private Menu createCastlePopUpMenu()
	{
		Menu popUpMenu = new Menu(shell, SWT.POP_UP);

		/**
		 * Adds a listener to handle enabling and disabling some items in the
		 * Edit submenu.
		 */
		popUpMenu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e)
			{
				//Menu menu = (Menu) e.widget;
				//MenuItem[] items = menu.getItems();
				//int count = table.getSelectionCount();

			}
		});

		MenuItem item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Build");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{

			}
		});

		item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Make");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{

			}
		});

		item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Split");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{

			}
		});

		item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Join");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{

			}
		});

		return popUpMenu;
	}

	/**
	 * Creates all the items located in the Help submenu and associate all the
	 * menu items with their appropriate functions.
	 *
	 * @param menuBar
	 *            Menu the <code>Menu</code> that file contain the Help
	 *            submenu.
	 */
	private void createHelpMenu(Menu menuBar)
	{

		// Help Menu
		MenuItem item = new MenuItem(menuBar, SWT.CASCADE);
		item.setText("Help");
		Menu menu = new Menu(shell, SWT.DROP_DOWN);
		item.setMenu(menu);

		// Help -> About Text Editor
		MenuItem subItem = new MenuItem(menu, SWT.NULL);
		subItem.setText("About");
		subItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				MessageBox box = new MessageBox(shell, SWT.NONE);
				box.setText(shell.getText());
				box.setMessage(shell.getText());
				box.open();
			}
		});
	}

	/**
	 * @param playerIndex
	 * @param player
	 * @return
	 */
	private void handleEndTurnCommand()
	{
		gameController.getGameState().getPlayers().elementAt(currentPlayerIndex).endTurn();

		removeDeadPlayers();
		if (this.gameController.isThereAWinner() != null)
			endGame(this.gameController.isThereAWinner());

		currentPlayerIndex = (currentPlayerIndex + 1) % this.gameController.getGameState().getNumberOfPlayers();

		createBoardWindow();
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
			System.out.println("winner is: " + winner.getName() + " with a score of: " + winner.finalScore());
			Helper.getScoreBoard().addToScoreBoard(winner, winner.finalScore());
		}
		Helper.getScoreBoard().save();
		handleHighscoreCommand();
		System.exit(0);
	}

	private void handleHighscoreCommand()
	{
		System.out.print(Helper.getScoreBoard().print());
	}

	/**
	 * @param player
	 * @param userInput
	 */
	private void handleMoveCommand(String[] userInput)
	{
		if (userInput.length < 3)
			System.out.println("Wrong parameters.");
		else
		{
			int newX, newY;
			newX = Helper.tryParseInt(userInput[1]);
			newY = Helper.tryParseInt(userInput[2]);

			if (!Helper.isIntBetween(newX, 0, Constants.BOARD_SIZE - 1) || !Helper.isIntBetween(newY, 0, Constants.BOARD_SIZE - 1))
				System.out.println("Wrong parameters.");
			else if (gameController.getGameState().getPlayers().elementAt(currentPlayerIndex).move(newX, newY,
					this.gameController.getGameState().getBoard()))
				createBoardWindow();
			else
			{
				System.out.println("Illegal move ! You can only move "
						+ gameController.getGameState().getPlayers().elementAt(currentPlayerIndex).getMovesLeft() + " steps more .");
			}
		}
	}
}
