package tau.heroes;

import java.util.List;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
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
	private Shell shell;
	private Color white;
	private Display display;
	private Table roomsTable, roomsDetailsTable;
	private GameController gameController;
	private RoomUpdateListener roomUpdateListener;
	private Label roomNameLabel;
	
	

	public NetworkGUI(Composite statusComposite, GameController gameController)
	{
		this.networkComposite = new Composite(statusComposite, SWT.NONE);
		this.gameController = gameController;
		display = networkComposite.getDisplay();
		shell = statusComposite.getShell();

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
		firstLabel.setText("     NETWORK MENU");

		roomNameLabel = new Label(networkComposite, SWT.CENTER | SWT.BORDER);
		roomNameLabel.setFont(IconCache.stockFonts[IconCache.titleFontIndex]);
		roomNameLabel.setText("Lobby");
		roomNameLabel.setBackground(networkComposite.getBackground());
		roomNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

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

		createLabel("");
		Button newGameButton = new Button(networkComposite, SWT.CENTER);
		newGameButton.setText("Create  New  Network Game ");
		newGameButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		newGameButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				NetworkResult<Boolean> result = handleNewNetworkGame();

				if (!result.getResult())
					HeroesGui.displayError(shell,"Error starting new game: " + result.getErrorMessage());
			}
		});
		
		
		
		createLabel(networkComposite, "");
		createLabel(networkComposite, "Enter your Chat Message : ");
		final Text chatText = new Text(networkComposite, SWT.BORDER);
		chatText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		Button chatButton = new Button(networkComposite, SWT.CENTER);
		chatButton.setText("Send Chat Message");
		chatButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		chatButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e)
			{
				String t = chatText.getText();
				if (t != null)
				{
					gameController.sendChat(t);
				}
			}
		});
		
		
		ScrolledComposite chatSc = new ScrolledComposite(networkComposite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		chatSc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Composite chatComposite = new Composite(chatSc, SWT.FILL);
		chatComposite = new Composite(chatSc, SWT.BORDER);
		//chatComposite.setBackground(white);
		GridData d1 = new GridData(GridData.FILL_BOTH);
		chatComposite.setLayoutData(d1);

		GridLayout tempLayout = new GridLayout(1, true);
		chatComposite.setLayout(tempLayout);
		chatSc.setExpandHorizontal(true);
		chatSc.setExpandVertical(true);
		Rectangle r = chatSc.getClientArea();
		chatSc.setMinSize(chatComposite.computeSize(r.width, SWT.DEFAULT));

		for (int i = 0; i<20; ++i)
		{
			if (gameController.chatsArray[i] != null)
			{
				createLabel(chatComposite, gameController.chatsArray[i]);
			}
		}
		chatSc.setContent(chatComposite);
		
		
		

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
		
		networkComposite.getParent().layout();
	}

	private void handleNewRoomCommand()
	{
		InputDialog createNewRoomID = new InputDialog(display.getActiveShell(), "Create New Room",
			"Enter Room Name: ", "", null);

		if (createNewRoomID.open() == Window.OK)
		{
			String roomName = createNewRoomID.getValue();

			if (roomName.trim().length()==0)
			{
				HeroesGui.displayError(shell,"You must enter a room name");

				return;
			}
			createNewRoom(roomName);
		}
	}

	private void handleJoinRoomCommand(RoomInfo roomInfo)
	{
		if (roomInfo.getMemberCount() == 4)
		{
			HeroesGui.displayError(shell,"The room selected is full, please choose another with less than 4 players");
			
			return;
		}
		
		NetworkResult<Boolean> result = gameController.joinRoom(roomInfo.getId());

		if (!result.getResult())
			HeroesGui.displayError(shell,"Error joining room: " + result.getErrorMessage());
	}

	public List<RoomInfo> getRoomsFromServer()
	{
		NetworkResult<List<RoomInfo>> result = gameController.getRoomsFromServer();

		if (result.getResult() == null)
		{
			HeroesGui.displayError(shell,"Error getting room list from server: "
				+ result.getErrorMessage());
		}

		return result.getResult();
	}

	public void createNewRoom(String roomName)
	{
		NetworkResult<Boolean> result = gameController.createRoom(roomName);

		if (!result.getResult())
			HeroesGui.displayError(shell,"Error creating room: " + result.getErrorMessage());
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
		roomsTable.setToolTipText("Double click to join room");
		roomsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		roomsTable.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event)
			{
				updateRoomDetailsTable((RoomInfo) event.item.getData());
			}

			public void widgetDefaultSelected(SelectionEvent event)
			{
				handleJoinRoomCommand((RoomInfo) event.item.getData());
			}
		});

		for (int i = 0; i < roomList.size(); i++)
		{
			RoomInfo roomInfo = roomList.get(i);
			addRoomToTable(roomInfo);
		}

		roomsTable.setSelection(0);
	}

	private void addRoomToTable(RoomInfo roomInfo)
	{
		TableItem ti = new TableItem(roomsTable, SWT.CENTER);
		ti.setText(new String[] { roomInfo.getName(), roomInfo.getOwner().getNickname(),
				String.valueOf(roomInfo.getMemberCount()) });
		ti.setData(roomInfo);
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

		updateRoomDetailsTable(roomList.get(0));
	}

	private void updateRoomDetailsTable(RoomInfo roomInfo)
	{
		NetworkResult<List<UserInfo>> result = gameController.getRoomsMemebrs(roomInfo.getId());

		List<UserInfo> roomMembersList = result.getResult();

		if (roomMembersList == null)
		{
			HeroesGui.displayError(shell,"Error getting members: " + result.getErrorMessage());
			return;
		}
		
		TableItem ti;

		roomsDetailsTable.removeAll();
		for (int i = 0; i < roomMembersList.size(); i++)
		{
			ti = new TableItem(roomsDetailsTable, SWT.CENTER);

			UserInfo user = roomMembersList.get(i);
			ti.setText(new String[] { user.getNickname(), user.getTotalScore() + "" });
		}
	}

	private NetworkResult<Boolean> handleNewNetworkGame()
	{
		return gameController.startNewNetworkGame();
	}

	private void handleRoomUpdated(RoomUpdateEvent e)
	{
		final RoomUpdateMessage message = e.getMessage();

		display.asyncExec(new Runnable() {
			public void run()
			{
				int index;
				switch (message.getRoomEventType())
				{
				case MemberAdded:
					index = gameController.roomsList().indexOf(message.getRoomInfo());
					if (index >= 0 && roomsTable != null && index < roomsTable.getItemCount())
						roomsTable.getItem(index).setText(2, String.valueOf(message.getRoomInfo()
							.getMemberCount()));
					if (roomsTable != null && message.getMember().equals(gameController.getUserInfo()))
					{
						roomsTable.setSelection(index);
						roomNameLabel.setText(message.getRoomInfo().getName());
					}
					if ( roomsTable != null && roomsTable.getSelectionIndex() == index)
						updateRoomDetailsTable(message.getRoomInfo());
					break;
				case MemberRemoved:
					index = gameController.roomsList().indexOf(message.getRoomInfo());
					if (index >= 0 && roomsTable != null && index < roomsTable.getItemCount())
						roomsTable.getItem(index).setText(2, String.valueOf(message.getRoomInfo()
							.getMemberCount()));
					if (roomsTable != null && roomsTable.getSelectionIndex() == index)
						updateRoomDetailsTable(message.getRoomInfo());
					break;
				case RoomOpened:
					addRoomToTable(message.getRoomInfo());
					break;
				case RoomClosed:
					if(roomsTable == null)
						break;
					for (TableItem ti : roomsTable.getItems())
						if (message.getRoomInfo().equals(ti.getData()))
						{
							ti.dispose();
							break;
						}
					break;
				}
			}
		});
	}
	
	private Label createLabel(Composite composite, String text)
	{
		Label tempLabel = new Label(composite, SWT.NONE);
		tempLabel.setText(text);
		tempLabel.setBackground(white);
		return tempLabel;
	}

}