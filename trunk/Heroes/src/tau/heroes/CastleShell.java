/**
 * 
 */
package tau.heroes;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Yuval & Shira
 * 
 */
public class CastleShell extends Shell
{
	private Castle castle;
	private Hero hero;

	private final Composite leftComposite;
	private final Label castleFactoriesLabel;
	private final CreatureFactoriesView castleFactoriesView;
	private final Label castleArmyLabel;
	private final ArmyView castleArmyView;
	private final Label heroArmyLabel;
	private final ArmyView heroArmyView;

	private final Composite rightComposite;
	private final ObjectInfoView objectInfoView;

	/**
	 * @param parent
	 * @param style
	 */
	public CastleShell(Shell parent, int style)
	{
		super(parent, style);

		setSize(600, 400);
		setLayout(new GridLayout(2, false));

		leftComposite = new Composite(this, SWT.BORDER);
		leftComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		leftComposite.setLayout(new GridLayout(1, true));
		{
			castleFactoriesLabel = new Label(leftComposite, SWT.CENTER);
			castleFactoriesLabel.setText("Castle's Factories");
			castleFactoriesLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

			castleFactoriesView = new CreatureFactoriesView(leftComposite, SWT.BORDER);
			castleFactoriesView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

			castleArmyLabel = new Label(leftComposite, SWT.CENTER);
			castleArmyLabel.setText("Castle's Army");
			castleArmyLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

			castleArmyView = new ArmyView(leftComposite, SWT.BORDER);
			castleArmyView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

			heroArmyLabel = new Label(leftComposite, SWT.CENTER);
			heroArmyLabel.setText("Hero is not in the castle");
			heroArmyLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

			heroArmyView = new ArmyView(leftComposite, SWT.BORDER);
			heroArmyView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			heroArmyView.setVisible(false);
		}

		rightComposite = new Composite(this, SWT.BORDER);
		rightComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		rightComposite.setLayout(new FillLayout());
		{
			objectInfoView = new ObjectInfoView(rightComposite, SWT.NONE);
		}
	}

	@Override
	protected void checkSubclass()
	{

	}

	public void setCastle(Castle castle)
	{
		this.castle = castle;

		castleFactoriesView.setFactories(castle.getFactories());
		castleArmyView.setArmy(castle.getArmy());

		for (int i = 0; i < castleFactoriesView.getLength(); i++)
		{
			castleFactoriesView.addMouseTrackListener(i, new CreatureFactoryMouseTrackListener(i));
			castleFactoriesView.addMouseListener(i, new CreatureFactoryMouseListener(i));
		}
	}

	public void setHero(Hero hero)
	{
		this.hero = hero;

		heroArmyLabel.setText("Hero's army");
		heroArmyView.setArmy(hero.getArmy());
		heroArmyView.setVisible(true);
	}

	private class CreatureFactoryMouseTrackListener extends MouseTrackAdapter
	{
		final int index;

		public CreatureFactoryMouseTrackListener(int index)
		{
			this.index = index;
		}

		@Override
		public void mouseEnter(MouseEvent e)
		{
			Class<? extends CreatureFactory> factoryClass = CreatureFactory
				.getCreatureFactoryClasses().get(index);

			CreatureFactory factory;
			String message;

			if (castle.hasFactory(factoryClass))
			{
				factory = castle.getFactory(factoryClass);
				message = "Double-Click factory image to make soldiers";
			}
			else
			{
				factory = CreatureFactory.createCreatureFactory(factoryClass);
				if (castle.canBuildFactory(factoryClass))
					message = "Double-Click factory image to build it";
				else
					message = "You don't have enough resources yet to build this factory";
			}

			objectInfoView.setImage(IconCache.getCreatureFactoryImage(factoryClass));
			objectInfoView.setTitle(factory.getName());
			objectInfoView.setPrices(factory.getPrices());
			objectInfoView.setMessage(message);
		}
	}

	private class CreatureFactoryMouseListener extends MouseAdapter
	{
		final int index;

		public CreatureFactoryMouseListener(int index)
		{
			this.index = index;
		}

		@Override
		public void mouseDoubleClick(MouseEvent e)
		{
			Class<? extends CreatureFactory> factoryClass = CreatureFactory
				.getCreatureFactoryClasses().get(index);

			CreatureFactory factory;
			if (castle.hasFactory(factoryClass))
			{
				factory = castle.getFactory(factoryClass);
			}
			else if (castle.canBuildFactory(factoryClass))
			{
				factory = castle.buildFactory(factoryClass);
				castle.addFactory(factory);
				castleFactoriesView.update();
			}
		}
	}
}
