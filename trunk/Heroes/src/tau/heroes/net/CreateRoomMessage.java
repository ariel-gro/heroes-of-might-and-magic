package tau.heroes.net;

public class CreateRoomMessage extends SyncMessage
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	
	public CreateRoomMessage(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
}
