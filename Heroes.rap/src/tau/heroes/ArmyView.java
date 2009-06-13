/**
 * 
 */
package tau.heroes;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

/**
 * Presents an army
 * 
 * @author Amir
 */
public class ArmyView extends BaseLabelRowView
{
	private static int IMAGE_WIDTH = 50;
	private static int IMAGE_HEIGHT = 50;

	private Army army = null;

	/**
	 * @param parent
	 * @param style
	 *            - Creates the view under the parent
	 */
	public ArmyView(Composite parent, int style)
	{
		super(parent, style, IMAGE_WIDTH, IMAGE_HEIGHT, Army.MAX_CREATURES);
	}

	/**
	 * Gets the army shown in the view
	 * 
	 * @return Army shown in the view
	 */
	public Army getArmy()
	{
		return army;
	}

	/**
	 * Sets the army to show in the view
	 * 
	 * @param army
	 *            - instance of Army
	 */
	public void setArmy(Army army)
	{
		this.army = army;
		update();
	}

	/*
	 * * Updates the view
	 */
	public void update()
	{
//		super.update();

		if (this.army != null)
		{
			Creature[] creaturesArray = this.army.getCreatures();
			String text;
			String tooltip;
			Image image;
			for (int i = 0; i < Army.MAX_CREATURES; i++)
			{
				if (creaturesArray[i] != null)
				{
					text = String.valueOf(creaturesArray[i].get_numberOfUnits());
					tooltip = creaturesArray[i].toString();
				}
				else
				{
					text = "";
					tooltip = "Empty";
				}

				// Get the appropriate image
				// If not null, gets a creature's image, otherwise an empty one
				image = IconCache.getCreatureImage(creaturesArray[i]);
				// Call base to set the image
				setLabel(i, image, SWT.NONE, text, tooltip);
			}
		}
		else
			for (int i = 0; i < Army.MAX_CREATURES; i++)
				setLabel(i, IconCache.getCreatureImage((Creature) null), SWT.NONE, "", "Empty");
	}
}
