package heroes.plugin;

import heroes.plugin.view.HeroesView;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class PerspectiveFactory2 implements IPerspectiveFactory 
{
	public void createInitialLayout(IPageLayout layout) 
	{
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);	
		layout.addStandaloneView(HeroesView.ID ,  false, IPageLayout.LEFT, 1.0f, editorArea);
	}

}
