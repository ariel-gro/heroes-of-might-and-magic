package tau.heroes;

import java.io.File;
import java.util.Vector;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class HeroesGui
{
	private Shell shell;

	private String file = null;

	private boolean isModified = false;

	private static IconCache iconCache = new IconCache();

	final int numOfCells;

	private Color black;

	private Color white;

	private Display display;

	static Control currentChild = null;

	private GameController gameController;

	private static int currentPlayerIndex = 0;

	private Composite boardComposite;

	private Composite statusComposite;

	SashForm sash;

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
		white = display.getSystemColor(SWT.COLOR_WHITE);
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
		sash = new SashForm(shell, SWT.BORDER);
		sash.setOrientation(SWT.HORIZONTAL);
		sash.setLayoutData(sashData);

		sc = new ScrolledComposite(sash, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		createBoardWindow();

		createStatusWindow();

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
			if (bs.getResource() != null && bs.getResource().getType().getTypeName().equals("wood"))
			{
				return iconCache.heroeInWoodIcon;
			} else if (bs.getResource() != null && bs.getResource().getType().getTypeName().equals("gold"))
			{
				return iconCache.heroInGlodMineIcon;
			} else if (bs.getResource() != null && bs.getResource().getType().getTypeName().equals("stone"))
			{
				return iconCache.heroInStoneIcon;
			} else if (bs.getCastle() != null)
			{
				return iconCache.heroInCastleIcon;
			} else
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
			if (bs.getResource() != null && bs.getResource().getType().getTypeName().equals("wood"))
			{
				return bs.getHero().player.getName() + "'s Hero in " + bs.getResource().getType().getTypeName() + " owned by "
						+ (bs.getResource().getOwner() == null ? "none" : bs.getResource().getOwner().getName()) + "\nLocation: " + x + ", " + y;
			} else if (bs.getResource() != null && bs.getResource().getType().getTypeName().equals("gold"))
			{
				return bs.getHero().player.getName() + "'s Hero in " + bs.getResource().getType().getTypeName() + " owned by "
						+ (bs.getResource().getOwner() == null ? "none" : bs.getResource().getOwner().getName()) + "\nLocation: " + x + ", " + y;
			} else if (bs.getResource() != null && bs.getResource().getType().getTypeName().equals("stone"))
			{
				return bs.getHero().player.getName() + "'s Hero in " + bs.getResource().getType().getTypeName() + " owned by "
						+ (bs.getResource().getOwner() == null ? "none" : bs.getResource().getOwner().getName()) + "\nLocation: " + x + ", " + y;
			} else if ((bs.getCastle()) != null)
			{
				return bs.getHero().player.getName() + "'s Hero in " + bs.getCastle().getPlayer().getName() + "'s Castle" + "\nLocation: " + x + ", " + y;
			} else
				return bs.getHero().player.getName() + "'s Hero" + "\nLocation: " + x + ", " + y;

		} else if ((bs.getCastle()) != null)
		{
			return bs.getCastle().getPlayer().getName() + "'s Castle" + "\nLocation: " + x + ", " + y;
		} else if ((bs.getResource()) != null)
		{
			if (bs.getResource().getType().getTypeName().equals("wood"))
			{
				return bs.getResource().getType().getTypeName() + " owned by " + (bs.getResource().getOwner() == null ? "none" : bs.getResource().getOwner().getName()) + "\nLocation: " + x + ", " + y;
			} else if (bs.getResource().getType().getTypeName().equals("gold"))
			{
				return bs.getResource().getType().getTypeName() + " owned by " + (bs.getResource().getOwner() == null ? "none" : bs.getResource().getOwner().getName()) + "\nLocation: " + x + ", " + y;
			} else if (bs.getResource().getType().getTypeName().equals("stone"))
			{
				return bs.getResource().getType().getTypeName() + " owned by " + (bs.getResource().getOwner() == null ? "none" : bs.getResource().getOwner().getName()) + "\nLocation: " + x + ", " + y;
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
			iconCache.freeResources();
			iconCache.initResources(display);
			shell.setImage(iconCache.stockImages[iconCache.appIcon]); // workaround - fix if there's time.
		}

		boardComposite = new Composite(sc, SWT.NONE);
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
					b.setImage(iconCache.stockImages[iconCache.blackIcon]);

				String description;
				if (t != iconCache.grassIcon)
				{
					description = fromBoardToDisplayDecription(x, y);
					b.setToolTipText(description);
				}

				if (t == iconCache.heroIcon || t == iconCache.heroInGlodMineIcon || t == iconCache.heroInStoneIcon || t == iconCache.heroeInWoodIcon)
					if (gameController.getGameState().getBoard().getBoardState(x, y).getHero().player.equals(gameController.getGameState().getPlayers().elementAt(currentPlayerIndex)))
						b.setMenu(createHeroPopUpMenu());

				if (t == iconCache.castleIcon)
					if (gameController.getGameState().getBoard().getBoardState(x, y).getCastle().getPlayer().equals(gameController.getGameState().getPlayers().elementAt(currentPlayerIndex)))
						b.setMenu(createCastlePopUpMenu());

				if (t == iconCache.heroInCastleIcon)
					if (gameController.getGameState().getBoard().getBoardState(x, y).getHero().player.equals(gameController.getGameState().getPlayers().elementAt(currentPlayerIndex)))
						b.setMenu(createHeroInCastlePopUpMenu());
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

			if (isVisible[x][y] && (t == iconCache.heroIcon || t == iconCache.heroInCastleIcon || t == iconCache.heroInGlodMineIcon || t == iconCache.heroInStoneIcon || t == iconCache.heroeInWoodIcon))
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

	private Label createLabel(Composite composite, String text)
	{
		Label tempLabel = new Label(composite, SWT.NONE);
		tempLabel.setText(text);
		tempLabel.setBackground(white);
		return tempLabel;
	}

	private void createStatusWindow()
	{
		statusComposite = new Composite(sash, SWT.BORDER);
		statusComposite.setBackground(white);
		GridData d = new GridData(GridData.FILL_BOTH);
		statusComposite.setLayoutData(d);

		GridLayout tempLayout = new GridLayout();
		statusComposite.setLayout(tempLayout);

		updateStatusWindow();
	}

	private void updateStatusWindow()
	{
		Player p = gameController.getGameState().getPlayers().elementAt(currentPlayerIndex);

		Control[] children = statusComposite.getChildren();
		for (int i = 0; i < children.length; i++)
		{
			children[i].dispose();
		}

		createLabel(statusComposite, "               PLAYER  STATUS");
		createLabel(statusComposite, "");

		String str = p.getName();
		createLabel(statusComposite, "PLAYER  NAME  :  " + str);
		createLabel(statusComposite, "");

		int xPos = p.getHero().getXPos();
		int yPos = p.getHero().getYPos();
		createLabel(statusComposite, "HERO  POSITION  :  " + xPos + " , " + yPos);
		createLabel(statusComposite, "");

		createLabel(statusComposite, "MINE  LIST");		
		Table table = new Table (statusComposite, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible (true);
		table.setHeaderVisible (true);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 200;
		table.setLayoutData(data);
		String[] titles = {" Mine ", " Quantity "};
		for (int i=0; i<titles.length; i++) 
		{
			TableColumn column = new TableColumn (table, SWT.NONE);
			column.setText (titles [i]);
		}	

			Integer woodNum = p.getMineQuantity("wood");
			Integer goldNum = p.getMineQuantity("gold");
			Integer stoneNum = p.getMineQuantity("stone");
			TableItem item1 = new TableItem (table, SWT.NONE);
			item1.setText (0, "Wood");
			item1.setText (1, woodNum.toString());
			item1 = new TableItem (table, SWT.NONE);
			item1.setText (0, "Gold");
			item1.setText (1, goldNum.toString());
			item1 = new TableItem (table, SWT.NONE);
			item1.setText (0, "Stone");
			item1.setText (1, stoneNum.toString());
			
		for (int i=0; i<titles.length; i++) 
		{
			table.getColumn (i).pack ();
		}	
	

		
			
		
		createLabel(statusComposite, "TREASURY LIST");	
		Table table2 = new Table (statusComposite, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		table2.setLinesVisible (true);
		table2.setHeaderVisible (true);
		GridData data2 = new GridData(SWT.FILL, SWT.FILL, true, true);
		data2.heightHint = 200;
		table2.setLayoutData(data2);
		String[] titles2 = {" Resource ", " Amount "};
		for (int i=0; i<titles2.length; i++) 
		{
			TableColumn column2 = new TableColumn (table2, SWT.NONE);
			column2.setText (titles2[i]);
		}	

		Integer woodAmount = p.getCurrentTreasuryAmount("wood");
		Integer goldAmount = p.getCurrentTreasuryAmount("gold");
		Integer stoneAmount = p.getCurrentTreasuryAmount("stone");
		TableItem item2 = new TableItem (table, SWT.NONE);
		item2.setText (0, "Wood");
		item2.setText (1, woodAmount.toString());
		item2 = new TableItem (table2, SWT.NONE);
		item2.setText (0, "Gold");
		item2.setText (1, goldAmount.toString());
		item2 = new TableItem (table2, SWT.NONE);
		item2.setText (0, "Stone");
		item2.setText (1, stoneAmount.toString());
			
		for (int i=0; i<titles.length; i++) 
		{
			table2.getColumn (i).pack ();
		}
		createLabel(statusComposite, "");
		
		
		createLabel(statusComposite, "Defence Skill : " + p.getHero().getDefenseSkill());
		createLabel(statusComposite, "Attack Skill : " + p.getHero().getAttackSkill());
		createLabel(statusComposite, "");

		createLabel(statusComposite, "Army");
		createLabel(statusComposite, "----");
		Creature[] creaturesArray = p.getHero().getArmy().getCreatures();
		for (int j = 0; j < 5; ++j)
		{

			if (creaturesArray[j] != null)
			{
				createLabel(statusComposite, "Creature number " + (j + 1) + " : " + creaturesArray[j].toString());
			} else
			{
				createLabel(statusComposite, "Creature number " + (j + 1) + " : none");
			}
		}

		int numOfCastles = p.getCastles().size();
		for (int i = 0; i < numOfCastles; ++i)
		{
			int castleXPos = p.getCastles().get(i).getXPos();
			int castleYPos = p.getCastles().get(i).getYPos();
			createLabel(statusComposite, "Castle at : " + castleXPos + " , " + castleYPos);

			Class<? extends CreatureFactory> soldierFactoryClass = (new SoldierFactory()).getClass();
			if (p.getCastles().get(i).hasFactory(soldierFactoryClass))
			{
				String str1 = p.getCastles().get(i).getFactory(soldierFactoryClass).toString();
				createLabel(statusComposite, "Castle's Soldier Factories : " + str1);
			} else
			{
				createLabel(statusComposite, "Castle's Soldier Factories : none");
			}

			Class<? extends CreatureFactory> goblinFactoryClass = (new GoblinFactory()).getClass();
			if (p.getCastles().get(i).hasFactory(goblinFactoryClass))
			{
				String str1 = p.getCastles().get(i).getFactory(goblinFactoryClass).toString();
				createLabel(statusComposite, "Castle's Goblin Factories : " + str1);
			} else
			{
				createLabel(statusComposite, "Castle's Goblin Factories : none");
			}

			if (p.getCastles().get(i).getArmy() != null)
			{
				createLabel(statusComposite, "Castle's Army : " + p.getCastles().get(i).getArmy().toString());
			} else
			{
				createLabel(statusComposite, "Castle's Army : none");
			}
		}

		statusComposite.layout(true, true);
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
		createHighScoreMenu(menuBar);
		createHelpMenu(menuBar);

		return menuBar;
	}

	public static void displayError(String msg)
	{
		MessageBox box = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_ERROR);
		box.setMessage(msg);
		box.open();
	}

	private void startNewGame()
	{
		int numberOfPlayers = getNumberOfPlayers();
		Vector<Player> players = getPlayers(numberOfPlayers);
		gameController.initNewGame(players);

		createBoardWindow();
		updateStatusWindow();
	}

	/**
	 * @return Number of players from user input
	 */
	protected static int getNumberOfPlayers()
	{
		String message = "Enter number of players (" + Constants.MIN_PLAYERS + "-" + Constants.MAX_PLAYERS + "): ";
		int numberOfPlayers = Integer.MIN_VALUE;
		String response = null;

		do
		{
			InputDialog numberInput = new InputDialog(Display.getCurrent().getActiveShell(), "Number of Players", message, null, null);
			if (numberInput.open() == Window.OK)
			{
				response = numberInput.getValue();
			}

			if (response != null)
				numberOfPlayers = Helper.tryParseInt(response);

		} while (!Helper.isIntBetween(numberOfPlayers, Constants.MIN_PLAYERS, Constants.MAX_PLAYERS));

		return numberOfPlayers;
	}

	protected static Vector<Player> getPlayers(int numberOfPlayers)
	{
		String message;
		String response = null;
		Vector<Player> players = new Vector<Player>();

		for (int i = 0; i < numberOfPlayers;)
		{
			message = (i == 0) ? "" : "If you want one of the players will be the computer, enter " + Player.COMPUTER_NAME + " as his name.\n";
			message += "Please enter player " + (i + 1) + "'s name: ";

			InputDialog stringInput = new InputDialog(Display.getCurrent().getActiveShell(), "Player Name", message, null, null);
			if (stringInput.open() == Window.OK)
			{
				response = stringInput.getValue();
			} else
			{
				return null;
			}

			if (response.length() > 0)
			{
				players.add(new Player(response));
				i++;
			}
		}

		return players;
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
		updateStatusWindow();

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
		gameController.saveGame(file);
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

		menu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e)
			{
				//Menu menu = (Menu) e.widget;
				//MenuItem[] items = menu.getItems();

				//////// ************ TBD ********** //////////////////
			}
		});

		// File -> New Game
		MenuItem subItem = new MenuItem(menu, SWT.NULL);
		subItem.setText("New Game");
		subItem.setAccelerator(SWT.MOD1 + 'N');
		subItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				startNewGame();
			}
		});

		// File -> Open Saved Game
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
				moveHero();
			}

		});

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

		return popUpMenu;
	}

	/**
	 * Creates all items located in the popup menu and associates all the menu
	 * items with their appropriate functions.
	 *
	 * @return Menu The created popup menu.
	 */
	private Menu createHeroInCastlePopUpMenu()
	{
		Menu popUpMenu = new Menu(shell, SWT.POP_UP);

		/**
		 * Adds a listener to handle enabling and disabling some items in the
		 * Edit submenu.
		 */
		popUpMenu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e)
			{
			}
		});

		MenuItem item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Move");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				moveHero();
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

		new MenuItem(popUpMenu, SWT.SEPARATOR);

		item = new MenuItem(popUpMenu, SWT.CASCADE);
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

		 //Help -> Gameplay assistance
		 MenuItem assistItem = new MenuItem(menu, SWT.NULL);
		 assistItem.setText("&Gameplay assistance");
		 assistItem.setAccelerator(SWT.MOD1 + 'G');
		 assistItem.addSelectionListener(new SelectionAdapter()
		 {
			 public void widgetSelected(SelectionEvent e)
			 {
				 String helpString;

				 helpString = "\n" +
				 "Heroes of Might and Magic (TAU Version)\n" +
				 "Gameplay assistance\n\n" +
				 "Right click your hero to:\n" +
				 "Move, End Turn.\n\n" +
				 "Right click your castel(s) to see Castle menu and options:\n" +
				 "Build: build a creature factory\n" +
				 "Make: make a new creature\n" +
				 "Split: move units from hero to castle\n" +
				 "Join: move units from castle to hero\n" +
				 "\n" +
				 "Player info is on the right part of the screen (status window)\n\n" +
				 "Use the File menu to save a game,load a game or start a new game (current game will not be saved automatically)\n\n" +
				 "Use the Highscores menu to view or reset the highscores table\n\n" +
				 "Quitting the game is via File -> Exit\n\n\n" +
				 "Enjoy the game";

				 Shell gameHelpShell = new Shell(Display.getCurrent().getActiveShell());
				 gameHelpShell.setLayout(new FillLayout());
				 gameHelpShell.setSize(700, 350);
				 gameHelpShell.setText("Gameplay assistance");
				 gameHelpShell.setImage(iconCache.stockImages[iconCache.appIcon]);
				 Label gameHelp = new Label(gameHelpShell, SWT.CENTER);
				 gameHelp.setBounds(gameHelpShell.getClientArea());
				 gameHelp.setText(helpString);
				 gameHelpShell.open();

				 while (!gameHelpShell.isDisposed())
					 if (!display.readAndDispatch())
						 display.sleep();
			 }
		 });


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
	 * creates all the items in the high scores sub-menu, and associates all menu
	 * items to the right functions
	 *
	 * @param menuBar
	 *            Menu the <code>Menu</code> that will contain this sub-menu.
	 *
	 */
	private void createHighScoreMenu(Menu menuBar)
	{
		// high scores Menu
		MenuItem item = new MenuItem(menuBar, SWT.CASCADE);
		item.setText("&Highscores");
		Menu menu = new Menu(shell, SWT.DROP_DOWN);
		item.setMenu(menu);

		MenuItem subItem1 = new MenuItem(menu, SWT.NULL);
		subItem1.setText("&Dispaly Highscores");
		subItem1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				GameScoreBoard board = new GameScoreBoard();
				board.load();
				displayTable(board);
			}
		});
		MenuItem subItem2 = new MenuItem(menu, SWT.NULL);
		subItem2.setText("&Reset Highscores");
		subItem2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				messageBox.setMessage("Are you sure you want to clear higscores table?\nThis action can not be reversed");
				messageBox.setText("Clear Highscores");
				int response = messageBox.open();
				if (response == SWT.YES)
				{
					GameScoreBoard board = new GameScoreBoard();
					board.load();
					board.clearScoreBoard();
					board.save();
					displayTable(board);
				}
			}
		});
	}

	/**
	 * helper for the high-scores table display
	 *
	 * @param board
	 */
	private void displayTable(GameScoreBoard board)
	{
		Player tempPlayer;
		int tempScore;
		String name, score;
		Shell tableShell = new Shell(Display.getCurrent().getActiveShell());
		tableShell.setLayout(new FillLayout());
		tableShell.setSize(200, 200);
		tableShell.setText("Highscores - The 10 Best Players");
		tableShell.setImage(iconCache.stockImages[iconCache.highscoreIcon]);
		Table scoreTable = new Table(tableShell, SWT.NULL);
		TableColumn col1 = new TableColumn(scoreTable, SWT.CENTER);
		TableColumn col2 = new TableColumn(scoreTable, SWT.CENTER);
		col1.setText("Player Name");
		col2.setText("Player Score");
		col1.setWidth(96);
		col2.setWidth(97);
		scoreTable.setHeaderVisible(true);

		TableItem ti;

		for (int i = 0; i < 10; i++)
		{
			tempPlayer = board.getPlayerAt(i);
			tempScore = board.getScoreAt(i);
			ti = new TableItem(scoreTable, SWT.NONE);

			if (tempPlayer == null)
				name = "----";
			else
				name = tempPlayer.getName();
			if (tempScore == 0)
				score = "0";
			else
				score = tempScore + "";

			ti.setText(new String[] { name, score });
		}

		tableShell.open();
		while (!tableShell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private void moveHero()
	{
		String message = "Move to X,Y, e.g. 12,31 (Currenly at: " + gameController.getGameState().getPlayers().elementAt(currentPlayerIndex).getHero().getXPos() + ","
				+ gameController.getGameState().getPlayers().elementAt(currentPlayerIndex).getHero().getYPos() + "): ";
		String response = null;
		String[] responseSplit = null;
		boolean ok = false;

		do
		{
			InputDialog numbersInput = new InputDialog(Display.getCurrent().getActiveShell(), "Move to X,Y", message, null, null);
			if (numbersInput.open() == Window.OK)
			{
				response = numbersInput.getValue();
			} else
			{
				numbersInput.close();
				break;
			}

			if (response != null)
				responseSplit = response.split(",");
			else
			{
				displayError("Invallid Input - Try again");
				continue;
			}

			if (responseSplit.length == 2)
			{
				if (handleMoveCommand(responseSplit) == true)
					ok = true;
			} else
			{
				displayError("Invallid Input - Try again");
				continue;
			}

		} while (ok == false);
	}

	/**
	 * @param playerIndex
	 * @param player
	 * @return
	 */
	private void handleEndTurnCommand()
	{
		Player p = gameController.getGameState().getPlayers().elementAt(currentPlayerIndex);
		p.endTurn();

		removeDeadPlayers();
		if (this.gameController.isThereAWinner() != null)
			endGame(this.gameController.isThereAWinner());

		currentPlayerIndex = (currentPlayerIndex + 1) % this.gameController.getGameState().getNumberOfPlayers();
		p = gameController.getGameState().getPlayers().elementAt(currentPlayerIndex);
		createBoardWindow();
		updateStatusWindow();

		//Here is the computer move.
		while (p.isComputer())
		{
			Hero hero = gameController.getGameState().getPlayers().elementAt(currentPlayerIndex).getHero();
			if (hero != null)
			{
				String[] computerMove = new String[2];
				computerMove[0] = String.valueOf(hero.getXPos() + (int) (Math.random() * 3) - 1);
				computerMove[1] = String.valueOf(hero.getYPos() + (int) (Math.random() * 3) - 1);
				handleMoveCommand(computerMove);
			}
			//End turn:
			p.endTurn();
			removeDeadPlayers();
			if (this.gameController.isThereAWinner() != null)
				endGame(this.gameController.isThereAWinner());

			currentPlayerIndex = (currentPlayerIndex + 1) % this.gameController.getGameState().getNumberOfPlayers();
			p = gameController.getGameState().getPlayers().elementAt(currentPlayerIndex);
			createBoardWindow();
			updateStatusWindow();
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
	private boolean handleMoveCommand(String[] userInput)
	{
		int newX, newY;
		newX = Helper.tryParseInt(userInput[0]);
		newY = Helper.tryParseInt(userInput[1]);

		if (!Helper.isIntBetween(newX, 0, Constants.BOARD_SIZE - 1) || !Helper.isIntBetween(newY, 0, Constants.BOARD_SIZE - 1))
		{
			if (!gameController.getGameState().getPlayers().elementAt(currentPlayerIndex).isComputer())
				displayError("Invallid Input. Outside of board - Try again");
			return false;
		} else if (gameController.getGameState().getPlayers().elementAt(currentPlayerIndex).move(newX, newY, this.gameController.getGameState().getBoard()))
		{
			createBoardWindow();
			updateStatusWindow();
			return true;
		} else
		{
			displayError("Illegal move !" + currentPlayerIndex + " You can only move " + gameController.getGameState().getPlayers().elementAt(currentPlayerIndex).getMovesLeft() + " steps more .");
			return false;
		}
	}

	/*
	private void handleBuildCommand()
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
		} else
			System.out.println("Unknown creature type!");

	}

	private void handleSplitCommand(Castle theCastle, String[] response)
	{
		if (response.length == 3)
		{
			Hero hero = gameController.getGameState().getPlayers().elementAt(currentPlayerIndex).getHero();
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

			Creature creature = null;
			if (response[1].equals("goblin"))
			{
				creature = new Goblin(numberOfUnits);
			} else if (response[1].equals("soldier"))
			{
				creature = new Soldier(numberOfUnits);
			} else
			{
				System.out.println("Unknown creature type!");
				return;
			}

			if (!theCastle.canAddToArmy(creature.getClass()))
			{
				System.out.println("Army in " + theCastle.printLocation() + " is full.");
				return;
			} else if (!hero.removeFromArmy(creature))
			{
				System.out.println("You dont have enough units to split");
				return;
			} else
			{
				theCastle.addToArmy(creature);
				return;
			}
		}
		System.out.println("Illegal move !");
	}

	private void handleJoinCommand(Castle theCastle, String[] response)
	{
		if (response.length == 3)
		{
			Hero hero = gameController.getGameState().getPlayers().elementAt(currentPlayerIndex).getHero();
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

			Creature creature = null;
			if (response[1].equals("goblin"))
			{
				creature = new Goblin(numberOfUnits);
			} else if (response[1].equals("soldier"))
			{
				creature = new Soldier(numberOfUnits);
			} else
			{
				System.out.println("Unknown creature type!");
				return;
			}

			if (!hero.canAddToArmy(creature.getClass()))
			{
				System.out.println("Army of " + hero.toString() + " is full.");
				return;
			} else if (!theCastle.canRemoveFromArmy(creature))
			{
				System.out.println("You dont have enough units to join.");
				return;
			} else
			{
				theCastle.removeFromArmy(creature);
				hero.addToArmy(creature);
				return;
			}
		}
		System.out.println("Illegal move !");
	}

	private static void handleMakeCommand(Castle theCastle, String[] response)
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
					String[] numOfUnitsResponse = MainModule.getCommandAndParameters("Enter desired number of units (1-" + maxUnits + "): ");

					if (numOfUnitsResponse.length > 0)
					{
						int numberOfUnits = Integer.parseInt(numOfUnitsResponse[0]);
						if (numberOfUnits > 0 && numberOfUnits <= maxUnits)
						{
							theCastle.makeUnits(creatureClass, numberOfUnits);
						} else
							System.out.println("Number of units is our of range.");
					} else
						System.out.println("Bad input.");
				} else
					System.out.println("Sorry, but you can't build units.");
			} else
				System.out.println("Unknown creature type.");
		}
	}
	*/
}