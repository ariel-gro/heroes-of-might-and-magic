package tau.heroes;

import java.io.Serializable;

public enum MapObject implements Serializable
{
	GRASS,
	FIRE;
	
	public int mapObjectIcon()
	{
		switch(this)
		{
		case GRASS:
			return IconCache.grassIcon;
		case FIRE:
			return IconCache.fireIcon;
			
		default:
			return IconCache.grassIcon;
		}
	}

}
