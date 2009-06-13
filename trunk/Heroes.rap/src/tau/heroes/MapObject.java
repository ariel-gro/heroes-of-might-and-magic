package tau.heroes;

import java.io.Serializable;

public enum MapObject implements Serializable
{
	GRASS,
	FIRE,
	ROCK,
	TREESTOMP;
	
	public int mapObjectIcon()
	{
		switch(this)
		{
		case GRASS:
			return IconCache.grassIcon;
		case FIRE:
			return IconCache.fireIcon;
		case ROCK:
			return IconCache.rockIcon;
		case TREESTOMP:
			return IconCache.treeStumpIcon;
		default:
			return IconCache.grassIcon;
		}
	}

}
