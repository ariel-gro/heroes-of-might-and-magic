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
		for(int i=0;i<MAX_CREATURES && i<creatures.length;i++)
			setCreature(i,creatures[i]);
	}
	public Creature[] getCreatures()
	{
		return _creatures;
	}
	public void setCreature(int location,Creature c)
	{
		if(location >= 0 && location < MAX_CREATURES)
		{
			_creatures[location] = c;
		}
	}
	public Creature getCreature(int location)
	{
		if(location < 0 && location >= MAX_CREATURES)
			return null;
		return _creatures[location];
	}
	public Creature getFirstCreature()
	{
		for(int i=0;i<MAX_CREATURES;i++)
		{
			if(_creatures[i] != null)
				return _creatures[i];
		}
		return null;
	}
	public void cleanDeadCreatures()
	{
		for(int i=0;i<MAX_CREATURES;i++)
		{
			if(_creatures[i] != null && _creatures[i].get_numberOfUnits() <= 0)
				_creatures[i] = null;
		}
	}
	public String toString()
	{
		String s = "";
		for(int i=0;i<MAX_CREATURES;i++)
		{
			s += "creature number "+(i+1)+": is "+ ( (_creatures[i] != null) ? _creatures[i].toString() : "empty");
			s += '\n';
		}
		return s;

	}

}