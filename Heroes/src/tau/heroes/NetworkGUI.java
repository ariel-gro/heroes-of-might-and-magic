package tau.heroes;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import tau.heroes.net.NetworkResult;
import tau.heroes.net.RoomInfo;

public class NetworkGUI
{
	private Composite networkComposite;
	private Color white;
	private Display display;
	private Combo roomsCombo, numOfPlayersCombo;
	private GameController gameController;

	public NetworkGUI(Composite statusComposite, GameController gameController)
	{
		networkComposite = statusComposite;
		this.gameController = gameController;
		display = networkComposite.getDisplay();

		white = display.getSystemColor(SWT.COLOR_WHITE);

		networkComposite.setBackground(white);
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

		createLabel("");
		createLabel("Existing Rooms :");
		List<RoomInfo> roomList = getRoomsFromServer();
		if (roomList != null)
		{
			displayTable(roomList);
		}
		networkComposite.layout(true, true);
	}

	private void handleNewRoomCommand()
	{
		roomsCombo.setEnabled(false);
		final Shell shell = new Shell(Display.getCurrent().getActiveShell());
		shell.setLayout(new GridLayout());
		shell.setSize(330, 225);
		shell.setText("Create New Room");
		shell.setImage(IconCache.stockImages[IconCache.appIcon]);

		Composite form = new Composite(shell, SWT.FILL);
		form.setLayout(new GridLayout(2, false));

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
		Label isComputerLabel = new Label(form, SWT.NONE);
		isComputerLabel.setText("Is one of the players a Computer ? ");
		final Button pcButton = new Button(form, SWT.CHECK);

		new Label(form, SWT.NONE);
		new Label(form, SWT.NONE);
		new Label(form, SWT.NONE);
		new Label(form, SWT.NONE);

		Button okButton = new Button(form, SWT.PUSH);
		okButton.setText("     OK     ");
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e)
			{
				String roomName = roomNameText.getText();
				Integer numOfPlayers = Integer.parseInt(numOfPlayersCombo.getText());
				boolean isComputer = pcButton.getSelection();
				if (!roomName.equalsIgnoreCase(""))
				{
					createNewRoom(roomName, numOfPlayers, isComputer);
					roomsCombo.setEnabled(true);
					shell.dispose();
				}
			}
		});

		Button cancelButton = new Button(form, SWT.PUSH);
		cancelButton.setText("     Cancel     ");
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e)
			{
				roomsCombo.setEnabled(true);
				shell.dispose();
			}
		});

		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}

		roomsCombo.setEnabled(true);
	}

	private void handleJoinRoomCommand()
	{
		String roomName = roomsCombo.getText();
		joinExistingRoom(roomName);
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

	public void createNewRoom(String roomName, int numOfPlayers, boolean isComputer)
	{

	}

	public void joinExistingRoom(String roomName)
	{
	}

	// TODO adjust to lobby
	private void displayTable(List<RoomInfo> roomList)
	{
		Table roomsTable = new Table(networkComposite, SWT.BORDER);
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
		roomsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		TableItem ti;
		Font newFont = roomsTable.getFont(); // just an initialization for the
		// compiler

		for (int i = 0; i < roomList.size(); i++)
		{
			ti = new TableItem(roomsTable, SWT.CENTER);

			RoomInfo room = roomList.get(i);
			ti.setText(new String[] { room.getName(), room.getOwner().getNickname(),
					String.valueOf(room.getMemberCount()) });
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
}