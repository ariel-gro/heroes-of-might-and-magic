package tau.heroes;

import java.io.File;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.eclipse.swt.widgets.Group;
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

import tau.heroes.net.ChatEvent;
import tau.heroes.net.ChatListener;
import tau.heroes.net.GameOverEvent;
import tau.heroes.net.GameOverListner;
import tau.heroes.net.GameStateEvent;
import tau.heroes.net.GameStateListener;
import tau.heroes.net.NetworkResult;

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

	@SuppressWarnings("static-access")
	public HeroesGui(Display d, GameController gameController)
	{
		this.display = d;
		this.gameController = gameController;
		this.gameController.addChatListener(new ChatListener() {
		
			public void chatMessageArrived(ChatEvent e)
			{
				handleIncomingChat(e);
			}
		});
		this.gameController.addGameStateListener(new GameStateListener() {
			
			public void gameStateMessageArrived(GameStateEvent e)
			{
				handleIncomingGameState(e);
			}
		});
		this.gameController.addGameOverListener(new GameOverListner() {
			
			@Override
			public void gameOverMessageArrived(GameOverEvent e) {
				// TODO Auto-generated method stub
				handleIncomingGameOver(e);
			}
		});
		
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

		if (gameController != null)
			gameController.Disconnect();

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
				return bs.getHero().player.getPlayerColor().inWoodIcon();
			}
			else if (bs.getResource() != null
				&& bs.getResource().getType().getTypeName().equals(ResourceType.GOLD.getTypeName()))
			{
				return bs.getHero().player.getPlayerColor().inGoldIcon();
			}
			else if (bs.getResource() != null
				&& bs.getResource().getType().getTypeName()
					.equals(ResourceType.STONE.getTypeName()))
			{
				return bs.getHero().player.getPlayerColor().inStoneIcon();
			}
			else if (bs.getCastle() != null)
			{
				switch (bs.getCastle().getCastleType())
				{
				case CASTLE:
					return bs.getCastle().getPlayer().getPlayerColor().inCastleIcon();
				case DUNGEON:
					return bs.getCastle().getPlayer().getPlayerColor().inDungeonIcon();
				case RAMPART:
					return bs.getCastle().getPlayer().getPlayerColor().inRampartIcon();
				case TOWER:
					return bs.getCastle().getPlayer().getPlayerColor().inTowerIcon();

				default:
					return bs.getCastle().getPlayer().getPlayerColor().inCastleIcon();
				}

			}
			else
				return bs.getHero().player.getPlayerColor().heroIcon();
			// return IconCache.blueHeroIcon;

		}
		else if ((bs.getCastle()) != null)
		{
			switch (bs.getCastle().getCastleType())
			{
			case CASTLE:
				return IconCache.castleIcon;
			case DUNGEON:
				return IconCache.dungeonIcon;
			case RAMPART:
				return IconCache.rampartIcon;
			case TOWER:
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
		// TODO: add new icons
		return bs.getMapObject().mapObjectIcon();
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
				return bs.getHero().player.getName() + "'s Hero in "
					+ bs.getCastle().getPlayer().getName() + "'s "
					+ bs.getCastle().getCastleType().castleNameByType() + "\nLocation: " + x + ", "
					+ y;
			}
			else
				return bs.getHero().player.getName() + "'s Hero" + "\nLocation: " + x + ", " + y;

		}
		else if ((bs.getCastle()) != null)
		{
			return bs.getCastle().getPlayer().getName() + "'s "
				+ bs.getCastle().getCastleType().castleNameByType() + "\nLocation: " + x + ", " + y;
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

		if (isBoardInit == false
			&& (boardComposite != null && boardComposite.isDisposed() == false))
			boardComposite.dispose();

		if (isBoardInit == false)
		{
			boardComposite = new Composite(sc1, SWT.NONE);
			boardComposite.setBackground(black);
			GridData d = new GridData(GridData.FILL_BOTH);
			boardComposite.setLayoutData(d);
		}
		boardComposite.setEnabled(true);
		sc1.addMouseWheelListener(new ScrollMove());
		sc1.forceFocus();
		if (initBoard == false)
		{
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

			isVisible = gameController.getGameState().getPlayers().elementAt(currentPlayerIndex)
				.getVisibleBoard();

			if (isBoardInit == false)
			{
				boardSquares = new Composite[numOfCells][numOfCells];
				boardLabels = new Label[numOfCells][numOfCells];
			}

			for (int y = 0; y < numOfCells; y++)
			{
				for (int x = 0; x < numOfCells; x++)
				{
					if (isBoardInit == false)
						boardSquares[x][y] = new Composite(boardComposite, SWT.NONE);

					Composite b = boardSquares[x][y];
					GridLayout cellLayout = new GridLayout();
					cellLayout.marginWidth = 0;
					cellLayout.marginHeight = 0;
					b.setLayout(cellLayout);

					if (isBoardInit == false)
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

					if (PlayerColor.isHeroIcon(t))
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

					if (PlayerColor.isInCastleIcon(t))
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
			sc1.setMinSize(boardComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

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

						if (!gameController.getGameState().getPlayers()
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

	@SuppressWarnings("unused")
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
		statusComposite.setEnabled(true);

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
			button1.setText(castle.getCastleType().castleNameByType() + " at  " + castleXPos
				+ " , " + castleYPos);
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
				diplayHelpByHelpItem(GameStringsHelper.StatusWindow);
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
		createNetworkMenu(menuBar);
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
		Display display = Display.getCurrent();
		Shell shell = display.getActiveShell();
		displayMessage(shell, msg);
	}
	
	public static void displayMessage(Shell shell, String msg)
	{
		MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION);
		box.setText(shell.getText());
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
					players.add(new Player(name1, PlayerColor.BLUE));

					if ((name2.length() > 0) || (pcButton1.getSelection()))
					{
						if (pcButton1.getSelection())
						{
							Player tempPlayer1 = new Player("computer", PlayerColor.RED);
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
							players.add(new Player(name2, PlayerColor.RED));
						}
					}

					if ((name3.length() > 0) || (pcButton2.getSelection()))
					{
						if (pcButton2.getSelection())
						{
							Player tempPlayer2 = new Player("computer", PlayerColor.BLACK);
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
							players.add(new Player(name3, PlayerColor.BLACK));
						}
					}

					if ((name4.length() > 0) || (pcButton3.getSelection()))
					{
						if (pcButton3.getSelection())
						{
							Player tempPlayer3 = new Player("computer", PlayerColor.PURPLE);
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
							players.add(new Player(name4, PlayerColor.PURPLE));
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
				diplayHelpByHelpItem(GameStringsHelper.NewGameWindow);
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
				players.add(new Player(response, PlayerColor.values()[i]));
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

		this.gameController.loadGame(name);
		handleUpdateGameState();

	}

	private boolean handleUpdateGameState()
	{
		Cursor waitCursor = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
		shell.setCursor(waitCursor);

		currentPlayerIndex = this.gameController.getGameState().getWhosTurn();
		if (gameController.isNetworkGame()
			&& gameController.getNetworkIndex() != currentPlayerIndex)
		{// This is a network game, and not my turn:
			// we can add a timers or progress bar or info...
	//		displayMessage(gameController.getUserInfo().getNickname()+ " Not your turn");
			boardComposite.setEnabled(false);
			statusComposite.setEnabled(false);
			return false;
		}

		if (gameController.getGameState().getBoard() == null)
		{
			displayMessage("The file you opened doesn't contain a valid Heroes saved game.\n"
				+ "Please try again with a different file");
			return false;
		}

		createBoardWindow(true);
		createStatusWindow(true);
		sash.setWeights(new int[] { 80, 20 });

		shell.setCursor(null);
		waitCursor.dispose();
		return true;
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
				Exit();
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
		
		// Help -> Network assistance
		MenuItem networkAssistItem = new MenuItem(menu, SWT.NULL);
		networkAssistItem.setText("&Network assistance\tCtrl+N");
		networkAssistItem.setAccelerator(SWT.MOD1 + 'N');
		networkAssistItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				showNetworkAssistanceMbox();
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

	/**
	 * Creates all the items located in the Help submenu and associate all the
	 * menu items with their appropriate functions.
	 * 
	 * @param menuBar
	 *            Menu the <code>Menu</code> that file contain the Help submenu.
	 */
	public void createNetworkMenu(Menu menuBar)
	{

		// Network Menu
		MenuItem item = new MenuItem(menuBar, SWT.CASCADE);
		item.setText("Network");
		Menu menu = new Menu(shell, SWT.DROP_DOWN);
		item.setMenu(menu);

		// Network -> Login
		MenuItem loginItem = new MenuItem(menu, SWT.NULL);
		loginItem.setText("&Login");
		loginItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				lgoinToServerWindow();
			}
		});

		// Network -> Add New User
		MenuItem addNewUserItem = new MenuItem(menu, SWT.NULL);
		addNewUserItem.setText("&Add New User");
		addNewUserItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				addNewUserToServerWindow();
			}
		});

		// Network -> Chat
		MenuItem chatItem = new MenuItem(menu, SWT.NULL);
		chatItem.setText("Show Network Status");
		chatItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				//chatWindow();
				startNetworkGame("","");
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
				diplayHelpByHelpItem(GameStringsHelper.GettingStarted);
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
				diplayHelpByHelpItem(GameStringsHelper.Army);
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
				diplayHelpByHelpItem(GameStringsHelper.HowToMove);
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
				diplayHelpByHelpItem(GameStringsHelper.Resources);
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
				diplayHelpByHelpItem(GameStringsHelper.StatusWindow);
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
				diplayHelpByHelpItem(GameStringsHelper.Battles);
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
				diplayHelpByHelpItem(GameStringsHelper.Highscores);
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

		// helpShell.pack();
		// helpShell.setSize(helpShell.getSize().x + 25, helpShell.getSize().y +
		// 25);
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

	public void showNetworkAssistanceMbox() 
	{
		// TODO fill the help menu
		
		final Shell helpShell = new Shell(Display.getCurrent().getActiveShell());
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.verticalSpacing = 5;
		gridLayout.marginHeight = gridLayout.marginWidth = 25;
		helpShell.setLayout(gridLayout);
		helpShell.setText("Heroes (TAU Ver.) - Network Assistance");
		helpShell.setImage(IconCache.stockImages[IconCache.appIcon]);
		helpShell.setSize(330, 230);

		/* The Network Game */
		Button button = new Button(helpShell, SWT.PUSH | SWT.CENTER);
		button.setText("The Network Game");
		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				helpShell.setEnabled(false);
				diplayHelpByHelpItem(GameStringsHelper.theNetworkGame);
				helpShell.setEnabled(true);
			}
		});
		
		/* Login */
		button = new Button(helpShell, SWT.PUSH | SWT.CENTER);
		button.setText("Login");
		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				helpShell.setEnabled(false);
				diplayHelpByHelpItem(GameStringsHelper.Login);
				helpShell.setEnabled(true);
			}
		});
		
		/* The Lobby */
		button = new Button(helpShell, SWT.PUSH | SWT.CENTER);
		button.setText("The Lobby");
		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				helpShell.setEnabled(false);
				diplayHelpByHelpItem(GameStringsHelper.theLobby);
				helpShell.setEnabled(true);
			}
		});

		/* creating or joining room */
		button = new Button(helpShell, SWT.PUSH | SWT.CENTER);
		button.setText("Creating or Joining room");
		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				helpShell.setEnabled(false);
				diplayHelpByHelpItem(GameStringsHelper.createJoinRoom);
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

		// helpShell.pack();
		// helpShell.setSize(helpShell.getSize().x + 25, helpShell.getSize().y +
		// 25);
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
	
	public static void diplayHelpByHelpItem(int helpItem)
	{
		String helpString = GameStringsHelper.getStringByIndex(helpItem);
		String header = GameStringsHelper.getHeaderByIndex(helpItem);

		final Shell helpShell = new Shell(Display.getCurrent().getActiveShell());
		helpShell.setText(header);
		helpShell.setImage(IconCache.stockImages[IconCache.appIcon]);
		helpShell.setLayout(new GridLayout(1, true));
		helpShell.setLocation(150, 150);

		Label helpLable = new Label(helpShell, SWT.LEFT);
		helpLable.setText(helpString);

		Font newFont = helpLable.getFont(); // just an initialization for the
		// compiler
		Font initialFont = helpLable.getFont();
		FontData[] fontData = initialFont.getFontData();
		for (int k = 0; k < fontData.length; k++)
		{
			fontData[k].setHeight(2 + initialFont.getFontData()[k].getHeight());
			// fontData[k].setStyle(SWT.BOLD);
		}
		newFont = new Font(display, fontData);
		helpLable.setFont(newFont);

		Button button = new Button(helpShell, SWT.PUSH | SWT.CENTER);
		button.setText("Close");
		button.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				helpShell.dispose();
			}
		});

		helpShell.pack();
		helpShell.setSize(helpShell.getSize().x + 15, helpShell.getSize().y + 15);
		helpShell.open();

		while (!helpShell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	public void showAboutMbox()
	{
		String aboutString;
		aboutString = GameStringsHelper.getStringByIndex(GameStringsHelper.About);
		MessageBox box = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_INFORMATION);
		box.setText(GameStringsHelper.getHeaderByIndex(GameStringsHelper.About));
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
				diplayHelpByHelpItem(GameStringsHelper.Highscores);
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
		{
			endGame(this.gameController.isThereAWinner());
			handleNetworkEndTurn();
			return;
		}
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
			{
				endGame(this.gameController.isThereAWinner());
				handleNetworkEndTurn();
				return;
			}
			currentPlayerIndex = (currentPlayerIndex + 1)
				% this.gameController.getGameState().getNumberOfPlayers();
			this.gameController.getGameState().setWhosTurn(currentPlayerIndex);
			p = gameController.getGameState().getPlayers().elementAt(currentPlayerIndex);
			createBoardWindow(true);
			updateStatusWindow();
		}
		handleNetworkEndTurn();
	}


	private void removeDeadPlayers()
	{
		for (Player player : this.gameController.removeDeadPlayers())
			GameController.handleMessage(player.getName() + " is out of the game.");
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
		boardComposite.setEnabled(false);
		statusComposite.setEnabled(false);
	//	System.exit(0);
		
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

	public void lgoinToServerWindow()
	{
		final Shell shell = new Shell(Display.getCurrent().getActiveShell());
		shell.setImage(IconCache.stockImages[IconCache.appIcon]);

		GridLayout layout1 = new GridLayout(4, true);
		shell.setText("Login to Server");
		shell.setLayout(layout1);

		GridData ipAddressData = new GridData();
		ipAddressData.horizontalSpan = 2;
		ipAddressData.grabExcessHorizontalSpace = true;
		ipAddressData.grabExcessVerticalSpace = true;
		final Label ipAddressLabel = new Label(shell, SWT.NULL);
		ipAddressLabel.setText("Host/IP:");
		ipAddressLabel.setLayoutData(ipAddressData);
		final Combo ipAddressText = new Combo(shell, SWT.NULL);
		GridData ipAddressTextData = new GridData();
		ipAddressTextData.horizontalSpan = 2;
		ipAddressTextData.grabExcessHorizontalSpace = true;
		ipAddressTextData.grabExcessVerticalSpace = true;
		ipAddressText.setLayoutData(ipAddressTextData);
		ipAddressText.setEnabled(true);
		ipAddressText.add("127.0.0.1");
		ipAddressText.add("kite.cs.tau.ac.il");
		ipAddressText.setText("127.0.0.1");

		GridData loginAsGuestData = new GridData();
		loginAsGuestData.horizontalSpan = 4;
		loginAsGuestData.grabExcessHorizontalSpace = true;
		loginAsGuestData.grabExcessVerticalSpace = true;
		final Button loginAsGuestButton = new Button(shell, SWT.RADIO);
		loginAsGuestButton.setSelection(true);
		loginAsGuestButton.setText("Login as Guest");
		loginAsGuestButton.setLayoutData(loginAsGuestData);
		loginAsGuestButton.setSelection(true);

		GridData loginAsUserData = new GridData();
		loginAsUserData.horizontalSpan = 4;
		loginAsUserData.grabExcessHorizontalSpace = true;
		loginAsUserData.grabExcessVerticalSpace = true;
		final Button loginAsUserButton = new Button(shell, SWT.RADIO);
		loginAsUserButton.setText("Login as User");
		loginAsUserButton.setLayoutData(loginAsUserData);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.makeColumnsEqualWidth = true;

		final Group userPasswordGroup = new Group(shell, SWT.NULL);
		userPasswordGroup.setLayout(gridLayout);
		GridData groupData = new GridData();
		groupData.horizontalSpan = 4;
		groupData.grabExcessHorizontalSpace = true;
		groupData.grabExcessVerticalSpace = true;
		userPasswordGroup.setLayoutData(groupData);

		GridData userNameData = new GridData();
		userNameData.horizontalSpan = 2;
		userNameData.grabExcessHorizontalSpace = true;
		userNameData.grabExcessVerticalSpace = true;
		final Label userNameLabel = new Label(userPasswordGroup, SWT.NULL);
		userNameLabel.setText("User Name:");
		userNameLabel.setLayoutData(userNameData);
		final Text userNameText = new Text(userPasswordGroup, SWT.BORDER);
		GridData userNameTextData = new GridData();
		userNameTextData.horizontalSpan = 2;
		userNameTextData.grabExcessHorizontalSpace = true;
		userNameTextData.grabExcessVerticalSpace = true;
		userNameText.setLayoutData(userNameTextData);
		userNameText.setEnabled(false);

		GridData passwordData = new GridData();
		passwordData.horizontalSpan = 2;
		passwordData.grabExcessHorizontalSpace = true;
		passwordData.grabExcessVerticalSpace = true;
		final Label passwordLabel = new Label(userPasswordGroup, SWT.NULL);
		passwordLabel.setText("Password:");
		passwordLabel.setLayoutData(passwordData);
		final Text passwordText = new Text(userPasswordGroup, SWT.BORDER);
		passwordText.setEchoChar('*');
		GridData passwordTextData = new GridData();
		passwordTextData.horizontalSpan = 2;
		passwordTextData.grabExcessHorizontalSpace = true;
		passwordTextData.grabExcessVerticalSpace = true;
		passwordText.setLayoutData(passwordTextData);
		passwordText.setEnabled(false);

		loginAsUserButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				if (loginAsGuestButton.getSelection())
				{
					userNameText.setEnabled(false);
					passwordText.setEnabled(false);
				}
				else
				{
					userNameText.setEnabled(true);
					passwordText.setEnabled(true);
				}
			}
		});

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
				String ip = ipAddressText.getText();
				String userName = userNameText.getText();
				String passWord = passwordText.getText();
				
				if(isValidIp(ip))
				{
					// Check the user name:
					NetworkResult<Boolean> res = gameController
						.Login(ipAddressText.getText(), userName, passWord, loginAsGuestButton
							.getSelection());
					// Validate return value!
					if (res.getResult() != true)
					{
						displayError(res.getErrorMessage());
						return;
					}
	
					startNetworkGame(userName, passWord);
					shell.dispose();
					statusComposite.layout(true, true);
				}
				else
				{
					displayError("IP Address provided is illegal !!!");
				}
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
				showNetworkAssistanceMbox();
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

	public void addNewUserToServerWindow()
	{
		final Shell shell = new Shell(Display.getCurrent().getActiveShell());
		shell.setImage(IconCache.stockImages[IconCache.appIcon]);

		GridLayout layout1 = new GridLayout(4, true);
		shell.setText("Add New User to Server");
		shell.setLayout(layout1);

		GridData ipAddressData = new GridData();
		ipAddressData.horizontalSpan = 2;
		ipAddressData.grabExcessHorizontalSpace = true;
		ipAddressData.grabExcessVerticalSpace = true;
		final Label ipAddressLabel = new Label(shell, SWT.NULL);
		ipAddressLabel.setText("Host/IP:");
		ipAddressLabel.setLayoutData(ipAddressData);
		final Combo ipAddressText = new Combo(shell, SWT.NULL);
		GridData ipAddressTextData = new GridData();
		ipAddressTextData.horizontalSpan = 2;
		ipAddressTextData.grabExcessHorizontalSpace = true;
		ipAddressTextData.grabExcessVerticalSpace = true;
		ipAddressText.setLayoutData(ipAddressTextData);
		ipAddressText.setEnabled(true);
		ipAddressText.add("127.0.0.1");
		ipAddressText.add("kite.cs.tau.ac.il");
		ipAddressText.setText("127.0.0.1");

		GridData userNameData = new GridData();
		userNameData.horizontalSpan = 2;
		userNameData.grabExcessHorizontalSpace = true;
		userNameData.grabExcessVerticalSpace = true;
		final Label userNameLabel = new Label(shell, SWT.NULL);
		userNameLabel.setText("User Name:");
		userNameLabel.setLayoutData(userNameData);
		final Text userNameText = new Text(shell, SWT.BORDER);
		GridData userNameTextData = new GridData();
		userNameTextData.horizontalSpan = 2;
		userNameTextData.grabExcessHorizontalSpace = true;
		userNameTextData.grabExcessVerticalSpace = true;
		userNameText.setLayoutData(userNameTextData);

		GridData passwordData = new GridData();
		passwordData.horizontalSpan = 2;
		passwordData.grabExcessHorizontalSpace = true;
		passwordData.grabExcessVerticalSpace = true;
		final Label passwordLabel = new Label(shell, SWT.NULL);
		passwordLabel.setText("Password:");
		passwordLabel.setLayoutData(passwordData);
		final Text passwordText = new Text(shell, SWT.BORDER);
		passwordText.setEchoChar('*');
		GridData passwordTextData = new GridData();
		passwordTextData.horizontalSpan = 2;
		passwordTextData.grabExcessHorizontalSpace = true;
		passwordTextData.grabExcessVerticalSpace = true;
		passwordText.setLayoutData(passwordTextData);

		GridData mailData = new GridData();
		mailData.horizontalSpan = 2;
		mailData.grabExcessHorizontalSpace = true;
		mailData.grabExcessVerticalSpace = true;
		final Label mailLabel = new Label(shell, SWT.NULL);
		mailLabel.setText("Mail:");
		mailLabel.setLayoutData(mailData);
		final Combo mailText = new Combo(shell, SWT.NULL);
		GridData mailTextData = new GridData();
		mailTextData.horizontalSpan = 2;
		mailTextData.grabExcessHorizontalSpace = true;
		mailTextData.grabExcessVerticalSpace = true;
		mailText.setLayoutData(mailTextData);

		GridData nicknameData = new GridData();
		nicknameData.horizontalSpan = 2;
		nicknameData.grabExcessHorizontalSpace = true;
		nicknameData.grabExcessVerticalSpace = true;
		final Label nicknameLabel = new Label(shell, SWT.NULL);
		nicknameLabel.setText("Nickname:");
		nicknameLabel.setLayoutData(mailData);
		final Combo nicknameText = new Combo(shell, SWT.NULL);
		GridData nicknameTextData = new GridData();
		nicknameTextData.horizontalSpan = 2;
		nicknameTextData.grabExcessHorizontalSpace = true;
		nicknameTextData.grabExcessVerticalSpace = true;
		nicknameText.setLayoutData(nicknameTextData);

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
				boolean mailValid, ipValid;
				String ip = ipAddressText.getText();
				String userName = userNameText.getText();
				String passWord = passwordText.getText();
				String email = mailText.getText();
				String nickname = nicknameText.getText();
				
				mailValid = isMailValid(email);
				ipValid = isValidIp(ip);
				
				if(mailValid && ipValid)
				{	
					// Check the user name:
					NetworkResult<Boolean> res = gameController.Register(ip, userName, passWord, email, nickname);
					// Validate return value!
					if (res.getResult() != true)
					{
						displayError(res.getErrorMessage());
						return;
					}
					startNetworkGame(userName, passWord);
					shell.dispose();
				}
				else
				{
					if(!mailValid && !ipValid)
						displayError("IP Address and Mail Address provided are illegal !!!");
					else
					{
						if(! mailValid )
							displayError("Mail Address provided is illegal !!!");
						if(!isValidIp(ip))
							displayError("IP Address provided is illegal !!!");
					}
				}
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
				showNetworkAssistanceMbox();
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
	
	private boolean isMailValid(String theMailAddress)
	{
		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		
		Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(theMailAddress);
		
		if(matcher.matches())
			return true;
		else
			return false;
	}
	
	private boolean isValidIp(String theIp)
	{
		if (theIp == null || theIp == "") return false;

		String[] fields = theIp.split("\\.");

		if (fields.length < 2) return false;
		
		/*
		if (fields.length != 4) return false;
		if (fields[0].equals("0")) return false;

		try
		{
			for (int i = 0; i < fields.length; i++)
			{
				int num = Integer.parseInt(fields[i]);
				if (num > 255 || num < 0) return false;
			}
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		*/
		return true;
	}

	public void chatWindow()
	{
		final Shell shell = new Shell(Display.getCurrent().getActiveShell());
		shell.setSize(300, 160);
		shell.setImage(IconCache.stockImages[IconCache.appIcon]);
		shell.setText("Chat");
		shell.setLayout(new GridLayout());

		// Composite c1 = new Composite(shell, SWT.FILL);
		// c1.setLayout(new GridLayout(2, false));
		// Label partnerLabel = new Label(c1, SWT.NONE);
		// partnerLabel.setText("Recipient : ");
		// final Text partnerText = new Text(c1, SWT.BORDER);

		Composite c2 = new Composite(shell, SWT.FILL);
		c2.setLayout(new GridLayout(1, false));
		final Text messageText = new Text(c2, SWT.BORDER | SWT.FILL);
		messageText.setSize(150, 50);
		// message.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Composite c3 = new Composite(shell, SWT.FILL);
		c3.setLayout(new GridLayout(1, false));
		Button sendButton = new Button(c3, SWT.PUSH);
		sendButton.setText("    Send  Message    ");
		sendButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{

				String message = messageText.getText();
				gameController.sendChat(message);
				shell.dispose();
			}
		});

		// Button cancelButton = new Button(c3, SWT.PUSH);
		// cancelButton.setText(" Cancel ");
		// cancelButton.addSelectionListener(new SelectionAdapter() {
		// public void widgetSelected(SelectionEvent e)
		// {
		// shell.dispose();
		// }
		// });

		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}

	}

	public void Exit()
	{
		if (gameController.isNetwork)
		{
			Player p = gameController.getGameState().getPlayers().elementAt(currentPlayerIndex);
			p.getHero().kill();
		}
		shell.close();
	}

	private void startNetworkGame(String userName, String password)
	{
		createStatusWindow(false);
		NetworkGUI networkGUI = new NetworkGUI(statusComposite, gameController);
		networkGUI.init();

	}
	
	private class ScrollMove implements MouseWheelListener
	{
		public void mouseScrolled(MouseEvent e)
		{
			try
			{
				ScrolledComposite sc = (ScrolledComposite) e.getSource();
				Point origin = sc.getOrigin();
				origin.y -= e.count * 4;
				sc.setOrigin(origin);
			}
			catch (Exception ex)
			{
				System.out.println("ScrollMove: exception = " + ex.getMessage());
			}
		}
	}

	private void handleIncomingChat(final ChatEvent e)
	{
		shell.getDisplay().asyncExec(new Runnable() {
			public void run()
			{
				displayMessage(shell, e.getChatMessage().getText());
			}
		});
	}
	private void handleNetworkEndTurn() {
		if(!gameController.isNetworkGame())
			return;
		gameController.sendGameState();
		
	}

	private void handleIncomingGameState(final GameStateEvent e)
	{
		shell.getDisplay().asyncExec(new Runnable() {
			
			public void run()
			{
				GameState gs = e.getGameStateMessage().getGameState();
				gameController.setGameState(gs);
				//set index for each player according to it's name
				//we must do it every time because index may change
				int i=0;
				for(Player p : gs.getPlayers())
				{
					if(p.getName().equals(gameController.getUserInfo().getNickname()))
					{
						gameController.setNetworkIndex(i);
					}
					//network game is played with auto fight:
					p.setAutoFight(true);
					
					i++;
				}
				if(gs.isWinner() != null)
				{//then the game is over
				//	displayMessage("game over - in the debug section. before startNetworkGame() is calles");
					boardComposite.setEnabled(false);
					statusComposite.setEnabled(true);
					shell.setCursor(null);
					startNetworkGame("","");
					return;
				}
				handleUpdateGameState();
			}
		});
	}
	private void handleIncomingGameOver(final GameOverEvent e)
	{
		shell.getDisplay().syncExec(new Runnable() {
			
			public void run()
			{
				System.out.println("Game Over message is in 1");
				Player winner =  e.getGameOverMessage().getWinner();
				boolean bFound = false;
				//check if I'm still in the game...
				for(Player p : gameController.getGameState().getPlayers())
				{
					if(p.getName().equals(gameController.getUserInfo().getNickname()))
					{
						bFound = true;	
					}	
				}
				System.out.println("Game Over message is in 2");
				//if you are not in the game... you lost
				if(!bFound)
				{
					System.out.println("Game Over message is in 2.1");
					//here we should show the network window (instead of the game window).
					startNetworkGame("","");
					System.out.println("Game Over message is in 2.2");
					return;
				}
				System.out.println("Game Over message is in 3");
				//if you are the winner... then good for you
				if(winner.getName().equals(gameController.getUserInfo().getNickname()))
				{
					System.out.println("Game Over message is in 3.1");
					startNetworkGame("","");
					System.out.println("Game Over message is in 3.2");
					return;
				}
				System.out.println("Game Over message is in 4");
			}
		});
	}
}