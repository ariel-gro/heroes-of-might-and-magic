package tau.heroes;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import tau.heroes.db.UserInfo;
import tau.heroes.net.NetworkResult;
import tau.heroes.net.RoomInfo;
import tau.heroes.net.RoomUpdateEvent;
import tau.heroes.net.RoomUpdateListener;
import tau.heroes.net.RoomUpdateMessage;

public class NetworkGUI
{
	private Composite networkComposite;
	private Color white;
	private Display display;
	private Combo numOfPlayersCombo;
	private Table roomsTable, roomsDetailsTable;
	private GameController gameController;
	private RoomUpdateListener roomUpdateListener;

	public NetworkGUI(Composite statusComposite, GameController gameController)
	{
		this.networkComposite = new Composite(statusComposite, SWT.NONE);
		this.gameController = gameController;
		display = networkComposite.getDisplay();

		white = display.getSystemColor(SWT.COLOR_WHITE);

		networkComposite.setBackground(white);
		statusComposite.setBackground(white);
		networkComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		GridLayout layout = new GridLayout(1, true);
		layout.marginHeight = layout.marginWidth = 0;
		networkComposite.setLayout(layout);
	}

	private Label createLabel(String text)
	{
		Label tempLabel = new Label(networkComposite, SWT.NONE);
		tempLabel.setText(text);
		tempLabel.setBackground(white);
		return tempLabel;
	}

