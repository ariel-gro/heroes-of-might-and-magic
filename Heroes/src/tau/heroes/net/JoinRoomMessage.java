package tau.heroes.net;

import java.util.UUID;

public class JoinRoomMessage extends SyncMessage
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private UUID id;
	
	public JoinRoomMessage(UUID id)
	{
		this.id = id;
	}
	
	public UUID getId()
	{
		return id;
	}
}
