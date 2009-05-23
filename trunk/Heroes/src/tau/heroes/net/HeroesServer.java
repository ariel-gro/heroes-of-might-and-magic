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
import java.util.concurrent.atomic.AtomicInteger;

import tau.heroes.db.UserInfo;

public class HeroesServer extends NetworkServer
{
	public static int SERVER_PORT = 40111;
	public static String LOBBY_ROOM_NAME = "Lobby";
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
		
		lobby = new Room(LOBBY_ROOM_NAME);
		addRoom(lobby);
	}

	public void addRoom(Room room)
	{
		rooms.put(room.getId(), room);
		roomNames.add(room.getName());
	}

	public void removeRoom(Room room)
	{
		rooms.remove(room.getId());
		roomNames.remove(room.getName());
	}
	
	public Room getLobby()
	{
		return lobby;
	}
	
	public List<HeroesServerPeer> getPeers()
	{
		return peers;
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
		userInfo.setUsername(username);
		userInfo.setGuest(true);
		
		return userInfo;
	}

	public static void main(String[] args)
	{
		try
		{
			HeroesServer server = new HeroesServer();
			server.startListening();
			System.out.println("Server started");
			Scanner scanner = new Scanner(System.in);
			scanner.next();
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
