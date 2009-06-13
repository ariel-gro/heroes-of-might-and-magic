package tau.heroes.net;

import java.util.UUID;

public class SyncMessage extends Message
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private UUID messageID;
	
	public UUID getMessageID()
	{
		return messageID;
	}
	
	public void setMessageID(UUID messageID)
	{
		this.messageID = messageID;
	}
}
