package heroes.plugin.actions;

import heroes.plugin.view.HeroesView;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import org.eclipse.swt.widgets.Composite;

import tau.heroes.GameController;
import tau.heroes.HeroesGui;
import tau.heroes.MainModule;

public class ExitGameAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		this.window = window;
	}

	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		//IWorkbenchAction exit = ActionFactory.QUIT.create(window);
		//exit.run();
	
		HeroesView.GUI.initBlankGame();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

}
