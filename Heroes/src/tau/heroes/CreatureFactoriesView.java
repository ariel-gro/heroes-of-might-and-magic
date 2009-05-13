/**
 * 
 */
package tau.heroes;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Yuval & Shira
 * 
 */
public class CreatureFactoriesView extends BaseLabelRowView
{
	private static int IMAGE_WIDTH = 50;
	private static int IMAGE_HEIGHT = 50;

	private List<CreatureFactory> factories;

	/**
	 * @param parent
	 * @param style
	 */
	public CreatureFactoriesView(Composite parent, int style)
	{
		super(parent, style, IMAGE_WIDTH, IMAGE_HEIGHT, CreatureFactory.getCreatureFactoryClasses()
			.size());
	}

	public void setFactories(List<CreatureFactory> factories)
	{
		this.factories = factories;
		update();
	}

	@Override
	public void update()
	{
		super.update();

		if (factories != null)
		{
			int index = 0;
			for (Class<? extends CreatureFactory> factoryClass : CreatureFactory
				.getCreatureFactoryClasses())
			{
				CreatureFactory foundFactory = null;

				// Search for a factory of class factoryClass
				for (CreatureFactory factory : this.factories)
					if (factory.getClass().equals(factoryClass))
					{
						foundFactory = factory;
						break;
					}

				String text = "";
				String tooltip = CreatureFactory.createCreatureFactory(factoryClass).getName();
				Image image = IconCache.getCreatureFactoryImage(factoryClass);
				int style = SWT.NONE;

				if (foundFactory == null)
					style = SWT.IMAGE_GRAY;

				setLabel(index, image, style, text, tooltip);
				index++;
			}
		}
	}
}
