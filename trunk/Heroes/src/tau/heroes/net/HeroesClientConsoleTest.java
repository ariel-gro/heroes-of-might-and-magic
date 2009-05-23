package tau.heroes.net;

public class HeroesClientConsoleTest
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String host = "127.0.0.1";
		int port = HeroesServer.SERVER_PORT;
		
		HeroesClientPeer client = new HeroesClientPeer();
		if (client.connect(host, port))
			System.out.println("Connected to server!");	
		
		NetworkResult<Boolean> res = client.LoginAsGuest();
		
		if (res.getResult())
		{
			System.out.println("Login successful");
			System.out.println("Username: " + client.getUserInfo().getUsername());
		}
		
		client.disconnect();
	}
}
