package tau.heroes.net;

import java.net.Socket;

import tau.heroes.db.UserInfo;

public class HeroesServerPeer extends NetworkPeer
{
	private HeroesServer heroesServer;
	private Room room;
	private boolean isLoggedIn = false;
	private UserInfo userInfo = null;

	public HeroesServerPeer(HeroesServer heroesServer, Socket socket)
	{
		super(socket);

		this.heroesServer = heroesServer;
	}

	public Room getRoom()
	{
		return room;
	}

	public void setRoom(Room room)
	{
		this.room = room;
	}

	@Override
	protected void handleDisconnect()
	{
		super.handleDisconnect();
		
		if (room != null)
			room.removeMember(this);
		
		heroesServer.getPeers().remove(this);
	}
	
	@Override
	protected void handleIncomingAsyncMessage(AsyncMessage message)
	{
		if (message instanceof DisconnectMessage)
			handleDisconnect();
		else
			super.handleIncomingAsyncMessage(message);
	}

	@Override
	protected AsyncMessage handleIncomingSyncMessage(SyncMessage message)
	{
		if (message instanceof LoginRequestMessage)
			return handleLoginRequest((LoginRequestMessage) message);
		else
			return super.handleIncomingSyncMessage(message);
	}

	private AsyncMessage handleLoginRequest(LoginRequestMessage message)
	{	
		if (isLoggedIn)
			return new ErrorMessage("You are already logged in.");
		
		if (message.isAsGuest())
		{
			userInfo = heroesServer.createGuestUser();
			return new LoginOKMessage(userInfo);
		}
		else
		{
			userInfo = new UserInfo();
			userInfo.setUsername(message.getUserName());
			return new LoginOKMessage(userInfo);
		}
	}
}