package heroes.rap;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import tau.heroes.GameController;
import tau.heroes.HeroesGui;
import tau.heroes.MainModule;

public class View extends ViewPart {
	public static final String ID = "Heroes.rap.view";
    public static HeroesGui GUI;
    public static Composite parent;


	//private TableViewer viewer;

	/**
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Task List, for
	 * example).
	 */
	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			return new String[] { "One", "Two", "Three" };
		}
	}

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	 public void createPartControl(Composite parent) {
         //TODO: here we should creat the game window.
         this.parent = parent;
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
	//	viewer.getControl().setFocus();
	}
}