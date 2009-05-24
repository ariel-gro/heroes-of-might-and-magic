package tau.heroes;

import java.io.Serializable;

public class BoardState implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Hero hero;
	private Resource resource;
	private Castle castle;
	private MapObject mapObject;
	private boolean isEmpty;

	public BoardState()
	{
		hero = null;
		resource = null;
		castle = null;
		mapObject = MapObject.GRASS;
		isEmpty = true;
	}

	public void setHero(Hero theHero)
	{
		this.hero = theHero;
		isEmpty = false;
	}

	public void setResource(Resource theResource)
	{
		this.resource = theResource;
		isEmpty = false;
	}

	public void setCastle(Castle theCastle)
	{
		this.castle = theCastle;
		isEmpty = false;
	}

	public Hero getHero()
	{
		return hero;
	}

	public Resource getResource()
	{
		return resource;
	}

	public Castle getCastle()
	{
		return castle;
	}
	
	public void setMapObject(MapObject mo)
	{
		this.mapObject = mo;
	}
	
	public MapObject getMapObject()
	{
		return this.mapObject;
	}

	public boolean getIsEmpty()
	{
		return isEmpty;
	}
}