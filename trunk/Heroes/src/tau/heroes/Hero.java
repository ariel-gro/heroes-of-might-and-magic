package tau.heroes;

import java.io.Serializable;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Hero implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static final int SKILLS_AFTER_ATTACK = 5;
	private boolean _alive;
	private boolean _autoFight;
	private Army _army;
	private int _attackSkill;
	private int _defenseSkill;
	private int xPos;
	private int yPos;
	public Player player;
	private int maxAllowedSteps = 5;

	// This is for testing... don't remove.
	public Hero(int attack, int defense, Army a)
	{
		_army = a;
		_alive = true;
		_attackSkill = attack;
		_defenseSkill = defense;
		_autoFight = false;
	}

	public Hero(Player player, Board theBoard, int X, int Y)
	{
		this.player = player;
		this.xPos = X;
		this.yPos = Y;
		_alive = true;
		_autoFight = player.isComputer();
		// randomly select a number between 0 and 2.
		_attackSkill = (int) (Math.random() * 3);
		_defenseSkill = (int) (Math.random() * 3);
		Creature[] c = new Creature[Army.MAX_CREATURES];
		// random number between 1 - 10.
		c[0] = new Soldier(1 + (int) (Math.random() * 10));
		_army = new Army(c);

		theBoard.placeHero(this, X, Y);
	}

	public Shell visualAttack(Hero defender)
	{
		if (_autoFight)
			return null;
		AttackGUI a = new AttackGUI(this, defender, Display.getCurrent());
		return a.open();
	}

	private void consolAttack(Hero defender)
	{
		System.out.println("Battle Started");
		System.out.println("**************");
		System.out.println("Attacker:");
		System.out.println(_army.toString());
		System.out.println("Defender:");
		System.out.println(defender._army.toString());
		while (defender.alive() && alive())
		{
			attackRound(defender);
			if (defender.alive())
				defender.attackRound(this);
			System.out.println("******round********");
			System.out.println("Attacker:");
			System.out.println(_army.toString());
			System.out.println("Defender:");
			System.out.println(defender._army.toString());

		}
		System.out.println("Battle Ended");
		System.out.println("**************");
	}
  
	// this will start a battle against h. (this - attacker, h - defender).
	public void attack(Hero defender)
	{
		//save the state of the auto-fight:
		boolean defenderAuto = defender._autoFight;
		boolean thisAuto = _autoFight;
		//if one of them is auto (then both of them are open):
		boolean auto = _autoFight || defender._autoFight;
		_autoFight = auto;
		defender._autoFight = auto;
		
		if (GameState.isGUI() && !auto)
		{
			Display d = Display.getCurrent();
			Shell s = visualAttack(defender);
			while (s != null && !s.isDisposed())
			{
				if (!d.readAndDispatch())
					d.sleep();
			}
		}
		else
		{//this is in case of auto fight:
			consolAttack(defender);
		}
		//restore the right state (of the auto fight):
		defender._autoFight = defenderAuto;
		_autoFight = thisAuto;
		
		//Add skills for the winner 
		//(We don't need to check who won, the loser will die anyway)
		this._attackSkill += SKILLS_AFTER_ATTACK;
		this._defenseSkill += SKILLS_AFTER_ATTACK;
		defender._attackSkill += SKILLS_AFTER_ATTACK;
		defender._defenseSkill += SKILLS_AFTER_ATTACK;
	}

	private void attackRound(Hero defender)
	{
		for (int i = 0; i < Army.MAX_CREATURES; i++)
		{
			if (!defender.alive())
			{// If the defender is dead then you win
				System.out.println(defender.toString() + " is dead!");
				return;
			}
			Creature attackerCreature = _army.getCreature(i);
			if (attackerCreature == null)
				continue;
			Creature defenderCreature;
			int defenderInt;
			// if we are during auto fight get the first creature (in
			// the catch).
			if (!_autoFight)
			{
				System.out.println("You are about to attack with creature number " + (i + 1));
				String[] s = MainModule
					.getCommandAndParameters("Please select the enemy creature you want to attack:");
				defenderInt = Integer.parseInt(s[0]) - 1;
				defenderCreature = defender._army.getCreature(defenderInt);
				if (defenderCreature == null)
				{
					defenderInt = -1;
					defenderCreature = defender._army.getFirstCreature();
				}
			}
			else
			{
				defenderInt = -1;
				defenderCreature = defender._army.getFirstCreature();
			}
			int totalDamage = attackCreature(i, defender, defenderInt);
			if (totalDamage < 0)
			{// This means the hero has no units, hence it is dead.
				System.out.println(defender.toString() + " is dead!");
				defender.kill();
				return;
			}
			System.out.print(attackerCreature.toString() + " attacked "
				+ defenderCreature.toString() + " with " + totalDamage + " damage");
			defenderCreature.defendFromAttack(totalDamage);
			System.out.println(" defenser left with " + defenderCreature.toString());

			defender.cleanDeadCreatures();// TODO : clean dead army
		}
	}

	public int attackCreature(int myUnit, Hero defender, int defenderUnit)
	{
		Creature attackerCreature = _army.getCreature(myUnit);
		if (attackerCreature == null)
			return 0;
		Creature defenderCreature = (defenderUnit != -1) ? defender._army.getCreature(defenderUnit)
			: defender._army.getFirstCreature();
		if (defenderCreature != null && defenderCreature.get_numberOfUnits() > 0)
		{
			int totalDefense = defenderCreature.get_defenseSkill() + defender._defenseSkill;
			int totalAttack = attackerCreature.get_attackSkill() + _attackSkill;
			// TODO: we should decide what to do with the skills...
			// for now we do:
			int addToDamage = totalAttack - totalDefense;
			addToDamage = (addToDamage >= 0) ? addToDamage : 0;
			if (!_autoFight)
			{// add a random effect to the fight: (value between -1 and
				// 1) only when manual fight is ser
				int randomDamage = (int) ((Math.random() * 3)) - 1;
				System.out.println(randomDamage);
				addToDamage += randomDamage;
			}
			int totalDamage = attackerCreature.get_numberOfUnits()
				* (attackerCreature.get_damage() + addToDamage);
			return totalDamage;
		}
		// This means the hero has no units, hence it is dead.
		else
		{
			return -1;
		}
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param theBoard
	 * @return
	 */
	public boolean moveTo(int x, int y, Board theBoard)
	{
		if (checkStepsAllowed(x, y, theBoard) == false)
			return false;

		if (theBoard.getBoardState(x, y).getResource() != null)
		{
			theBoard.getBoardState(x, y).getResource().setOwner(this.player);
		}

		theBoard.placeHero(this, x, y);
		theBoard.removeHero(xPos, yPos);

		if (theBoard.getBoardState(x, y).getCastle() != null)
		{
			theBoard.getBoardState(x, y).getCastle().enterHero(this);
		}

		this.xPos = x;
		this.yPos = y;

		return true;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param theBoard
	 * @return
	 */
	public boolean checkStepsAllowed(int x, int y, Board theBoard)
	{
		if ((Math.abs(xPos - x) + Math.abs(yPos - y)) > maxAllowedSteps)
		{
			System.out.println("Move not legal. Hero can only move up to " + maxAllowedSteps
				+ " steps !!!");
			return false;
		}

		if (x >= theBoard.getSize() || y >= theBoard.getSize())
		{
			System.out.println("Move not legal. Hero cannot move off map !!!");
			return false;
		}

		return true;
	}

	public Army getArmy()
	{
		return _army;
	}

	public int getAttackSkill()
	{
		return _attackSkill;
	}

	public int getDefenseSkill()
	{
		return _defenseSkill;
	}

	public boolean alive()
	{
		if (_army == null || _army.getFirstCreature() == null)
			kill();
		return _alive;
	}

	public void kill()
	{
		_alive = false;
	}

	public int getXPos()
	{
		return this.xPos;
	}

	public int getYPos()
	{
		return this.yPos;
	}

	public void cleanDeadCreatures()
	{
		_army.cleanDeadCreatures();
	}

	// For tests only
	public void setAutoFight(boolean bAuto)
	{
		_autoFight = bAuto;
	}

	public boolean canAddToArmy(Class<? extends Creature> creatureClass)
	{
		return (this._army == null || this._army.canAdd(creatureClass));
	}

	public void addToArmy(Creature creature)
	{
		if (this._army == null)
		{
			Creature[] creatures = new Creature[] { creature };
			this._army = new Army(creatures);
		}
		else
			this._army.add(creature);
	}

	public boolean removeFromArmy(Creature creature)
	{
		if (_army == null || !_army.canRemove(creature))
			return false;
		else if (_army.getTotalNumberOfUnits() <= creature.get_numberOfUnits())
		{
			System.out.println("Sorry, you can't remove all units from the hero's army.");
			return false;
		}
		else
		{
			_army.remove(creature);
			return true;
		}
	}

	public String toString()
	{
		String name = (player != null) ? player.getName() : super.toString();
		return name;
	}

	public String printLocation()
	{
		return "(" + this.xPos + ", " + this.yPos + ")";
	}
}