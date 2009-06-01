package tau.heroes.db.test;

import static org.junit.Assert.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tau.heroes.db.DataAccess;
import tau.heroes.db.GameHistory;
import tau.heroes.db.UserInfo;

public class DataAccessTest
{
	UserInfo user1;
	UserInfo user2;
	UserInfo user3;
	UserInfo user4;
	
	GameHistory gameHistory1;
	GameHistory gameHistory2;
	
	List<String> opponentPlayers;

	@Before
	public void setUp() throws Exception
	{
		user1 = new UserInfo("user 1", "password 1", "email 1", "nick 1");
		user2 = new UserInfo("user 2", "password 2", "email 2", "nick 2");
		user3 = new UserInfo("user 3", "password 3", "email 3", "nick 3");
		user4 = new UserInfo("user 4", "password 4", "email 4", "nick 4");
		
		opponentPlayers = new ArrayList<String>();
		opponentPlayers.add(user1.getNickname());
		opponentPlayers.add(user2.getNickname());
				
		gameHistory1 = new GameHistory();
		gameHistory1.setGameDate(Date.valueOf("2009-06-22"));
		gameHistory1.setGameScore(5);
		gameHistory1.setOpponentPlayersNames(opponentPlayers);
		
		opponentPlayers.add(user3.getNickname());
		opponentPlayers.add(user4.getNickname());
		
		gameHistory2 = new GameHistory();
		gameHistory2.setGameDate(Date.valueOf("2009-12-22"));
		gameHistory2.setGameScore(10);
		gameHistory2.setOpponentPlayersNames(opponentPlayers);
		
		
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testInit()
	{
		assertTrue(DataAccess.init());
	}
		
	@Test
	public void testAddUser()
	{
		assertTrue(DataAccess.addUser(user1));
		assertTrue(DataAccess.addUser(user2));
		assertTrue(DataAccess.addUser(user3));
		assertTrue(DataAccess.addUser(user4));
	}

	@Test
	public void testValidateUser()
	{
		assertTrue(DataAccess.validateUser(user1.getUsername(), user1.getPassword()));
		assertFalse(DataAccess.validateUser(user1.getUsername(), "wrong password"));
		assertFalse(DataAccess.validateUser("wrong username", user1.getPassword()));
		
		assertTrue(DataAccess.validateUser(user2.getUsername(), user2.getPassword()));
		assertFalse(DataAccess.validateUser(user2.getUsername(), "wrong password"));
		assertFalse(DataAccess.validateUser("wrong username", user2.getPassword()));
		
		assertTrue(DataAccess.validateUser(user3.getUsername(), user3.getPassword()));
		assertFalse(DataAccess.validateUser(user3.getUsername(), "wrong password"));
		assertFalse(DataAccess.validateUser("wrong username", user3.getPassword()));
		
		assertTrue(DataAccess.validateUser(user4.getUsername(), user4.getPassword()));
		assertFalse(DataAccess.validateUser(user4.getUsername(), "wrong password"));
		assertFalse(DataAccess.validateUser("wrong username", user4.getPassword()));
		
		assertFalse(DataAccess.validateUser("wrong username", "wrong password"));		
	}

	@Test
	public void testGetUserInfo()
	{
		UserInfo tempUser = DataAccess.getUserInfo(user1.getUsername());
		assertEquals(tempUser.getUsername(), user1.getUsername());		
		assertEquals(tempUser.getEmail(), user1.getEmail());
		assertEquals(tempUser.getNickname(), user1.getNickname());
		assertEquals(tempUser.getTotalScore(), user1.getTotalScore());
		user1.setUserID(tempUser.getUserID());
		
		tempUser = DataAccess.getUserInfo(user2.getUsername());
		assertEquals(tempUser.getUsername(), user2.getUsername());
		assertEquals(tempUser.getEmail(), user2.getEmail());
		assertEquals(tempUser.getNickname(), user2.getNickname());
		assertEquals(tempUser.getTotalScore(), user2.getTotalScore());
		user2.setUserID(tempUser.getUserID());
	}
	
	@Test
	public void testInsertGameHistory()
	{
		assertTrue(DataAccess.insertGameHistory(user1.getUserID(), gameHistory1));
		assertTrue(DataAccess.insertGameHistory(user1.getUserID(), gameHistory2));
		
	}

	@Test
	public void testGetGameHistory()
	{
		List<GameHistory> gameHistoryList = DataAccess.getGameHistory(user1.getUserID());
		
		GameHistory tempGameHistory = gameHistoryList.get(0);
		assertEquals(tempGameHistory.getGameDate(), gameHistory1.getGameDate());
		assertEquals(tempGameHistory.getGameScore(), gameHistory1.getGameScore());
		assertEquals(tempGameHistory.getOpponentPlayersNames(), gameHistory1.getOpponentPlayersNames());
		
		tempGameHistory = gameHistoryList.get(1);
		assertEquals(tempGameHistory.getGameDate(), gameHistory2.getGameDate());
		assertEquals(tempGameHistory.getGameScore(), gameHistory2.getGameScore());
		assertEquals(tempGameHistory.getOpponentPlayersNames(), gameHistory2.getOpponentPlayersNames());
	}
	
	@Test
	public void testCheckUsernameExists()
	{
		assertTrue(DataAccess.checkUsernameExists(user1.getUsername()));
		assertFalse(DataAccess.checkUsernameExists("no way"));
	}
	
}
