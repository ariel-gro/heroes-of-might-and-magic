package tau.heroes.net;

import java.net.Socket;
import java.util.List;
import java.util.Vector;

import tau.heroes.db.UserInfo;

public class HeroesClientPeer extends NetworkPeer
{
	private boolean isLoggedIn = false;
	private UserInfo userInfo = null;
	private Vector<ChatListener> chatListeners;
	private Vector<GameStateListener> gameSateListeners;

	public HeroesClientPeer()
	{
		super(new Socket());

		chatListeners = new Vector<ChatListener>();
		gameSateListeners = new Vector<GameStateListener>();
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

	public NetworkResult<Boolean> Register(String username, String password, String email,
		String nickname)
	{
		RegisterRequestMessage message = new RegisterRequestMessage(username, password, email,
			nickname);

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
	
	public NetworkResult<List<RoomInfo>> getRooms()
	{
		RoomListRequestMessage message = new RoomListRequestMessage();
		
		Message reply = syncSendMessage(message);
		
		if (reply == null)
			return new NetworkResult<List<RoomInfo>>(null, "Network Error");
		else if (reply instanceof RoomListResponseMessage)
		{
			List<RoomInfo> roomList = ((RoomListResponseMessage)reply).getRooms();
			return new NetworkResult<List<RoomInfo>>(roomList);
		}
		else if (reply instanceof ErrorMessage)
			return new NetworkResult<List<RoomInfo>>(null, ((ErrorMessage) reply).getText());
		else
			return new NetworkResult<List<RoomInfo>>(null, "Unknown Reply");
	}

	@Override
	public void handleIncomingAsyncMessage(AsyncMessage message)
	{
		if (message instanceof ChatMessage)
		{
			handleIncomingChatMessage((ChatMessage) message);
		}
		if (message instanceof GameStateMessage)
		{
			handleIncomingGameStateMessage((GameStateMessage) message);
		}
	}

	private void handleIncomingChatMessage(ChatMessage message)
	{
		for (ChatListener listener : chatListeners)
			listener.chatMessageArrived(new ChatEvent(message));
	}

	private void handleIncomingGameStateMessage(GameStateMessage message)
	{
		for (GameStateListener listener : gameSateListeners)
			listener.gameStateMessageArrived(new GameStateEvent(message));
	}

	public void addChatListener(ChatListener listener)
	{
		chatListeners.add(listener);
	}

	public void addGameStateListener(GameStateListener listener)
	{
		gameSateListeners.add(listener);
	}
}