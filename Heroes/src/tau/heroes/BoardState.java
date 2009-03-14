package tau.heroes;

public class BoardState {

	private Hero _hero;
	private Property property;

	public BoardState()
	{
		_hero =  null;
		property = null;
	}

	public void setHero(Hero hero)
	{
		this._hero = hero;
	}

	public void setProperty(Property property)
	{
		this.property = property;
	}

	public Hero getHero()
	{
		return _hero;
	}

	public Property getProperty()
	{
		return property;
	}

	public boolean getIsEmpty() {
		// TODO Auto-generated method stub
		return false;
	}
}
