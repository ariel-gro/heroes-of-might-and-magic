package tau.heroes.net;

import java.io.Serializable;
import java.util.UUID;

import tau.heroes.db.UserInfo;

public class RoomInfo implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private UUID id;
	private String name;
	private UserInfo owner = null;
	private int memberCount;

	public RoomInfo(UUID id, String name)
	{
		this.id = id;
		this.name = name;
	}

	public UUID getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public UserInfo getOwner()
	{
		return owner;
	}

	public void setOwner(UserInfo owner)
	{
		this.owner = owner;
	}

	public int getMemberCount()
	{
		return memberCount;
	}

	public void setMemberCount(int memberCount)
	{
		this.memberCount = memberCount;
	}
}
