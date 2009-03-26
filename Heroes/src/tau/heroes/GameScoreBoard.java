package tau.heroes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Vector;

public class GameScoreBoard implements Serializable
{
	private static final long serialVersionUID = 1L;
	private Vector<Scores> scoreBoard;

	private class Scores implements Serializable
	{
		private static final long serialVersionUID = 1L;
		final Player player;
		final Integer score;

		public Scores() 
		{
			this.player = null;
			this.score = 0;
		}
		
		Scores (Player player, Integer score)
		{
			this.player = player;
			this.score = score;
		}

		public int getScore()
		{
			return this.score;
		}

		public Player getPlayer() {
			return this.player;
		}		
	}

	public GameScoreBoard ()
	{
		this.scoreBoard = new Vector<Scores>(11);
		this.clearScoreBoard();
	}
	
	public void addToScoreBoard (Player player, int score)
	{
		Scores playerScore = new Scores(player, score);
		scoreBoard.setElementAt(playerScore, 10);
		this.sort();
		scoreBoard.setElementAt(new Scores(), 10);
	}

	public int getScoreAt(int index) {
		return this.scoreBoard.elementAt(index).getScore();
	}

	private void sort()
	{
		Scores temp, myScore;
		for (int i = scoreBoard.size()-1; i > 0; i--)
		{
			temp = scoreBoard.elementAt(i-1);
			myScore = scoreBoard.elementAt(i);
			if (myScore.getScore() > temp.getScore())
			{
				scoreBoard.setElementAt(scoreBoard.elementAt(i), i-1);
				scoreBoard.setElementAt(temp, i);
			}
		}
	}

	public Player getPlayerAt(int index) 
	{
		return this.scoreBoard.elementAt(index).getPlayer();
	}

	public void save() {
		try
		{
			File saveFile = new File("scoreBoard.tbl");
			FileOutputStream fileOut = new FileOutputStream(saveFile);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			
			if(!saveFile.exists())
				saveFile.createNewFile();			
			out.writeObject(this.scoreBoard);
			out.close();
			fileOut.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.out.println("could not save score board, due to I/O error.");
		}
		
	}

	public void load() {
		try
		{	
			FileInputStream fileIn = new FileInputStream("scoreBoard.tbl");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			this.scoreBoard =  (Vector<Scores>) in.readObject();
			in.close();
			fileIn.close();
		}
		catch (FileNotFoundException e)
		{
			this.clearScoreBoard();
			e.printStackTrace();
		}
		catch (IOException e)
		{
			//do nothing, corrupt file. file will be saved later
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (ClassCastException e)
		{
			e.printStackTrace();
		}		
	}

	public void print() 
	{
		Scores score;
		String name;
		System.out.println("name\t  score");
		for (int i = 0; i < 10; i++)
		{
			score = this.scoreBoard.elementAt(i);
			if (score.getPlayer() != null)
				name = score.getPlayer().getName();
			else 
				name = "----";
			System.out.println(name+"\t\t"+score.getScore());
		}
		
	}
	
	public void clearScoreBoard()
	{
		for (int i = 0; i < 11; i++)
			this.scoreBoard.insertElementAt(new Scores(), i);
	}

	
}
