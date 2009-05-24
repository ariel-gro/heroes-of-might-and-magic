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
	
	public int inGoldIcon()
	{
		switch (this)
		{
			case BLUE:
				return IconCache.blueInGoldMineIcon;
			case RED:
				return IconCache.redInGoldMineIcon;
			case BLACK:
				return IconCache.blackInGoldMineIcon;
			case PURPLE:
				return IconCache.purpleInGoldMineIcon;
				
			default:
				return IconCache.blueInGoldMineIcon;
		}
	}
	
	public int inStoneIcon()
	{
		switch (this)
		{
			case BLUE:
				return IconCache.blueInStoneIcon;
			case RED:
				return IconCache.redInStoneIcon;
			case BLACK:
				return IconCache.blackInStoneIcon;
			case PURPLE:
				return IconCache.purpleInStoneIcon;
				
			default:
				return IconCache.blueInStoneIcon;
		}
	}
	
	public int inWoodIcon()
	{
		switch (this)
		{
			case BLUE:
				return IconCache.blueInWoodIcon;
			case RED:
				return IconCache.redInWoodIcon;
			case BLACK:
				return IconCache.blackInWoodIcon;
			case PURPLE:
				return IconCache.purpleInWoodIcon;
				
			default:
				return IconCache.blueInWoodIcon;
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
				return true;
			
			case IconCache.blueInGoldMineIcon:
			case IconCache.redInGoldMineIcon:
			case IconCache.blackInGoldMineIcon:
			case IconCache.purpleInGoldMineIcon:
				return true;
			
			case IconCache.blueInStoneIcon:
			case IconCache.redInStoneIcon:
			case IconCache.blackInStoneIcon:
			case IconCache.purpleInStoneIcon:
				return true;
			
			case IconCache.blueInWoodIcon:
			case IconCache.redInWoodIcon:
			case IconCache.blackInWoodIcon:
			case IconCache.purpleInWoodIcon:
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
