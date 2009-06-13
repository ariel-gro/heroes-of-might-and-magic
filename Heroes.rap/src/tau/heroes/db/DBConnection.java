package tau.heroes.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection
{
	private Connection connection;
	private String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	private String connectionURL = "jdbc:derby:HeroesDB;create=true";

	public DBConnection() throws ClassNotFoundException, SQLException
	{
		Class.forName(driver);

		connection = DriverManager.getConnection(connectionURL);
	}

	public Connection getConnection()
	{
		return connection;
	}
}
