package tau.heroes;

import java.util.List;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

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
		firstLabel.setText("     NETWORK MENU");

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
					HeroesGui.displayError("Error starting new game: " + result.getErrorMessage());
			}
		});

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
		InputDialog createNewRoomID = new InputDialog(display.getActiveShell(), "Create New Room",
			"Enter Room Name: ", "", null);

		if (createNewRoomID.open() == Window.OK)
		{
			String roomName = createNewRoomID.getValue();

			if (roomName.isEmpty())
			{
				HeroesGui.displayError("You must enter a room name");

				return;
			}
			createNewRoom(roomName);
		}
	}

	private void handleJoinRoomCommand(RoomInfo roomInfo)
	{
		NetworkResult<Boolean> result = gameController.joinRoom(roomInfo.getId());

		if (!result.getResult())
			HeroesGui.displayError("Error joining room: " + result.getErrorMessage());
	}

	public List<RoomInfo> getRoomsFromServer()
	{
		NetworkResult<List<RoomInfo>> result = gameController.getRoomsFromServer();

		if (result.getResult() == null)
		{
			HeroesGui.displayError("Error getting room list from server: "
				+ result.getErrorMessage());
		}

		return result.getResult();
	}

	public void createNewRoom(String roomName)
	{
		NetworkResult<Boolean> result = gameController.createRoom(roomName);

		if (!result.getResult())
			HeroesGui.displayError("Error creating room: " + result.getErrorMessage());
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

		List<UserInfo> roomMembersList = gameController.getRoomsMemebrs(roomList.get(0).getId())
			.getResult();

		TableItem ti;

		for (int i = 0; i < roomMembersList.size(); i++)
		{
			ti = new TableItem(roomsDetailsTable, SWT.CENTER);

			UserInfo user = roomMembersList.get(i);
			ti.setText(new String[] { user.getNickname(), user.getTotalScore() + "" });
		}
	}

	private void updateRoomDetailsTable(RoomInfo roomInfo)
	{
		List<UserInfo> roomMembersList = gameController.getRoomsMemebrs(roomInfo.getId())
			.getResult();

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
					if (index >= 0 && index < roomsTable.getItemCount())
						roomsTable.getItem(index).setText(2, String.valueOf(message.getRoomInfo()
							.getMemberCount()));
					if (message.getMember().equals(gameController.getUserInfo()))
						roomsTable.setSelection(index);
					if (roomsTable.getSelectionIndex() == index)
						updateRoomDetailsTable(message.getRoomInfo());
					break;
				case MemberRemoved:
					index = gameController.roomsList().indexOf(message.getRoomInfo());
					if (index >= 0 && index < roomsTable.getItemCount())
						roomsTable.getItem(index).setText(2, String.valueOf(message.getRoomInfo()
							.getMemberCount()));
					if (roomsTable.getSelectionIndex() == index)
						updateRoomDetailsTable(message.getRoomInfo());
					break;
				case RoomOpened:
					addRoomToTable(message.getRoomInfo());
					break;
				case RoomClosed:
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

}