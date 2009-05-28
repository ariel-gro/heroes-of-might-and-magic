package tau.heroes.net;

import java.util.List;

public class RoomListResponseMessage extends AsyncMessage
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<RoomInfo> rooms;
	
	public RoomListResponseMessage(List<RoomInfo> rooms)
	{
		this.rooms = rooms;
	}
	
	public List<RoomInfo> getRooms()
	{
		return rooms;
	}
}
