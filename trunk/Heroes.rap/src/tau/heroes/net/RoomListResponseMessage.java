package tau.heroes.net;

import java.util.List;

import tau.heroes.db.UserInfo;

public class RoomListResponseMessage extends AsyncMessage
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<RoomInfo> rooms;
	private RoomInfo roomInfo;
	private List<UserInfo> members;

	public RoomListResponseMessage(List<RoomInfo> rooms, RoomInfo roomInfo, List<UserInfo> members)
	{
		this.rooms = rooms;
		this.roomInfo = roomInfo;
		this.members = members;
	}

	public List<RoomInfo> getRooms()
	{
		return rooms;
	}
	
	public RoomInfo getRoomInfo()
	{
		return roomInfo;
	}
	
	public List<UserInfo> getMembers()
	{
		return members;
	}
}
