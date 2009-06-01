package tau.heroes.net;

import java.util.List;

import tau.heroes.db.UserInfo;

/**
 * An async message sent from server to client to notify it of room events.
 */
public class RoomUpdateMessage extends AsyncMessage
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum RoomEventType
	{
		MemberAdded,
		MemberRemoved,
		RoomOpened,
		RoomClosed
	};

	private RoomEventType roomEventType;
	private RoomInfo roomInfo;
	private List<UserInfo> members;

	public RoomUpdateMessage(RoomEventType roomEventType, RoomInfo roomInfo, List<UserInfo> members)
	{
		this.roomEventType = roomEventType;
		this.roomInfo = roomInfo;
		this.members = members;
	}

	public RoomEventType getRoomEventType()
	{
		return roomEventType;
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
