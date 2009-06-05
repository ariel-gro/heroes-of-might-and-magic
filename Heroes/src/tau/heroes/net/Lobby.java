package tau.heroes.net;

import tau.heroes.db.UserInfo;

public class Lobby extends Room
{
	private static String LOBBY_ROOM_NAME = "Lobby";
	private static String LOBBY_NICKNAME = "Lobby";
	private UserInfo dummyUser;

	public Lobby(HeroesServer heroesServer)
	{
		super(LOBBY_ROOM_NAME, heroesServer);
		
		dummyUser = new UserInfo();
		dummyUser.setNickname(LOBBY_NICKNAME);
	}
	
	@Override
	public RoomInfo getRoomInfo()
	{
		RoomInfo roomInfo = super.getRoomInfo();
		roomInfo.setOwner(dummyUser);
		return roomInfo;
	}
	
	@Override
	protected void closeRoom()
	{				
	}
}
