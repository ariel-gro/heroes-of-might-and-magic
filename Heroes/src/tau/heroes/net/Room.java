package tau.heroes.net;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import tau.heroes.db.UserInfo;

public class Room
{
	private UUID id;
	private String name;
	private HeroesServerPeer creator;
	private List<HeroesServerPeer> members;
	private final HeroesServer heroesServer;

	public Room(String name, HeroesServer heroesServer)
	{
		this.id = UUID.randomUUID();
		this.name = name;
		this.members = Collections.synchronizedList(new LinkedList<HeroesServerPeer>());
		this.heroesServer = heroesServer;
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
		heroesServer.asyncSendMessage(new RoomUpdateMessage(
			RoomUpdateMessage.RoomEventType.MemberAdded, getRoomInfo(), member.getUserInfo()));
	}

	public void removeMember(HeroesServerPeer member)
	{
		members.remove(member);
		member.setRoom(null);
		heroesServer.asyncSendMessage(new RoomUpdateMessage(
			RoomUpdateMessage.RoomEventType.MemberRemoved, getRoomInfo(), member.getUserInfo()));
	}

	public HeroesServerPeer getCreator()
	{
		return creator;
	}

	public void setCreator(HeroesServerPeer creator)
	{
		this.creator = creator;
	}

	public void asyncSendMessage(AsyncMessage message)
	{
		for (HeroesServerPeer member : members)
		{
			member.asyncSendMessage(message);
			System.out.println(member.getName()+": send message to the room "+this.getName());
		}

	}

	
	public RoomInfo getRoomInfo()
	{
		RoomInfo roomInfo = new RoomInfo(id, name);
		roomInfo.setMemberCount(members.size());
		if (creator != null)
			roomInfo.setOwner(creator.getUserInfo());

		return roomInfo;
	}

	public List<UserInfo> getUserInfos()
	{
		List<UserInfo> userInfos = new LinkedList<UserInfo>();
		for (HeroesServerPeer member : members)
			userInfos.add(member.getUserInfo());
		return userInfos;
	}
}
