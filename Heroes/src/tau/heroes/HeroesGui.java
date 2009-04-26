package tau.heroes;

import java.io.File;
import java.util.Vector;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tracker;

public class HeroesGui
{
	private Shell shell;

	private String file = null;

	private boolean isModified = false;

	private static IconCache iconCache = new IconCache();

	int numOfCells;

	private Color black;

	private Color white;

	private Display display;

	static Control currentChild = null;

	private GameController gameController;

	private static int currentPlayerIndex = 0;

	private static Point currentPoint;
	
	private static Point newPoint;
	
	public Composite eclipseComposite;

	private Composite boardComposite;

	private Composite statusComposite;
	
	private Point[][] boardPoints = null;

	SashForm sash;

	private ScrolledComposite sc;
	
	Cursor cursor;;
	
	Cursor defaultCursor;

	public Display getDisplay()
	{
		return display;
	}

	public HeroesGui(Display d, GameController gameController)
	{
		this.display = d;
		this.gameController = gameController;
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
		cursor = new Cursor(display, SWT.CURSOR_NO);	
		defaultCursor  = new Cursor(display, SWT.NONE);
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
		sc.setBackground(black);
		sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		initBlankGame();	

		shell.open();
		
		//displayStartWindow();
		
		return shell;
	}

	public void createEclipseView(Composite theEclipseComposite)
	{
		eclipseComposite = theEclipseComposite;
		shell = eclipseComposite.getShell();
		black = display.getSystemColor(SWT.COLOR_BLACK);
		white = display.getSystemColor(SWT.COLOR_WHITE);
		cursor = new Cursor(display, SWT.CURSOR_NO);	
		defaultCursor  = new Cursor(display, SWT.NONE);
		eclipseComposite.setBackground(black);
		
		GridLayout layout = new GridLayout();
		eclipseComposite.setLayout(layout);

		GridData sashData = new GridData();
		sashData.grabExcessHorizontalSpace = true;
		sashData.grabExcessVerticalSpace = true;
		sashData.horizontalAlignment = GridData.FILL;
		sashData.verticalAlignment = GridData.FILL;
		sash = new SashForm(eclipseComposite, SWT.BORDER);
		sash.setOrientation(SWT.HORIZONTAL);
		sash.setLayoutData(sashData);

		sc = new ScrolledComposite(sash, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		initBlankGame();
		
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e)
			{
				e.doit = close();
			}
		});
		
		
		
		//displayStartWindow();
	}

	public void initBlankGame() {
		createBoardWindow(false);
		createStatusWindow(false);
		sash.setWeights(new int[] { 85, 15 });
	}
	
	public boolean close()
	{
		if (isModified)
		{
			// ask user if they want to save current game
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
		
		cursor.dispose();
		defaultCursor.dispose();
		white.dispose();
		black.dispose();
		

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

	private void createBoardWindow(boolean initBoard)
	{
		boolean[][] isVisible;
		Composite currentHero = null;

		if (boardComposite != null && boardComposite.isDisposed() == false)
		{
			boardComposite.dispose();
		}
		
		boardComposite = new Composite(sc, SWT.NONE);
		boardComposite.setBackground(black);
		GridData d = new GridData(GridData.FILL_BOTH);
		boardComposite.setLayoutData(d);

		if(initBoard)
		{
			this.numOfCells = gameController.getGameState().getBoard().getSize();
			
			boardPoints = new Point[numOfCells][numOfCells];
			for (int y = 0; y < numOfCells; y++)
				for (int x = 0; x < numOfCells; x++)
					boardPoints[x][y] = new Point(x,y);	
			
			GridLayout tableLayout = new GridLayout();
			tableLayout.numColumns = numOfCells;
			tableLayout.makeColumnsEqualWidth = true;
			tableLayout.horizontalSpacing = 0;
			tableLayout.verticalSpacing = 0;
			boardComposite.setLayout(tableLayout);
	
			isVisible = gameController.getGameState().getPlayers().elementAt(currentPlayerIndex).getVisibleBoard();
	
			MouseListener focusListener = new MouseListener() {
				public void mouseDown(MouseEvent e)
				{
					Label selectedLabel = (Label) e.getSource();
					currentPoint = (Point) selectedLabel.getData();
				}
	
				public void mouseDoubleClick(MouseEvent arg0)
				{}
	
				public void mouseUp(MouseEvent arg0)
				{}
			};
			
			Listener listener = new Listener() {
				Point point = null;
				
				public void handleEvent(Event event)
				{
					switch (event.type)
					{
						case SWT.MouseDown:
							if (event.button == 1)
							{
								point = new Point(event.x, event.y);
							}
						break;
						case SWT.MouseMove:
							if (point == null)
								break;
		
							int x = point.x - event.x;
							int y = point.y - event.y;
							if (Math.abs(x) < 8 && Math.abs(y) < 8)
								break;			
	
							Control control = (Control) event.widget;
							final Tracker tracker = new Tracker(boardComposite, SWT.NONE);
							Rectangle rect = control.getBounds();
							final Rectangle r1 = display.map(control, boardComposite, rect);
							tracker.setRectangles(new Rectangle[] { r1 });
							tracker.addListener(SWT.Move, new Listener() {
								public void handleEvent(Event event)
								{
									Rectangle r2 = tracker.getRectangles()[0];								
									newPoint = new Point(r2.x/r2.width, r2.y/r2.height);
									
									if(!gameController.getGameState().getPlayers().elementAt(currentPlayerIndex).checkMove((r2.x/r2.width), (r2.y/r2.height), gameController.getGameState().getBoard()))
										tracker.setCursor(cursor);
									else
										tracker.setCursor(defaultCursor);
								}
							});
							
							if (!tracker.open())
								break;
							
							if(newPoint != null && currentPoint != null && !((newPoint.x == currentPoint.x) && (newPoint.y == currentPoint.y)))
								handleMoveCommand(new String[]{newPoint.x+"", newPoint.y+""});
							
							point = null;
						break;
					}
				}
			};
	
			for (int y = 0; y < numOfCells; y++)
			{
				for (int x = 0; x < numOfCells; x++)
				{
					Composite b = new Composite(boardComposite, SWT.NONE);
					GridLayout cellLayout = new GridLayout();
					cellLayout.marginWidth = 0;
					cellLayout.marginHeight = 0;
					b.setLayout(cellLayout);
					Label l = new Label(b, SWT.NONE);
					l.setLayoutData(new GridData(GridData.FILL_BOTH));
					int t = fromBoardToDisplayIcons(x, y);
	
					if (isVisible[x][y])
						l.setImage(iconCache.stockImages[t]);
					else
						l.setImage(iconCache.stockImages[iconCache.blackIcon]);
					
					String description;
					if (t != iconCache.grassIcon && t != iconCache.blackIcon)
					{
						description = fromBoardToDisplayDecription(x, y);
						l.setToolTipText(description);
					}
	
					if (t == iconCache.heroIcon || t == iconCache.heroInGlodMineIcon || t == iconCache.heroInStoneIcon || t == iconCache.heroeInWoodIcon)
						if (gameController.getGameState().getBoard().getBoardState(x, y).getHero().player.equals(gameController.getGameState().getPlayers().elementAt(currentPlayerIndex)))
						{
							l.setData(boardPoints[x][y]);
							l.setMenu(createHeroPopUpMenu(SWT.POP_UP));
							l.addMouseListener(focusListener);
							l.addListener(SWT.MouseDown, listener);
							l.addListener(SWT.MouseMove, listener);
							currentHero = b;
						}
	
					if (t == iconCache.castleIcon)
						if (gameController.getGameState().getBoard().getBoardState(x, y).getCastle().getPlayer().equals(gameController.getGameState().getPlayers().elementAt(currentPlayerIndex)))
						{
							l.setData(boardPoints[x][y]);
							l.setMenu(createCastlePopUpMenu(SWT.POP_UP));
							l.addMouseListener(focusListener);
							
							if(gameController.getGameState().getBoard().getBoardState(x, y).getHero() == null)
								currentHero = b;			
						}
	
					if (t == iconCache.heroInCastleIcon)
						if (gameController.getGameState().getBoard().getBoardState(x, y).getHero().player.equals(gameController.getGameState().getPlayers().elementAt(currentPlayerIndex)))
						{
							l.setData(boardPoints[x][y]);
							l.setMenu(createHeroInCastlePopUpMenu());
							l.addMouseListener(focusListener);
							l.addListener(SWT.MouseDown, listener);
							l.addListener(SWT.MouseMove, listener);
							currentHero = b;
						}					
				}
			}
	
			sc.setContent(boardComposite);
			sc.setExpandHorizontal(true);
			sc.setExpandVertical(true);
			sc.setMinSize(boardComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	
			Rectangle bounds = currentHero.getBounds();
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


	private Label createLabel(Composite composite, String text)
	{
		Label tempLabel = new Label(composite, SWT.NONE);
		tempLabel.setText(text);
		tempLabel.setBackground(white);
		return tempLabel;
	}

	private void displayStartWindow()
	{
		final Shell shell = new Shell(Display.getCurrent().getActiveShell());
		shell.setImage(iconCache.stockImages[iconCache.appIcon]);
		
		GridLayout layout1 = new GridLayout (4, true);
		layout1.marginWidth = layout1.marginHeight = 10;
		shell.setLayout (layout1);
	
		GridData newGameData = new GridData();
		newGameData.horizontalSpan = 4;
		newGameData.grabExcessHorizontalSpace = true;
		newGameData.grabExcessVerticalSpace = true;
		final Button newGame = new Button (shell, SWT.RADIO);
		newGame.setSelection(true);
		newGame.setText ("Start a New Game");
		newGame.setLayoutData(newGameData);
		
		GridData loadGameData = new GridData();
		loadGameData.horizontalSpan = 4;
		loadGameData.grabExcessHorizontalSpace = true;
		loadGameData.grabExcessVerticalSpace = true;
		final Button loadGame = new Button (shell, SWT.RADIO);
		loadGame.setText ("Load a Saved Game");
		loadGame.setLayoutData(loadGameData);
		
		GridData emptyLabelData1 = new GridData();
		emptyLabelData1.grabExcessHorizontalSpace = true;
		emptyLabelData1.horizontalSpan = 4;
		Label emptyLabel1 = new Label(shell, SWT.None);
		emptyLabel1.setVisible(false);
		emptyLabel1.setLayoutData(emptyLabelData1);
		
		GridData okData = new GridData();
		okData.grabExcessHorizontalSpace = true;
		okData.grabExcessVerticalSpace = true;
		okData.horizontalSpan = 1;
		Button okButton = new Button (shell, SWT.PUSH);
		okButton.setText ("    OK    ");
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				if(newGame.getSelection())
					startNewGame();
				else if(loadGame.getSelection())
					openFileDlg();

				shell.dispose();
			}
		});
		okButton.setLayoutData(okData);
		
		GridData cancelData = new GridData();
		cancelData.grabExcessHorizontalSpace = true;
		Button cancelButton = new Button (shell, SWT.PUSH);
		cancelButton.setText (" Cancel ");
		cancelButton.setLayoutData(cancelData);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				shell.dispose();
			}
		});
		
		GridData emptyLabelData2 = new GridData();
		emptyLabelData2.grabExcessHorizontalSpace = true;
		Label emptyLabel2 = new Label(shell, SWT.None);
		emptyLabel2.setVisible(false);
		emptyLabel2.setLayoutData(emptyLabelData2);
		
		GridData helpData = new GridData();
		helpData.grabExcessHorizontalSpace = true;
		Button helpButton = new Button (shell, SWT.PUSH);
		helpButton.setText ("  Help  ");
		helpButton.setLayoutData(helpData);
		helpButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				showGameAssistanceMbox();
			}
		});
		
		shell.pack ();
		
		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private void displayCastleInfo(Castle castle)
	{
		Shell shell = new Shell(Display.getCurrent().getActiveShell());
		shell.setLayout(new GridLayout());
		shell.setBackground(white);
		shell.setSize(250, 200);
		shell.setLocation(1012, 555);
		shell.setText("Castle info");
		shell.setImage(iconCache.stockImages[iconCache.castleIcon]);
		int xPos = castle.getXPos();
		int yPos = castle.getYPos();
		createLabel(shell, "Castle location  :  " + xPos + " , " + yPos);
		Class<? extends CreatureFactory> soldierFactoryClass = (new SoldierFactory()).getClass();
		String str;
		if (castle.hasFactory(soldierFactoryClass))
		{
			str = castle.getFactory(soldierFactoryClass).getName();
			createLabel(shell, "Soldier factories  :  " + str);
		}
		else
		{
			createLabel(shell, "Soldier factories  :  none");
		}
		Class<? extends CreatureFactory> goblinFactoryClass = (new GoblinFactory()).getClass();
		if (castle.hasFactory(goblinFactoryClass))
		{
			str = castle.getFactory(goblinFactoryClass).getName();
			createLabel(shell, "Goblin factories  :  " + str);
		}
		else
		{
			createLabel(shell, "Goblin factories  :  none");
		}
		if (castle.getArmy() != null)
		{
			str = castle.getArmy().toString();
			createLabel(shell, "Army  :\n" + str);
		}
		else
		{
			createLabel(shell, "Army  :  none");
		}
		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
	}


	private void createStatusWindow(boolean initStatus)
	{

		if (statusComposite != null && statusComposite.isDisposed() == false)
		{
			statusComposite.dispose();
		}
		
		statusComposite = new Composite(sash, SWT.BORDER);
		statusComposite.setBackground(black);
		GridData d = new GridData(GridData.FILL_BOTH);
		statusComposite.setLayoutData(d);

		GridLayout tempLayout = new GridLayout();
		statusComposite.setLayout(tempLayout);

		if(initStatus)
			updateStatusWindow();
	}


	private void updateStatusWindow()
	{
		Player p = gameController.getGameState().getPlayers().elementAt(currentPlayerIndex);
		TableItem ti;

		statusComposite.setBackground(white);
		Control[] children = statusComposite.getChildren();
		for (int i = 0; i < children.length; i++)
		{
			children[i].dispose();
		}

		CLabel firstLabel = new CLabel(statusComposite, SWT.CENTER);
		firstLabel.setBackground(white);
		firstLabel.setImage(iconCache.stockImages[iconCache.appIcon]);
		firstLabel.setText("  PLAYER   STATUS");

		String str = p.getName();
		createLabel(statusComposite, "PLAYER  NAME  :  " + str);
		createLabel(statusComposite, "");
		createLabel(statusComposite, "DAY  :  " + p.getDayAsString());
		createLabel(statusComposite, "");

		if (p.getHero() == null)
		{
			createLabel(statusComposite, "You  have  no  hero  !!!");
		}
		else
		{
			int xPos = p.getHero().getXPos();
			int yPos = p.getHero().getYPos();
			createLabel(statusComposite, "HERO  POSITION  :  " + xPos + " , " + yPos);
			createLabel(statusComposite, "HERO MOVES LEFT  :  " + p.getMovesLeft());
			createLabel(statusComposite, "DEFENCE  SKILL  :  " + p.getHero().getDefenseSkill());
			createLabel(statusComposite, "ATTACK   SKILL  :  " + p.getHero().getAttackSkill());
			createLabel(statusComposite, "");

			createLabel(statusComposite, "ARMY  :");
			Table armyTable = new Table(statusComposite, SWT.BORDER);
			TableColumn armyCol1 = new TableColumn(armyTable, SWT.CENTER);
			TableColumn armyCol2 = new TableColumn(armyTable, SWT.CENTER);
			armyCol1.setText("Slot");
			armyCol2.setText("Units");
			armyCol1.setWidth(40);
			armyCol2.setWidth(138);
			armyTable.setHeaderVisible(true);
			Creature[] creaturesArray = p.getHero().getArmy().getCreatures();
			for (Integer j=1 ; j<6 ; ++j)
			{
				ti = new TableItem(armyTable, SWT.NONE);
				ti.setText (0, j.toString());
				if (creaturesArray[j-1] != null)
				{
					ti.setText (1, creaturesArray[j-1].toString());
				}
				else
				{
					ti.setText (1, "none");
				}
			}
		}

		createLabel(statusComposite, "MINE  LIST  :");
		Table minesTable = new Table(statusComposite, SWT.BORDER);
		TableColumn minesCol1 = new TableColumn(minesTable, SWT.CENTER);
		TableColumn minesCol2 = new TableColumn(minesTable, SWT.CENTER);
		minesCol1.setText("Mine");
		minesCol2.setText("Quantity");
		minesCol1.setWidth(89);
		minesCol2.setWidth(89);
		minesTable.setHeaderVisible(true);
		Integer woodNum = p.getMineQuantity("wood");
		Integer goldNum = p.getMineQuantity("gold");
		Integer stoneNum = p.getMineQuantity("stone");
		ti = new TableItem(minesTable, SWT.NONE);
		ti.setText(new String[] {"Wood", woodNum.toString()});
		ti = new TableItem(minesTable, SWT.NONE);
		ti.setText(new String[] {"Gold", goldNum.toString()});
		ti = new TableItem(minesTable, SWT.NONE);
		ti.setText(new String[] {"Stone", stoneNum.toString()});

		createLabel(statusComposite, "TREASURY LIST  :");
		Table treasursTable = new Table(statusComposite, SWT.BORDER);
		TableColumn treasursCol1 = new TableColumn(treasursTable, SWT.CENTER);
		TableColumn treasursCol2 = new TableColumn(treasursTable, SWT.CENTER);
		treasursCol1.setText("Resource");
		treasursCol2.setText("Amount");
		treasursCol1.setWidth(89);
		treasursCol2.setWidth(89);
		treasursTable.setHeaderVisible(true);
		Integer woodAmount = p.getCurrentTreasuryAmount("wood");
		Integer goldAmount = p.getCurrentTreasuryAmount("gold");
		Integer stoneAmount = p.getCurrentTreasuryAmount("stone");
		ti = new TableItem(treasursTable, SWT.NONE);
		ti.setText(new String[] {"Wood", woodAmount.toString()});
		ti = new TableItem(treasursTable, SWT.NONE);
		ti.setText(new String[] {"Gold", goldAmount.toString()});
		ti = new TableItem(treasursTable, SWT.NONE);
		ti.setText(new String[] {"Stone", stoneAmount.toString()});

		createLabel(statusComposite, "CASTLES  :");
		int numOfCastles = p.getCastles().size();
		if (numOfCastles == 0)
		{
			createLabel(statusComposite, "You have no castles");
			createLabel(statusComposite, "Days witout castle  :  " + (p.getDaysWithoutCastles()+1));
		}
		for (int i=0 ; i<numOfCastles ; ++i)
		{
			final Castle castle = p.getCastles().get(i);
			int castleXPos = castle.getXPos();
			int castleYPos = castle.getYPos();
			Button button = new Button(statusComposite, SWT.NONE);
			button.setText("Castle at  " + castleXPos + " , " + castleYPos);
			button.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					displayCastleInfo(castle);
				}
			});
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

	public static void displayMessage(String msg)
	{
		MessageBox box = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION);
		box.setText(Display.getCurrent().getActiveShell().getText());
		box.setMessage(msg);
		box.open();
	}

	public void startNewGame()
	{
		int numberOfPlayers = getNumberOfPlayers();
		if(numberOfPlayers != 0)
		{
			Vector<Player> players = getPlayers(numberOfPlayers);
			if(players != null)
			{
				gameController.initNewGame(players);
		
				createBoardWindow(true);
				createStatusWindow(true);
				sash.setWeights(new int[] { 85, 15 });
			}
		}
	}

	/**
	 * @return Number of players from user input
	 */
	public static int getNumberOfPlayers()
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
			else
				return 0;

			if (response != null)
				numberOfPlayers = Helper.tryParseInt(response);

		} while (!Helper.isIntBetween(numberOfPlayers, Constants.MIN_PLAYERS, Constants.MAX_PLAYERS));

		return numberOfPlayers;
	}

	public static Vector<Player> getPlayers(int numberOfPlayers)
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

	public void openFileDlg()
	{
	
		FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);

		fileDialog.setFilterExtensions(new String[] { "*.sav;", "*.*" });
		fileDialog.setFilterNames(new String[] { "Saved Games" + " (*.sav)", "All Files" + " (*.*)" });
		String name = fileDialog.open();

		if (name == null)
			return;
		if (!name.endsWith(".sav"))
		{
			displayMessage("Not a valid Heroes *.sav file.\nTry again or start a new game");
			return;
		}
		File file = new File(name);
		if (!file.exists())
		{
			displayError("File " + file.getName() + " " + "Does not exist");
			return;
		}

		Cursor waitCursor = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
		shell.setCursor(waitCursor);

		this.gameController.loadGame(name);
		currentPlayerIndex = this.gameController.getGameState().getWhosTurn();

		if (gameController.getGameState().getBoard() == null)
		{
			displayMessage("The file you opened doesn't contain a valid Heroes saved game.\n" +
							"Please try again with a different file");
			return;
		}
		
		createBoardWindow(true);
		createStatusWindow(true);
		sash.setWeights(new int[] { 85, 15 });

		shell.setCursor(null);
		waitCursor.dispose();
	}

	public boolean save()
	{
		if (file == null)
			return saveAs();

		Cursor waitCursor = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
		shell.setCursor(waitCursor);

		gameController.saveGame(file);
		this.gameController.saveGame(file);
		displayMessage("Game successfully saved to: " + file);

		shell.setCursor(null);
		waitCursor.dispose();

		return true;
	}

	public boolean saveAs()
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
		final MenuItem saveAsSubItem, saveSubItem;
		
		// File menu.
		MenuItem item = new MenuItem(menuBar, SWT.CASCADE);
		item.setText("File");
		Menu menu = new Menu(shell, SWT.DROP_DOWN);
		item.setMenu(menu);

		menu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e)
			{}
		});

		//space reserve for File -> New and File -> Open Saved Game
		MenuItem subItem = new MenuItem(menu, SWT.NULL);
		MenuItem openSubItem = new MenuItem(menu, SWT.NULL);
		new MenuItem(menu, SWT.SEPARATOR);
		

		// File -> Save.
		saveSubItem = new MenuItem(menu, SWT.NULL);
		saveSubItem.setEnabled(gameController.getGameState().getBoard() != null);
		saveSubItem.setText("Save Game");
		saveSubItem.setAccelerator(SWT.MOD1 + 'S');
		saveSubItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				save();
			}
		});


		// File -> Save As.
		saveAsSubItem = new MenuItem(menu, SWT.NULL);
		saveAsSubItem.setEnabled(gameController.getGameState().getBoard() != null);
		saveAsSubItem.setText("Save Game as");
		saveAsSubItem.setAccelerator(SWT.MOD1 + 'A');
		saveAsSubItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				saveAs();
			}
		});
		
		// File -> New Game
		subItem.setText("New Game");
		subItem.setAccelerator(SWT.MOD1 + 'N');
		subItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				//TODO: ariel
				startNewGame();
				saveSubItem.setEnabled(gameController.getGameState().getBoard() != null);
				saveAsSubItem.setEnabled(gameController.getGameState().getBoard() != null);
			}
		});
		
		
		// File -> Open Saved Game
		openSubItem.setText("Open Saved Game");
		openSubItem.setAccelerator(SWT.MOD1 + 'O');
		openSubItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				//TODO: ariel
				openFileDlg();
				saveSubItem.setEnabled(gameController.getGameState().getBoard() != null);
				saveAsSubItem.setEnabled(gameController.getGameState().getBoard() != null);
			}
		});

		new MenuItem(menu, SWT.SEPARATOR);

		// File -> Exit.
		subItem = new MenuItem(menu, SWT.NULL);
		subItem.setText("Exit");
		subItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				close();
				shell.close();
			}
		});
	}

	/**
	 * Creates all items located in the popup menu and associates all the menu
	 * items with their appropriate functions.
	 * @param style
	 * 			if style == SWT.DROP_DOWN create as drop down menu
	 * 			else create as pop up menu
	 *
	 * @return Menu The created popup menu.
	 */
	
	private Menu createHeroPopUpMenu(int style)
	{
		Menu popUpMenu;
		if(eclipseComposite != null)
		{
			popUpMenu = new Menu(eclipseComposite);
		}
		else
		{
			if (style == SWT.DROP_DOWN)
				popUpMenu = new Menu(shell, SWT.DROP_DOWN);
			else
				popUpMenu = new Menu(shell, SWT.POP_UP);
		}

		popUpMenu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e)
			{}
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
	 * @param style
	 * 			if style == SWT.DROP_DOWN create as drop down menu
	 * 			else create as pop up menu
	 *
	 * @return Menu The created popup menu.
	 */
	private Menu createCastlePopUpMenu(int style)
	{
		Menu popUpMenu;
		if(eclipseComposite != null)
		{
			popUpMenu = new Menu(eclipseComposite);
		}
		else
		{
			if (style == SWT.DROP_DOWN)
				popUpMenu = new Menu(shell, SWT.DROP_DOWN);
			else
				popUpMenu = new Menu(shell, SWT.POP_UP);
		}

		popUpMenu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e)
			{}
		});

		if(eclipseComposite!=null)
		{
		    final MenuItem goblinBuildItem = new MenuItem(popUpMenu, SWT.PUSH);
		    goblinBuildItem.setText("Build Goblin factory");
		    goblinBuildItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					handleBuildCommand("goblin");
				}
			});
	
		    final MenuItem soldierBuildItem = new MenuItem(popUpMenu, SWT.PUSH);
		    soldierBuildItem.setText("Build Soldier factory");
		    soldierBuildItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					handleBuildCommand("soldier");
				}
			});
		    
		    final MenuItem buildPricesItem = new MenuItem(popUpMenu, SWT.PUSH);
		    buildPricesItem.setText("Build Prices");
		    buildPricesItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					Shell buildPricesShell = new Shell(Display.getCurrent().getActiveShell());
					buildPricesShell.setLayout(new FillLayout());
					buildPricesShell.setSize(200, 200);
					buildPricesShell.setText("Build Prices");
					buildPricesShell.setLocation(450, 450);
					buildPricesShell.setImage(iconCache.stockImages[iconCache.appIcon]);
					Label pricesLable = new Label(buildPricesShell, SWT.NULL);
					pricesLable.setBackground(new Color(display, 255,255,255));
					pricesLable.setBounds(buildPricesShell.getClientArea());
					pricesLable.setText(GameController.handleBuildPricesCommand());
					buildPricesShell.open();
					
					while (!buildPricesShell.isDisposed())
						 if (!display.readAndDispatch())
							 display.sleep();
				}
			});
	
		    new MenuItem(popUpMenu, SWT.SEPARATOR);

		    final MenuItem goblinMakeItem = new MenuItem(popUpMenu, SWT.PUSH);
		    goblinMakeItem.setText("Make Goblin");
		    goblinMakeItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					handleMakeCommand("goblin");
				}
			});
	
		    final MenuItem soldierMakeItem = new MenuItem(popUpMenu, SWT.PUSH);
		    soldierMakeItem.setText("Make Soldier");
		    soldierMakeItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					handleMakeCommand("soldier");
				}
			});
		    
		    final MenuItem makePricesItem = new MenuItem(popUpMenu, SWT.PUSH);
		    makePricesItem.setText("Unit Prices");
		    makePricesItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					Shell makePricesShell = new Shell(Display.getCurrent().getActiveShell());
					makePricesShell.setLayout(new FillLayout());
					makePricesShell.setSize(200, 200);
					makePricesShell.setText("Unit Prices");
					makePricesShell.setLocation(450, 450);
					makePricesShell.setImage(iconCache.stockImages[iconCache.appIcon]);
					Label pricesLable = new Label(makePricesShell, SWT.NULL);
					pricesLable.setBackground(new Color(display, 255,255,255));
					pricesLable.setBounds(makePricesShell.getClientArea());
					pricesLable.setText(GameController.handleMakePricesCommand());
					makePricesShell.open();
					
					while (!makePricesShell.isDisposed())
						 if (!display.readAndDispatch())
							 display.sleep();
				}
			});
	
		    new MenuItem(popUpMenu, SWT.SEPARATOR);
	
		    final MenuItem item = new MenuItem(popUpMenu, SWT.PUSH);
			item.setText("End Turn");
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					handleEndTurnCommand();
				}
			});
		}
		else
		{
			MenuItem item = new MenuItem(popUpMenu, SWT.CASCADE);
			item.setText("Build");
	
			Menu buildMenu;
			if(eclipseComposite != null)
				buildMenu = new Menu(eclipseComposite);
			else
				buildMenu = new Menu(shell, SWT.DROP_DOWN);
			
			item.setMenu(buildMenu);
		    final MenuItem goblinBuildItem = new MenuItem(buildMenu, SWT.PUSH);
		    goblinBuildItem.setText("Goblin factory");
		    goblinBuildItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					handleBuildCommand("goblin");
				}
			});
	
		    final MenuItem soldierBuildItem = new MenuItem(buildMenu, SWT.PUSH);
		    soldierBuildItem.setText("Soldier factory");
		    soldierBuildItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					handleBuildCommand("soldier");
				}
			});
		    
		    final MenuItem buildPricesItem = new MenuItem(buildMenu, SWT.PUSH);
		    buildPricesItem.setText("Build Prices");
		    buildPricesItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					Shell buildPricesShell = new Shell(Display.getCurrent().getActiveShell());
					buildPricesShell.setLayout(new FillLayout());
					buildPricesShell.setSize(200, 200);
					buildPricesShell.setText("Build Prices");
					buildPricesShell.setLocation(450, 450);
					buildPricesShell.setImage(iconCache.stockImages[iconCache.appIcon]);
					Label pricesLable = new Label(buildPricesShell, SWT.NULL);
					pricesLable.setBackground(new Color(display, 255,255,255));
					pricesLable.setBounds(buildPricesShell.getClientArea());
					pricesLable.setText(GameController.handleBuildPricesCommand());
					buildPricesShell.open();
					
					while (!buildPricesShell.isDisposed())
						 if (!display.readAndDispatch())
							 display.sleep();
				}
			});
	
			item = new MenuItem(popUpMenu, SWT.CASCADE);
			item.setText("Make");
	
			Menu makeMenu;
			if(eclipseComposite != null)
				makeMenu = new Menu(eclipseComposite);
			else
				makeMenu = new Menu(shell, SWT.DROP_DOWN);
			item.setMenu(makeMenu);
		    final MenuItem goblinMakeItem = new MenuItem(makeMenu, SWT.PUSH);
		    goblinMakeItem.setText("Goblin");
		    goblinMakeItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					handleMakeCommand("goblin");
				}
			});
	
		    final MenuItem soldierMakeItem = new MenuItem(makeMenu, SWT.PUSH);
		    soldierMakeItem.setText("Soldier");
		    soldierMakeItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					handleMakeCommand("soldier");
				}
			});
		    
		    final MenuItem makePricesItem = new MenuItem(makeMenu, SWT.PUSH);
		    makePricesItem.setText("Unit Prices");
		    makePricesItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					Shell makePricesShell = new Shell(Display.getCurrent().getActiveShell());
					makePricesShell.setLayout(new FillLayout());
					makePricesShell.setSize(200, 200);
					makePricesShell.setText("Unit Prices");
					makePricesShell.setLocation(450, 450);
					makePricesShell.setImage(iconCache.stockImages[iconCache.appIcon]);
					Label pricesLable = new Label(makePricesShell, SWT.NULL);
					pricesLable.setBackground(new Color(display, 255,255,255));
					pricesLable.setBounds(makePricesShell.getClientArea());
					pricesLable.setText(GameController.handleMakePricesCommand());
					makePricesShell.open();
					
					while (!makePricesShell.isDisposed())
						 if (!display.readAndDispatch())
							 display.sleep();
				}
			});
	
		    new MenuItem(popUpMenu, SWT.SEPARATOR);
	
		    item = new MenuItem(popUpMenu, SWT.CASCADE);
			item.setText("End Turn");
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					handleEndTurnCommand();
				}
			});
		}

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
		Menu popUpMenu;
		if(eclipseComposite != null)
			popUpMenu = new Menu(eclipseComposite);
		else
			popUpMenu = new Menu(shell, SWT.POP_UP);

		/**
		 * Adds a listener to handle enabling and disabling some items in the
		 * Edit submenu.
		 */
	
		popUpMenu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e)
			{
			}
		});
		

		if(eclipseComposite!=null)
		{
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
			
			new MenuItem(popUpMenu, SWT.SEPARATOR);
			
			final MenuItem goblinBuildItem = new MenuItem(popUpMenu, SWT.PUSH);
		    goblinBuildItem.setText("Build Goblin factory");
		    goblinBuildItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					handleBuildCommand("goblin");
				}
			});
	
		    final MenuItem soldierBuildItem = new MenuItem(popUpMenu, SWT.PUSH);
		    soldierBuildItem.setText("Build Soldier factory");
		    soldierBuildItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					handleBuildCommand("soldier");
				}
			});
		    
		    final MenuItem buildPricesItem = new MenuItem(popUpMenu, SWT.PUSH);
		    buildPricesItem.setText("Build Prices");
		    buildPricesItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					Shell buildPricesShell = new Shell(Display.getCurrent().getActiveShell());
					buildPricesShell.setLayout(new FillLayout());
					buildPricesShell.setSize(200, 200);
					buildPricesShell.setText("Build Prices");
					buildPricesShell.setLocation(450, 450);
					buildPricesShell.setImage(iconCache.stockImages[iconCache.appIcon]);
					Label pricesLable = new Label(buildPricesShell, SWT.NULL);
					pricesLable.setBackground(new Color(display, 255,255,255));
					pricesLable.setBounds(buildPricesShell.getClientArea());
					pricesLable.setText(GameController.handleBuildPricesCommand());
					buildPricesShell.open();
					
					while (!buildPricesShell.isDisposed())
						 if (!display.readAndDispatch())
							 display.sleep();
				}
			});
	
		    new MenuItem(popUpMenu, SWT.SEPARATOR);

		    final MenuItem goblinMakeItem = new MenuItem(popUpMenu, SWT.PUSH);
		    goblinMakeItem.setText("Make Goblin");
		    goblinMakeItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					handleMakeCommand("goblin");
				}
			});
	
		    final MenuItem soldierMakeItem = new MenuItem(popUpMenu, SWT.PUSH);
		    soldierMakeItem.setText("Make Soldier");
		    soldierMakeItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					handleMakeCommand("soldier");
				}
			});
		    
		    final MenuItem makePricesItem = new MenuItem(popUpMenu, SWT.PUSH);
		    makePricesItem.setText("Unit Prices");
		    makePricesItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					Shell makePricesShell = new Shell(Display.getCurrent().getActiveShell());
					makePricesShell.setLayout(new FillLayout());
					makePricesShell.setSize(200, 200);
					makePricesShell.setText("Unit Prices");
					makePricesShell.setLocation(450, 450);
					makePricesShell.setImage(iconCache.stockImages[iconCache.appIcon]);
					Label pricesLable = new Label(makePricesShell, SWT.NULL);
					pricesLable.setBackground(new Color(display, 255,255,255));
					pricesLable.setBounds(makePricesShell.getClientArea());
					pricesLable.setText(GameController.handleMakePricesCommand());
					makePricesShell.open();
					
					while (!makePricesShell.isDisposed())
						 if (!display.readAndDispatch())
							 display.sleep();
				}
			});
	
		    new MenuItem(popUpMenu, SWT.SEPARATOR);
			
		    final MenuItem goblinSplitItem = new MenuItem(popUpMenu, SWT.PUSH);
		    goblinSplitItem.setText("Split Goblin");
		    goblinSplitItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					handleSplitCommand("goblin");
				}
			});
	
		    final MenuItem soldierSplitItem = new MenuItem(popUpMenu, SWT.PUSH);
		    soldierSplitItem.setText("Split Soldier");
		    soldierSplitItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					handleSplitCommand("soldier");
				}
			});
	
		    new MenuItem(popUpMenu, SWT.SEPARATOR);
			
		    final MenuItem goblinJoinItem = new MenuItem(popUpMenu, SWT.PUSH);
		    goblinJoinItem.setText("Join Goblin");
		    goblinJoinItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					handleJoinCommand("goblin");
				}
			});
	
		    final MenuItem soldierJoinItem = new MenuItem(popUpMenu, SWT.PUSH);
		    soldierJoinItem.setText("Join Soldier");
		    soldierJoinItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					handleJoinCommand("soldier");
				}
			});
		}
		else
		{
			MenuItem item = new MenuItem(popUpMenu, SWT.CASCADE);
			
			item.setText("Hero Options");
			item.setMenu(createHeroPopUpMenu(SWT.DROP_DOWN));
			
			item = new MenuItem(popUpMenu, SWT.CASCADE);
			item.setText("Castle Options");
			item.setMenu(createCastlePopUpMenu(SWT.DROP_DOWN));
			
			
			item = new MenuItem(popUpMenu, SWT.CASCADE);
			item.setText("Split");
	
			Menu splitMenu = new Menu(shell, SWT.DROP_DOWN);
			item.setMenu(splitMenu);
		    final MenuItem goblinSplitItem = new MenuItem(splitMenu, SWT.PUSH);
		    goblinSplitItem.setText("Goblin");
		    goblinSplitItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					handleSplitCommand("goblin");
				}
			});
	
		    final MenuItem soldierSplitItem = new MenuItem(splitMenu, SWT.PUSH);
		    soldierSplitItem.setText("Soldier");
		    soldierSplitItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					handleSplitCommand("soldier");
				}
			});
	
			item = new MenuItem(popUpMenu, SWT.CASCADE);
			item.setText("Join");
	
			Menu joinMenu = new Menu(shell, SWT.DROP_DOWN);
			item.setMenu(joinMenu);
		    final MenuItem goblinJoinItem = new MenuItem(joinMenu, SWT.PUSH);
		    goblinJoinItem.setText("Goblin");
		    goblinJoinItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					handleJoinCommand("goblin");
				}
			});
	
		    final MenuItem soldierJoinItem = new MenuItem(joinMenu, SWT.PUSH);
		    soldierJoinItem.setText("Soldier");
		    soldierJoinItem.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					handleJoinCommand("soldier");
				}
			});
		}

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
				 showGameAssistanceMbox();
			 }
		 });


		 // Help -> About Text Editor
		 MenuItem subItem = new MenuItem(menu, SWT.NULL);
		 subItem.setText("About");
		 subItem.addSelectionListener(new SelectionAdapter() {
			 public void widgetSelected(SelectionEvent e)
			 {
				 showAboutMbox();
			 }
		 });
	 }
	 public void showGameAssistanceMbox()
	 {
		 String helpString;

		 helpString = "\n" +
		 "Heroes of Might and Magic (TAU Version)\n" +
		 "Gameplay assistance\n\n" +
		 "To move: Right click your hero, or just click hero and click destenation.\n" +
		 "To end turn: Right click your hero or castle(s) and select 'end turn'\n" +
		 "Castle menu and options are:\n" +
		 "Build - build a creature factory\n" +
		 "Make - make a new creature\n" +
		 "Split - move units from hero to castle\n" +
		 "Join - move units from castle to hero\n" +
		 "\n" +
		 "Player info is on the right part of the screen (status window)\n\n" +
		 "Use the File menu to save a game,load a game or start a new game (current game will not be saved automatically)\n\n" +
		 "Use the Highscores menu to view or reset the highscores table\n\n" +
		 "Quitting the game is via File -> Exit\n\n\n" +
		 "Enjoy the game";
		 displayMessage(helpString);
	 }
	 public void showAboutMbox()
	 {
		 String aboutString;
		 aboutString = "\n" +
		 "Heroes of Might and Magic (TAU Version)\n"+
		 "(C) 2009 - All right reserved!";
		 displayMessage(aboutString);
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
				displayHighscores();
			}
		});
		MenuItem subItem2 = new MenuItem(menu, SWT.NULL);
		subItem2.setText("&Reset Highscores");
		subItem2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				resetHighscores();	
			}
		});
	}
	public void displayHighscores()
	{
		GameScoreBoard board = new GameScoreBoard();
		board.load();
		displayTable(board);
	}
	public void resetHighscores()
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
		tableShell.setSize(300, 300);
		tableShell.setText("Highscores - The 10 Best Players");
		tableShell.setImage(iconCache.stockImages[iconCache.highscoreIcon]);
		Table scoreTable = new Table(tableShell, SWT.CENTER);
		TableColumn col1 = new TableColumn(scoreTable, SWT.CENTER);
		TableColumn col2 = new TableColumn(scoreTable, SWT.CENTER);
		col1.setText("Player Name");
		col2.setText("Player Score");
		col1.setWidth(146);
		col2.setWidth(147);
		scoreTable.setHeaderVisible(true);

		TableItem ti;
		Font newFont = scoreTable.getFont(); //just an initialization for the compiler

		for (int i = 0; i < 10; i++)
		{
			tempPlayer = board.getPlayerAt(i);
			tempScore = board.getScoreAt(i);
			ti = new TableItem(scoreTable, SWT.CENTER);

			if (tempPlayer == null)
				name = "----";
			else
				name = tempPlayer.getName();
			if (tempScore == 0)
				score = "0";
			else
				score = tempScore + "";

			ti.setText(new String[] { name, score });
			Font initialFont = ti.getFont();
			FontData[] fontData = initialFont.getFontData();
		    for (int k = 0; k < fontData.length; k++)
		    {
		      fontData[k].setHeight(2 + initialFont.getFontData()[k].getHeight());
		      fontData[k].setStyle(SWT.BOLD);
		    }
		    newFont = new Font(display, fontData);
		    ti.setFont(newFont);
		}

		tableShell.open();
		while (!tableShell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		newFont.dispose();
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
		this.gameController.getGameState().setWhosTurn(currentPlayerIndex);
		p = gameController.getGameState().getPlayers().elementAt(currentPlayerIndex);
		createBoardWindow(true);
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
			this.gameController.getGameState().setWhosTurn(currentPlayerIndex);
			p = gameController.getGameState().getPlayers().elementAt(currentPlayerIndex);
			createBoardWindow(true);
			updateStatusWindow();
		}
	}

	private void removeDeadPlayers()
	{
		for (Player player : this.gameController.removeDeadPlayers())
			displayMessage(player.getName() + " is out of the game.");
	}

	private void endGame(Player winner)
	{
		displayMessage("game ended.");
		if (winner != null)
		{
			displayMessage("winner is: " + winner.getName() + " with a score of: " + winner.finalScore());
			Helper.getScoreBoard().addToScoreBoard(winner, winner.finalScore());
		}
		Helper.getScoreBoard().save();
		System.exit(0);
	}

	/**
	 * @param player
	 * @param userInput
	 */
	private boolean handleMoveCommand(String[] userInput)
	{
		int newX, newY;
		newX = Helper.tryParseInt(userInput[0].trim());
		newY = Helper.tryParseInt(userInput[1].trim());

		if (!Helper.isIntBetween(newX, 0, Constants.BOARD_SIZE - 1) || !Helper.isIntBetween(newY, 0, Constants.BOARD_SIZE - 1))
		{
			if (!gameController.getGameState().getPlayers().elementAt(currentPlayerIndex).isComputer())
				displayError("Invallid Input. Outside of board - Try again");
			return false;
		} else if (gameController.getGameState().getPlayers().elementAt(currentPlayerIndex).move(newX, newY, this.gameController.getGameState().getBoard()))
		{
			createBoardWindow(true);
			updateStatusWindow();
			return true;
		} else
		{
			displayError("Illegal move ! " + gameController.getGameState().getPlayers().elementAt(currentPlayerIndex).getName() + " You can only move " + gameController.getGameState().getPlayers().elementAt(currentPlayerIndex).getMovesLeft() + " more step(s) .");
			return false;
		}
	}

	private void handleBuildCommand(String type)
	{
		CreatureFactory factory = null;
		Castle currentCastle = gameController.getGameState().getBoard().getBoardState(currentPoint.x, currentPoint.y).getCastle();

		if(currentCastle != null)
		{
			if (type.equals("goblin"))
				factory = new GoblinFactory();
			else if (type.equals("soldier"))
				factory = new SoldierFactory();

			if (factory != null)
			{
				Class<? extends CreatureFactory> factoryClass = factory.getClass();

				if (currentCastle.hasFactory(factoryClass))
					displayError("There is already a factory of this type in this castle");
				else
				{
					if (currentCastle.canBuildFactory(factoryClass))
						currentCastle.addFactory(currentCastle.buildFactory(factoryClass));
					else
					{
						String msg = currentCastle.getPlayer().getName() +
										" doesn't have enough resources.\n\n" +
										"Need:\n";
						
						for (ResourceType rType : ResourceType.values())
						{
							msg += rType.getTypeName() + ":\t " + factory.getPrice(rType.getTypeName()) + "\n";
						}
						msg += "\nHas only:\n";
						for (ResourceType rType : ResourceType.values())
						{
							msg += rType.getTypeName() + ":\t " + currentCastle.getPlayer().getCurrentTreasuryAmount(rType.getTypeName()) + "\n";
						}
						displayError(msg);
					}
				}
				
			} else
				displayError("Unknown creature type!");
		}
		updateStatusWindow();
	}

	private void handleSplitCommand(String type)
	{
		Castle currentCastle = gameController.getGameState().getBoard().getBoardState(currentPoint.x, currentPoint.y).getCastle();
		String message;
		String response = null;
		int numberOfUnits = 0;

		Hero hero = gameController.getGameState().getPlayers().elementAt(currentPlayerIndex).getHero();
		if (hero == null)
		{
			displayError("Sorry, but you don't have a hero.");
			return;
		}

		message = "Enter desired number of units to split to the castle: ";

		InputDialog numberInput = new InputDialog(Display.getCurrent().getActiveShell(), "Number of Units", message, null, null);
		if (numberInput.open() == Window.OK)
		{
			response = numberInput.getValue();
		}
		else
		{
			numberInput.close();
			return;
		}

		if (response != null)
			numberOfUnits = Helper.tryParseInt(response);
		if (numberOfUnits < 1)
		{
			displayError("Illegal input.");
			return;
		}

		Creature creature = null;
		if (type.equals("goblin"))
		{
			creature = new Goblin(numberOfUnits);
		} else if (type.equals("soldier"))
		{
			creature = new Soldier(numberOfUnits);
		} else
		{
			displayError("Unknown creature type!");
			return;
		}

		if (!currentCastle.canAddToArmy(creature.getClass()))
		{
			displayError("Army in " + currentCastle.printLocation() + " is full.");
			return;
		} else if (!hero.removeFromArmy(creature))
		{
			displayError("You dont have enough units to split");
			return;
		} else
		{
			currentCastle.addToArmy(creature);
			updateStatusWindow();
			return;
		}
	}

	private void handleJoinCommand(String type)
	{
		Castle currentCastle = gameController.getGameState().getBoard().getBoardState(currentPoint.x, currentPoint.y).getCastle();
		String message;
		String response = null;
		int numberOfUnits = 0;

		Hero hero = gameController.getGameState().getPlayers().elementAt(currentPlayerIndex).getHero();
		if (hero == null)
		{
			displayError("Sorry, but you don't have a hero.");
			return;
		}

		message = "Enter desired number of units to join from the castle: ";

		InputDialog numberInput = new InputDialog(Display.getCurrent().getActiveShell(), "Number of Units", message, null, null);
		if (numberInput.open() == Window.OK)
		{
			response = numberInput.getValue();
		} else
		{
			numberInput.close();
			return;
		}

		if (response != null)
			numberOfUnits = Helper.tryParseInt(response);
		if (numberOfUnits < 1)
		{
			displayError("Illegal input.");
			return;
		}

		Creature creature = null;
		if (type.equals("goblin"))
		{
			creature = new Goblin(numberOfUnits);
		} else if (type.equals("soldier"))
		{
			creature = new Soldier(numberOfUnits);
		} else
		{
			displayError("Unknown creature type!");
			return;
		}

		if (!hero.canAddToArmy(creature.getClass()))
		{
			displayError("Army of " + hero.toString() + " is full.");
			return;
		} else if (!currentCastle.canRemoveFromArmy(creature))
		{
			displayError("You dont have enough units to join.");
			return;
		} else
		{
			currentCastle.removeFromArmy(creature);
			hero.addToArmy(creature);
			updateStatusWindow();
			return;
		}
	}
		

	private void handleMakeCommand(String type)
	{
		Class<? extends Creature> creatureClass = null;
		Castle currentCastle = gameController.getGameState().getBoard().getBoardState(currentPoint.x, currentPoint.y).getCastle();
		String message;
		String response = null;
		int numberOfUnits = 0;

		if (currentCastle != null)
		{
			if (type.equals("goblin"))
				creatureClass = Goblin.class;
			else if (type.equals("soldier"))
				creatureClass = Soldier.class;

			if (creatureClass != null)
			{
				int maxUnits = currentCastle.getAvailableUnits(creatureClass);

				if (maxUnits > 0)
				{
					message = "Enter desired number of units (1-" + maxUnits + "): ";

					InputDialog numberInput = new InputDialog(Display.getCurrent().getActiveShell(), "Number of Units", message, null, null);
					if (numberInput.open() == Window.OK)
					{
						response = numberInput.getValue();
					}
					else
					{
						numberInput.close();
						return;
					}

					if (response != null)
						numberOfUnits = Helper.tryParseInt(response);

					if (numberOfUnits > 0 && numberOfUnits <= maxUnits)
					{
						currentCastle.makeUnits(creatureClass, numberOfUnits);
						updateStatusWindow();
					}
					else
					{
						displayError("Number of units is our of range.");
					}
				}
				else
					displayError("Sorry, but you can't build units.");
			} else
				displayError("Unknown creature type.");
		}
	}
}