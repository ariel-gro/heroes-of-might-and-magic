package tau.heroes.rcp;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import tau.heroes.GameController;
import tau.heroes.HeroesGui;
import tau.heroes.MainModule;


public class View extends ViewPart 
{
	public static final String ID = "Heroes.rcp.view";
	public static HeroesGui GUI;
	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		//TODO: here we should creat the game window.
		GameController game = new GameController(true);
		GUI = MainModule.runGraphicalView(game, parent);
		if(GUI == null)
		{
			//exit!
		}
	}
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		
	}
}