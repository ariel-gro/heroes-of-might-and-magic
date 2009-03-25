package tau.heroes;

import java.io.Serializable;

public class Hero implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean _alive;
	private boolean _autoFight;
	private Army _army;
	private int _attackSkill;
	private int _defenseSkill;
	//private AlignmentType _type;
	private int xPos;
	private int yPos;
	public Player player;
	private int maxAllowedSteps = 5;

	//This is for testing... don't remove.
	public Hero(int attack, int defense, Army a)
	{
		_army = a;
		_alive = true;
		_attackSkill = attack;
		_defenseSkill = defense;
		_autoFight = false;
	}

	public Hero(Player player, Board theBoard, int X, int Y) {
		this.player = player;
		this.xPos  = X;
		this.yPos = Y;
		_alive = true;
		_autoFight = false;
		//randomly select a number between 0 and 2.
		_attackSkill = (int) (Math.random() * 3);
		_defenseSkill = (int) (Math.random() * 3);
		Creature[] c = new Creature[Army.MAX_CREATURES];
		//random number between 1 - 10.
		c[0] = new Soldier(1+ (int)(Math.random() * 10));
		_army = new Army(c);

		theBoard.placeHero(this, X, Y);
	}

	//this will start a battle against h. (this - attacker, h - defender).
	public void attack(Hero defender)
	{
		System.out.println("Battle Started");
		System.out.println("**************");
		System.out.println("Attacker:");
		System.out.println(_army.toString());
		System.out.println("Defender:");
		System.out.println(defender._army.toString());
		while(defender.alive() && alive())
		{
			attackRound(defender);
			if(defender.alive())
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
	private void attackRound(Hero defender)
	{
		for(int i=0;i<Army.MAX_CREATURES;i++)
		{
			Creature attackerCreature = _army.getCreature(i);
			if(attackerCreature != null)
			{
				Creature defenderCreature;
				try
				{
					//if we are during auto fight get the first creature (in the catch).
					if(_autoFight)
					{
						throw new Exception();
					}

					System.out.println("You are about to attack with creature number "+(i+1));
					String[] s = MainModule.getCommandAndParameters("Please select the enemy creature you want to attack:");

					int defenderInt =  Integer.parseInt(s[0]) - 1;
					defenderCreature = defender._army.getCreature(defenderInt);
					defenderCreature.get_name();
				}
				catch(Exception e)
				{
					defenderCreature = defender._army.getFirstCreature();
				}
				if(defenderCreature != null)
				{
					int totalDefense = defenderCreature.get_defenseSkill() + defender._defenseSkill;
					int totalAttack = attackerCreature.get_attackSkill() + _attackSkill;
					//TODO: we should decide what to do with the skills...
					//for now we do:
					int addToDamage = totalAttack - totalDefense;
					addToDamage = (addToDamage >= 0) ? addToDamage : 0;
					if(!_autoFight)
					{//add a random effect to the fight: (value between -1 and 1) only when manual fight is ser
						int randomDamage = (int)((Math.random() * 3)) - 1;
						System.out.println(randomDamage);
						addToDamage += randomDamage;
					}

					System.out.print("Attacker "+this.toString()+": "+"with addToDamage = "+addToDamage+". ");
					int totalDamage = attackerCreature.get_numberOfUnits()*(attackerCreature.get_damage()+addToDamage);
					System.out.print(attackerCreature.toString() + " attacked "+defenderCreature.toString()+ " with "+totalDamage+" damage");
					defenderCreature.defendFromAttack(totalDamage);
					System.out.println(" defenser left with "+defenderCreature.toString());
				}
				//This means the hero has no units, hence it is dead.
				else
				{
					System.out.println(defender.toString() +" is dead!");
					defender.kill();
					return;
				}
				defender._army.cleanDeadCreatures();//TODO : clean dead army and hero
			}
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
		if(checkStepsAllowed(x, y, theBoard) == false)
			return false;

		if (theBoard.getBoardState(x, y).getResource() != null)
		{
			theBoard.getBoardState(x, y).getResource().setOwner(this.player);
		}

		theBoard.placeHero(this, x, y);
		theBoard.removeHero(xPos, yPos);

		if (theBoard.getBoardState(x, y).getCastle() != null) {
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
	private boolean checkStepsAllowed(int x, int y, Board theBoard)
	{
		if((Math.abs(xPos - x) +  Math.abs(yPos - y)) > maxAllowedSteps)
		{
			System.out.println("Move not legal. Hero can only move up to " + maxAllowedSteps + " steps !!!");
			return false;
		}

		if(x >= theBoard.getSize() || y >= theBoard.getSize())
		{
			System.out.println("Move not legal. Hero cannot move off map !!!");
			return false;
		}

		return true;
	}


	public Army getArmy(){
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
		if(_army == null || _army.getFirstCreature() == null)
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
	//For tests only
	public void setAutoFight(boolean bAuto)
	{
		_autoFight = bAuto;
	}
	
	public boolean removeFromArmy(Creature creature)
	{
		int numOfUnitsToRemove = creature.get_numberOfUnits();
		if (_army == null)
		{
			return false;
		}
		for (int i = 0 ; i < Army.MAX_CREATURES ; i++)
		{
			Creature armyCreature = _army.getCreature(i);
			if (armyCreature != null) {
				int numOfUnits = armyCreature.get_numberOfUnits();
				if (armyCreature.getClass().equals(creature.getClass()) && 
						numOfUnits >= numOfUnitsToRemove)
				{
					armyCreature.set_numberOfUnits(numOfUnits - numOfUnitsToRemove);
					// If creature has no units left remove it from the army
					if (armyCreature.get_numberOfUnits() == 0)
						_army.setCreature(i, null);
					return true;
				}	
			}
		}
		return false;
	}
}