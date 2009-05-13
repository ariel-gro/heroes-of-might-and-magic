/**
 * 
 */
package tau.heroes;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

/**
 * Presents a mines table
 * 
 * @author Amir
 */
public class MinesView extends BaseLabelRowView
{
	private static int IMAGE_WIDTH = 50;
	private static int IMAGE_HEIGHT = 50;

	private Map<String, Integer> mines;

	/**
	 * @param parent
	 * @param style
	 */
	public MinesView(Composite parent, int style)
	{
		super(parent, style, IMAGE_WIDTH, IMAGE_HEIGHT, ResourceType.values().length);
	}

	public void setMines(Map<String, Integer> mines)
	{
		this.mines = mines;
		update();
	}

	@Override
	public void update()
	{
		super.update();

		if (mines != null)
		{
			String text;
			String tooltip;
			Image image;
			int index = 0;
			for (ResourceType rType : ResourceType.values())
			{
				text = mines.get(rType.getTypeName()).toString();
				tooltip = rType.getTypeName() + " Mine";
				image = IconCache.getMineImage(rType.getTypeName());
				setLabel(index, image, SWT.NONE, text, tooltip);
				index++;
			}
		}
	}
}
