package tau.heroes;

import java.io.Serializable;

public abstract class Creature implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AlignmentType _type;
	private int _speed;
	private int _shoots;
	private int _attackSkill;
	private int _defenseSkill;
	private int _hitPoints;
	private int _lastUnitHitPoints;
	private int _damage;// The avg of min damage and max damage
	private String _name;
	private int _numberOfUnits; // the number of creatures in the group.

	public Creature(AlignmentType _type, int _speed, int _shoots, int attack, int defense,
		int points, int _damage, String _name, int numberOfUnits)
	{
		super();
		this._type = _type;
		this._speed = _speed;
		this._shoots = _shoots;
		_attackSkill = attack;
		_defenseSkill = defense;
		_hitPoints = points;
		this._damage = _damage;
		this._name = _name;
		_numberOfUnits = numberOfUnits;
		_lastUnitHitPoints = _hitPoints;
	}

	public AlignmentType get_AlignmentType()
	{
		return _type;
	}

	public int get_numberOfUnits()
	{
		return _numberOfUnits;
	}

	public void set_numberOfUnits(int numberOfUnits)
	{
		_numberOfUnits = numberOfUnits;
	}

	public String toString()
	{
		return _numberOfUnits + " creatures of " + _name;
	}

	public int get_speed()
	{
		return _speed;
	}

	public int get_shoots()
	{
		return _shoots;
	}

	public int get_attackSkill()
	{
		return _attackSkill;
	}

	public int get_defenseSkill()
	{
		return _defenseSkill;
	}

	public int get_hitPoints()
	{
		return _hitPoints;
	}

	public int get_damage()
	{
		return _damage;
	}

	public String get_name()
	{
		return _name;
	}

	public void defendFromAttack(int damage)
	{
		int leftDamage = damage;
		while (leftDamage > 0 && _numberOfUnits > 0)
		{
			if (leftDamage < _lastUnitHitPoints)
			{
				_lastUnitHitPoints -= leftDamage;
				leftDamage = 0;
			}
			else
			{
				leftDamage -= _lastUnitHitPoints;
				_numberOfUnits--;

				_lastUnitHitPoints = _hitPoints;
			}
		}

	}

	public int get_lastUnitHitPoints()
	{
		return _lastUnitHitPoints;
	}

	public void addUnits(int numberOfUnits)
	{
		this._numberOfUnits += numberOfUnits;
	}

	public void removeUnits(int numberOfUnits)
	{
		this._numberOfUnits -= numberOfUnits;
	}

	public static Class<? extends Creature> getCreatureClass(String creatureName)
	{
		Class<? extends Creature> creatureClass = null;
		creatureName = creatureName.toLowerCase();

		if (creatureName.equals("goblin"))
			creatureClass = Goblin.class;
		else if (creatureName.equals("soldier"))
			creatureClass = Soldier.class;
		else if (creatureName.equals("dwarf"))
			creatureClass = Dwarf.class;
		else if (creatureName.equals("archer"))
			creatureClass = Archer.class;
		else if (creatureName.equals("dragon"))
			creatureClass = FireDragon.class;

		return creatureClass;
	}
}