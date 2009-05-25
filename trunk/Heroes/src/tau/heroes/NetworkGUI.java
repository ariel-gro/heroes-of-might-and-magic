package tau.heroes;



import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tau.heroes.net.Room;



public class NetworkGUI 
{
	private Composite networkComposite;
	private Color black, white, red; 
	private Display display;
	private Combo roomsCombo, numOfPlayersCombo;
	
	
	public NetworkGUI(Composite statusComposite)
	{
		networkComposite = statusComposite;
		display = networkComposite.getDisplay();
		
		black = display.getSystemColor(SWT.COLOR_BLACK);
		white = display.getSystemColor(SWT.COLOR_WHITE);
		red = display.getSystemColor(SWT.COLOR_RED);
		
		networkComposite.setBackground(white);
	}
	
	private Label createLabel(String text)
	{
		Label tempLabel = new Label(networkComposite, SWT.NONE);
		tempLabel.setText(text);
		tempLabel.setBackground(white);
		return tempLabel;
	}
	
	public void  init()
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
		roomsCombo = new Combo(networkComposite, SWT.NONE);	
		String[] roomsArray = getRoomsFromServer();
		roomsCombo.setItems(roomsArray);
		roomsCombo.setText(roomsArray[0]);
	
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
			}});
		
		
		Button cancelButton = new Button(form, SWT.PUSH);
		cancelButton.setText("     Cancel     ");
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e)
			{
				roomsCombo.setEnabled(true);
				shell.dispose();
			}});
		
	
		
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
	

	public String[] getRoomsFromServer()
	{
		String[] replay = {"room 1", "room2"};
		
		return replay;
	}
	
	
	public void createNewRoom(String roomName, int numOfPlayers, boolean isComputer)
	{
		Room room = new Room(roomName);		
	}
	
	
	public void joinExistingRoom(String roomName)
	{	
	}	
}