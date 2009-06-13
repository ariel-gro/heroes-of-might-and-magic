package tau.heroes.net;

import java.util.List;

import tau.heroes.db.UserInfo;

/**
 * A response message for RoomMembersRequestMessage.
 */
public class RoomMembersResponseMessage extends AsyncMessage
{
	/**
	 * For serialization.
	 */
	private static final long serialVersionUID = 1L;

	private List<UserInfo> members;

	/**
	 * Initializes a new instance of RoomMembersResponseMessage.
	 * 
	 * @param members
	 *            - Room members list.
	 */
	public RoomMembersResponseMessage(List<UserInfo> members)
	{
		this.members = members;
	}

	/**
	 * Gets the room members list.
	 * 
	 * @return Room members list.
	 */
	public List<UserInfo> getMembers()
	{
		return members;
	}
}
