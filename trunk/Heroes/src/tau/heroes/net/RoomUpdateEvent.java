package tau.heroes.net;

public class RoomUpdateEvent
{
	private RoomUpdateMessage message;
	
	public RoomUpdateEvent(RoomUpdateMessage message)
	{
		this.message = message;
	}
	
	public RoomUpdateMessage getMessage()
	{
		return message;
	}
}
