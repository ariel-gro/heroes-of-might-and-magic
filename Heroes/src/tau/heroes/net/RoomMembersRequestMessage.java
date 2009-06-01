package tau.heroes.net;

import java.util.UUID;

/**
 * A message class to request members inside a room.
 * Response for this message is a RoomMembersResponseMessage.
 */
public class RoomMembersRequestMessage extends SyncMessage
{
	/**
	 * For serialization.
	 */
	private static final long serialVersionUID = 1L;

	private UUID id;

	/**
	 * Initializes a new instance of RoomMembersRequestMessage with a null room
	 * ID, to request members from current room.
	 */
	public RoomMembersRequestMessage()
	{
		this.id = null;
	}

	/**
	 * Initializes a new instance of RoomMembersRequestMessage with the supplied
	 * room ID.
	 * 
	 * @param id
	 *            - The room ID for which the members list is requested.
	 */
	public RoomMembersRequestMessage(UUID id)
	{
		this.id = id;
	}

	/**
	 * Returns room ID.
	 * 
	 * @return Room ID.
	 */
	public UUID getId()
	{
		return id;
	}
}
