package tau.heroes.db.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tau.heroes.db.UserInfo;

public class UserInfoTest
{
	UserInfo userInfo1;
	UserInfo userInfo2;
	
	@Before
	public void setUp() throws Exception
	{
		userInfo1 = new UserInfo("user1", "password1", "email1", "nickname1");
		userInfo2 = new UserInfo("user2", "password2", "email2", "nickname2");
	}

	@After
	public void tearDown() throws Exception
	{
	}
	
	@Test
	public void testGetUsername()
	{
		assertEquals("user1", userInfo1.getUsername());
		assertEquals("user2", userInfo2.getUsername());
	}

	@Test
	public void testSetUsername()
	{
		userInfo1.setUsername("user2");
		assertEquals("user2", userInfo1.getUsername());
		userInfo1.setUsername("user1");
		assertEquals("user1", userInfo1.getUsername());
	}

	@Test
	public void testGetPassword()
	{
		assertEquals("password1", userInfo1.getPassword());
		assertEquals("password2", userInfo2.getPassword());
	}

	@Test
	public void testSetPassword()
	{
		userInfo1.setPassword("password2");
		assertEquals("password2", userInfo1.getPassword());
		userInfo1.setPassword("password1");
		assertEquals("password1", userInfo1.getPassword());
	}

	@Test
	public void testGetEmail()
	{
		assertEquals("email1", userInfo1.getEmail());
		assertEquals("email2", userInfo2.getEmail());
	}

	@Test
	public void testSetEmail()
	{
		userInfo1.setEmail("email2");
		assertEquals("email2", userInfo1.getEmail());
		userInfo1.setEmail("email1");
		assertEquals("email1", userInfo1.getEmail());
	}

	@Test
	public void testGetNickname()
	{
		assertEquals("nickname1", userInfo1.getNickname());
		assertEquals("nickname2", userInfo2.getNickname());
	}

	@Test
	public void testSetNickname()
	{
		userInfo1.setNickname("nickname2");
		assertEquals("nickname2", userInfo1.getNickname());
		userInfo1.setNickname("nickname1");
		assertEquals("nickname1", userInfo1.getNickname());
	}

	@Test
	public void testGetTotalScore()
	{
		assertEquals(0, userInfo1.getTotalScore());
		assertEquals(0, userInfo2.getTotalScore());
	}

	@Test
	public void testSetTotalScore()
	{
		userInfo1.setTotalScore(555);
		assertEquals(555, userInfo1.getTotalScore());
	}

	@Test
	public void testGetGameHistory()
	{
		assertEquals(null, userInfo1.getGameHistory());
		assertEquals(null, userInfo2.getGameHistory());
	}
}
