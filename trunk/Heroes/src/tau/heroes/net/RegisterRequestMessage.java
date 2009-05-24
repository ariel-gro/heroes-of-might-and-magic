package tau.heroes.net;

public class RegisterRequestMessage extends SyncMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String userName = "";
	private String password = "";
	private String email 	= "";
	private String nickname = "";
	

	
	public RegisterRequestMessage(String userName, String password,String email,String nickname)
	{
		this.userName = userName;
		this.password = password;
		this.email = email;
		this.nickname = nickname;
	}
	
	
	public String getUserName()
	{
		return userName;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public String getEmail()
	{
		return email;
	}
	
	public String getNickname()
	{
		return nickname;
	}
}
