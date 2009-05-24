package tau.heroes;

import java.io.Serializable;

public enum PlayerColor implements Serializable {
	
	BLUE,
	RED,
	BLACK,
	PURPLE;
	
	public int heroIcon()
	{
		switch (this)
		{
			case BLUE:
				return IconCache.blueHeroIcon;
			case RED:
				return IconCache.redHeroIcon;
			case BLACK:
				return IconCache.blackHeroIcon;
			case PURPLE:
				return IconCache.purpleHeroIcon;
				
			default:
				return IconCache.blueHeroIcon;
		}
	}
	
	public static boolean isHeroIcon(int iconInt)
	{
		switch (iconInt)
		{
			case IconCache.blueHeroIcon:
			case IconCache.redHeroIcon:
			case IconCache.blackHeroIcon:
			case IconCache.purpleHeroIcon:
			
			case IconCache.blueInGlodMineIcon:
				
			
			case IconCache.blueInStoneIcon:
				
			
			case IconCache.blueInWoodIcon:
				

				return true;

			default:
				return false;
		}
	}
	
	public int inCastleIcon()
	{
		switch (this)
		{
			case BLUE:
				return IconCache.blueInCastleIcon;
			case RED:
				return IconCache.redInCastleIcon;
			case BLACK:
				return IconCache.blackInCastleIcon;
			case PURPLE:
				return IconCache.purpleInCastleIcon;
				
			default:
				return IconCache.blueInCastleIcon;
		}
	}
	
	public int inDungeonIcon()
	{
		switch (this)
		{
			case BLUE:
				return IconCache.blueInDungeonIcon;
			case RED:
				return IconCache.redInDungeonIcon;
			case BLACK:
				return IconCache.blackInDungeonIcon;
			case PURPLE:
				return IconCache.purpleInDungeonIcon;
				
			default:
				return IconCache.blueInDungeonIcon;
		}
	}
	
	public int inRampartIcon()
	{
		switch (this)
		{
			case BLUE:
				return IconCache.blueInRampartIcon;
			case RED:
				return IconCache.redInRampartIcon;
			case BLACK:
				return IconCache.blackInRampartIcon;
			case PURPLE:
				return IconCache.purpleInRampartIcon;
				
			default:
				return IconCache.blueInRampartIcon;
		}
	}
	
	public int inTowerIcon()
	{
		switch (this)
		{
			case BLUE:
				return IconCache.blueInTowerIcon;
			case RED:
				return IconCache.redInTowerIcon;
			case BLACK:
				return IconCache.blackInTowerIcon;
			case PURPLE:
				return IconCache.purpleInTowerIcon;
				
			default:
				return IconCache.blueInTowerIcon;
		}
	}
	
	public static boolean isInCastleIcon(int iconInt)
	{
		switch (iconInt)
		{
			case IconCache.blueInCastleIcon:
			case IconCache.blueInDungeonIcon:
			case IconCache.blueInRampartIcon:
			case IconCache.blueInTowerIcon:
				return true;
				
			case IconCache.redInCastleIcon:
			case IconCache.redInDungeonIcon:
			case IconCache.redInRampartIcon:
			case IconCache.redInTowerIcon:
				return true;
				
			case IconCache.blackInCastleIcon:
			case IconCache.blackInDungeonIcon:
			case IconCache.blackInRampartIcon:
			case IconCache.blackInTowerIcon:
				return true;
				
			case IconCache.purpleInCastleIcon:
			case IconCache.purpleInDungeonIcon:
			case IconCache.purpleInRampartIcon:
			case IconCache.purpleInTowerIcon:
				return true;
				

			default:
				return false;
		}
	}
	
}
