package tau.heroes;
import java.util.*;

public class Board
{
	private int size;
	private BoardState[][] world;
	private LinkedList<Hero> HeroesList = new LinkedList<Hero>();
	private LinkedList<Property> propertyList = new LinkedList<Property>();

	public Board(int size)
	{
		world = new BoardState[size][size];
		for (int i=0; i<size ; i++)
		{
			for (int j=0 ; j<size ; j++)
			{
				world[i][j]= new BoardState();
			}
		}

		this.size = size;

		setWorld();
	}

	public int getSize()
	{
		return size;
	}

	/**
	 * @pre world[x][y].Hero == null
	 * @param x
	 * @param y
	 */
	public void placeHero(int x, int y)
	{
		Hero h = new Hero(null);
		HeroesList.addFirst(h);
		world[x][y].setHero(h);
	}

	/**
	 * @pre world[x][y].property == null
	 * @param x
	 * @param y
	 */
	public void placeProperty(int x, int y)
	{
		Property property = new Property();
		propertyList.add(property);
		world[x][y].setProperty(property);
	}

	private void setWorld()
	{
		placeHero(0,0);
		placeHero(0,19);
		placeHero(19,0);
		placeHero(19,19);

		placeProperty(0, 0);
		placeProperty(0, 19);
		placeProperty(19, 0);
		placeProperty(19, 19);
	}
	
	public BoardState[][] getWorld()
	{
		return this.world;
	}
	
	

	public BoardState getBoardState(int x, int y)
	{
		return world[x][y];
	}
}