	public void init()
	{
		CLabel firstLabel = new CLabel(networkComposite, SWT.CENTER);
		firstLabel.setBackground(white);
		firstLabel.setImage(IconCache.stockImages[IconCache.appIcon]);
		firstLabel.setFont(IconCache.stockFonts[IconCache.titleFontIndex]);
		firstLabel.setText("     NETWORK   MENU");

		createLabel("");
		Button newRoomButton = new Button(networkComposite, SWT.CENTER);
		newRoomButton.setText("Create  New  Room ");
		newRoomButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		newRoomButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				handleNewRoomCommand();
			}
		});

		createLabel("");
		Button existingRoomButton = new Button(networkComposite, SWT.CENTER);
		existingRoomButton.setText("Join Existing Room ");
		existingRoomButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		existingRoomButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				handleJoinRoomCommand();
			}
		});

		Button newGameButton = new Button(networkComposite, SWT.CENTER);
		newGameButton.setText("Create  New  Network Game ");
		newGameButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		newGameButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				handleNewNetworkGame();
			}
		});

		createLabel("");
		createLabel("Existing Rooms :");
		List<RoomInfo> roomList = getRoomsFromServer();
		if (roomList != null)
		{
			displayRoomsTable(roomList);
		}

		createLabel("");
		createLabel("Room Details :");

		if (roomList != null)
		{
			displayRoomDetailsTable(roomList);
		}
		
		
		networkComposite.layout(true, true);

		roomUpdateListener = new RoomUpdateListener() {
			
			public void roomUpdated(RoomUpdateEvent e)
			{
				handleRoomUpdated(e);
			}
		};
		gameController.addRoomUpdateListener(roomUpdateListener);

		networkComposite.addDisposeListener(new DisposeListener() {
			
			public void widgetDisposed(DisposeEvent arg0)
			{
				gameController.removeRoomUpdateListener(roomUpdateListener);
			}
		});

	}

	private void handleNewRoomCommand()
	{
		//roomsCombo.setEnabled(false);
		final Shell shell = new Shell(Display.getCurrent().getActiveShell());
		shell.setLayout(new GridLayout());
		shell.setSize(225, 160);
		shell.setText("Create New Room");
		shell.setImage(IconCache.stockImages[IconCache.appIcon]);

		Composite form = new Composite(shell, SWT.FILL);
		form.setLayout(new GridLayout(2, true));

		final Label roomNameLabel = new Label(form, SWT.NONE);
		roomNameLabel.setText("Room Name : ");
		final Text roomNameText = new Text(form, SWT.BORDER);
		new Label(form, SWT.NONE);
		new Label(form, SWT.NONE);

		Label numOfPlayersLabel = new Label(form, SWT.NONE);
		numOfPlayersLabel.setText("Number of Players : ");
		numOfPlayersCombo = new Combo(form, SWT.NONE);
		numOfPlayersCombo.setItems(new String[] { "2", "3", "4" });
		numOfPlayersCombo.setText("2");

		new Label(form, SWT.NONE);
		new Label(form, SWT.NONE);

		Button okButton = new Button(form, SWT.PUSH);
		okButton.setText("     OK     ");
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e)
			{
				String roomName = roomNameText.getText();
				Integer numOfPlayers = Integer.parseInt(numOfPlayersCombo.getText());
				if (!roomName.equalsIgnoreCase(""))
				{
					createNewRoom(roomName, numOfPlayers);				
					shell.dispose();
				}
			}
		});

		Button cancelButton = new Button(form, SWT.PUSH);
		cancelButton.setText("     Cancel     ");
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e)
			{
				shell.dispose();
			}
		});

		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private void handleJoinRoomCommand()
	{
		//String roomName = roomsCombo.getText();
		//joinExistingRoom(roomName);
	}

	public List<RoomInfo> getRoomsFromServer()
	{
		NetworkResult<List<RoomInfo>> result = gameController.getRoomsFromServer();

		if (result.getResult() == null)
		{
			HeroesGui.displayError("Error getting room list from server: " + result.getErrorMessage());
		}

		return result.getResult();
	}

	public void createNewRoom(String roomName, int numOfPlayers)
	{
		
	}

	public void joinExistingRoom(String roomName)
	{
	}

	private void displayRoomsTable(List<RoomInfo> roomList)
	{
		roomsTable = new Table(networkComposite, SWT.BORDER | SWT.FULL_SELECTION);
		TableColumn col1 = new TableColumn(roomsTable, SWT.CENTER);
		TableColumn col2 = new TableColumn(roomsTable, SWT.CENTER);
		TableColumn col3 = new TableColumn(roomsTable, SWT.CENTER);
		col1.setText("Room Name");
		col2.setText("Owner");
		col3.setText("Members");
		col1.setWidth(80);
		col2.setWidth(60);
		col3.setWidth(60);
		col1.setResizable(true);
		col2.setResizable(true);
		col3.setResizable(true);
		roomsTable.setSortColumn(col1);
		roomsTable.setHeaderVisible(true);
		roomsTable.setToolTipText("Right click to join group");
		roomsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		roomsTable.setMenu(createJoinRoomPopUpMenu());
		roomsTable.addListener(SWT.Selection, new Listener() {
		      public void handleEvent(Event event) {
		    	  
		    	  updateRoomDetailsTable((RoomInfo)event.item.getData());
		      }
		    });

		TableItem ti;
		Font newFont = roomsTable.getFont(); // just an initialization for the
		// compiler

		for (int i = 0; i < roomList.size(); i++)
		{
			ti = new TableItem(roomsTable, SWT.CENTER);

			RoomInfo room = roomList.get(i);
			ti.setText(new String[] { room.getName(), room.getOwner().getNickname(), String.valueOf(room.getMemberCount()) });
			ti.setData(room);
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
		
		roomsTable.setSelection(0);

		newFont.dispose();
	}
	
	private Menu createJoinRoomPopUpMenu()
	{
		Menu popUpMenu;

		popUpMenu = new Menu(roomsTable);

		popUpMenu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e)
			{
			}
		});

		final MenuItem joinRoomItem = new MenuItem(popUpMenu, SWT.PUSH);
		joinRoomItem.setText("Join Room");
		joinRoomItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{


			}
		});
		
		return popUpMenu;
	}
	
	private void displayRoomDetailsTable(List<RoomInfo> roomList)
	{
		roomsDetailsTable = new Table(networkComposite, SWT.BORDER | SWT.FULL_SELECTION);
		TableColumn col1 = new TableColumn(roomsDetailsTable, SWT.CENTER);
		TableColumn col2 = new TableColumn(roomsDetailsTable, SWT.CENTER);
		col1.setText("User");
		col2.setText("Score");
		col1.setWidth(80);
		col2.setWidth(60);
		col1.setResizable(true);
		col2.setResizable(true);
		roomsDetailsTable.setSortColumn(col1);
		roomsDetailsTable.setHeaderVisible(true);
		roomsDetailsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		List<UserInfo> roomMembersList = gameController.getRoomsMemebrs(roomList.get(0).getId()).getResult();
		
		TableItem ti;
		Font newFont = roomsTable.getFont(); // just an initialization for the compiler

		for (int i = 0; i < roomMembersList.size(); i++)
		{
			ti = new TableItem(roomsDetailsTable, SWT.CENTER);

			UserInfo user = roomMembersList.get(0);		
			ti.setText(new String[] { user.getNickname(), user.getTotalScore()+"" });
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

		newFont.dispose();
	}
	
	private void updateRoomDetailsTable(RoomInfo roomInfo)
	{
		List<UserInfo> roomMembersList = gameController.getRoomsMemebrs(roomInfo.getId()).getResult();
		
		TableItem ti;
		Font newFont = roomsDetailsTable.getFont(); // just an initialization for the compiler

		roomsDetailsTable.removeAll();
		for (int i = 0; i < roomMembersList.size(); i++)
		{
			ti = new TableItem(roomsDetailsTable, SWT.CENTER);

			UserInfo user = roomMembersList.get(0);		
			ti.setText(new String[] { user.getNickname(), user.getTotalScore()+"" });
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

		newFont.dispose();
	}

	private void handleNewNetworkGame() {
		gameController.startNewNetworkGame();
		
	}


	private void handleRoomUpdated(RoomUpdateEvent e)
	{
		final RoomUpdateMessage message = e.getMessage();

		display.syncExec(new Runnable() {
			public void run()
			{
				int index;
				switch (message.getRoomEventType())
				{
				case MemberAdded:
					index = gameController.roomsList().indexOf(message.getRoomInfo());
					if (index >= 0 && index < roomsTable.getItemCount())
						roomsTable.getItem(index).setText(2, String.valueOf(message.getRoomInfo().getMemberCount()));
					if(roomsDetailsTable.getSelectionIndex() == index)
						updateRoomDetailsTable(message.getRoomInfo());
					break;
				case MemberRemoved:
					index = gameController.roomsList().indexOf(message.getRoomInfo());
					if (index >= 0 && index < roomsTable.getItemCount())
						roomsTable.getItem(index).setText(2, String.valueOf(message.getRoomInfo().getMemberCount()));
					if(roomsDetailsTable.getSelectionIndex() == index)
						updateRoomDetailsTable(message.getRoomInfo());
					break;
				case RoomOpened:
					break;
				case RoomClosed:
					break;
				}
			}
		});
	}

}