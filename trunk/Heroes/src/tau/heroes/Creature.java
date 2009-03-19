package tau.heroes;




public abstract class Creature
{
	private AlignmentType _type;
	private int _speed;
	private int _shoots;
	private int _attackSkill;
	private int _defenseSkill;
	private int _hitPoints;
	private int _lastUnitHitPoints;
	private int _damage;//The avg of min damage and max damage
	private String _name;
	private int _numberOfUnits; //the number of creatures in the group.

	public Creature(AlignmentType _type,int _speed, int _shoots, int attack, int defense, int points,
			int _damage, String _name, int numberOfCreatures) {
		super();
		this._type = _type;
		this._speed = _speed;
		this._shoots = _shoots;
		_attackSkill = attack;
		_defenseSkill = defense;
		_hitPoints = points;
		this._damage = _damage;
		this._name = _name;
		_numberOfUnits = numberOfCreatures;
		_lastUnitHitPoints = _hitPoints;
	}
	public AlignmentType get_AlignmentType()
	{
		return _type;
	}
	public int get_numberOfUnits() {
		return _numberOfUnits;
	}
	public void set_numberOfUnits(int numberOfUnits)
	{
		_numberOfUnits = numberOfUnits;
	}
	public String toString()
	{
		return  _numberOfUnits +" creatures of "+ _name;
	}

	public int get_speed() {
		return _speed;
	}

	public int get_shoots() {
		return _shoots;
	}

	public int get_attackSkill() {
		return _attackSkill;
	}

	public int get_defenseSkill() {
		return _defenseSkill;
	}

	public int get_hitPoints() {
		return _hitPoints;
	}

	public int get_damage() {
		return _damage;
	}

	public String get_name() {
		return _name;
	}
	public void defendFromAttack(int damage)
	{
		int leftDamage = damage;
		while(leftDamage > 0 && _numberOfUnits > 0)
		{
			if(leftDamage < _lastUnitHitPoints)
			{
				_lastUnitHitPoints -= leftDamage;
				leftDamage = 0;
			}
			else
			{
				leftDamage -= _lastUnitHitPoints;
				_numberOfUnits --;

				_lastUnitHitPoints = _hitPoints;
			}
		}

	}
	
	public int get_lastUnitHitPoints() {
		return _lastUnitHitPoints;
	}
	
	public void addUnits(int numberOfUnits) {
		this._numberOfUnits += numberOfUnits;
	}
}