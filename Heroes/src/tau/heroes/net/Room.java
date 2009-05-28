package tau.heroes.net;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Room
{
	private UUID id;
	private String name;
	private HeroesServerPeer creator;
	private List<HeroesServerPeer> members;

	public Room(String name)
	{
		this.id = UUID.randomUUID();
		this.name = name;
		this.members = Collections.synchronizedList(new LinkedList<HeroesServerPeer>());
	}

	public UUID getId()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public List<HeroesServerPeer> getMembers()
	{
		return members;
	}

	public void addMember(HeroesServerPeer member)
	{
		members.add(member);
		member.setRoom(this);
	}

	public void removeMember(HeroesServerPeer member)
	{
		members.remove(member);
		member.setRoom(null);
	}
	
	public HeroesServerPeer getCreator()
	{
		return creator;
	}
	
	public void setCreator(HeroesServerPeer creator)
	{
		this.creator = creator;
	}
	
	public void asyncSendMessage(Message message)
	{
		for (HeroesServerPeer member : members)
		{
			member.asyncSendMessage(message);
			System.out.println(member.getName() + ": Chat message sent");
		}
	}
}
