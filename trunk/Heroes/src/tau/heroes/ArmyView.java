/**
 * 
 */
package tau.heroes;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Presents an army
 * 
 * @author Amir
 */
public class ArmyView extends Composite
{
	private static int IMAGE_WIDTH = 50;
	private static int IMAGE_HEIGHT = 50;

	private static IconCache iconCache = new IconCache();

	private Army army = null;
	private CLabel[] creatureLabels;

	/**
	 * @param parent
	 * @param style
	 *            - Creates the view under the parent
	 */
	public ArmyView(Composite parent, int style)
	{
		super(parent, style);
		this.setLayout(new RowLayout());

		creatureLabels = new CLabel[Army.MAX_CREATURES];
		Color foreColor = parent.getDisplay().getSystemColor(SWT.COLOR_WHITE);

		for (int i = 0; i < Army.MAX_CREATURES; i++)
		{
			creatureLabels[i] = new CLabel(this, SWT.RIGHT);
			creatureLabels[i].setLayoutData(new RowData(IMAGE_WIDTH, IMAGE_HEIGHT));
			creatureLabels[i].setForeground(foreColor);
			creatureLabels[i].setFont(new Font(getDisplay(), "Tahoma", 10, SWT.BOLD));
		}

		iconCache.initResources(parent.getDisplay());
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

	@Override
	/*
	 * * Updates the view
	 */
	public void update()
	{
		super.update();

		if (this.army != null)
		{
			Creature[] creaturesArray = this.army.getCreatures();
			for (int i = 0; i < Army.MAX_CREATURES; i++)
			{
				if (creaturesArray[i] != null)
				{
					creatureLabels[i]
						.setText(String.valueOf(creaturesArray[i].get_numberOfUnits()));
					creatureLabels[i].setToolTipText(creaturesArray[i].toString());
				}
				else
				{
					creatureLabels[i].setText("");
					creatureLabels[i].setToolTipText("Empty");
				}

				// Get the appropriate image
				// If not null, gets a creature's image, otherwise an empty one
				Image img = iconCache.getCreatureImage(creaturesArray[i]);
				// Scale the image to fit nicely into the label
				img = new Image(getDisplay(), img.getImageData()
					.scaledTo(IMAGE_WIDTH, IMAGE_HEIGHT));
				creatureLabels[i].setBackground(img);
			}
		}
	}
}
