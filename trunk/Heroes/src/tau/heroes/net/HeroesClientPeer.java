package tau.heroes.net;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import tau.heroes.db.UserInfo;

public class HeroesClientPeer extends NetworkPeer
{
	private boolean isLoggedIn = false;
	private UserInfo userInfo = null;
	private RoomInfo roomInfo = null;
	private List<RoomInfo> roomsList = null;
	private List<UserInfo> roomMembers = null;
	private List<ChatListener> chatListeners;
	private List<GameStateListener> gameSateListeners;
	private List<RoomUpdateListener> roomUpdateListeners;
	private List<GameOverListner> gameOverListeners;

	private static HeroesClientPeer instance;

	public synchronized static HeroesClientPeer instance()
	{
		if (instance == null)
			instance = new HeroesClientPeer();

		return instance;
	}

	private HeroesClientPeer()
	{
		super();

		chatListeners = new LinkedList<ChatListener>();
		gameSateListeners = new LinkedList<GameStateListener>();
		roomUpdateListeners = new LinkedList<RoomUpdateListener>();
		gameOverListeners = new LinkedList<GameOverListner>();
		registerInternalListeners();
	}

	public boolean isLoggedIn()
	{
		return isLoggedIn;
	}

	public UserInfo getUserInfo()
	{
		return userInfo;
	}

	public RoomInfo getRoomInfo()
	{
		return roomInfo;
	}

	public List<RoomInfo> getRoomsList()
	{
		return roomsList;
	}

