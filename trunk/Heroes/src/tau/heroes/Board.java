package tau.heroes;

import java.util.*;



public class Board
{
	private int size;
	private BoardState[][] theBoard;
	
	/**
	 * 
	 * @param size
	 */
	public Board(int size)
	{
		theBoard = new BoardState[size][size];
		for (int i=0; i<size ; i++)
		{
			for (int j=0 ; j<size ; j++)
			{
				theBoard[i][j]= new BoardState();
			}
		}

		this.size = size;
	}
	
	/**
	 * 
	 */
	public void printBoard()
	{
		String objectName;
		
		System.out.print(" ");
		for (int i=0; i<size+1 ; i++)
			if(i!=size)
				System.out.print(i + ((i<10)?" ":""));
		
		System.out.println();
		
		for (int i=0; i<size+1 ; i++)
			System.out.print("--");
		
		System.out.println();
		
		for (int i=0; i<size ; i++)
		{
			for (int j=0 ; j<size+2 ; j++)
			{
				if(j==0)
				{
					System.out.print("|");
				}
				else if (j==size+1)
				{
					System.out.print("|" + i);
				}
				else
				{
					objectName="";
					if(theBoard[i][j-1].getHero() != null)
						objectName += "H";
					if(theBoard[i][j-1].getCastle() != null)
						objectName += "C";
					if(theBoard[i][j-1].getResource() != null)
						objectName += theBoard[i][j-1].getResource().getType().getTypeName().toUpperCase().charAt(0);
					
					if(objectName.length()==0)
						System.out.print("  ");
					else if(objectName.length()==1)
						System.out.print(objectName + " ");
					else
						System.out.print(objectName);
				}				
			}
			System.out.println();
		}
		
		for (int i=0; i<size+1 ; i++)
			System.out.print("--");
		
		System.out.println();
	}

	/**
	 * 
	 * @return
	 */
	public int getSize()
	{
		return size;
	}

	/**
	 * 
	 * @param theHero
	 * @param x
	 * @param y
	 */
	public void placeHero(Hero theHero, int x, int y)
	{
		theBoard[x][y].setHero(theHero);
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 */
	public void removeHero(int x, int y)
	{
		theBoard[x][y].setHero(null);
	}

	/**
	 * 
	 * @param theResource
	 * @param x
	 * @param y
	 */
	public void placeResource(Resource theResource, int x, int y)
	{
		theBoard[x][y].setResource(theResource);
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 */
	public void removeResource(int x, int y)
	{
		theBoard[x][y].setResource(null);
	}
	
	/**
	 * 
	 * @param theCastle
	 * @param x
	 * @param y
	 */
	public void placeCastle(Castle theCastle, int x, int y)
	{
		theBoard[x][y].setCastle(theCastle);
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 */
	public void removeCastle(int x, int y)
	{
		theBoard[x][y].setCastle(null);
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public BoardState getBoardState(int x, int y)
	{
		return theBoard[x][y];
	}
}