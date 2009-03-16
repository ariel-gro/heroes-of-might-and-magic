package tau.heroes;

public class Hero
{
	private boolean _alive;
	private Army _army;
	private int _attackSkill;
	private int _defenseSkill;
	private AlignmentType _type;
	private int xPos;
	private int yPos;
	public Player player;
	private int maxAllowedSteps = 5;

	public Hero(int attack, int defense, Army a)
	{
		_army = a;
		_alive = true;
		_attackSkill = attack;
		_defenseSkill = defense;
	}

	public Hero(Player player, Board theBoard, int x, int y) {
		this.player = player;
		this.xPos = x;
		this.yPos = y;
		
		theBoard.placeHero(this, x, y);
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
				Creature defenderCreature = defender._army.getFirstCreature();
				if(defenderCreature != null)
				{
					int totalDefense = defenderCreature.get_defenseSkill() + defender._defenseSkill;
					int totalAttack = attackerCreature.get_attackSkill() + _attackSkill;
					//TODO: we should decide what to do with the skills...
					//for now we do:
					int addToDamage = totalAttack - totalDefense;
					addToDamage = (addToDamage >= 0) ? addToDamage : 0;

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
		
		this.xPos = x;
		this.yPos = y;
		
		theBoard.printBoard();
		
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
}
