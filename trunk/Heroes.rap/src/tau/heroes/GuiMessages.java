package tau.heroes;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class GuiMessages
{
	public static void displayErrorMsg(String title, String error, Shell shell)
	{
		MessageDialog msg = new MessageDialog(shell,title,null,error,MessageDialog.ERROR,new String[] {"OK"},1);
		msg.open();
    }
	        
	public static void displayFeedbackMsg(String title, String feedback, Shell shell)
	{
		MessageDialog msg = new MessageDialog(shell,title,null,feedback,MessageDialog.INFORMATION,new String[] {"OK"},1);
		msg.open();
	}
	
	public static String displayInputDialog(String title, String text, Shell shell)
	{
		InputDialog msg = new InputDialog(shell, title, text, "Saved Game 1", null);
		msg.open();
		return msg.getValue();
	}
	
	
}