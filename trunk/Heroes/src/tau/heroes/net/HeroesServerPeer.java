package tau.heroes.net;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import tau.heroes.GameController;
import tau.heroes.Player;
import tau.heroes.PlayerColor;
import tau.heroes.db.DataAccess;
import tau.heroes.db.UserInfo;

public class HeroesServerPeer extends NetworkPeer
{
	private HeroesServer heroesServer;
	private Room room;
	private boolean isLoggedIn = false;
	private UserInfo userInfo = null;
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
			printDebug("New connection from " + ia.getHostAddress() + ":" + isa.getPort());
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

	public UserInfo getUserInfo()
	{
		return userInfo;
	}

	@Override
	protected void handleDisconnect(boolean sendDisconnectMessage)
	{
		super.handleDisconnect(sendDisconnectMessage);

		if (room != null)
			room.removeMember(this);

		heroesServer.getPeers().remove(this);

		printDebug("Disconnected (" + sendDisconnectMessage + ")");
	}

	protected void handleChatMessage(AsyncMessage message)
	{
		printDebug("Chat message received");

		room.asyncSendMessage(message);
	}

	private void handleGameStateMessage(GameStateMessage message)
	{
		printDebug("game state message recieved");
		room.handleGameStateMessage(message);
	}

	private AsyncMessage handleNewGameRequest(NewGameMessage message)
	{
		GameController gc = new GameController(true);
		Vector<Player> players = new Vector<Player>();
		int i = 0;
		PlayerColor[] colors = PlayerColor.values();
		for (HeroesServerPeer peer : room.getMembers())
		{
			players.add(new Player(peer.userInfo.getNickname(), colors[i]));
			i++;
		}
		gc.initNewGame(players);
		// dispatch the message to all:
		room.startGame();
		room.asyncSendMessage(new GameStateMessage(gc.getGameState()));

		return new OKMessage();

	}

	@Override
	protected void handleIncomingAsyncMessage(AsyncMessage message)
	{
		if (message instanceof DisconnectMessage)
			handleDisconnect(false);
		else if (message instanceof ChatMessage)
			handleChatMessage(message);
		else if (message instanceof GameStateMessage)
			handleGameStateMessage((GameStateMessage) message);
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
		else if (message instanceof RoomListRequestMessage)
			return handleRoomListRequest((RoomListRequestMessage) message);
		else if (message instanceof RoomMembersRequestMessage)
			return handleRoomMembersRequest((RoomMembersRequestMessage) message);
		else if (message instanceof NewGameMessage)
			return handleNewGameRequest((NewGameMessage) message);
		else if (message instanceof CreateRoomMessage)
			return handleCreateRoomRequest((CreateRoomMessage) message);
		else if (message instanceof JoinRoomMessage)
			return handleJoinRoomRequest((JoinRoomMessage) message);
		else
			return super.handleIncomingSyncMessage(message);
	}

	private AsyncMessage handleLoginRequest(LoginRequestMessage message)
	{
		printDebug("Login requested");

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
			userInfo = DataAccess.getUserInfo(message.getUserName());
		}

		isLoggedIn = true;
		heroesServer.getLobby().addMember(this);

		printDebug("Login OK");

		return new LoginOKMessage(userInfo);
	}

	private AsyncMessage handleRegisterRequest(RegisterRequestMessage message)
	{
		printDebug("Register requested");

		if (isLoggedIn)
			return new ErrorMessage("You are already logged in.");

		userInfo = new UserInfo(message.getUserName(), message.getPassword(), message.getEmail(),
			message.getNickname());

		boolean bRes = DataAccess.addUser(userInfo);
		if (!bRes)
		{
			return new ErrorMessage("Username is already exist, please choose different username.");
		}

		printDebug("Register OK");

		return new LoginOKMessage(userInfo);
	}

	private AsyncMessage handleRoomListRequest(RoomListRequestMessage message)
	{
		printDebug("Room list requested");

		if (!isLoggedIn)
			return new ErrorMessage("You must be logged in.");

		return new RoomListResponseMessage(heroesServer.getRoomInfos(), room.getRoomInfo(), room
			.getUserInfos());
	}

	private AsyncMessage handleRoomMembersRequest(RoomMembersRequestMessage message)
	{
		printDebug("Room members list requested");

		if (!isLoggedIn)
			return new ErrorMessage("You must be logged in.");

		Room room;
		if (message.getId() == null)
			room = this.room;
		else
			room = heroesServer.getRoom(message.getId());

		if (room == null)
			return new RoomMembersResponseMessage(new Vector<UserInfo>());

		return new RoomMembersResponseMessage(room.getUserInfos());
	}

	private AsyncMessage handleCreateRoomRequest(CreateRoomMessage message)
	{
		printDebug("Create room '" + message.getName() + "' requested");

		if (!isLoggedIn)
			return new ErrorMessage("You must be logged in.");

		if (heroesServer.hasRoomName(message.getName()))
			return new ErrorMessage("Room name is taken.");

		Room room = new Room(message.getName(), heroesServer);
		room.setCreator(this);
		heroesServer.addRoom(room);
		this.room.removeMember(this);
		room.addMember(this);

		printDebug("Create room '" + message.getName() + "' OK");
		
		return new OKMessage();
	}
	
	private AsyncMessage handleJoinRoomRequest(JoinRoomMessage message)
	{
		printDebug("Join room requested");

		if (!isLoggedIn)
			return new ErrorMessage("You must be logged in.");

		if (!message.getId().equals(room.getId()))
		{
			room.removeMember(this);
			Room room = heroesServer.getRoom(message.getId());
			if (room != null)
				room.addMember(this);
		}
		
		return new OKMessage();
	}

	private void printDebug(String msg)
	{
		System.out.println(serverPeerName + ": " + msg);
	}

	public String getName()
	{
		return serverPeerName;
	}
}
