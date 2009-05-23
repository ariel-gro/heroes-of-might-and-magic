package tau.heroes.net;

import java.net.Socket;

import tau.heroes.db.UserInfo;

public class HeroesClientPeer extends NetworkPeer
{
	private boolean isLoggedIn = false;
	private UserInfo userInfo = null;

	public HeroesClientPeer()
	{
		super(new Socket());
	}

	public boolean isLoggedIn()
	{
		return isLoggedIn;
	}

	public UserInfo getUserInfo()
	{
		return userInfo;
	}

	public NetworkResult<Boolean> Login(String username, String password)
	{
		return Login(username, password, false);
	}

	public NetworkResult<Boolean> LoginAsGuest()
	{
		return Login(null, null, true);
	}

	public NetworkResult<Boolean> Login(String username, String password, boolean asGuest)
	{
		LoginRequestMessage message = new LoginRequestMessage(username, password, asGuest);

		Message reply = syncSendMessage(message);

		if (reply == null)
			return new NetworkResult<Boolean>(false, "Network Error");
		else if (reply instanceof LoginOKMessage)
		{
			isLoggedIn = true;
			userInfo = ((LoginOKMessage) reply).getUserInfo();
			return new NetworkResult<Boolean>(true);
		}
		else if (reply instanceof ErrorMessage)
			return new NetworkResult<Boolean>(false, ((ErrorMessage) reply).getText());
		else
			return new NetworkResult<Boolean>(false, "Unknown Reply");
	}
}
