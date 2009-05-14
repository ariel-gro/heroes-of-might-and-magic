package tau.heroes;

import java.io.File;
import java.util.Vector;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseWheelListener;
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
import org.eclipse.swt.widgets.Combo;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tracker;

public class HeroesGui
{
	private Shell shell;

	private String file = null;

	private boolean isModified = false;
	
	private boolean isBoardInit = false;

	int numOfCells;

	private Color black;

	private Color white;

	private Color red;

	private static Display display;

	static Control currentChild = null;

	private GameController gameController;

	private static int currentPlayerIndex = 0;

	private static Point currentPoint;

	private static Point newPoint;

	public Composite eclipseComposite;

	private Composite boardComposite;

	private Composite statusComposite;

	private Point[][] boardPoints = null;
	
	private Composite[][] boardSquares = null;
	
	private Label[][] boardLabels = null;

	SashForm sash;

	private ScrolledComposite sc1;

	private ScrolledComposite sc2;

	Cursor cursor;;

	Cursor defaultCursor;

	public ResourcesView resourcesView;
	
	public ArmyView armyView;

	public Display getDisplay()
	{
		return display;
	}

	public HeroesGui(Display d, GameController gameController)
	{
		this.display = d;
		this.gameController = gameController;
		IconCache.initResources(display);
	}
 
