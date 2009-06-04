package tau.heroes.net;

import java.sql.Date;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import org.apache.derby.tools.sysinfo;

import tau.heroes.Player;
import tau.heroes.db.DataAccess;
import tau.heroes.db.GameHistory;
import tau.heroes.db.UserInfo;

public class Room
{
	private UUID id;
	private String name;
	private HeroesServerPeer creator;
	private List<HeroesServerPeer> members;
	private final HeroesServer heroesServer;
	private boolean isGameStarted;
	private GameHistory history; 

	public Room(String name, HeroesServer heroesServer)
	{
		this.id = UUID.randomUUID();
		this.name = name;
		this.members = Collections.synchronizedList(new LinkedList<HeroesServerPeer>());
		this.heroesServer = heroesServer;
		this.isGameStarted = false;
		history = new GameHistory();
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
	public boolean startGame()
	{
		if(isGameStarted)
			return false;
		isGameStarted = true;
		//save the game history:		
		history.setGameDate(new java.sql.Date(new java.util.Date().getTime()));
		List<String> opponents = new Vector<String>();
		for(HeroesServerPeer user: this.members)
		{
			opponents.add(user.getUserInfo().getNickname());
		}
		history.setOpponentPlayersNames(opponents);	
		
		return true;
	}
	public GameHistory getGameHistory()
	{
		return history;
	}
	public void handleGameStateMessage(GameStateMessage message) 
	{
		Player winner = message.getGameState().isWinner();
		List<HeroesServerPeer> roomUsers = this.getMembers();
		Vector<Player> gamePlayes = message.getGameState().getPlayers();
		if(gamePlayes.size() < roomUsers.size())
		{//One of the players quit the game (or died...)
			List<HeroesServerPeer> usersToRemove = new Vector<HeroesServerPeer>();
			//find him and remove him from the room.
			for(HeroesServerPeer user : roomUsers)
			{
				boolean bFound = false;
				for(Player p : gamePlayes)
				{
					if(user.getUserInfo().getNickname().equals(p.getName()))
					{
						bFound = true;
					}
				}
				if(!bFound)
				{
					usersToRemove.add(user);
				}
			}
			//remove all users that are not playing:
			if(usersToRemove.size() > 0)
			{
				for(HeroesServerPeer user : usersToRemove)
				{
					this.removeMember(user);
					heroesServer.getLobby().addMember(user);
					//here we should set the history to the DB
					DataAccess.insertGameHistory(user.getUserInfo().getUserID(), history);
				}
			}
			//this will notify all the others that one of the players is gone!
			handleGameOver(winner);
		}
		//The game is over we have a winner!
		if(winner != null)
		{
			List<HeroesServerPeer> members = this.getMembers();
			if(members.size() != 1)
			{
				System.out.println("handleGameStateMessage error! there is a winner and not only one user");
			}
			history.setGameScore(1);
			DataAccess.insertGameHistory(members.get(0).getUserInfo().getUserID(), history);
			DataAccess.updateTotalScore(members.get(0).getUserInfo().getUserID());
			return;	
		}
		this.asyncSendMessage(message);
		
	}
	private void handleGameOver(Player winner)
	{
		GameOverMessage goMessage = new GameOverMessage(winner);	
		this.asyncSendMessage(goMessage);
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
