package heroes.plugin.view;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;

import org.eclipse.ui.part.ViewPart;

import tau.heroes.GameController;
import tau.heroes.MainModule;
import tau.heroes.HeroesGui;

public class HeroesView extends ViewPart 
{
	
	public static HeroesGui GUI;
	

	public HeroesView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		//TODO: here we should creat the game window.
		GameController game = new GameController(true);
		GUI = MainModule.runGraphicalView(game, parent);
		if(GUI == null)
		{
			//exit!
		}
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
