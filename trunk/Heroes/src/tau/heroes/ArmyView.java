/**
 * 
 */
package tau.heroes;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author Amir
 * 
 */
public class ArmyView extends Composite
{
	private Army				army	= null;

	private final Table			armyTable;
	private final TableColumn	armyCol1;
	private final TableColumn	armyCol2;

	/**
	 * @param parent
	 * @param style
	 */
	public ArmyView(Composite parent, int style)
	{
		super(parent, style);
		this.setLayout(new FillLayout());

		armyTable = new Table(this, SWT.NONE);
		armyCol1 = new TableColumn(armyTable, SWT.CENTER);
		armyCol2 = new TableColumn(armyTable, SWT.CENTER);

		armyCol1.setText("Slot");
		armyCol2.setText("Units");

		armyTable.setHeaderVisible(true);
		armyTable.setItemCount(5);

		armyCol1.setWidth(40);
		armyCol2.setWidth(this.getSize().x - 50);

		this.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e)
			{
				armyCol2.setWidth(((Composite) e.getSource()).getSize().x - 50);
			}
		});
	}

	public void setArmy(Army army)
	{
		this.army = army;
		update();
	}

	@Override
	public void update()
	{
		super.update();

		if (this.army != null)
		{
			armyTable.removeAll();
			Creature[] creaturesArray = this.army.getCreatures();
			TableItem ti;
			for (Integer j = 1; j < Army.MAX_CREATURES + 1; j++)
			{
				ti = new TableItem(armyTable, SWT.NONE);
				ti.setText(0, j.toString());
				if (creaturesArray[j - 1] != null)
				{
					ti.setText(1, creaturesArray[j - 1].toString());
				}
				else
				{
					ti.setText(1, "none");
				}
			}
		}
	}
}
