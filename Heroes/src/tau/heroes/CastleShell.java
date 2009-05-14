/**
 * 
 */
package tau.heroes;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Yuval & Shira
 * 
 */
public class CastleShell extends Shell
{
	private HeroesGui heroesGui;
	private Castle castle;
	private Hero hero;
	private Button createNewHeroButton;

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
	public CastleShell(HeroesGui heroesGui, Shell parent, int style)
	{
		super(parent, style);
		this.setImage(IconCache.stockImages[IconCache.appIcon]);
		this.heroesGui = heroesGui;

		setSize(635, 400);
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

			Button castleHelpButton = new Button(leftComposite, SWT.PUSH | SWT.CENTER);
			castleHelpButton.setText("About This Window");
			castleHelpButton.setToolTipText("Displays help about this window");
			castleHelpButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					HeroesGui.diplayHelpByHelpItem(GameStringsHelper.CastleWindow);
				}
			});

			createNewHeroButton = new Button(leftComposite, SWT.PUSH | SWT.CENTER);
			createNewHeroButton.setText("Create Hero (" + Constants.HERO_PRICE_GOLD + " Gold)");
			createNewHeroButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e)
				{
					handleNewHeroButtonPress();
				}
			});
		}

		rightComposite = new Composite(this, SWT.BORDER);
		rightComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		rightComposite.setLayout(new FillLayout());
		{
			objectInfoView = new ObjectInfoView(rightComposite, SWT.NONE);
			objectInfoView.setVisible(false);
		}
	}

	@Override
	protected void checkSubclass()
	{

	}

	public void setCastle(Castle castle)
	{
		this.castle = castle;
		setText(castle.printLocation());
		
		initNewHeroButton();

		castleFactoriesView.setFactories(castle.getFactories());
		castleArmyView.setArmy(castle.getArmy());

		for (int i = 0; i < castleFactoriesView.getLength(); i++)
		{
			CreatureFactoryListener listener = new CreatureFactoryListener(i);
			castleFactoriesView.addMouseTrackListener(i, listener);
			castleFactoriesView.addMouseListener(i, listener);
		}

		for (int i = 0; i < castleArmyView.getLength(); i++)
		{
			castleArmyView.addMouseTrackListener(i, new CastleCreatureMouseListener(i));
			castleArmyView.addMouseListener(i, new CastleCreatureMouseListener(i));
		}
	}

	public void setHero(Hero hero)
	{
		this.hero = hero;

		heroArmyLabel.setText("Hero's army");
		heroArmyView.setArmy(this.hero.getArmy());
		heroArmyView.setVisible(true);

		for (int i = 0; i < heroArmyView.getLength(); i++)
		{
			heroArmyView.addMouseTrackListener(i, new HeroCreatureMouseListener(i));
			heroArmyView.addMouseListener(i, new HeroCreatureMouseListener(i));
		}
	}

	private void handleNewHeroButtonPress()
	{
		Hero h = castle.CreateNewHero();
		if (h != null)
		{
			setHero(h);
			createNewHeroButton.setEnabled(false);
			heroesGui.updateStatusWindow();
		}
	}

	private void initNewHeroButton()
	{
		String tooltip = this.castle.CanCreateNewHero() ? "Will create a new Hero for the cost of "
			+ Constants.HERO_PRICE_GOLD + " Gold"
			: "You have a hero [or you don't have enough Gold (" + Constants.HERO_PRICE_GOLD + ")]";
		createNewHeroButton.setToolTipText(tooltip);
		createNewHeroButton.setEnabled(this.castle.CanCreateNewHero());
	}

	private void updateMainGUIResourcesView()
	{
		if (heroesGui.resourcesView != null)
			heroesGui.resourcesView.update();
	}

	private void updateMainGUIArmyView()
	{
		if (heroesGui.armyView != null)
			heroesGui.armyView.update();
	}

	private class CreatureFactoryListener implements MouseTrackListener, MouseListener
	{
		final int index;

		public CreatureFactoryListener(int index)
		{
			this.index = index;
		}

		public void mouseEnter(MouseEvent e)
		{
			Class<? extends CreatureFactory> factoryClass = CreatureFactory
				.getCreatureFactoryClasses().get(index);

			CreatureFactory factory = null;
			String message = null;
			String message2 = null;
			Map<String, Integer> prices = null;

			if (castle.hasFactory(factoryClass))
			{
				factory = castle.getFactory(factoryClass);
				prices = factory.getPricesPerUnit();
				int unitsAvailable = castle.getAvailableUnits(factory.getCreatureClass());
				message = "Factory creatures available: " + factory.getUnitsAvailableToBuild();
				if (unitsAvailable > 0)
					message2 = "Double-Click factory image to make creatures";
				else
					message2 = "You don't have enough resources yet to make creatures";
			}
			else
			{
				factory = CreatureFactory.createCreatureFactory(factoryClass);
				prices = factory.getPrices();
				if (castle.canBuildFactory(factoryClass))
					message = "Double-Click factory image to build it";
				else
					message = "You don't have enough resources yet to build this factory";
			}

			objectInfoView.setImage(IconCache.getCreatureFactoryImage(factoryClass));
			objectInfoView.setTitle(factory.getName());
			objectInfoView.setPrices(prices);
			objectInfoView.setMessage(message);
			objectInfoView.setMessage2(message2);
			objectInfoView.setVisible(true);
		}

		public void mouseExit(MouseEvent arg0)
		{
			objectInfoView.setVisible(false);
		}

		public void mouseHover(MouseEvent arg0)
		{
		}

		public void mouseDoubleClick(MouseEvent e)
		{
			Class<? extends CreatureFactory> factoryClass = CreatureFactory
				.getCreatureFactoryClasses().get(index);

			CreatureFactory factory;
			if (castle.hasFactory(factoryClass))
			{
				factory = castle.getFactory(factoryClass);
				if (castle.getAvailableUnits(factory.getCreatureClass()) > 0)
				{
					castle.makeUnits(factory.getCreatureClass(), 1);
					if (castleArmyView.getArmy() == null)
						castleArmyView.setArmy(castle.getArmy());
					castleArmyView.update();
					updateMainGUIResourcesView();
					this.mouseEnter(e);
				}
			}
			else if (castle.canBuildFactory(factoryClass))
			{
				factory = castle.buildFactory(factoryClass);
				castle.addFactory(factory);
				castleFactoriesView.update();
				updateMainGUIResourcesView();
				this.mouseEnter(e);
			}
		}

		public void mouseDown(MouseEvent arg0)
		{
		}

		public void mouseUp(MouseEvent arg0)
		{
		}
	}

	private class CastleCreatureMouseListener implements MouseTrackListener, MouseListener
	{
		final int index;

		public CastleCreatureMouseListener(int index)
		{
			this.index = index;
		}

		public void mouseEnter(MouseEvent e)
		{
			if (castle != null && castle.getArmy() != null)
			{
				Creature originalCreature = castle.getArmy().getCreature(index);
				if (originalCreature != null)
				{
					Class<? extends Creature> creatureClass = originalCreature.getClass();
					Class<? extends CreatureFactory> factoryClass = CreatureFactory
						.getCreatureFactoryClass(creatureClass);
					CreatureFactory factory = CreatureFactory.createCreatureFactory(factoryClass);
					Creature creature = factory.buildCreature(1);
					Map<String, Integer> prices = factory.getPricesPerUnit();
					String message = null;
					String message2 = null;

					if (hero == null)
					{
						message = "Hero is not in the castle.";
						message2 = "Move hero into the castle in order to split or join armies.";
					}
					else if (castle.canRemoveFromArmy(creature) && hero.canAddToArmy(creatureClass))
					{
						message = "Double-Click creature image to add it to the hero's army.";
					}
					else
						message = "You can't add creatures to the hero's army.";

					objectInfoView.setImage(IconCache.getCreatureImage(creatureClass));
					objectInfoView.setTitle(creature.get_name());
					objectInfoView.setPrices(prices);
					objectInfoView.setMessage(message);
					objectInfoView.setMessage2(message2);
					objectInfoView.setVisible(true);
				}
				else
					this.mouseExit(e);
			}
			else
				mouseExit(e);
		}

		public void mouseExit(MouseEvent e)
		{
			objectInfoView.setVisible(false);
		}

		public void mouseHover(MouseEvent e)
		{
		}

		public void mouseDoubleClick(MouseEvent e)
		{
			if (castle != null && hero != null && castle.getArmy() != null)
			{
				Creature originalCreature = castle.getArmy().getCreature(index);
				if (originalCreature != null)
				{
					Class<? extends Creature> creatureClass = originalCreature.getClass();
					Class<? extends CreatureFactory> factoryClass = CreatureFactory
						.getCreatureFactoryClass(creatureClass);
					CreatureFactory factory = CreatureFactory.createCreatureFactory(factoryClass);
					Creature creature = factory.buildCreature(1);
					if (castle.canRemoveFromArmy(creature) && hero.canAddToArmy(creatureClass))
					{
						castle.removeFromArmy(creature);
						hero.addToArmy(creature);
						castleArmyView.update();
						heroArmyView.update();
						updateMainGUIArmyView();
						this.mouseEnter(e);
					}
				}
			}
		}

		public void mouseDown(MouseEvent e)
		{
		}

		public void mouseUp(MouseEvent e)
		{
		}
	}

	private class HeroCreatureMouseListener implements MouseTrackListener, MouseListener
	{
		final int index;

		public HeroCreatureMouseListener(int index)
		{
			this.index = index;
		}

		public void mouseEnter(MouseEvent e)
		{
			if (hero != null && castle != null)
			{
				Creature originalCreature = hero.getArmy().getCreature(index);
				if (originalCreature != null)
				{
					Class<? extends Creature> creatureClass = originalCreature.getClass();
					Class<? extends CreatureFactory> factoryClass = CreatureFactory
						.getCreatureFactoryClass(creatureClass);
					CreatureFactory factory = CreatureFactory.createCreatureFactory(factoryClass);
					Creature creature = factory.buildCreature(1);
					Map<String, Integer> prices = factory.getPricesPerUnit();
					String message = null;
					String message2 = null;

					if (hero.canRemoveFromArmy(creature) && castle.canAddToArmy(creatureClass))
					{
						message = "Double-Click creature image to add it to the castle's army.";
					}
					else
						message = "You can't add creatures to the castle's army.";

					objectInfoView.setImage(IconCache.getCreatureImage(creatureClass));
					objectInfoView.setTitle(creature.get_name());
					objectInfoView.setPrices(prices);
					objectInfoView.setMessage(message);
					objectInfoView.setMessage2(message2);
					objectInfoView.setVisible(true);
				}
				else
					this.mouseExit(e);
			}
			else
				this.mouseExit(e);
		}

		public void mouseExit(MouseEvent e)
		{
			objectInfoView.setVisible(false);
		}

		public void mouseHover(MouseEvent e)
		{
		}

		public void mouseDoubleClick(MouseEvent e)
		{
			if (castle != null && hero != null && hero.getArmy() != null)
			{
				Creature originalCreature = hero.getArmy().getCreature(index);
				if (originalCreature != null)
				{
					Class<? extends Creature> creatureClass = originalCreature.getClass();
					Class<? extends CreatureFactory> factoryClass = CreatureFactory
						.getCreatureFactoryClass(creatureClass);
					CreatureFactory factory = CreatureFactory.createCreatureFactory(factoryClass);
					Creature creature = factory.buildCreature(1);
					if (castle.canAddToArmy(creature.getClass())
						&& hero.canRemoveFromArmy(creature))
					{
						hero.removeFromArmy(creature);
						castle.addToArmy(creature);
						if (castleArmyView.getArmy() == null)
							castleArmyView.setArmy(castle.getArmy());
						castleArmyView.update();
						heroArmyView.update();
						updateMainGUIArmyView();
						this.mouseEnter(e);
					}
				}
			}
		}

		public void mouseDown(MouseEvent e)
		{
		}

		public void mouseUp(MouseEvent e)
		{
		}
	}
}