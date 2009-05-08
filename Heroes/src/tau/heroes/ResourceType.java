package tau.heroes;

import java.io.Serializable;

public enum ResourceType implements Serializable
{

	WOOD("Wood", 2),
	GOLD("Gold", 1000),
	STONE("Stone", 2),
	// GEMS("gems", 1)
	;

	private final String type;
	private final int perDay;

	private ResourceType(String type, int perDay)
	{
		this.type = type;
		this.perDay = perDay;
	}

	public String getTypeName()
	{
		return this.type;
	}

	public int getPerDay()
	{
		return perDay;
	}
}