	public List<UserInfo> getRoomMembers()
	{
		return roomMembers;
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
			roomsList = ((RoomListResponseMessage) reply).getRooms();
			roomInfo = ((RoomListResponseMessage) reply).getRoomInfo();
			roomMembers = ((RoomListResponseMessage) reply).getMembers();
			return new NetworkResult<List<RoomInfo>>(roomsList);
		}
		else if (reply instanceof ErrorMessage)
			return new NetworkResult<List<RoomInfo>>(null, ((ErrorMessage) reply).getText());
		else
			return new NetworkResult<List<RoomInfo>>(null, "Unknown Reply");
	}

	public NetworkResult<List<UserInfo>> getRoomMembers(UUID roomID)
	{
		RoomMembersRequestMessage message = new RoomMembersRequestMessage(roomID);

		Message reply = syncSendMessage(message);

		if (reply == null)
			return new NetworkResult<List<UserInfo>>(null, "Network Error");
		else if (reply instanceof RoomMembersResponseMessage)
		{
			List<UserInfo> roomMembers = ((RoomMembersResponseMessage) reply).getMembers();
			return new NetworkResult<List<UserInfo>>(roomMembers);
		}
		else if (reply instanceof ErrorMessage)
			return new NetworkResult<List<UserInfo>>(null, ((ErrorMessage) reply).getText());
		else
			return new NetworkResult<List<UserInfo>>(null, "Unknown Reply");
	}

	public NetworkResult<Boolean> startNewNetworkGame()
	{		
		NewGameMessage message = new NewGameMessage();

		Message reply = syncSendMessage(message);

		if (reply == null)
			return new NetworkResult<Boolean>(false, "Network Error");
		else if (reply instanceof OKMessage)
		{
			return new NetworkResult<Boolean>(true);
		}
		else if (reply instanceof ErrorMessage)
			return new NetworkResult<Boolean>(false, ((ErrorMessage) reply).getText());
		else
			return new NetworkResult<Boolean>(false, "Unknown Reply");
	}
	
	public NetworkResult<Boolean> createRoom(String name)
	{
		CreateRoomMessage message = new CreateRoomMessage(name);

		Message reply = syncSendMessage(message);

		if (reply == null)
			return new NetworkResult<Boolean>(false, "Network Error");
		else if (reply instanceof OKMessage)
		{
			return new NetworkResult<Boolean>(true);
		}
		else if (reply instanceof ErrorMessage)
			return new NetworkResult<Boolean>(false, ((ErrorMessage) reply).getText());
		else
			return new NetworkResult<Boolean>(false, "Unknown Reply");
	}
	
	public NetworkResult<Boolean> joinRoom(UUID id)
	{
		JoinRoomMessage message = new JoinRoomMessage(id);

		Message reply = syncSendMessage(message);

		if (reply == null)
			return new NetworkResult<Boolean>(false, "Network Error");
		else if (reply instanceof OKMessage)
		{
			return new NetworkResult<Boolean>(true);
		}
		else if (reply instanceof ErrorMessage)
			return new NetworkResult<Boolean>(false, ((ErrorMessage) reply).getText());
		else
			return new NetworkResult<Boolean>(false, "Unknown Reply");
	}

	@Override
	public void handleIncomingAsyncMessage(AsyncMessage message)
	{
		if (message instanceof ChatMessage)
			handleIncomingChatMessage((ChatMessage) message);
		else if (message instanceof GameStateMessage)
			handleIncomingGameStateMessage((GameStateMessage) message);
		else if (message instanceof RoomUpdateMessage)
			handleIncomingRoomUpdateMessage((RoomUpdateMessage) message);
		else if(message instanceof GameOverMessage)
			handleIncomingGameOverMessage((GameOverMessage) message);
	}

	private void handleIncomingChatMessage(ChatMessage message)
	{
		for (ChatListener listener : chatListeners)
			listener.chatMessageArrived(new ChatEvent(message));
	}
	private void handleIncomingGameOverMessage(GameOverMessage message)
	{
		for (GameOverListner listener : gameOverListeners)
			listener.gameOverMessageArrived(new GameOverEvent(message));
		
	}

	private void handleIncomingGameStateMessage(GameStateMessage message)
	{
		for (GameStateListener listener : gameSateListeners)
			listener.gameStateMessageArrived(new GameStateEvent(message));
	}

	private void handleIncomingRoomUpdateMessage(RoomUpdateMessage message)
	{
		for (RoomUpdateListener listener : roomUpdateListeners)
			listener.roomUpdated(new RoomUpdateEvent(message));
	}

	public void addChatListener(ChatListener listener)
	{
		chatListeners.add(listener);
	}
	public void removeChatListener(ChatListener listener)
	{
		chatListeners.remove(listener);
	}

	public void addGameStateListener(GameStateListener listener)
	{
		gameSateListeners.add(listener);
	}
	public void removeGameStateListener(GameStateListener listener)
	{
		gameSateListeners.remove(listener);
	}


	public void addRoomUpdateListener(RoomUpdateListener listener)
	{
		roomUpdateListeners.add(listener);
	}

	public void removeRoomUpdateListener(RoomUpdateListener listener)
	{
		roomUpdateListeners.remove(listener);
	}

	private void registerInternalListeners()
	{
		addRoomUpdateListener(new RoomUpdateListener() {

			public void roomUpdated(RoomUpdateEvent e)
			{
				RoomUpdateMessage message = e.getMessage();
				int index = -1;
				if (roomsList != null)
					index = roomsList.indexOf(message.getRoomInfo());

				switch (message.getRoomEventType())
				{
				case MemberAdded:
					if (index != -1)
						roomsList.set(index, message.getRoomInfo());
					if (message.getMember().equals(userInfo))
						roomInfo = message.getRoomInfo();
					if (roomInfo != null && roomInfo.equals(message.getRoomInfo()))
						roomMembers.add(message.getMember());
					break;
				case MemberRemoved:
					if (index != -1)
						roomsList.set(index, message.getRoomInfo());
					if (roomInfo != null && roomInfo.equals(message.getRoomInfo()))
						roomMembers.remove(message.getMember());
					if (message.getMember().equals(userInfo))
						roomInfo = null;
					break;
				case RoomOpened:
					if (roomsList != null)
						roomsList.add(message.getRoomInfo());
					break;
				case RoomClosed:
					if (roomsList != null)
						roomsList.remove(message.getRoomInfo());
					break;
				}
			}
		});
	}

	public void addGameOverListener(GameOverListner listener) 
	{
		this.gameOverListeners.add(listener);
	}
	public void removeGameOverListener(GameOverListner listener)
	{
		this.gameOverListeners.remove(listener);
	}
}