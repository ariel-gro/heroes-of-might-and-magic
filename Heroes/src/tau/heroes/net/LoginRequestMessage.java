package tau.heroes.net;

public class LoginRequestMessage extends SyncMessage
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean asGuest = false;
	private String userName = "";
	private String password = "";
	
	public LoginRequestMessage(boolean asGuest)
	{
		this.asGuest = true;
	}
	
	public LoginRequestMessage(String userName, String password)
	{
		this.userName = userName;
		this.password = password;
	}
	
	public LoginRequestMessage(String userName, String password, boolean asGuest)
	{
		this(userName, password);
		this.asGuest = asGuest;
	}
	
	public String getUserName()
	{
		return userName;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public boolean isAsGuest()
	{
		return asGuest;
	}
}
