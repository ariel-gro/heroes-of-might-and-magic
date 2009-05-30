package tau.heroes.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class DataAccess
{
	private static DBConnection dbConnection;

	private DataAccess()
	{

	}

	private static Statement createGeneralStatment() throws SQLException
	{
		return dbConnection.getConnection().createStatement();
	}

	private static PreparedStatement prepareGeneralStatement(String sql) throws SQLException
	{
		return dbConnection.getConnection().prepareStatement(sql);
	}

	public static boolean init()
	{
		try
		{
			dbConnection = new DBConnection();
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("ClassNotFoundException caught in init():");
			e.printStackTrace();
			return false;
		}
		catch (SQLException e)
		{
			System.out.println("SQLException caught in init():");
			e.printStackTrace();
			return false;
		}

		return (initUsersTable() && initGameHistoryTable() && initOpponenetsTable());
	}

	private static boolean initUsersTable()
	{
		try
		{
			ResultSet usersTableExists = dbConnection.getConnection().getMetaData()
				.getTables(null, null, "USERS", null);

			if (!usersTableExists.next())
			{
				createGeneralStatment()
					.execute("CREATE TABLE USERS ("
						+ "UserID INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
						+ "Username VARCHAR(20) NOT NULL, " + "Password VARCHAR(10) NOT NULL, "
						+ "Email VARCHAR(50), Nickname VARCHAR(20), TotalScore INT NOT NULL, "
						+ "PRIMARY KEY (UserID))");

				usersTableExists = dbConnection.getConnection().getMetaData()
					.getTables(null, null, "USERS", null);

				boolean res = usersTableExists.next();
				return res;
			}

			return true;
		}
		catch (SQLException e)
		{
			System.out.println("SQLException caught in initUsersTable():");
			e.printStackTrace();
			return false;
		}
	}

	private static boolean initGameHistoryTable()
	{
		try
		{
			ResultSet historyTableExists = dbConnection.getConnection().getMetaData()
				.getTables(null, null, "GAMEHISTORY", null);

			if (!historyTableExists.next())
			{
				createGeneralStatment()
					.execute("CREATE TABLE GAMEHISTORY ("
						+ "HistoryID INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
						+ "UserID INT NOT NULL, " + "GameDate DATE NOT NULL, "
						+ "GameScore INT NOT NULL, PRIMARY KEY (HistoryID))");

				historyTableExists = dbConnection.getConnection().getMetaData()
					.getTables(null, null, "GAMEHISTORY", null);

				boolean res = historyTableExists.next();
				return res;
			}

			return true;
		}
		catch (SQLException e)
		{
			System.out.println("SQLException caught in initGameHistoryTable():");
			e.printStackTrace();
			return false;
		}
	}

	private static boolean initOpponenetsTable()
	{
		try
		{
			ResultSet opponentsTableExists = dbConnection.getConnection().getMetaData()
				.getTables(null, null, "OPPONENTS", null);

			if (!opponentsTableExists.next())
			{
				createGeneralStatment()
					.execute("CREATE TABLE OPPONENTS ("
						+ "OpponentID INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
						+ "HistoryID INT NOT NULL, "
						+ "Nickname VARCHAR(20) NOT NULL, PRIMARY KEY (OpponentID))");

				opponentsTableExists = dbConnection.getConnection().getMetaData()
					.getTables(null, null, "OPPONENTS", null);

				boolean res = opponentsTableExists.next();
				return res;
			}

			return true;
		}
		catch (SQLException e)
		{
			System.out.println("SQLException caught in initOpponenetsTable():");
			e.printStackTrace();
			return false;
		}
	}

	public static boolean addUser(UserInfo userInfo)
	{
		if (validateUser(userInfo.getUsername(), userInfo.getPassword()))
			return true;
		
		try
		{
			String sql = "INSERT INTO USERS(Username, Password, Email, Nickname, TotalScore) "
				+ "VALUES(?, ?, ?, ?, ?)";
			PreparedStatement addUserStatement = prepareGeneralStatement(sql);
			addUserStatement.setString(1, userInfo.getUsername());
			addUserStatement.setString(2, userInfo.getPassword());
			addUserStatement.setString(3, userInfo.getEmail());
			addUserStatement.setString(4, userInfo.getNickname());
			addUserStatement.setInt(5, userInfo.getTotalScore());

			return (addUserStatement.executeUpdate() == 1);
		}
		catch (SQLException e)
		{
			return false;
		}
	}

	public static boolean validateUser(String username, String password)
	{
		String sql = "SELECT UserID FROM USERS WHERE Username = ? and Password = ?";

		try
		{
			PreparedStatement validateUserStatement = prepareGeneralStatement(sql);
			validateUserStatement.setString(1, username);
			validateUserStatement.setString(2, password);

			ResultSet validateRes = validateUserStatement.executeQuery();

			return (validateRes.next());
		}
		catch (SQLException e)
		{
			return false;
		}
	}

	public static UserInfo getUserInfo(String username)
	{
		String sql = "SELECT UserID, Email, Nickname, TotalScore FROM USERS WHERE Username = ?";

		try
		{
			PreparedStatement getUserInfoSts = prepareGeneralStatement(sql);
			getUserInfoSts.setString(1, username);

			ResultSet userInfoRS = getUserInfoSts.executeQuery();

			if (userInfoRS.next())
			{
				UserInfo userInfo = new UserInfo();
				
				userInfo.setUsername(username);
				userInfo.setUserID(userInfoRS.getInt("UserID"));
				userInfo.setEmail(userInfoRS.getString("Email"));
				userInfo.setNickname(userInfoRS.getString("Nickname"));
				userInfo.setTotalScore(userInfoRS.getInt("TotalScore"));

				return userInfo;
			}
		}
		catch (Exception e)
		{
			return null;
		}

		return null;
	}

	public static List<GameHistory> getGameHistory(int userID)
	{
		List<GameHistory> gameHistoryList = new ArrayList<GameHistory>();

		String sql = "SELECT HistoryID, GameDate, GameScore FROM GAMEHISTORY WHERE UserID = ?";

		try
		{
			PreparedStatement getGameHistorySts = prepareGeneralStatement(sql);
			getGameHistorySts.setInt(1, userID);

			ResultSet gameHistoryRS = getGameHistorySts.executeQuery();

			while (gameHistoryRS.next())
			{
				GameHistory gameHistory = new GameHistory();

				gameHistory.setGameDate(gameHistoryRS.getDate("GameDate"));
				gameHistory.setGameScore(gameHistoryRS.getInt("GameScore"));
				gameHistory
					.setOpponentPlayersNames(getOpponents(gameHistoryRS.getInt("HistoryID")));

				gameHistoryList.add(gameHistory);
			}
		}
		catch (Exception e)
		{
			return null;
		}

		return gameHistoryList;
	}

	private static List<String> getOpponents(int historyID)
	{
		List<String> opponentsNames = new ArrayList<String>();

		String sql = "SELECT Nickname FROM OPPONENTS WHERE HistoryID = ?";

		try
		{
			PreparedStatement getOpponentsSts = prepareGeneralStatement(sql);
			getOpponentsSts.setInt(1, historyID);

			ResultSet opponentsRS = getOpponentsSts.executeQuery();

			while (opponentsRS.next())
			{
				opponentsNames.add(opponentsRS.getString("Nickname"));
			}

			return opponentsNames;
		}
		catch (Exception e)
		{
			return null;
		}
	}
		
	public static boolean insertGameHistory(int userID, GameHistory gameHistory)
	{
		String sql = "INSERT INTO GAMEHISTORY(UserID, GameDate, GameScore) VALUES(?, ?, ?)";
		
		try
		{
			PreparedStatement insertGameHistorySts = prepareGeneralStatement(sql);
			insertGameHistorySts.setInt(1, userID);
			insertGameHistorySts.setDate(2, gameHistory.getGameDate());
			insertGameHistorySts.setInt(3, gameHistory.getGameScore());
			
			if (insertGameHistorySts.executeUpdate() == 1)
			{
				int historyID = getLastIdentity();
				
				if (historyID != 0)
					insertOpponents(historyID, gameHistory.getOpponentPlayersNames());
				
				return true;
			}
			
			return false;
		}
		catch (Exception e) 
		{
			return false;
		}
	}

	private static void insertOpponents(int historyID, List<String> opponentPlayersNames)
	{
		String sql = "INSERT INTO OPPONENTS(HistoryID, Nickname) VALUES(?, ?)";
		
		try
			{
			PreparedStatement insertOpponentSts = prepareGeneralStatement(sql);
			insertOpponentSts.setInt(1, historyID);
			
			for (int i = 0; i < opponentPlayersNames.size(); i++)
			{
				insertOpponentSts.setString(2, opponentPlayersNames.get(i));
				
				insertOpponentSts.executeUpdate();
			}
		}
		catch (Exception e) 
		{
			return;
		}
		
	}

	public static int getLastIdentity() throws SQLException
	{
		String sql = "VALUES IDENTITY_VAL_LOCAL()";
		
		PreparedStatement getLastIdentitySts = prepareGeneralStatement(sql);
		ResultSet lastIdentityRS = getLastIdentitySts.executeQuery();
						
		if (lastIdentityRS.next())
			return(lastIdentityRS.getInt(1));
		else
			return 0;
	}
	
}