/**
 * 
 */
package tau.heroes;

import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Yuval & Shira
 *
 */
public class ResourcesView extends BaseLabelRowView
{
	private static int IMAGE_WIDTH = 50;
	private static int IMAGE_HEIGHT = 50;

	private Map<String, Integer> resources;
	
	/**
	 * @param parent
	 * @param style
	 */
	public ResourcesView(Composite parent, int style)
	{
		super(parent, style, IMAGE_WIDTH, IMAGE_HEIGHT, ResourceType.values().length);
	}

	public void setResources(Map<String, Integer> resources)
	{
		this.resources = resources;
		update();
	}

	@Override
	public void update()
	{
		super.update();

		if (resources != null)
		{
			String text;
			String tooltip;
			Image image;
			int index = 0;
			for (ResourceType rType : ResourceType.values())
			{
				text = resources.get(rType.getTypeName()).toString();
				tooltip = rType.getTypeName() + ": " + text;
				image = IconCache.getResourceImage(rType.getTypeName());
				setLabel(index, image, text, tooltip);
				index++;
			}
		}
	}
}
