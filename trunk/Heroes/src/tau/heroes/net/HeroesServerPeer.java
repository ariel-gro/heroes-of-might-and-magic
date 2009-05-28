package tau.heroes.net;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

import tau.heroes.db.DataAccess;
import tau.heroes.db.UserInfo;

public class HeroesServerPeer extends NetworkPeer
{
	private HeroesServer heroesServer;
	private Room room;
	private boolean isLoggedIn = false;
	public UserInfo userInfo = null;
	private String serverPeerName;
	private static AtomicInteger debugCounter = new AtomicInteger(1);

	public HeroesServerPeer(HeroesServer heroesServer, Socket socket)
	{
		super(socket);

		this.heroesServer = heroesServer;

		serverPeerName = "Peer " + debugCounter.getAndAdd(1);

		try
		{
			InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();
			Inet4Address ia = (Inet4Address) isa.getAddress();
			System.out.println(serverPeerName + ": New connection from " + ia.getHostAddress()
				+ ":" + isa.getPort());
		}
		catch (Exception e)
		{
		}
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
	protected void handleDisconnect(boolean sendDisconnectMessage)
	{
		super.handleDisconnect(sendDisconnectMessage);

		if (room != null)
			room.removeMember(this);

		heroesServer.getPeers().remove(this);

		System.out.println(serverPeerName + ": Disconnected (" + sendDisconnectMessage + ")");
	}

	protected void handleRoomMessage(Message message)
	{
		System.out.println(serverPeerName + ": Chat message received");
		
		room.asyncSendMessage(message);
	}


	@Override
	protected void handleIncomingAsyncMessage(AsyncMessage message)
	{
		if (message instanceof DisconnectMessage)
			handleDisconnect(false);
		else if ((message instanceof ChatMessage) || (message instanceof GameStateMessage))
			handleRoomMessage(message);
		else
			super.handleIncomingAsyncMessage(message);
	}

	@Override
	protected AsyncMessage handleIncomingSyncMessage(SyncMessage message)
	{
		if (message instanceof LoginRequestMessage)
			return handleLoginRequest((LoginRequestMessage) message);
		else if (message instanceof RegisterRequestMessage)
			return handleRegisterRequest((RegisterRequestMessage) message);
		else
			return super.handleIncomingSyncMessage(message);
	}

	private AsyncMessage handleLoginRequest(LoginRequestMessage message)
	{
		System.out.println(serverPeerName + ": Login requested");

		if (isLoggedIn)
			return new ErrorMessage("You are already logged in.");

		if (message.isAsGuest())
		{
			userInfo = heroesServer.createGuestUser();
		}
		else
		{
			boolean bRes = DataAccess.validateUser(message.getUserName(), message.getPassword());
			if (!bRes)
			{
				return new ErrorMessage("User isn't registered (or wrong password).");
			}
			userInfo = new UserInfo();
			userInfo.setUsername(message.getUserName());
		}

		isLoggedIn = true;
		heroesServer.getLobby().addMember(this);

		System.out.println(serverPeerName + ": Login OK");

		return new LoginOKMessage(userInfo);
	}

	private AsyncMessage handleRegisterRequest(RegisterRequestMessage message)
	{
		System.out.println(serverPeerName + ": Register requested");

		if (isLoggedIn)
			return new ErrorMessage("You are already logged in.");

		userInfo = new UserInfo(message.getUserName(), message.getPassword(), message.getEmail(),
			message.getNickname());

		boolean bRes = DataAccess.addUser(userInfo);
		if (!bRes)
		{
			return new ErrorMessage("Username is already exist, please choose different username.");
		}
		
		System.out.println(serverPeerName + ": Register OK");
		
		return new LoginOKMessage(userInfo);
	}
	public String getName()
	{
		return serverPeerName;
	}
}
