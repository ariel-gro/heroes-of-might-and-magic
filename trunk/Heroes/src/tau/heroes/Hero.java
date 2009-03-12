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

	public Hero(int attack, int defense,Army a)
	{
		_army = a;
		_alive = true;
		_attackSkill = attack;
		_defenseSkill = defense;
	}

	public Hero(Player player) {
		this.player = player;
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

	public void moveTo(int x, int y, World w)
	{
		this.xPos = x;
		this.yPos = y;
		if (w.getResourcesGrid(x,y) != null)
		{
			w.getResourcesGrid(x,y).owner = this.player;
		}
		w.setHerosGrid(this, x, y);
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
