package tau.heroes.net;

import java.io.Serializable;
import java.util.UUID;

public abstract class Message implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private UUID replyID;

	public UUID getReplyID()
	{
		return replyID;
	}

	public void setReplyID(UUID replyID)
	{
		this.replyID = replyID;
	}
}
