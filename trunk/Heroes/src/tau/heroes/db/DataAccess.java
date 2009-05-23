package tau.heroes.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataAccess
{
	private static DBConnection dbConnection;

	private DataAccess()
	{

	}

	public static Statement createGeneralStatment() throws SQLException
	{
		return dbConnection.getConnection().createStatement();
	}

	public static PreparedStatement prepareGeneralStatement(String sql) throws SQLException
	{
		return dbConnection.getConnection().prepareStatement(sql);
	}

	public static boolean init()
	{
		try
		{
			dbConnection = new DBConnection();

			ResultSet usersTableExists = dbConnection.getConnection().getMetaData()
				.getTables(null, null, "USERS", null);

			if (!usersTableExists.next())
			{
				createGeneralStatment()
					.execute("CREATE TABLE USERS ("
						+ "UserID INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
						+ "Username VARCHAR(20) NOT NULL, " + "Password VARCHAR(10) NOT NULL, "
						+ "Email VARCHAR(50), Nickname VARCHAR(20), PRIMARY KEY (UserID))");

				usersTableExists = dbConnection.getConnection().getMetaData()
					.getTables(null, null, "USERS", null);

				return (usersTableExists.next());
			}

			return true;
		}
		catch (ClassNotFoundException e)
		{
			return false;
		}
		catch (SQLException e)
		{
			return false;
		}
	}

	public static boolean addUser(UserInfo userInfo)
	{
		try
		{
			String sql = "INSERT INTO USERS(Username, Password, Email, Nickname) VALUES(?, ?, ?, ?)";
			PreparedStatement addUserStatement = prepareGeneralStatement(sql);
			addUserStatement.setString(1, userInfo.getUsername());
			addUserStatement.setString(2, userInfo.getPassword());
			addUserStatement.setString(3, userInfo.getEmail());
			addUserStatement.setString(4, userInfo.getNickname());

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
}
