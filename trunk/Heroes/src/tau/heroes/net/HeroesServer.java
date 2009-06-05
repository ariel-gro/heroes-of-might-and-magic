package tau.heroes.net;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import tau.heroes.db.DataAccess;
import tau.heroes.db.UserInfo;
import tau.heroes.net.RoomUpdateMessage.RoomEventType;

public class HeroesServer extends NetworkServer
{
	public static int SERVER_PORT = 40111;
	private static AtomicInteger guestCounter;

	private Room lobby;
	private Map<UUID, Room> rooms;
	private Set<String> roomNames;
	private List<HeroesServerPeer> peers;

	private HeroesServer() throws IOException
	{
		super(SERVER_PORT);

		rooms = Collections.synchronizedMap(new TreeMap<UUID, Room>());
		roomNames = Collections.synchronizedSet(new TreeSet<String>());
		peers = Collections.synchronizedList(new LinkedList<HeroesServerPeer>());
		guestCounter = new AtomicInteger(1);

		lobby = new Lobby(this);
		addRoom(lobby);
	}

	public void addRoom(Room room)
	{
		rooms.put(room.getId(), room);
		roomNames.add(room.getName());

		RoomUpdateMessage message = new RoomUpdateMessage(RoomEventType.RoomOpened, room
			.getRoomInfo(), null);
		asyncSendMessage(message);
	}

	public void removeRoom(Room room)
	{
		rooms.remove(room.getId());
		roomNames.remove(room.getName());

		RoomUpdateMessage message = new RoomUpdateMessage(RoomEventType.RoomClosed, room
			.getRoomInfo(), null);
		asyncSendMessage(message);
}

	public Room getRoom(UUID roomID)
	{
		return rooms.get(roomID);
	}

	public boolean hasRoomName(String name)
	{
		return roomNames.contains(name);
	}

	public Room getLobby()
	{
		return lobby;
	}

	public void asyncSendMessage(AsyncMessage message)
	{
		for (HeroesServerPeer peer : peers)
			peer.asyncSendMessage(message);
	}

	public List<HeroesServerPeer> getPeers()
	{
		return peers;
	}

	public List<RoomInfo> getRoomInfos()
	{
		List<RoomInfo> roomInfos = new Vector<RoomInfo>();
		roomInfos.add(lobby.getRoomInfo());
		for (Room room : rooms.values())
			if (room != lobby)
				roomInfos.add(room.getRoomInfo());

		return roomInfos;
	}

	@Override
	protected void handleNewPeer(Socket newSocket)
	{
		super.handleNewPeer(newSocket);

		HeroesServerPeer newPeer = new HeroesServerPeer(this, newSocket);
		newPeer.startListening();
		peers.add(newPeer);

	}

	public UserInfo createGuestUser()
	{
		int count = guestCounter.getAndAdd(1);
		String username = "Guest " + count;

		UserInfo userInfo = new UserInfo();
		userInfo.setUserID(-count);
		userInfo.setUsername(username);
		userInfo.setNickname(username);
		userInfo.setGuest(true);

		return userInfo;
	}

	public static void main(String[] args)
	{
		try
		{
			if (DataAccess.init())
				System.out.println("DB is online");
			else
			{
				System.out.println("DB is offline");
				return;
			}

			HeroesServer server = new HeroesServer();
			server.startListening();
			System.out.println("Server started");

			if (args != null && args.length > 0 && args[0].equalsIgnoreCase("deamon"))
			{
				System.out.println("Running in deamon mode");
				return;
			}

			System.out.println("Enter 'quit' to quit...");
			Scanner scanner = new Scanner(System.in);
			while (!scanner.next().equalsIgnoreCase("quit"))
				;
			server.stopListening();
			System.out.println("Server stopped");
		}
		catch (IOException e)
		{
			System.out.println("IOException caught in main:");
			e.printStackTrace();
		}
	}
}
