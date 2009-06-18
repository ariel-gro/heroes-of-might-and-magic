package tau.heroes;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import tau.heroes.net.ChatMessage;
import tau.heroes.net.HeroesClientPeer;
import tau.heroes.net.Message;


public class Chat 
{
	public static void sendChat(String message, HeroesClientPeer hcp)
	{
		ChatMessage chatMessage = new ChatMessage(message);
		hcp.asyncSendMessage(chatMessage);
	}
	
	public static void recieveChat(Message message)
	{
		
		ChatMessage msg = (ChatMessage) message;
		final Shell shell = new Shell(Display.getCurrent().getActiveShell());
		Display display = shell.getDisplay();
		
		shell.setImage(IconCache.stockImages[IconCache.appIconSmall]);
		shell.setText("Chat Messsage");
		Label label = new Label(shell, SWT.FILL);
		label.setText(msg.getText());
		
		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
}