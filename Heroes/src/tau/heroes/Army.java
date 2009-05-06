package tau.heroes;

import java.io.Serializable;

public class Army implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int MAX_CREATURES = 5;
	private Creature[] _creatures = new Creature[MAX_CREATURES];

	public Army(Creature[] creatures)
	{
		for (int i = 0; i < MAX_CREATURES && i < creatures.length; i++)
			setCreature(i, creatures[i]);
	}

	public Creature[] getCreatures()
	{
		return _creatures;
	}

	public void setCreature(int location, Creature c)
	{
		if (location >= 0 && location < MAX_CREATURES)
		{
			_creatures[location] = c;
		}
	}

	public Creature getCreature(int location)
	{
		if (location < 0 && location >= MAX_CREATURES)
			return null;
		return _creatures[location];
	}

	public Creature getFirstCreature()
	{
		for (int i = 0; i < MAX_CREATURES; i++)
		{
			if (_creatures[i] != null)
				return _creatures[i];
		}
		return null;
	}

	public void cleanDeadCreatures()
	{
		for (int i = 0; i < MAX_CREATURES; i++)
		{
			if (_creatures[i] != null && _creatures[i].get_numberOfUnits() <= 0)
				_creatures[i] = null;
		}
	}

	public int getTotalNumberOfUnits()
	{
		int total = 0;

		for (int i = 0; i < MAX_CREATURES; i++)
			if (this._creatures[i] != null)
				total += this._creatures[i].get_numberOfUnits();

		return total;
	}

	public boolean canAdd(Class<? extends Creature> creatureClass)
	{
		for (int i = 0; i < MAX_CREATURES; i++)
			if (this._creatures[i] == null || this._creatures[i].getClass().equals(creatureClass))
				return true;

		return false;
	}

	public void add(Creature creature)
	{
		// Look for an existing creature from the same type
		// If found, add it the argument creature's units
		for (int i = 0; i < Army.MAX_CREATURES; i++)
			if (_creatures[i] != null && _creatures[i].getClass().equals(creature.getClass()))
			{
				Creature existingCreature = _creatures[i];
				existingCreature.addUnits(creature.get_numberOfUnits());
				return;
			}
		// If not found, place it in a new location
		for (int i = 0; i < Army.MAX_CREATURES; i++)
			if (_creatures[i] == null)
			{
				_creatures[i] = creature;
				return;
			}
	}

	public boolean canRemove(Creature creature)
	{
		for (int i = 0; i < MAX_CREATURES; i++)
			if (this._creatures[i] != null
				&& this._creatures[i].getClass().equals(creature.getClass())
				&& this._creatures[i].get_numberOfUnits() >= creature.get_numberOfUnits())
				return true;

		return false;
	}

	public void remove(Creature creature)
	{
		for (int i = 0; i < MAX_CREATURES; i++)
			if (this._creatures[i] != null
				&& this._creatures[i].getClass().equals(creature.getClass()))
			{
				this._creatures[i].removeUnits(creature.get_numberOfUnits());

				this.cleanDeadCreatures();
			}
	}

	public String toString()
	{
		String s = "";
		for (int i = 0; i < MAX_CREATURES; i++)
		{
			s += "creature number " + (i + 1) + ": is "
				+ ((_creatures[i] != null) ? _creatures[i].toString() : "empty");
			s += '\n';
		}
		return s;

	}

}