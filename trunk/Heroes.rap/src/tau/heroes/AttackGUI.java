package tau.heroes;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class AttackGUI
{
	private Display display;
	private Shell shell;
	private Color black;
	@SuppressWarnings("unused")
	private Color green;
	private Color white;

	private SashForm sash;
	private Composite boardComposite;
	private Composite statusComposite;

	private Hero[] heroes;
	private List<MyMouseListner> mouseListeners;
	private int heroIndex;
	private int creatureIndex;
	private Label statusLabel;

	public AttackGUI(Hero h1, Hero h2, Display d)
	{
		display = d;
		heroes = new Hero[2];
		heroes[0] = h1;
		heroes[1] = h2;
		heroIndex = 0;
		creatureIndex = 0;
		//if the first hero don't have units in the first location: 
		while(h1.getArmy().getCreature(creatureIndex) == null && creatureIndex < Army.MAX_CREATURES)
		{
			creatureIndex++;
		}
		IconCache.initResources(display);
	}

	public Shell open()
	{
		shell = new Shell(display, SWT.CENTER | SWT.BORDER);
		shell.setLayout(new FillLayout());
		shell.setText("Battle");
		shell.setMaximized(false);
		black = display.getSystemColor(SWT.COLOR_BLACK);
		green = display.getSystemColor(SWT.COLOR_GREEN);
		white = display.getSystemColor(SWT.COLOR_WHITE);
		shell.setBackground(black);
		shell.setSize(525, 625);
		shell.setLocation(300, 50);
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e)
			{
				e.doit = close();
			}
		});

		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = true;
		shell.setLayout(layout);

		sash = new SashForm(shell, SWT.NONE);
		sash.setOrientation(SWT.VERTICAL);
		sash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		createBoardWindow();
		createStatusWindow();
		setStatusLabel("Next move: " + heroes[heroIndex] + " make your move with unit "
			+ (creatureIndex + 1) + " (double click on the selected unit to attack)");
		sash.setWeights(new int[] { 85, 15 });

		shell.open();
		return shell;
	}

	private void finish(Hero winner)
	{
		String swinner = (winner.alive()) ? winner.toString() : "";
		GameController.handleMessage("The fight is over: " + swinner + " won!");
		shell.close();
	}

	private boolean close()
	{
		return true;
	}

	private void createBoardWindow()
	{
		if (boardComposite != null && boardComposite.isDisposed() == false)
		{
			boardComposite.dispose();
			mouseListeners.clear();
		}
		boardComposite = new Composite(sash, SWT.NONE);
		boardComposite.setBackground(black);

		Hero[] tempHeroes = new Hero[] { heroes[0], null, null, null, heroes[1] };
		GridData d = new GridData(GridData.FILL_BOTH);
		boardComposite.setLayoutData(d);
		GridLayout tableLayout = new GridLayout();
		tableLayout.numColumns = tempHeroes.length;
		tableLayout.makeColumnsEqualWidth = true;
		tableLayout.horizontalSpacing = 0;
		tableLayout.verticalSpacing = 0;
		boardComposite.setLayout(tableLayout);
		mouseListeners = new ArrayList<MyMouseListner>();
		for (int i = 0; i < Army.MAX_CREATURES; i++)
		{
			for (int j = 0; j < tempHeroes.length; j++)
			{
				int left = (j == 0) ? 0 : 1;
				Label b = new Label(boardComposite, SWT.NONE);
				int t = fromBattleToDisplayIcons(tempHeroes[j], i, left);
				b.setImage(IconCache.stockImages[t]);
				b.setCursor(IconCache.stockCursors[IconCache.cursorNo]);

				String description;
				if (t != IconCache.battleGrassIcon)
				{
					description = fromBattleToDisplayDecription(tempHeroes[j], i);
					b.setToolTipText(description);
					MyMouseListner m = new MyMouseListner();
					m.defender = tempHeroes[j];
					m.defenderUnit = i;
					m.label = b;
					mouseListeners.add(m);
					b.addMouseListener(m);
					b.setBackground(white);
					b.setCursor(IconCache.stockCursors[IconCache.cursorAttackLeft + left]);
				}
			}
		}
		setNextCreatureAttack();
	}

	private void createStatusWindow()
	{
		statusComposite = new Composite(sash, SWT.BORDER);
		statusComposite.setBackground(black);
		GridData d = new GridData(GridData.FILL_BOTH);
		statusComposite.setLayoutData(d);
		statusLabel = new Label(statusComposite, SWT.NONE);
		statusLabel.setBackground(white);
		setStatusLabel("\t\t\t\t\t\t\t\t\t\t     \n\n\n\n");

		GridLayout tempLayout = new GridLayout();
		statusComposite.setLayout(tempLayout);
	}

	private void setStatusLabel(String status)
	{
		String currentText = statusLabel.getText();
		status += (currentText == null) ? "\n" : "\n" + currentText;
		statusLabel.setText(status);
	}

	private int fromBattleToDisplayIcons(Hero h, int i, int left)
	{
		if (h == null || !h.alive())
			return IconCache.battleGrassIcon;

		Creature c = h.getArmy().getCreature(i);
		if (c == null)
			return IconCache.battleGrassIcon;
		if (c.getClass().equals(Goblin.class))
			return IconCache.goblinFaceRightIcon + left;
		else if (c.getClass().equals(Soldier.class))
			return IconCache.soldierFaceRightIcon + left;
		else if (c.getClass().equals(Dwarf.class))
			return IconCache.dwarfFaceRightIcon + left;
		else if (c.getClass().equals(Archer.class))
			return IconCache.archerFaceRightIcon + left;
		else if (c.getClass().equals(FireDragon.class))
			return IconCache.fireDragonFaceRightIcon + left;
		return IconCache.battleGrassIcon;
	}

	private String fromBattleToDisplayDecription(Hero h, int i)
	{
		if (h == null || !h.alive())
			return "";
		Creature c = h.getArmy().getCreature(i);
		if (c == null)
			return "";
		return c.toString();
	}

	private void incrementCreature()
	{
		do
		{
			creatureIndex = (creatureIndex + 1) % Army.MAX_CREATURES;
			if (creatureIndex == 0)
				heroIndex = (heroIndex + 1) % 2;
		} while (heroes[heroIndex].getArmy().getCreature(creatureIndex) == null);

		setNextCreatureAttack();
		setStatusLabel("Next move: " + heroes[heroIndex] + " make your move with unit "
			+ (creatureIndex + 1) + " (double click on the selected unit to attack)");
	}

	private void setNextCreatureAttack()
	{
		// the hero that attacks is: heroIndex
		for (MyMouseListner iter : mouseListeners)
		{
			iter.attacker = heroes[heroIndex];
			iter.attackUnit = creatureIndex;
			if (iter.attacker.equals(iter.defender))
				iter.label.setCursor(IconCache.stockCursors[IconCache.cursorNo]);
			else
				iter.label.setCursor(IconCache.stockCursors[IconCache.cursorAttackLeft
					+ ((heroIndex + 1) % 2)]);
		}
	}

	class MyMouseListner implements MouseListener
	{
		public int attackUnit, defenderUnit;
		public Hero attacker, defender;
		public Label label;

		public void mouseDoubleClick(MouseEvent arg0)
		{
			handleAttack();

		}

		public void mouseDown(MouseEvent arg0)
		{
			// TODO Auto-generated method stub

		}

		public void mouseUp(MouseEvent arg0)
		{
			// TODO Auto-generated method stub
		}

		private void handleAttack()
		{
			if (attacker.equals(defender))
				return;
			String status = "Attack move: ";
			status += attacker + " attacks with unit " + (attackUnit + 1) + " at unit "
				+ (defenderUnit + 1);
			Creature defenderCreature = defender.getArmy().getCreature(defenderUnit);
			int totalDamage = attacker.attackCreature(attackUnit, defender, defenderUnit);
			status += " and did " + totalDamage + " damage!";

			setStatusLabel(status);
			if (totalDamage < 0 && !defender.alive())
			{// This means the hero has no units, hence it is dead.
				System.out.println(defender.toString() + " is dead!");
				finish(attacker);
				return;
			}
			if (defenderCreature != null)
				defenderCreature.defendFromAttack(totalDamage);

			defender.cleanDeadCreatures();
			int t = fromBattleToDisplayIcons(defender, defenderUnit, (heroIndex + 1) % 2);
			label.setImage(IconCache.stockImages[t]);
			String description = fromBattleToDisplayDecription(defender, defenderUnit);
			label.setToolTipText(description);
			System.out.println("defender = " + defender.alive() + " \nArmy: "
				+ defender.getArmy().toString());
			if (defender.alive())
				incrementCreature();
			else
				finish(attacker);
		}

	}
}
