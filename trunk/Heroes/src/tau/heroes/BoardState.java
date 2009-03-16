package tau.heroes;



public class BoardState {

	private Hero hero;
	private Resource resource;
	private Castle castle;
	private boolean isEmpty;

	public BoardState()
	{
		hero =  null;
		resource = null;
		castle = null;
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
	
	public boolean getIsEmpty()
	{
		return isEmpty;
	}
}