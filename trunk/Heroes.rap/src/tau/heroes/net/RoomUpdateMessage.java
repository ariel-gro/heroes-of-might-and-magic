package tau.heroes.net;

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
	private UserInfo member;

	public RoomUpdateMessage(RoomEventType roomEventType, RoomInfo roomInfo, UserInfo member)
	{
		this.roomEventType = roomEventType;
		this.roomInfo = roomInfo;
		this.member = member;
	}

	public RoomEventType getRoomEventType()
	{
		return roomEventType;
	}

	public RoomInfo getRoomInfo()
	{
		return roomInfo;
	}

	public UserInfo getMember()
	{
		return member;
	}
}
