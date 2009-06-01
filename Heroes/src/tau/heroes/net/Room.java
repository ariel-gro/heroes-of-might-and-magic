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
		this.asyncSendMessage(new RoomUpdateMessage(RoomUpdateMessage.RoomEventType.MemberAdded,
			getRoomInfo(), getUserInfos()));
	}

	public void removeMember(HeroesServerPeer member)
	{
		members.remove(member);
		member.setRoom(null);
		this.asyncSendMessage(new RoomUpdateMessage(RoomUpdateMessage.RoomEventType.MemberRemoved,
			getRoomInfo(), getUserInfos()));
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
		int index = 0;
		for (HeroesServerPeer member : members)
		{
			if (message instanceof GameStateMessage)
			{
				((GameStateMessage) message).setIndex(index);
			}
			member.asyncSendMessage(message);
			index++;
			System.out.println(member.getName() + ": Chat message sent");
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