	public Shell open()
	{
		shell = new Shell(display/* , SWT.APPLICATION_MODAL */);
		shell.setLayout(new FillLayout());
		shell.setImage(IconCache.stockImages[IconCache.appIcon]);
		shell.setText("Heroes of Might and Magic");
		shell.setMaximized(true);
		black = display.getSystemColor(SWT.COLOR_BLACK);
		white = display.getSystemColor(SWT.COLOR_WHITE);
		red = display.getSystemColor(SWT.COLOR_RED);
		cursor = new Cursor(display, SWT.CURSOR_NO);
		defaultCursor = new Cursor(display, SWT.NONE);
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

		sc1 = new ScrolledComposite(sash, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		sc1.setBackground(black);
		sc1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		sc2 = new ScrolledComposite(sash, SWT.BORDER | SWT.V_SCROLL);
		sc2.setBackground(black);
		sc2.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e)
			{
				Rectangle r = sc2.getClientArea();
				sc2.setMinSize(statusComposite.computeSize(r.width, SWT.DEFAULT));
			}
		});
		// sc2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
		// 1));

		initBlankGame();

		shell.open();

		// displayStartWindow();

		return shell;
	}

	public void createEclipseView(Composite theEclipseComposite)
	{
		eclipseComposite = theEclipseComposite;
		shell = eclipseComposite.getShell();
		black = display.getSystemColor(SWT.COLOR_BLACK);
		white = display.getSystemColor(SWT.COLOR_WHITE);
		cursor = new Cursor(display, SWT.CURSOR_NO);
		defaultCursor = new Cursor(display, SWT.NONE);
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

		sc1 = new ScrolledComposite(sash, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		sc1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		sc2 = new ScrolledComposite(sash, SWT.BORDER | SWT.V_SCROLL);
		sc2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		initBlankGame();

		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e)
			{
				e.doit = close();
			}
		});

		// displayStartWindow();
	}

	public void initBlankGame()
	{
		createBoardWindow(false);
		createStatusWindow(false);
		// sash.setWeights(new int[] { 85, 15 });
		sash.setWeights(new int[] { 80, 20 });
	}

	public boolean close()
	{
		if (isModified)
		{
			// ask user if they want to save current game
			MessageBox box = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_WARNING
				| SWT.YES | SWT.NO | SWT.CANCEL);
			box.setText(Display.getCurrent().getActiveShell().getText());
			box.setMessage("Save game before closing?");

			int choice = box.open();
			if (choice == SWT.CANCEL)
			{
				return false;
			}
			else if (choice == SWT.YES)
			{
				if (!save())
					return false;
			}
		}

		IconCache.freeResources();

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
			if (bs.getResource() != null
				&& bs.getResource().getType().getTypeName().equals(ResourceType.WOOD.getTypeName()))
			{
				return IconCache.blueInWoodIcon;
			}
			else if (bs.getResource() != null
				&& bs.getResource().getType().getTypeName().equals(ResourceType.GOLD.getTypeName()))
			{
				return IconCache.blueInGlodMineIcon;
			}
			else if (bs.getResource() != null
				&& bs.getResource().getType().getTypeName()
					.equals(ResourceType.STONE.getTypeName()))
			{
				return IconCache.blueInStoneIcon;
			}
			else if (bs.getCastle() != null)
			{
				switch (currentPlayerIndex)
				{
				case 0:
					return IconCache.blueInCastleIcon;
				case 1:
					return IconCache.blueInDungeonIcon;
				case 2:
					return IconCache.blueInRampartIcon;
				case 3:
					return IconCache.blueInTowerIcon;

				default:
					return IconCache.blueInDungeonIcon;
				}

			}
			else
				return IconCache.blueHeroIcon;

		}
		else if ((bs.getCastle()) != null)
		{
			switch (currentPlayerIndex)
			{
			case 0:
				return IconCache.castleIcon;
			case 1:
				return IconCache.dungeonIcon;
			case 2:
				return IconCache.rampartIcon;
			case 3:
				return IconCache.towerIcon;

			default:
				return IconCache.castleIcon;
			}
		}
		else if ((bs.getResource()) != null)
		{
			if (bs.getResource().getType().getTypeName().equals(ResourceType.WOOD.getTypeName()))
			{
				return IconCache.woodIcon;
			}
			else if (bs.getResource().getType().getTypeName().equals(ResourceType.GOLD
				.getTypeName()))
			{
				return IconCache.goldMineIcon;
			}
			else if (bs.getResource().getType().getTypeName().equals(ResourceType.STONE
				.getTypeName()))
			{
				return IconCache.stoneIcon;
			}
		}

		return IconCache.grassIcon;
	}

	private String fromBoardToDisplayDecription(int x, int y)
	{
		BoardState bs = gameController.getGameState().getBoard().getBoardState(x, y);

		if ((bs.getHero()) != null)
		{
			if (bs.getResource() != null
				&& bs.getResource().getType().getTypeName().equals(ResourceType.WOOD.getTypeName()))
			{
				return bs.getHero().player.getName()
					+ "'s Hero in "
					+ bs.getResource().getType().getTypeName()
					+ " owned by "
					+ (bs.getResource().getOwner() == null ? "none" : bs.getResource().getOwner()
						.getName()) + "\nLocation: " + x + ", " + y;
			}
			else if (bs.getResource() != null
				&& bs.getResource().getType().getTypeName().equals(ResourceType.GOLD.getTypeName()))
			{
				return bs.getHero().player.getName()
					+ "'s Hero in "
					+ bs.getResource().getType().getTypeName()
					+ " owned by "
					+ (bs.getResource().getOwner() == null ? "none" : bs.getResource().getOwner()
						.getName()) + "\nLocation: " + x + ", " + y;
			}
			else if (bs.getResource() != null
				&& bs.getResource().getType().getTypeName()
					.equals(ResourceType.STONE.getTypeName()))
			{
				return bs.getHero().player.getName()
					+ "'s Hero in "
					+ bs.getResource().getType().getTypeName()
					+ " owned by "
					+ (bs.getResource().getOwner() == null ? "none" : bs.getResource().getOwner()
						.getName()) + "\nLocation: " + x + ", " + y;
			}
			else if ((bs.getCastle()) != null)
			{
				String s;
				switch (currentPlayerIndex)
				{
				case 0:
					s = "Castle";
					break;
				case 1:
					s = "Dungeon";
					break;
				case 2:
					s = "Rampart";
					break;
				case 3:
					s = "Tower";
					break;
				default:
					s = "Castle";
				}
				return bs.getHero().player.getName() + "'s Hero in "
					+ bs.getCastle().getPlayer().getName() + "'s " + s + "\nLocation: " + x + ", "
					+ y;
			}
			else
				return bs.getHero().player.getName() + "'s Hero" + "\nLocation: " + x + ", " + y;

		}
		else if ((bs.getCastle()) != null)
		{
			String s;
			switch (currentPlayerIndex)
			{
			case 0:
				s = "Castle";
				break;
			case 1:
				s = "Dungeon";
				break;
			case 2:
				s = "Rampart";
				break;
			case 3:
				s = "Tower";
				break;
			default:
				s = "Castle";
			}
			return bs.getCastle().getPlayer().getName() + "'s " + s + "\nLocation: " + x + ", " + y;
		}
		else if ((bs.getResource()) != null)
		{
			if (bs.getResource().getType().getTypeName().equals(ResourceType.WOOD.getTypeName()))
			{
				return bs.getResource().getType().getTypeName()
					+ " owned by "
					+ (bs.getResource().getOwner() == null ? "none" : bs.getResource().getOwner()
						.getName()) + "\nLocation: " + x + ", " + y;
			}
			else if (bs.getResource().getType().getTypeName().equals(ResourceType.GOLD
				.getTypeName()))
			{
				return bs.getResource().getType().getTypeName()
					+ " owned by "
					+ (bs.getResource().getOwner() == null ? "none" : bs.getResource().getOwner()
						.getName()) + "\nLocation: " + x + ", " + y;
			}
			else if (bs.getResource().getType().getTypeName().equals(ResourceType.STONE
				.getTypeName()))
			{
				return bs.getResource().getType().getTypeName()
					+ " owned by "
					+ (bs.getResource().getOwner() == null ? "none" : bs.getResource().getOwner()
						.getName()) + "\nLocation: " + x + ", " + y;
			}
		}

		return "";
	}

	private void createBoardWindow(boolean initBoard)
	{
		boolean[][] isVisible;
		Composite currentHero = null;

		if (isBoardInit == false && (boardComposite != null && boardComposite.isDisposed() == false))
			boardComposite.dispose();
		
		if(isBoardInit == false)
		{
			boardComposite = new Composite(sc1, SWT.NONE);
			boardComposite.setBackground(black);
			GridData d = new GridData(GridData.FILL_BOTH);
			boardComposite.setLayoutData(d);
		}

		sc1.addMouseWheelListener(new ScrollMove() );
		sc1.forceFocus();
		if (initBoard == false) {
			final GridLayout gridLayout = new GridLayout();
			boardComposite.setLayout(gridLayout);
			final Label img_Label = new Label(boardComposite, SWT.NONE);
			GridData labelGridDataLayout = new GridData(GridData.FILL_BOTH);
			labelGridDataLayout.grabExcessHorizontalSpace = true;
			labelGridDataLayout.grabExcessVerticalSpace = true;
			labelGridDataLayout.horizontalAlignment = GridData.CENTER;
			labelGridDataLayout.verticalAlignment = GridData.CENTER;
			img_Label.setLayoutData(labelGridDataLayout);
			img_Label.setImage(IconCache.stockImages[IconCache.heroesStartScreenIcon]);

			sc1.setContent(boardComposite);
			sc1.setExpandHorizontal(true);
			sc1.setExpandVertical(true);
			sc1.setMinSize(boardComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		}
		else
		// initBoard == true
		{
			isModified = true;

			this.numOfCells = gameController.getGameState().getBoard().getSize();

			boardPoints = new Point[numOfCells][numOfCells];
			for (int y = 0; y < numOfCells; y++)
				for (int x = 0; x < numOfCells; x++)
					boardPoints[x][y] = new Point(x, y);

			GridLayout tableLayout = new GridLayout();
			tableLayout.numColumns = numOfCells;
			tableLayout.makeColumnsEqualWidth = true;
			tableLayout.horizontalSpacing = 0;
			tableLayout.verticalSpacing = 0;
			boardComposite.setLayout(tableLayout);

			isVisible = gameController.getGameState().getPlayers().elementAt(currentPlayerIndex).getVisibleBoard();

			if(isBoardInit == false)
			{
				boardSquares = new Composite[numOfCells][numOfCells];
				boardLabels = new Label[numOfCells][numOfCells];
			}
			
			for (int y = 0; y < numOfCells; y++)
			{
				for (int x = 0; x < numOfCells; x++)
				{
					if(isBoardInit == false)
						boardSquares[x][y] = new Composite(boardComposite, SWT.NONE);
					
					Composite b = boardSquares[x][y];
					GridLayout cellLayout = new GridLayout();
					cellLayout.marginWidth = 0;
					cellLayout.marginHeight = 0;
					b.setLayout(cellLayout);
					
					if(isBoardInit == false)
						boardLabels[x][y] = new Label(b, SWT.NONE);
					
					Label l = boardLabels[x][y];
					l.setLayoutData(new GridData(GridData.FILL_BOTH));
					int t = fromBoardToDisplayIcons(x, y);

					if (isVisible[x][y])
						l.setImage(IconCache.stockImages[t]);
					else
						l.setImage(IconCache.stockImages[IconCache.blackIcon]);

					String description;
					if (t != IconCache.grassIcon && t != IconCache.blackIcon)
					{
						description = fromBoardToDisplayDecription(x, y);
						l.setToolTipText(description);
					}

					if (t == IconCache.blueHeroIcon || t == IconCache.blueInGlodMineIcon
						|| t == IconCache.blueInStoneIcon || t == IconCache.blueInWoodIcon)
						if (gameController.getGameState().getBoard().getBoardState(x, y).getHero().player
							.equals(gameController.getGameState().getPlayers()
								.elementAt(currentPlayerIndex)))
						{
							l.setData(boardPoints[x][y]);
							// l.setMenu(createHeroPopUpMenu(SWT.POP_UP));
							l.addMouseListener(focusListener);
							l.addListener(SWT.MouseDown, listener);
							l.addListener(SWT.MouseMove, listener);
							currentHero = b;
						}

					if (t == IconCache.castleIcon || t == IconCache.dungeonIcon
						|| t == IconCache.rampartIcon || t == IconCache.towerIcon)
						if (gameController.getGameState().getBoard().getBoardState(x, y)
							.getCastle().getPlayer().equals(gameController.getGameState()
								.getPlayers().elementAt(currentPlayerIndex)))
						{
							l.setData(boardPoints[x][y]);
							l.setMenu(createCastlePopUpMenu(SWT.POP_UP));
							l.addMouseListener(focusListener);

							if (gameController.getGameState().getBoard().getBoardState(x, y)
								.getCastle().getPlayer().getHero() == null)
								currentHero = b;
						}

					if (t == IconCache.blueInCastleIcon || t == IconCache.blueInDungeonIcon
						|| t == IconCache.blueInRampartIcon || t == IconCache.blueInTowerIcon)
						if (gameController.getGameState().getBoard().getBoardState(x, y).getHero().player
							.equals(gameController.getGameState().getPlayers()
								.elementAt(currentPlayerIndex)))
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
			isBoardInit = true;

			sc1.setContent(boardComposite);
			sc1.setExpandHorizontal(true);
			sc1.setExpandVertical(true);
			sc1.setMinSize(boardComposite.computeSize(SWT.DEFAULT,SWT.DEFAULT));
	
			if (currentHero != null)
			{
				Rectangle bounds = currentHero.getBounds();
				Rectangle area = sc1.getClientArea();
				Point origin = sc1.getOrigin();
				if (origin.x > bounds.x)
					origin.x = Math.max(0, bounds.x);
				if (origin.y > bounds.y)
					origin.y = Math.max(0, bounds.y);
				if (origin.x + area.width < bounds.x + bounds.width)
					origin.x = Math.max(0, bounds.x + bounds.width - area.width / 2);
				if (origin.y + area.height < bounds.y + bounds.height)
					origin.y = Math.max(0, bounds.y + bounds.height - area.height / 2);

				sc1.setOrigin(origin);
			}
		}
	}
	

	MouseListener focusListener = new MouseListener() {
		public void mouseDown(MouseEvent e)
		{
			Label selectedLabel = (Label) e.getSource();
			currentPoint = (Point) selectedLabel.getData();
		}

		public void mouseDoubleClick(MouseEvent arg0)
		{
		}

		public void mouseUp(MouseEvent arg0)
		{
		}
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
						newPoint = new Point(r2.x / r2.width, r2.y / r2.height);

						if (!gameController
							.getGameState()
							.getPlayers()
							.elementAt(currentPlayerIndex)
							.checkMove((r2.x / r2.width), (r2.y / r2.height), gameController
								.getGameState().getBoard()))
							tracker.setCursor(cursor);
						else
							tracker.setCursor(defaultCursor);
					}
				});

				if (!tracker.open())
					break;

				if (newPoint != null && currentPoint != null
					&& !((newPoint.x == currentPoint.x) && (newPoint.y == currentPoint.y)))
					handleMoveCommand(new String[] { newPoint.x + "", newPoint.y + "" });

				point = null;
				break;
			}
		}
	};

	private Label createLabel(Composite composite, String text)
	{
		Label tempLabel = new Label(composite, SWT.NONE);
		tempLabel.setText(text);
		tempLabel.setBackground(white);
		return tempLabel;
	}

	private Label createLabel(Composite composite, String text, Font font)
	{
		Label tempLabel = createLabel(composite, text);
		tempLabel.setFont(font);
		return tempLabel;
	}

	@SuppressWarnings("unused")
	private void displayStartWindow()
	{
		final Shell shell = new Shell(Display.getCurrent().getActiveShell());
		shell.setImage(IconCache.stockImages[IconCache.appIcon]);

		GridLayout layout1 = new GridLayout(4, true);
		layout1.marginWidth = layout1.marginHeight = 10;
		shell.setLayout(layout1);

		GridData newGameData = new GridData();
		newGameData.horizontalSpan = 4;
		newGameData.grabExcessHorizontalSpace = true;
		newGameData.grabExcessVerticalSpace = true;
		final Button newGame = new Button(shell, SWT.RADIO);
		newGame.setSelection(true);
		newGame.setText("Start a New Game");
		newGame.setLayoutData(newGameData);

		GridData loadGameData = new GridData();
		loadGameData.horizontalSpan = 4;
		loadGameData.grabExcessHorizontalSpace = true;
		loadGameData.grabExcessVerticalSpace = true;
		final Button loadGame = new Button(shell, SWT.RADIO);
		loadGame.setText("Load a Saved Game");
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
		Button okButton = new Button(shell, SWT.PUSH);
		okButton.setText("    OK    ");
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				if (newGame.getSelection())
					startNewGame();
				else if (loadGame.getSelection())
					openFileDlg();

				shell.dispose();
			}
		});
		okButton.setLayoutData(okData);

		GridData cancelData = new GridData();
		cancelData.grabExcessHorizontalSpace = true;
		Button cancelButton = new Button(shell, SWT.PUSH);
		cancelButton.setText(" Cancel ");
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
		Button helpButton = new Button(shell, SWT.PUSH);
		helpButton.setText("  Help  ");
		helpButton.setLayoutData(helpData);
		helpButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				showGameAssistanceMbox();
			}
		});

		shell.pack();

		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private void displayCastleInfo(Castle castle)
	{
		CastleShell shell = new CastleShell(this, this.shell, SWT.DIALOG_TRIM | SWT.TITLE);

		// Set the castle
		shell.setCastle(castle);

		// Set the hero, if he's in the castle
		int x = castle.getXPos();
		int y = castle.getYPos();
		Hero hero = gameController.getGameState().getBoard().getBoardState(x, y).getHero();
		if (hero != null)
			shell.setHero(hero);

		// Open and display castle window
		shell.open();

		// Block game while window is open
		this.shell.setEnabled(false);
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}

		// Re-enable game
		this.shell.setEnabled(true);
		// update the board view (basically for buying a new hero in the
		// castle).
		createBoardWindow(true);
		updateStatusWindow();

	}

	private void createStatusWindow(boolean initStatus)
	{

		if (statusComposite != null && statusComposite.isDisposed() == false)
		{
			statusComposite.dispose();
		}

		// statusComposite = new ScrolledComposite(sash, SWT.BORDER);
		statusComposite = new Composite(sc2, SWT.BORDER);
		statusComposite.setBackground(black);
		GridData d = new GridData(GridData.FILL_BOTH);
		statusComposite.setLayoutData(d);

		GridLayout tempLayout = new GridLayout(1, true);
		statusComposite.setLayout(tempLayout);

		if (initStatus)
			updateStatusWindow();

		sc2.setContent(statusComposite);
		sc2.setExpandHorizontal(true);
		sc2.setExpandVertical(true);
		Rectangle r = sc2.getClientArea();
		sc2.setMinSize(statusComposite.computeSize(r.width, SWT.DEFAULT));

	}

	public void updateStatusWindow()
	{
		Player p = gameController.getGameState().getPlayers().elementAt(currentPlayerIndex);

		statusComposite.setBackground(white);

		Control[] children = statusComposite.getChildren();
		for (int i = 0; i < children.length; i++)
		{
			children[i].dispose();
		}

		CLabel firstLabel = new CLabel(statusComposite, SWT.CENTER);
		firstLabel.setBackground(white);
		firstLabel.setImage(IconCache.stockImages[IconCache.appIcon]);
		firstLabel.setFont(IconCache.stockFonts[IconCache.titleFontIndex]);
		String str1 = p.getName();
		String str2 = p.getDayAsString();
		firstLabel.setText("     " + str1);
		createLabel(statusComposite, "    " + str2);

		Button button = new Button(statusComposite, SWT.CENTER);

		button.setText("END TURN");
		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				handleEndTurnCommand();
			}
		});

		createLabel(statusComposite, "");

		if (p.getHero() == null)
		{
			createLabel(statusComposite, "You  have  no  hero  !!!");
		}
		else
		{
			int xPos = p.getHero().getXPos();
			int yPos = p.getHero().getYPos();
			Label movesLabel = new Label(statusComposite, SWT.NONE);
			movesLabel.setForeground(red);
			movesLabel.setText("HERO  MOVES  LEFT  :  " + p.getMovesLeft());
			movesLabel.setBackground(white);
			createLabel(statusComposite, "HERO  POSITION  :  " + xPos + " , " + yPos);
			createLabel(statusComposite, "");
			createLabel(statusComposite, "DEFENSE  SKILL  :  " + p.getHero().getDefenseSkill());
			createLabel(statusComposite, "ATTACK  SKILL  :  " + p.getHero().getAttackSkill());
			createLabel(statusComposite, "");

			Label l1 = createLabel(statusComposite, "ARMY");
			l1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
			armyView = new ArmyView(statusComposite, SWT.BORDER);
			armyView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			armyView.setArmy(p.getHero().getArmy());
		}

		createLabel(statusComposite, "");
		Label l2 = createLabel(statusComposite, "MINES");
		l2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));

		MinesView minesView = new MinesView(statusComposite, SWT.BORDER);
		minesView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		minesView.setMines(p.getMines());

		createLabel(statusComposite, "");
		l2 = createLabel(statusComposite, "TREASURES");
		l2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));

		resourcesView = new ResourcesView(statusComposite, SWT.BORDER);
		resourcesView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		resourcesView.setResources(p.getCurrentTreasury());

		createLabel(statusComposite, "");
		createLabel(statusComposite, "CASTLES  :");
		int numOfCastles = p.getCastles().size();
		if (numOfCastles == 0)
		{
			createLabel(statusComposite, "You have no castles");
			createLabel(statusComposite, "Days witout castle  :  "
				+ (p.getDaysWithoutCastles() + 1));
		}
		for (int i = 0; i < numOfCastles; ++i)
		{
			final Castle castle = p.getCastles().get(i);
			int castleXPos = castle.getXPos();
			int castleYPos = castle.getYPos();
			Button button1 = new Button(statusComposite, SWT.NONE);
			String s;
			switch (currentPlayerIndex)
			{
			case 0:
				s = "Castle";
				break;
			case 1:
				s = "Dungeon";
				break;
			case 2:
				s = "Rampart";
				break;
			case 3:
				s = "Tower";
				break;
			default:
				s = "Castle";
			}
			button1.setText(s + " at  " + castleXPos + " , " + castleYPos);
			button1.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					displayCastleInfo(castle);
				}
			});
		}

		createLabel(statusComposite, "");
		button = new Button(statusComposite, SWT.PUSH | SWT.CENTER);
		button.setText("About This Window");
		button.setToolTipText("Displays help about this window");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				shell.setEnabled(false);
				diplayHelpByHelpItem("Status Window");
				shell.setEnabled(true);
			}
		});

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

	private Vector<Player> newGameMenu()
	{
		final Vector<Player> players = new Vector<Player>();
		final Combo pcLevel1;
		final Combo pcLevel2;
		final Combo pcLevel3;
		final int[] exitHelperArray = new int[1];
		final int ExitCANCEL = 5;
		final int ExitOK = 6;

		final Shell shell1 = new Shell(Display.getCurrent().getActiveShell());
		// , SWT.APPLICATION_MODAL | SWT.TITLE | SWT.BORDER | SWT.CLOSE);
		shell1.setLayout(new GridLayout());
		shell1.setSize(330, 225);
		shell1.setText("New Game Menu");
		shell1.setImage(IconCache.stockImages[IconCache.appIcon]);

		Composite form = new Composite(shell1, SWT.FILL);
		form.setLayout(new GridLayout(4, false));

		final Label emptyLabel1 = new Label(form, SWT.NONE);
		emptyLabel1.setText("      ");
		final Label namesLabel = new Label(form, SWT.NONE);
		namesLabel.setText("      Name     ");
		final Label computer3 = new Label(form, SWT.NONE);
		computer3.setText("  Computer     ");
		final Label levelLabel = new Label(form, SWT.NONE);
		levelLabel.setText("      Level     ");
		final Label emptyLabel12 = new Label(form, SWT.NONE);
		emptyLabel12.setText("      ");
		new Label(form, SWT.SEPARATOR | SWT.HORIZONTAL);
		new Label(form, SWT.SEPARATOR | SWT.HORIZONTAL);
		new Label(form, SWT.SEPARATOR | SWT.HORIZONTAL);

		final Label player1Label = new Label(form, SWT.NONE);
		player1Label.setText("Player 1 : ");
		final Text player1Name = new Text(form, SWT.BORDER);
		final Label emptyLabel5 = new Label(form, SWT.NONE);
		emptyLabel5.setText("");
		final Label emptyLabel6 = new Label(form, SWT.NONE);
		emptyLabel6.setText("");

		final Label player2Label = new Label(form, SWT.NONE);
		player2Label.setText("Player 2 : ");
		final Text player2Name = new Text(form, SWT.BORDER);
		final Button pcButton1 = new Button(form, SWT.CHECK);
		pcButton1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		pcLevel1 = new Combo(form, SWT.NONE);
		pcLevel1.setText("level");
		pcLevel1.setItems(new String[] { "Novice", "Expert" });
		pcLevel1.setEnabled(false);
		pcButton1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				if (pcButton1.getSelection())
				{
					player2Name.setEnabled(false);
					pcLevel1.setEnabled(true);
					pcLevel1.setText("Novice");
				}
				else
				{
					player2Name.setEnabled(true);
					pcLevel1.setEnabled(false);
				}
			}
		});

		final Label player3Label = new Label(form, SWT.NONE);
		player3Label.setText("Player 3 : ");
		final Text player3Name = new Text(form, SWT.BORDER);
		final Button pcButton2 = new Button(form, SWT.CHECK);
		pcButton2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		pcLevel2 = new Combo(form, SWT.BEGINNING);
		pcLevel2.setItems(new String[] { "Novice", "Expert" });
		pcLevel2.setEnabled(false);
		pcButton2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				if (pcButton2.getSelection())
				{
					player3Name.setEnabled(false);
					pcLevel2.setEnabled(true);
					pcLevel2.setText("Novice");
				}
				else
				{
					player3Name.setEnabled(true);
					pcLevel2.setEnabled(false);
				}
			}
		});

		final Label player4Label = new Label(form, SWT.NONE);
		player4Label.setText("Player 4 : ");
		final Text player4Name = new Text(form, SWT.BORDER);
		final Button pcButton3 = new Button(form, SWT.CHECK);
		pcButton3.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		pcLevel3 = new Combo(form, SWT.NONE);
		pcLevel3.setText("level");
		pcLevel3.setItems(new String[] { "Novice", "Expert" });
		pcLevel3.setEnabled(false);
		pcButton3.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent arg0)
			{
				if (pcButton3.getSelection())
				{
					player4Name.setEnabled(false);
					pcLevel3.setEnabled(true);
					pcLevel3.setText("Novice");
				}
				else
				{
					player4Name.setEnabled(true);
					pcLevel3.setEnabled(false);
				}
			}
		});

		Composite form2 = new Composite(shell1, SWT.FILL);

		form2.setLayout(new GridLayout(3, false));
		Button okButton = new Button(form2, SWT.NONE);
		okButton.setText("          OK          ");
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e)
			{
				boolean wrongLevel = false;
				String name1 = player1Name.getText();
				String name2 = player2Name.getText();
				String name3 = player3Name.getText();
				String name4 = player4Name.getText();

				if (name1.length() == 0)
				{
					displayError("You must choose the first player !");
				}
				else
				{
					players.removeAllElements();
					players.add(new Player(name1));

					if ((name2.length() > 0) || (pcButton1.getSelection()))
					{
						if (pcButton1.getSelection())
						{
							Player tempPlayer1 = new Player("computer");
							if (pcLevel1.getText().equals("Novice"))
							{
								tempPlayer1.setComputerLevel(1);
								players.add(tempPlayer1);
							}
							else if (pcLevel1.getText().equals("Expert"))
							{
								tempPlayer1.setComputerLevel(2);
								players.add(tempPlayer1);
							}
							else
							{
								wrongLevel = true;
							}
						}
						else
						{
							players.add(new Player(name2));
						}
					}

					if ((name3.length() > 0) || (pcButton2.getSelection()))
					{
						if (pcButton2.getSelection())
						{
							Player tempPlayer2 = new Player("computer");
							if (pcLevel2.getText().equals("Novice"))
							{
								tempPlayer2.setComputerLevel(1);
								players.add(tempPlayer2);
							}
							else if (pcLevel2.getText().equals("Expert"))
							{
								tempPlayer2.setComputerLevel(2);
								players.add(tempPlayer2);
							}
							else
							{
								wrongLevel = true;
							}
						}
						else
						{
							players.add(new Player(name3));
						}
					}

					if ((name4.length() > 0) || (pcButton3.getSelection()))
					{
						if (pcButton3.getSelection())
						{
							Player tempPlayer3 = new Player("computer");
							if (pcLevel3.getText().equals("Novice"))
							{
								tempPlayer3.setComputerLevel(1);
								players.add(tempPlayer3);
							}
							else if (pcLevel3.getText().equals("Expert"))
							{
								tempPlayer3.setComputerLevel(2);
								players.add(tempPlayer3);
							}
							else
							{
								wrongLevel = true;
							}
						}
						else
						{
							players.add(new Player(name4));
						}
					}

					if (wrongLevel)
					{
						displayError("Only Novice or Expert levels are alowed for computer !");
					}
					else if (players.size() < 2)
					{
						displayError("You must select at least 2 players");
						// players.removeAllElements();
						// return;
					}
					else
					{
						exitHelperArray[0] = ExitOK;
						shell1.dispose();
					}
				}
			}
		});

		/* help button */
		Button helpButton = new Button(form2, SWT.PUSH | SWT.CENTER);
		helpButton.setText("About This Window");
		helpButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		helpButton.setToolTipText("Displays help about this window");
		helpButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				shell.setEnabled(false);
				shell1.setEnabled(false);
				diplayHelpByHelpItem("New Game Window");
				shell1.setEnabled(true);
			}
		});

		Button cancelButton = new Button(form2, SWT.NONE | SWT.RIGHT);
		cancelButton.setText("       Cancel       ");
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e)
			{
				exitHelperArray[0] = ExitCANCEL;
				shell1.dispose();
			}
		});

		shell1.open();
		shell.setEnabled(false);
		while (!shell1.isDisposed()) 
		{
			if (!display.readAndDispatch()) 
			{
				if (exitHelperArray[0] != ExitOK) 
				{
					exitHelperArray[0] = ExitCANCEL;
				}
				display.sleep();
			}
		}
		if (exitHelperArray[0] != ExitCANCEL)
		{
			shell.setEnabled(true);
			return players;
		}
		else
		{
			shell.setEnabled(true);
			return null;
		}
	}

	public void startNewGame()
	{

		// getGameDetails();

		// int numberOfPlayers = getNumberOfPlayers();
		// if(numberOfPlayers != 0)
		// {
		Vector<Player> players = newGameMenu();
		if (players != null)
		{
			gameController.initNewGame(players);

			createBoardWindow(true);
			createStatusWindow(true);
			sash.setWeights(new int[] { 80, 20 });
		}
		// }
	}

	/**
	 * @return Number of players from user input
	 */
	public static int getNumberOfPlayers()
	{
		String message = "Enter number of players (" + Constants.MIN_PLAYERS + "-"
			+ Constants.MAX_PLAYERS + "): ";
		int numberOfPlayers = Integer.MIN_VALUE;
		String response = null;

		do
		{
			InputDialog numberInput = new InputDialog(Display.getCurrent().getActiveShell(),
				"Number of Players", message, null, null);
			if (numberInput.open() == Window.OK)
			{
				response = numberInput.getValue();
			}
			else
				return 0;

			if (response != null)
				numberOfPlayers = Helper.tryParseInt(response);

		} while (!Helper
			.isIntBetween(numberOfPlayers, Constants.MIN_PLAYERS, Constants.MAX_PLAYERS));

		return numberOfPlayers;
	}

	public static Vector<Player> getPlayers(int numberOfPlayers)
	{
		String message;
		String response = null;
		Vector<Player> players = new Vector<Player>();

		for (int i = 0; i < numberOfPlayers;)
		{
			message = (i == 0) ? "" : "If you want one of the players will be the computer, enter "
				+ Player.COMPUTER_NAME + " as his name.\n";
			message += "Please enter player " + (i + 1) + "'s name: ";

			InputDialog stringInput = new InputDialog(Display.getCurrent().getActiveShell(),
				"Player Name", message, null, null);
			if (stringInput.open() == Window.OK)
			{
				response = stringInput.getValue();
			}
			else
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
		fileDialog
			.setFilterNames(new String[] { "Saved Games" + " (*.sav)", "All Files" + " (*.*)" });
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
			displayMessage("The file you opened doesn't contain a valid Heroes saved game.\n"
				+ "Please try again with a different file");
			return;
		}

		createBoardWindow(true);
		createStatusWindow(true);
		sash.setWeights(new int[] { 80, 20 });

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
		saveDialog
			.setFilterNames(new String[] { "Saved Games" + " (*.sav)", "All Files" + " (*.*)" });

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
	 *            Menu the <code>Menu</code> that file contain the File submenu.
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
			{
			}
		});

		// space reserve for File -> New and File -> Open Saved Game
		MenuItem subItem = new MenuItem(menu, SWT.NULL);
		MenuItem openSubItem = new MenuItem(menu, SWT.NULL);
		// new MenuItem(menu, SWT.SEPARATOR);

		// File -> Save.
		saveSubItem = new MenuItem(menu, SWT.NULL);
		saveSubItem.setEnabled(gameController.getGameState().getBoard() != null);
		saveSubItem.setText("&Save Game\tCtrl+S");
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
		saveAsSubItem.setText("&Save Game as\tCtrl+A");
		saveAsSubItem.setAccelerator(SWT.MOD1 + 'A');
		saveAsSubItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				saveAs();
			}
		});

		// File -> New Game
		subItem.setText("&New Game\tCtrl+N");
		subItem.setAccelerator(SWT.MOD1 + 'N');
		subItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				if (isModified)
				{
					// ask user if they want to save current game
					MessageBox box = new MessageBox(Display.getCurrent().getActiveShell(),
						SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
					box.setText(Display.getCurrent().getActiveShell().getText());
					box.setMessage("Save game before closing?");

					int choice = box.open();
					if (choice == SWT.YES)
					{
						save();
					}
				}
				startNewGame();
				saveSubItem.setEnabled(gameController.getGameState().getBoard() != null);
				saveAsSubItem.setEnabled(gameController.getGameState().getBoard() != null);
			}
		});

		// File -> Open Saved Game
		openSubItem.setText("&Load Game\tCtrl+L");
		openSubItem.setAccelerator(SWT.MOD1 + 'L');
		openSubItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				if (isModified)
				{
					// ask user if they want to save current game
					MessageBox box = new MessageBox(Display.getCurrent().getActiveShell(),
						SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
					box.setText(Display.getCurrent().getActiveShell().getText());
					box.setMessage("Save game before closing?");

					int choice = box.open();
					if (choice == SWT.YES)
					{
						save();
					}
				}
				openFileDlg();
				saveSubItem.setEnabled(gameController.getGameState().getBoard() != null);
				saveAsSubItem.setEnabled(gameController.getGameState().getBoard() != null);
			}
		});

		new MenuItem(menu, SWT.SEPARATOR);

		// File -> Exit.
		subItem = new MenuItem(menu, SWT.NULL);
		subItem.setText("&Exit\tCtrl+Q");
		subItem.setAccelerator(SWT.MOD1 + 'Q');
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
	 * @param style
	 *            if style == SWT.DROP_DOWN create as drop down menu else create
	 *            as pop up menu
	 * 
	 * @return Menu The created popup menu.
	 */

	/**
	 * private Menu createHeroPopUpMenu(int style) { Menu popUpMenu; if
	 * (eclipseComposite != null) { popUpMenu = new Menu(eclipseComposite); }
	 * else { if (style == SWT.DROP_DOWN) popUpMenu = new Menu(shell,
	 * SWT.DROP_DOWN); else popUpMenu = new Menu(shell, SWT.POP_UP); }
	 * 
	 * popUpMenu.addMenuListener(new MenuAdapter() { public void
	 * menuShown(MenuEvent e) { } });
	 * 
	 * MenuItem item = new MenuItem(popUpMenu, SWT.CASCADE);
	 * item.setText("End Turn"); item.addSelectionListener(new
	 * SelectionAdapter() { public void widgetSelected(SelectionEvent e) {
	 * handleEndTurnCommand(); } });
	 * 
	 * return popUpMenu; }
	 */

	/**
	 * Creates all items located in the popup menu and associates all the menu
	 * items with their appropriate functions.
	 * 
	 * @param style
	 *            if style == SWT.DROP_DOWN create as drop down menu else create
	 *            as pop up menu
	 * 
	 * @return Menu The created popup menu.
	 */
	private Menu createCastlePopUpMenu(int style)
	{
		Menu popUpMenu;
		if (eclipseComposite != null)
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
			{
			}
		});

		if (eclipseComposite != null)
		{
			for (final CreatureFactory factory : CreatureFactory.getCreatureFactories())
			{
				final MenuItem buildFactoryItem = new MenuItem(popUpMenu, SWT.PUSH);
				buildFactoryItem.setText("Build " + factory.getName());
				buildFactoryItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e)
					{
						handleBuildCommand(factory.getClass());
					}
				});
			}

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
					buildPricesShell.setImage(IconCache.stockImages[IconCache.appIcon]);
					Label pricesLable = new Label(buildPricesShell, SWT.NULL);
					pricesLable.setBackground(new Color(display, 255, 255, 255));
					pricesLable.setBounds(buildPricesShell.getClientArea());
					pricesLable.setText(GameController.handleBuildPricesCommand());
					buildPricesShell.open();

					while (!buildPricesShell.isDisposed())
						if (!display.readAndDispatch())
							display.sleep();
				}
			});

			new MenuItem(popUpMenu, SWT.SEPARATOR);

			for (final CreatureFactory factory : CreatureFactory.getCreatureFactories())
			{
				final MenuItem makeCreatureItem = new MenuItem(popUpMenu, SWT.PUSH);
				makeCreatureItem.setText("Make " + factory.getUnitName());
				makeCreatureItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e)
					{
						handleMakeCommand(factory.getCreatureClass());
					}
				});
			}

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
					makePricesShell.setImage(IconCache.stockImages[IconCache.appIcon]);
					Label pricesLable = new Label(makePricesShell, SWT.NULL);
					pricesLable.setBackground(new Color(display, 255, 255, 255));
					pricesLable.setBounds(makePricesShell.getClientArea());
					pricesLable.setText(GameController.handleMakePricesCommand());
					makePricesShell.open();

					while (!makePricesShell.isDisposed())
						if (!display.readAndDispatch())
							display.sleep();
				}
			});

			// new MenuItem(popUpMenu, SWT.SEPARATOR);

			// final MenuItem item = new MenuItem(popUpMenu, SWT.PUSH);
			// item.setText("End Turn");
			// item.addSelectionListener(new SelectionAdapter() {
			// public void widgetSelected(SelectionEvent e)
			// {
			// handleEndTurnCommand();
			// }
			// });
		}
		else
		{
			MenuItem item = new MenuItem(popUpMenu, SWT.CASCADE);
			item.setText("Build");

			Menu buildMenu;
			if (eclipseComposite != null)
				buildMenu = new Menu(eclipseComposite);
			else
				buildMenu = new Menu(shell, SWT.DROP_DOWN);

			item.setMenu(buildMenu);

			for (final CreatureFactory factory : CreatureFactory.getCreatureFactories())
			{
				final MenuItem buildFactoryItem = new MenuItem(buildMenu, SWT.PUSH);
				buildFactoryItem.setText(factory.getName());
				buildFactoryItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e)
					{
						handleBuildCommand(factory.getClass());
					}
				});
			}

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
					buildPricesShell.setImage(IconCache.stockImages[IconCache.appIcon]);
					Label pricesLable = new Label(buildPricesShell, SWT.NULL);
					pricesLable.setBackground(new Color(display, 255, 255, 255));
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
			if (eclipseComposite != null)
				makeMenu = new Menu(eclipseComposite);
			else
				makeMenu = new Menu(shell, SWT.DROP_DOWN);
			item.setMenu(makeMenu);

			for (final CreatureFactory factory : CreatureFactory.getCreatureFactories())
			{
				final MenuItem makeCreatureItem = new MenuItem(makeMenu, SWT.PUSH);
				makeCreatureItem.setText(factory.getUnitName());
				makeCreatureItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e)
					{
						handleMakeCommand(factory.getCreatureClass());
					}
				});
			}

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
					makePricesShell.setImage(IconCache.stockImages[IconCache.appIcon]);
					Label pricesLable = new Label(makePricesShell, SWT.NULL);
					pricesLable.setBackground(new Color(display, 255, 255, 255));
					pricesLable.setBounds(makePricesShell.getClientArea());
					pricesLable.setText(GameController.handleMakePricesCommand());
					makePricesShell.open();

					while (!makePricesShell.isDisposed())
						if (!display.readAndDispatch())
							display.sleep();
				}
			});

			// new MenuItem(popUpMenu, SWT.SEPARATOR);

			// item = new MenuItem(popUpMenu, SWT.CASCADE);
			// item.setText("End Turn");
			// item.addSelectionListener(new SelectionAdapter() {
			// public void widgetSelected(SelectionEvent e)
			// {
			// handleEndTurnCommand();
			// }
			// });
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
		if (eclipseComposite != null)
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

		if (eclipseComposite != null)
		{
			// MenuItem item = new MenuItem(popUpMenu, SWT.CASCADE);
			// item.setText("End Turn");
			// item.addSelectionListener(new SelectionAdapter() {
			// public void widgetSelected(SelectionEvent e)
			// {
			// handleEndTurnCommand();
			// }
			// });

			new MenuItem(popUpMenu, SWT.SEPARATOR);

			for (final CreatureFactory factory : CreatureFactory.getCreatureFactories())
			{
				final MenuItem buildFactoryItem = new MenuItem(popUpMenu, SWT.PUSH);
				buildFactoryItem.setText("Build " + factory.getName());
				buildFactoryItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e)
					{
						handleBuildCommand(factory.getClass());
					}
				});
			}

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
					buildPricesShell.setImage(IconCache.stockImages[IconCache.appIcon]);
					Label pricesLable = new Label(buildPricesShell, SWT.NULL);
					pricesLable.setBackground(new Color(display, 255, 255, 255));
					pricesLable.setBounds(buildPricesShell.getClientArea());
					pricesLable.setText(GameController.handleBuildPricesCommand());
					buildPricesShell.open();

					while (!buildPricesShell.isDisposed())
						if (!display.readAndDispatch())
							display.sleep();
				}
			});

			new MenuItem(popUpMenu, SWT.SEPARATOR);

			for (final CreatureFactory factory : CreatureFactory.getCreatureFactories())
			{
				final MenuItem makeCreatureItem = new MenuItem(popUpMenu, SWT.PUSH);
				makeCreatureItem.setText("Make " + factory.getUnitName());
				makeCreatureItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e)
					{
						handleMakeCommand(factory.getCreatureClass());
					}
				});
			}

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
					makePricesShell.setImage(IconCache.stockImages[IconCache.appIcon]);
					Label pricesLable = new Label(makePricesShell, SWT.NULL);
					pricesLable.setBackground(new Color(display, 255, 255, 255));
					pricesLable.setBounds(makePricesShell.getClientArea());
					pricesLable.setText(GameController.handleMakePricesCommand());
					makePricesShell.open();

					while (!makePricesShell.isDisposed())
						if (!display.readAndDispatch())
							display.sleep();
				}
			});

			new MenuItem(popUpMenu, SWT.SEPARATOR);

			for (final CreatureFactory factory : CreatureFactory.getCreatureFactories())
			{
				final MenuItem splitFactoryItem = new MenuItem(popUpMenu, SWT.PUSH);
				splitFactoryItem.setText("Split " + factory.getUnitName());
				splitFactoryItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e)
					{
						handleSplitCommand(factory.getClass());
					}
				});
			}

			new MenuItem(popUpMenu, SWT.SEPARATOR);

			for (final CreatureFactory factory : CreatureFactory.getCreatureFactories())
			{
				final MenuItem joinFactoryItem = new MenuItem(popUpMenu, SWT.PUSH);
				joinFactoryItem.setText("Join " + factory.getUnitName());
				joinFactoryItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e)
					{
						handleJoinCommand(factory.getClass());
					}
				});
			}
		}
		else
		{
			// MenuItem item = new MenuItem(popUpMenu, SWT.CASCADE);

			// item.setText("Hero Options");
			// item.setMenu(createHeroPopUpMenu(SWT.DROP_DOWN));

			MenuItem item = new MenuItem(popUpMenu, SWT.CASCADE);
			item.setText("Castle Options");
			item.setMenu(createCastlePopUpMenu(SWT.DROP_DOWN));

			item = new MenuItem(popUpMenu, SWT.CASCADE);
			item.setText("Split");

			Menu splitMenu = new Menu(shell, SWT.DROP_DOWN);
			item.setMenu(splitMenu);

			for (final CreatureFactory factory : CreatureFactory.getCreatureFactories())
			{
				final MenuItem splitFactoryItem = new MenuItem(splitMenu, SWT.PUSH);
				splitFactoryItem.setText(factory.getUnitName());
				splitFactoryItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e)
					{
						handleSplitCommand(factory.getClass());
					}
				});
			}

			item = new MenuItem(popUpMenu, SWT.CASCADE);
			item.setText("Join");

			Menu joinMenu = new Menu(shell, SWT.DROP_DOWN);
			item.setMenu(joinMenu);

			for (final CreatureFactory factory : CreatureFactory.getCreatureFactories())
			{
				final MenuItem joinFactoryItem = new MenuItem(joinMenu, SWT.PUSH);
				joinFactoryItem.setText(factory.getUnitName());
				joinFactoryItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e)
					{
						handleJoinCommand(factory.getClass());
					}
				});
			}
		}

		return popUpMenu;
	}

	/**
	 * Creates all the items located in the Help submenu and associate all the
	 * menu items with their appropriate functions.
	 * 
	 * @param menuBar
	 *            Menu the <code>Menu</code> that file contain the Help submenu.
	 */
	private void createHelpMenu(Menu menuBar)
	{

		// Help Menu
		MenuItem item = new MenuItem(menuBar, SWT.CASCADE);
		item.setText("Help");
		Menu menu = new Menu(shell, SWT.DROP_DOWN);
		item.setMenu(menu);

		// Help -> Gameplay assistance
		MenuItem assistItem = new MenuItem(menu, SWT.NULL);
		assistItem.setText("&Gameplay assistance\tCtrl+G");
		assistItem.setAccelerator(SWT.MOD1 + 'G');
		assistItem.addSelectionListener(new SelectionAdapter() {
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

		final Shell helpShell = new Shell(Display.getCurrent().getActiveShell());
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.verticalSpacing = 5;
		gridLayout.marginHeight = gridLayout.marginWidth = 25;
		helpShell.setLayout(gridLayout);
		helpShell.setText("Heroes (TAU Ver.) - Gameplay Assistance");
		helpShell.setImage(IconCache.stockImages[IconCache.appIcon]);
		helpShell.setSize(330, 330);

		/* Getting Started */
		Button button = new Button(helpShell, SWT.PUSH | SWT.CENTER);
		button.setText("Getting Started");
		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				helpShell.setEnabled(false);
				diplayHelpByHelpItem("Getting Started");
				helpShell.setEnabled(true);
			}
		});

		/* Army */
		button = new Button(helpShell, SWT.PUSH | SWT.CENTER);
		button.setText("Army");
		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				helpShell.setEnabled(false);
				diplayHelpByHelpItem("Army");
				helpShell.setEnabled(true);
			}
		});

		/* How to Move */
		button = new Button(helpShell, SWT.PUSH | SWT.CENTER);
		button.setText("How to Move");
		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				helpShell.setEnabled(false);
				diplayHelpByHelpItem("How to Move");
				helpShell.setEnabled(true);
			}
		});

		/* Resources */
		button = new Button(helpShell, SWT.PUSH | SWT.CENTER);
		button.setText("Resources");
		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				helpShell.setEnabled(false);
				diplayHelpByHelpItem("Resources");
				helpShell.setEnabled(true);
			}
		});

		/* Status Window */
		button = new Button(helpShell, SWT.PUSH | SWT.CENTER);
		button.setText("Status Window");
		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				helpShell.setEnabled(false);
				diplayHelpByHelpItem("Status Window");
				helpShell.setEnabled(true);
			}
		});

		/* Battles */
		button = new Button(helpShell, SWT.PUSH | SWT.CENTER);
		button.setText("Battles");
		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				helpShell.setEnabled(false);
				diplayHelpByHelpItem("Battles");
				helpShell.setEnabled(true);
			}
		});
		
		/* Scores */
		button = new Button(helpShell, SWT.PUSH | SWT.CENTER);
		button.setText("Game Score");
		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				helpShell.setEnabled(false);
				diplayHelpByHelpItem("Game Score and Highscores");
				helpShell.setEnabled(true);
			}
		});		

		/* About the game */
		button = new Button(helpShell, SWT.PUSH | SWT.CENTER);
		button.setText("About Heroes of Might and Magic");
		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				helpShell.setEnabled(false);
				showAboutMbox();
				helpShell.setEnabled(true);
			}
		});

		/* Close */
		button = new Button(helpShell, SWT.PUSH | SWT.CENTER);
		button.setText("Close");
		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				helpShell.dispose();
				shell.setEnabled(true);
			}
		});
		
		//helpShell.pack();
		//helpShell.setSize(helpShell.getSize().x + 25, helpShell.getSize().y + 25);
		helpShell.open();
		shell.setEnabled(false);
		while (!helpShell.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
		shell.setEnabled(true);
	}

	static void diplayHelpByHelpItem(String helpItem)
	{
		/**
		 * TODO: add helpful text make use of shell with text in it instead of
		 * 
		 */
		String defaultHelpString = "helpful text here";
		String helpString = defaultHelpString;

		if (helpItem.equals("Getting Started"))
		{
			helpString = "Startint to play Heroes of Might and Magic ©  (TAU Version)\n\n"
						+"This game is a turn based starategy game for 2-4 players. The first player must be\n"
						+"a user. All the rest can be either user or computer players.\n\n"

						+"The goal of the game is to be the last player left. To do so, you must build an army\n"
						+"(see more under 'Army'), gather resources (see more under 'Resources'), fight the\n"
						+"other players in battles (see more under 'Battles') and take over their castles.\n\n"

						+"A game is a scenario in which a random map is generated placing map objects such as\n"
						+"castles, and resources on the map."
						+"A player starts with one hero and one castle. The hero starts with a random\n"
						+"amount of soldier units. A player with no castles gets a 7 day period to try and\n"
						+"capture a castle. The 7 days timer is reaset when a castle is captured.\n\n"

						+"The map is located in the main part of the view. On the map you can see where\n"
						+"your castle is, your hero, resource mines and other players' heroes and catles.\n"
						+"The hero is the knight on a horse icon.\n"
						+"A player's view is bounded by the places his hero already been to. Each hero has\n"
						+"a view radius of one square in each direction from where the hero is standing.\n"
						+"Each turn is a day, and when you end a turn (using the 'End Turn' button) the turn\n"
						+"passes to the next player. When all players finished their turn by using 'End Turn'\n"
						+"the turn cycle starts again and a day passed. On each day your hero can move\n"
						+"up to 5 squares on the map (see more under 'How to Move'). By moving your hero\n"
						+"on the map you can capture resources and get into battles with other players' heros\n"
						+"and castles.\n\n"

						+"In order for your hero to win battles, you must have a strong army. To gather a strong\n"
						+"army you need to build creature factories in your castle(s), and build units in those\n"
						+"factories. To do so you need to gather resources. When a hero is in the castle, you can\n"
						+"either move units from the hero to the castle (the hero must have at least 1 unit left) by\n"
						+"using the 'Split' option in the castle's right-click menu, or move units from the castle to the\n"
						+"hero using the 'Join' option in the castle's right-click menu (the castle can be left without\n"
						+"units in it but this will make it easier to capture by enemy heroes). You can also move\n"
						+"units between hero and castle using the castle window.\n"
						+"Each creature factory in the castle has a limited amount of units that can be built. This\n"
						+"quota is regenerated when Day 7 turnes into Day 1. The amount regenerated depends\n"
						+"on the factory. No units are lost (i.e. if 1 unit was left at the end of Day 7 and 10 units\n"
						+"regenerated on Day 1 - 11 units will be available on Day 1).\n\n\n"
						
						+"Good Luck and we hope you enjoy the game.\n"
						+"The Heroes of Might and Magic (TAU Version) Team\n"
						+ "© 2009";
		}
		else if (helpItem.equals("Castle"))
		{
			helpItem = "Castle Window";
			
			helpString = "In this window you can interact with your castle and hero (if one is in it).\n\n"

						+"On the left side of this window you can see the castle's creature facories,\n"
						+"the castle's army and when a hero is present in the castle you will be able\n"
						+"to see the hero's army as well. On the bottom of the left side you will notice\n"
						+"a button 'Create Hero' which will be available if you lose a hero in a battle\n"
						+"(for more about battales see 'Battels' in the help section).\n"
						+"On the right side of this window you will get info about parts of the left side\n"
						+"whenever your cursor is over a part of the left side.\n\n"

						+"Building factories and units:\n"
						+"To build an army you need to purchase army units. To do so you must have a\n"
						+"creature factory first. On the creature factories section of the left side window\n"
						+"you can see the factories available. If a factory is grayed  out that means you\n"
						+"do not have that factory yet. To build a factory double click the image. If you\n"
						+"have the needed resources the factory will be built at once (for more about\n"
						+"resources see 'Resources' under help section).  When the factory image is not\n"
						+"gray you have that factory - only one of each factory can be built in one castle.\n"
						+"After having a creature factory you can purchase army units from that factory,\n"
						+"simply by double clicking the image of the factory. Each double click adds a unit\n"
						+"to the castle's army.\n"
						+"Factories and units prices will show on the right part of this window when your\n"
						+"cursor is over a factory or creature image respectively.\n\n"

						+"Moving units between hero and castle:\n"
						+"When a hero is present in the castle, you can move units between them. To do so\n"
						+"simply double click a creature and one unit will be moved. If you double click on\n"
						+"a creature in the castle a unit will move from the castle to the hero, and vice versa.\n"
						+"Note that a castle can be left with no creatures in it (though not recommanded since\n"
						+"it will be open for enemy to capture with no fight), while a hero must have at least\n"
						+"one unit (of some creature) left at all times.\n\n"

						+"Create Hero button:\n"
						+"Only if you lose your hero in battle, you can purchase a new hero at the castle using\n"
						+"the 'Create Hero' button. A new hero costs 3000 Gold.\n";
		}
		else if (helpItem.equals("How to Move"))
		{
			helpString = "Each hero (the hero icon is the knight on a horse icon) can move 5 squares on each day (see more\n"
						+"about days under 'Getting Started').\n"
						+"To move your hero on the map, simply click the hero icon once . After clicking your hero once\n"
						+"you will notice that the cursor has changed. Move the cursor to the desiered destination square,\n"
						+"and click that square. The hero will move to that square at once.\n"
						+"Another way to move your hero is to 'drag and drop' it on the map.\n\n"
						+"Some map objects will respond when a hero moves to them:\n"
						+"If you try to move to a castle your hero will enter that castle. If that castle is owned by you,\n"
						+"you will be able to move units to and from that castle.\n"
						+"If the castle is not owned by you, you will try to take over it. If it is defended, a battle will start\n"
						+"automatically (see more about balles under 'Battles'). If it is not defended it will become yours.\n"
						+"If you try to move your hero to a resource (see more under 'Resources') you will get ownership\n"
						+"over that resource. If it was already yours, nothing will change.\n"
						+"If you try to move your hero to another player's hero a battle will start automatically (see more\n"
						+"under 'Battles')."; 
		}			
		else if (helpItem.equals("Battles"))
		{
			helpItem = "Battles and Battle Window";
			
			helpString = "To win this game you need to eliminate the other players. To fight another hero,\n"
						+"just walk up to him (see 'How to Move' for more about moving your hero). To fight\n"
						+"another player's castle, just move to his castle. If the castle is protected by an army,\n"
						+"a combat screen will appear automatically. If unguarded you will capture it at once.\n\n"

						+"The Combat Screen:\n"
						+"When a battle starts, you will see a battle field with your army facing the opponenets\n"
						+"army. In each turn, one of your creatures can attack an enemy target of your choice.\n"
						+"For example, if you have 3 different types of creatures you can attack 3 times in each\n"
						+"round. The cursor will change into a sword pointing to the defending army. To attack\n"
						+"just double click on your target.\n\n"

						+"Status:\n"
						+"At the bottom of the screen you will see a status screen. The screen will tell you who's turn\n"
						+"is it, what happend since the battle started, how much damge was done by an attack etc'.\n"
						+"You can hover your cursor over a creature to see how much units remains from it.\n\n"

						+"Winning and losing:\n"
						+"When one hero has no more army units left he is defeated. The other hero is the winner of\n"
						+"the battle. The defeated hero is remove from the map. If you were fighting for a castle, and\n"
						+"won you now own the castle, good for you!\n"
						+"If you lose your hero, a new one can be purchased in a castle for only 3000 gold.\n";
		}
		else if (helpItem.equals("Army"))
		{
			helpString = "An army is a collection of creature units in a castle or travelling with a hero. To build an army\n"
						+"you need to have creature factories in your castle(s). For more about building creature\n"
						+"factories and creature units see the help under castle window.\n\n"

						+"Each creature has unique traits, such as: attack, defense, hit points, damage etc.\n"
						+"The hit points trait determains how much damage a unit can take before it parishes.\n"
						+"The demage trait determains how much damage a unit can deal to a target with 0 defense.\n"
						+"when attacking an enemy creature, the attacking creature's demage is multiplied by the\n"
						+"amount of units of that same creature in that army.\n"
						+"The attack trait is a bonus modifier on the demage a unit deals when attacking an enemy creature.\n"
						+"The defense trait is a bonus modifier that lowers the amount of demage taken by a unit when\n"
						+"attacked by an enemy creature."
				;
		}
		else if (helpItem.equals("Resources"))
		{
			helpString = "To win the game you should build a strong army. To build an army\n"
						+"a player must gather resources.\n"
						+"There are 3 different types of resources in this game: Gold, Wood\n"
						+"and Stone. When a day (turn) starts you recive 1000 Gold for each\n"
						+"castle you own, and for each Gold mine you own. You also get 2\n"
						+"Stone and 2 Wood for each Stone mine and wood-mill you own respectively.\n"
						+"To get ownership over mines you need to look for them on the map\n"
						+"(see more about moving in 'How to Move').\n";
		}
		else if (helpItem.equals("Status Window"))
		{
			helpString = "In this window you can see the status of the game as well as the status of your hero and castles.\n"
						+"We will go over this window from to to bottom.\n\n"

						+"At the top of this status window you will see the application's icon and a player's name. This is\n"
						+"the name of the player currently playing his turn. Under that you can see what day of the week\n"
						+"it is for your player and the 'End Turn' button. Pressing that button will instantly end your turn and\n"
						+"give the turn to the next player. Don't press that button if you're not sure you want to end your turn.\n\n"

						+"Under the 'End Turn' button you can see your hero's stats: moves left, position on the map, defense\n"
						+"skill, attack skill and army. Attack and defense skills gives you better attack and defense against\n"
						+"enemy units attacks during battles (for more about battles see 'Battels' under the help section).\n"
						+"to learn more about a creature in your hero's army put your cursor on the creature's image.\n\n"

						+"Under the hero stats you can see the stats of your kingdom: resource mines you own, your treasury\n"
						+"and castles you own. To learn more on a resource mine or treasury put your cursor on an image.\n"
						+"To learn more about a castle you own press the castle's button to open the castle window (for more\n"
						+"about the castle window check the help in the castle window).\n";
		}
		else if (helpItem.equals("New Game Window"))
		{
			helpString = "Starting a new game is easy:\n"
						+"The first player must be a user. Use the text box to enter player name.\n"
						+"A game will have as many players as names entered.\n"
						+"To add a computer player, just click the chec-box near the text line.\n"
						+"After checking that box you can decide whether the computer player will be a 'Novice' or an 'Expert' player.\n"
						+"'Novice' will make random moves on the map, while 'Expert' will also try to build an army.\n"
						+"If you wish to open a previously saved game of Heroes of Might and Magic, \n"
						+"close this window, go to File menu and select 'Open Saved Game'\n\n"
						+"For information about how to play the game check 'Getting Started' in the help section.\n\n\n"
						+"Good Luck and we hope you enjoy the game.\n"
						+"The Heroes of Might and Magic (TAU Version) Team\n"
						+ "© 2009";
		}
		else if (helpItem.equals("Game Score and Highscores"))
		{
			helpString = "The Heroes of MIght and Magic (TAU Version) has a score board.\n\n"
						+"Only the player who won the scenario (see 'Getting Started' about scenario) gets a score\n"
						+"at the end of the game. The winner gets one point for every resource mine, one point for\n"
						+"each treasury (i.e. 500 Gold equals 500 points) and one point for each unit (in castle\n"
						+"and with hero)\n"
						+"The High-Score table is available through the 'Highscores' menu on the top menu bar.\n"
						+"On the 'Highscores' menu you will find the option to view the highscores,\n"
						+"or reset the highscores table.\n";
		}
		/* end if */
		
		final Shell helpShell = new Shell(Display.getCurrent().getActiveShell());
		helpShell.setText("Help about: " + helpItem);
		helpShell.setImage(IconCache.stockImages[IconCache.appIcon]);
		helpShell.setLayout(new GridLayout(1, true));
		helpShell.setLocation(150, 150);
		
		Label helpLable = new Label(helpShell, SWT.LEFT);
		helpLable.setText(helpString);
		
		Font newFont = helpLable.getFont(); // just an initialization for the
		// compiler
		Font initialFont = helpLable.getFont();
		FontData[] fontData = initialFont.getFontData();
		for (int k = 0; k < fontData.length; k++) {
			fontData[k].setHeight(2 + initialFont.getFontData()[k]
					.getHeight());
			//fontData[k].setStyle(SWT.BOLD);
		}
		newFont = new Font(display, fontData);
		helpLable.setFont(newFont);
		
		Button button = new Button(helpShell, SWT.PUSH | SWT.CENTER);
		button.setText("Close");
		button.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		button.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				helpShell.dispose();
			}
		});
		
		helpShell.pack();
		helpShell.setSize(helpShell.getSize().x + 15, helpShell.getSize().y + 15);
		helpShell.open();
		
		while (!helpShell.isDisposed()){
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	public void showAboutMbox()
	{
		String aboutString;
		aboutString = "\n" + "Heroes of Might and Magic (TAU Version)\n\n"
					+ "Developped by the Heroes team for:\n"
					+ "Advanced development of Java based systemes\n"
					+ "course at TAU\n\n"
					+ "© 2009 - All right reserved!";
		MessageBox box = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION);
		box.setText("Heroes of Might and Magic (TAU Version)");
		box.setMessage(aboutString);
		box.open();
	}

	/**
	 * creates all the items in the high scores sub-menu, and associates all
	 * menu items to the right functions
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
				shell.setEnabled(false);
				displayHighscores();
				shell.setEnabled(true);
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
		
		MenuItem subItem3 = new MenuItem(menu, SWT.NULL);
		subItem3.setText("&About Highscores");
		subItem3.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				shell.setEnabled(false);
				diplayHelpByHelpItem("Game Score and Highscores");
				shell.setEnabled(true);
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
		MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(),
			SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox
			.setMessage("Are you sure you want to clear higscores table?\nThis action can not be reversed");
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
		tableShell.setImage(IconCache.stockImages[IconCache.highscoreIcon]);
		Table scoreTable = new Table(tableShell, SWT.CENTER);
		TableColumn col1 = new TableColumn(scoreTable, SWT.CENTER);
		TableColumn col2 = new TableColumn(scoreTable, SWT.CENTER);
		col1.setText("Player Name");
		col2.setText("Player Score");
		col1.setWidth(146);
		col2.setWidth(147);
		scoreTable.setHeaderVisible(true);

		TableItem ti;
		Font newFont = scoreTable.getFont(); // just an initialization for the
		// compiler

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

	@SuppressWarnings("unused")
	private void moveHero()
	{
		String message = "Move to X,Y, e.g. 12,31 (Currenly at: "
			+ gameController.getGameState().getPlayers().elementAt(currentPlayerIndex).getHero()
				.getXPos()
			+ ","
			+ gameController.getGameState().getPlayers().elementAt(currentPlayerIndex).getHero()
				.getYPos() + "): ";
		String response = null;
		String[] responseSplit = null;
		boolean ok = false;

		do
		{
			InputDialog numbersInput = new InputDialog(Display.getCurrent().getActiveShell(),
				"Move to X,Y", message, null, null);
			if (numbersInput.open() == Window.OK)
			{
				response = numbersInput.getValue();
			}
			else
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
			}
			else
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
		p.AIMove();
		p.endTurn();

		removeDeadPlayers();
		if (this.gameController.isThereAWinner() != null)
			endGame(this.gameController.isThereAWinner());

		currentPlayerIndex = (currentPlayerIndex + 1)
			% this.gameController.getGameState().getNumberOfPlayers();
		this.gameController.getGameState().setWhosTurn(currentPlayerIndex);
		p = gameController.getGameState().getPlayers().elementAt(currentPlayerIndex);
		createBoardWindow(true);
		updateStatusWindow();

		// Here is the computer move.
		while (p.isComputer())
		{
			Hero hero = gameController.getGameState().getPlayers().elementAt(currentPlayerIndex)
				.getHero();
			if (hero != null)
			{
				String[] computerMove = new String[2];
				computerMove[0] = String.valueOf(hero.getXPos() + (int) (Math.random() * 3) - 1);
				computerMove[1] = String.valueOf(hero.getYPos() + (int) (Math.random() * 3) - 1);
				handleMoveCommand(computerMove);
			}
			p.AIMove();
			// End turn:
			p.endTurn();
			removeDeadPlayers();
			if (this.gameController.isThereAWinner() != null)
				endGame(this.gameController.isThereAWinner());

			currentPlayerIndex = (currentPlayerIndex + 1)
				% this.gameController.getGameState().getNumberOfPlayers();
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
			displayMessage("winner is: " + winner.getName() + " with a score of: "
				+ winner.finalScore());
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

		if (!Helper.isIntBetween(newX, 0, Constants.BOARD_SIZE - 1)
			|| !Helper.isIntBetween(newY, 0, Constants.BOARD_SIZE - 1))
		{
			if (!gameController.getGameState().getPlayers().elementAt(currentPlayerIndex)
				.isComputer())
				displayError("Invallid Input. Outside of board - Try again");
			return false;
		}
		else if (gameController.getGameState().getPlayers().elementAt(currentPlayerIndex)
			.move(newX, newY, this.gameController.getGameState().getBoard()))
		{
			createBoardWindow(true);
			updateStatusWindow();
			return true;
		}
		else
		{
			displayError("Illegal move ! \n"
				+ gameController.getGameState().getPlayers().elementAt(currentPlayerIndex)
					.getName()
				+ " ,You can only move "
				+ gameController.getGameState().getPlayers().elementAt(currentPlayerIndex)
					.getMovesLeft() + " more step(s) .");
			return false;
		}
	}

	private void handleBuildCommand(Class<? extends CreatureFactory> creatureFactoryClass)
	{
		Castle currentCastle = gameController.getGameState().getBoard()
			.getBoardState(currentPoint.x, currentPoint.y).getCastle();

		if (currentCastle != null)
		{
			CreatureFactory factory = CreatureFactory.createCreatureFactory(creatureFactoryClass);
			if (factory != null)
			{
				if (currentCastle.hasFactory(creatureFactoryClass))
					displayError("There is already a factory of this type in this castle");
				else
				{
					if (currentCastle.canBuildFactory(creatureFactoryClass))
						currentCastle.addFactory(currentCastle.buildFactory(creatureFactoryClass));
					else
					{
						String msg = currentCastle.getPlayer().getName()
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
								+ currentCastle.getPlayer().getCurrentTreasuryAmount(rType
									.getTypeName()) + "\n";
						}
						displayError(msg);
					}
				}

			}
			else
				displayError("Unknown creature type!");
		}
		updateStatusWindow();
	}

	private void handleSplitCommand(Class<? extends CreatureFactory> creatureFactoryClass)
	{
		Castle currentCastle = gameController.getGameState().getBoard()
			.getBoardState(currentPoint.x, currentPoint.y).getCastle();
		String message;
		String response = null;
		int numberOfUnits = 0;

		Hero hero = gameController.getGameState().getPlayers().elementAt(currentPlayerIndex)
			.getHero();
		if (hero == null)
		{
			displayError("Sorry, but you don't have a hero.");
			return;
		}

		message = "Enter desired number of units to split to the castle: ";

		InputDialog numberInput = new InputDialog(Display.getCurrent().getActiveShell(),
			"Number of Units", message, null, null);
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

		CreatureFactory factory = CreatureFactory.createCreatureFactory(creatureFactoryClass);
		if (factory == null)
		{
			displayError("Unknown creature type!");
			return;
		}

		Creature creature = factory.buildCreature(numberOfUnits);

		if (!currentCastle.canAddToArmy(creature.getClass()))
		{
			displayError("Army in " + currentCastle.printLocation() + " is full.");
			return;
		}
		else if (!hero.removeFromArmy(creature))
		{
			displayError("You dont have enough units to split");
			return;
		}
		else
		{
			currentCastle.addToArmy(creature);
			updateStatusWindow();
			return;
		}
	}

	private void handleJoinCommand(Class<? extends CreatureFactory> creatureFactoryClass)
	{
		Castle currentCastle = gameController.getGameState().getBoard()
			.getBoardState(currentPoint.x, currentPoint.y).getCastle();
		String message;
		String response = null;
		int numberOfUnits = 0;

		Hero hero = gameController.getGameState().getPlayers().elementAt(currentPlayerIndex)
			.getHero();
		if (hero == null)
		{
			displayError("Sorry, but you don't have a hero.");
			return;
		}

		message = "Enter desired number of units to join from the castle: ";

		InputDialog numberInput = new InputDialog(Display.getCurrent().getActiveShell(),
			"Number of Units", message, null, null);
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

		CreatureFactory factory = CreatureFactory.createCreatureFactory(creatureFactoryClass);
		if (factory == null)
		{
			displayError("Unknown creature type!");
			return;
		}

		Creature creature = factory.buildCreature(numberOfUnits);

		if (!hero.canAddToArmy(creature.getClass()))
		{
			displayError("Army of " + hero.toString() + " is full.");
			return;
		}
		else if (!currentCastle.canRemoveFromArmy(creature))
		{
			displayError("You dont have enough units to join.");
			return;
		}
		else
		{
			currentCastle.removeFromArmy(creature);
			hero.addToArmy(creature);
			updateStatusWindow();
			return;
		}
	}

	private void handleMakeCommand(Class<? extends Creature> creatureClass)
	{
		Castle currentCastle = gameController.getGameState().getBoard()
			.getBoardState(currentPoint.x, currentPoint.y).getCastle();
		String message;
		String response = null;
		int numberOfUnits = 0;

		if (currentCastle != null)
		{
			if (creatureClass != null)
			{
				int maxUnits = currentCastle.getAvailableUnits(creatureClass);

				if (maxUnits > 0)
				{
					message = "Enter desired number of units (1-" + maxUnits + "): ";

					InputDialog numberInput = new InputDialog(
						Display.getCurrent().getActiveShell(), "Number of Units", message, null,
						null);
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
			}
			else
				displayError("Unknown creature type.");
		}
	}
	private class ScrollMove implements MouseWheelListener
	{
			public void mouseScrolled(MouseEvent e)
			{
				try
				{
					ScrolledComposite sc = (ScrolledComposite)e.getSource();
					Point origin = sc.getOrigin();
					origin.y -= e.count*4;
					sc.setOrigin(origin);
				}
				catch(Exception ex)
				{
					System.out.println("ScrollMove: exception = "+ex.getMessage());
				}
			}
	}
}