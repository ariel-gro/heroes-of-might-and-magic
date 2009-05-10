/**
 * 
 */
package tau.heroes.test;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tau.heroes.*;

/**
 * @author yuval eitan
 * 
 */
public class CastleTest extends TestCase
{
	Board theBoard;
	Player player1;
	Player player2;
	Hero hero1;
	Castle castle1;
	Castle castle2;
	SoldierFactory sFactory;
	GoblinFactory gFactory;
	Class<? extends CreatureFactory> sFactoryClass;
	Class<? extends CreatureFactory> gFactoryClass;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		theBoard = new Board(10);
		player1 = new Player("Jay");
		player2 = new Player("Silent Bob");
		hero1 = new Hero(player1, theBoard, 4, 4);
		castle1 = new Castle(player1, theBoard, 6, 6);
		castle2 = new Castle(player2, theBoard, 4, 6);
		sFactory = new SoldierFactory();
		gFactory = new GoblinFactory();
		sFactoryClass = sFactory.getClass();
		gFactoryClass = gFactory.getClass();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
	}

	/**
	 * Test that setup is correct
	 */
	@Test
	public void testSetup()
	{
		assertEquals(4, hero1.getXPos());
		assertEquals(4, hero1.getYPos());
		assertEquals(hero1, theBoard.getBoardState(4, 4).getHero());
		assertEquals(6, castle1.getXPos());
		assertEquals(6, castle1.getYPos());
		assertEquals(castle1, theBoard.getBoardState(6, 6).getCastle());
		assertEquals(4, castle2.getXPos());
		assertEquals(6, castle2.getYPos());
		assertEquals(castle2, theBoard.getBoardState(4, 6).getCastle());
		assertEquals(player1, castle1.getPlayer());
		assertEquals(player2, castle2.getPlayer());
		assertEquals(1, player1.getCastles().size());
		assertEquals(1, player2.getCastles().size());
		assertTrue(player1.getCastles().contains(castle1));
		assertTrue(player2.getCastles().contains(castle2));
	}

	/**
	 * Test method for {@link tau.heroes.Castle#enterHero(tau.heroes.Hero)}.
	 */
	@Test
	public void testEnterHeroOwnCastle()
	{
		Boolean result = hero1.moveTo(6, 6, theBoard);
		assertTrue(result);
		assertEquals(6, hero1.getXPos());
		assertEquals(6, hero1.getYPos());
		assertEquals(hero1, theBoard.getBoardState(6, 6).getHero());
		assertEquals(6, castle1.getXPos());
		assertEquals(6, castle1.getYPos());
		assertEquals(castle1, theBoard.getBoardState(6, 6).getCastle());
		assertEquals(player1, castle1.getPlayer());
		assertEquals(player2, castle2.getPlayer());
		assertEquals(1, player1.getCastles().size());
		assertEquals(1, player2.getCastles().size());
		assertTrue(player1.getCastles().contains(castle1));
		assertTrue(player2.getCastles().contains(castle2));
	}

	public void testEnterHeroEmptyCastle()
	{
		Boolean result = hero1.moveTo(4, 6, theBoard);
		assertTrue(result);
		assertEquals(4, hero1.getXPos());
		assertEquals(6, hero1.getYPos());
		assertEquals(hero1, theBoard.getBoardState(4, 6).getHero());
		assertEquals(4, castle2.getXPos());
		assertEquals(6, castle2.getYPos());
		assertEquals(castle2, theBoard.getBoardState(4, 6).getCastle());
		assertEquals(player1, castle1.getPlayer());
		assertEquals(player1, castle2.getPlayer());
		assertEquals(2, player1.getCastles().size());
		assertEquals(0, player2.getCastles().size());
		assertTrue(player1.getCastles().contains(castle1));
		assertTrue(player1.getCastles().contains(castle2));
	}

	public void testAddRemoveFactory()
	{
		assertTrue(!castle1.hasFactory(sFactoryClass));
		assertTrue(!castle1.hasFactory(gFactoryClass));

		castle1.addFactory(sFactory);

		assertTrue(castle1.hasFactory(sFactoryClass));
		assertTrue(!castle1.hasFactory(gFactoryClass));

		castle1.addFactory(gFactory);

		assertTrue(castle1.hasFactory(sFactoryClass));
		assertTrue(castle1.hasFactory(gFactoryClass));

		castle1.removeFactory(sFactory);

		assertTrue(!castle1.hasFactory(sFactoryClass));
		assertTrue(castle1.hasFactory(gFactoryClass));

		castle1.removeFactory(gFactory);

		assertTrue(!castle1.hasFactory(sFactoryClass));
		assertTrue(!castle1.hasFactory(gFactoryClass));
	}

	public void testCanBuildFactory()
	{
		assertTrue(!castle1.canBuildFactory(sFactoryClass));
		assertTrue(!castle1.canBuildFactory(gFactoryClass));
		assertTrue(!castle2.canBuildFactory(sFactoryClass));
		assertTrue(!castle2.canBuildFactory(gFactoryClass));

		for (ResourceType rType : ResourceType.values())
		{
			int price = sFactory.getPrice(rType.getTypeName());
			player1.incrementTreasury(rType.getTypeName(), price);
		}

		assertTrue(castle1.canBuildFactory(sFactoryClass));
	}

	public void testBuildFactory()
	{
		for (ResourceType rType : ResourceType.values())
		{
			int price = sFactory.getPrice(rType.getTypeName());
			player1.incrementTreasury(rType.getTypeName(), price);
		}

		assertTrue(castle1.canBuildFactory(sFactoryClass));
		CreatureFactory newFactory = castle1.buildFactory(sFactoryClass);
		assertNotNull(newFactory);
		assertEquals(sFactoryClass, newFactory.getClass());
		for (ResourceType rType : ResourceType.values())
			assertEquals(0, player1.getCurrentTreasuryAmount(rType.getTypeName()));

		for (ResourceType rType : ResourceType.values())
		{
			int price = gFactory.getPrice(rType.getTypeName());
			player2.incrementTreasury(rType.getTypeName(), price);
		}

		assertTrue(castle2.canBuildFactory(gFactoryClass));
		newFactory = castle2.buildFactory(gFactoryClass);
		assertNotNull(newFactory);
		assertEquals(gFactoryClass, newFactory.getClass());
		for (ResourceType rType : ResourceType.values())
			assertEquals(0, player2.getCurrentTreasuryAmount(rType.getTypeName()));
	}

	public void testGetAvailableUnits()
	{
		for (ResourceType rType : ResourceType.values())
		{
			int price = sFactory.getPrice(rType.getTypeName());
			player1.incrementTreasury(rType.getTypeName(), price);
		}

		assertTrue(castle1.canBuildFactory(sFactoryClass));
		CreatureFactory newFactory = castle1.buildFactory(sFactoryClass);
		castle1.addFactory(newFactory);
		assertNotNull(newFactory);
		assertEquals(sFactoryClass, newFactory.getClass());
		for (ResourceType rType : ResourceType.values())
			assertEquals(0, player1.getCurrentTreasuryAmount(rType.getTypeName()));

		assertEquals(0, castle1.getAvailableUnits(Soldier.class));
		assertEquals(0, castle1.getAvailableUnits(Goblin.class));
		assertEquals(0, castle2.getAvailableUnits(Soldier.class));
		assertEquals(0, castle2.getAvailableUnits(Goblin.class));

		for (int i = 0; i < 8; i++)
			player1.incrementTreasury(sFactory.getPricesPerUnit());

		assertEquals(8, castle1.getAvailableUnits(Soldier.class));
		assertEquals(0, castle1.getAvailableUnits(Goblin.class));

		for (int i = 0; i < 5; i++)
			player1.incrementTreasury(sFactory.getPricesPerUnit());

		// here we see that even though the player has more than enough treasury
		// he can only purchase the amount of units in:
		// castle1.getFactory(sFactoryClass).getUnitsLeftToBuild();

		assertEquals(10, castle1.getAvailableUnits(Soldier.class));
		assertEquals(0, castle1.getAvailableUnits(Goblin.class));

		Creature[] creatures = new Creature[Army.MAX_CREATURES];
		for (int i = 0; i < Army.MAX_CREATURES; i++)
			creatures[i] = new Goblin(5);

		castle1.setArmy(new Army(creatures));

		assertEquals(0, castle1.getAvailableUnits(Soldier.class));
		assertEquals(0, castle1.getAvailableUnits(Goblin.class));

		castle1.addFactory(new GoblinFactory());

		assertEquals(0, castle1.getAvailableUnits(Soldier.class));
	}

	public void testMakeUnits()
	{
		castle1.addFactory(sFactory);
		castle1.addFactory(gFactory);

		for (int i = 0; i < 10; i++)
			player1.incrementTreasury(sFactory.getPricesPerUnit());
		for (int i = 0; i < 10; i++)
			player1.incrementTreasury(gFactory.getPricesPerUnit());

		assertNull(castle1.getArmy());
		assertEquals(10, castle1.getAvailableUnits(Soldier.class));
		assertEquals(10, castle1.getAvailableUnits(Goblin.class));

		castle1.makeUnits(Soldier.class, 4);
		assertNotNull(castle1.getArmy());
		assertNotNull(castle1.getArmy().getCreature(0));
		assertNull(castle1.getArmy().getCreature(1));
		assertNull(castle1.getArmy().getCreature(2));
		assertNull(castle1.getArmy().getCreature(3));
		assertNull(castle1.getArmy().getCreature(4));
		assertEquals(Soldier.class, castle1.getArmy().getCreature(0).getClass());
		assertEquals(4, castle1.getArmy().getCreature(0).get_numberOfUnits());
		assertEquals(6, castle1.getAvailableUnits(Soldier.class));
		assertEquals(10, castle1.getAvailableUnits(Goblin.class));

		castle1.makeUnits(Goblin.class, 3);
		assertNotNull(castle1.getArmy());
		assertNotNull(castle1.getArmy().getCreature(0));
		assertNotNull(castle1.getArmy().getCreature(1));
		assertNull(castle1.getArmy().getCreature(2));
		assertNull(castle1.getArmy().getCreature(3));
		assertNull(castle1.getArmy().getCreature(4));
		assertEquals(Soldier.class, castle1.getArmy().getCreature(0).getClass());
		assertEquals(Goblin.class, castle1.getArmy().getCreature(1).getClass());
		assertEquals(4, castle1.getArmy().getCreature(0).get_numberOfUnits());
		assertEquals(3, castle1.getArmy().getCreature(1).get_numberOfUnits());
		assertEquals(6, castle1.getAvailableUnits(Soldier.class));
		assertEquals(7, castle1.getAvailableUnits(Goblin.class));

		castle1.makeUnits(Soldier.class, 6);
		castle1.makeUnits(Goblin.class, 7);
		assertNotNull(castle1.getArmy());
		assertNotNull(castle1.getArmy().getCreature(0));
		assertNotNull(castle1.getArmy().getCreature(1));
		assertNull(castle1.getArmy().getCreature(2));
		assertNull(castle1.getArmy().getCreature(3));
		assertNull(castle1.getArmy().getCreature(4));
		assertEquals(Soldier.class, castle1.getArmy().getCreature(0).getClass());
		assertEquals(Goblin.class, castle1.getArmy().getCreature(1).getClass());
		assertEquals(10, castle1.getArmy().getCreature(0).get_numberOfUnits());
		assertEquals(10, castle1.getArmy().getCreature(1).get_numberOfUnits());
		assertEquals(0, castle1.getAvailableUnits(Soldier.class));
		assertEquals(0, castle1.getAvailableUnits(Goblin.class));

		for (ResourceType rType : ResourceType.values())
			assertEquals(0, player1.getCurrentTreasuryAmount(rType.getTypeName()));

		for (int i = 0; i < 10; i++)
			player1.incrementTreasury(sFactory.getPricesPerUnit());
		for (int i = 0; i < 10; i++)
			player1.incrementTreasury(gFactory.getPricesPerUnit());

		assertEquals(0, castle1.getAvailableUnits(Soldier.class));
		assertEquals(0, castle1.getAvailableUnits(Goblin.class));
	}

	public void testWeekEndAndDayEnd()
	{
		castle1.addFactory(sFactory);
		castle1.addFactory(gFactory);

		for (int i = 0; i < 15; i++)
		{
			player1.incrementTreasury(sFactory.getPricesPerUnit());
			player1.incrementTreasury(gFactory.getPricesPerUnit());
		}

		assertEquals(10, castle1.getAvailableUnits(Soldier.class));
		assertEquals(10, castle1.getAvailableUnits(Goblin.class));

		int gold = player1.getCurrentTreasuryAmount("Gold");

		assertEquals("Day 1", castle1.getFactory(gFactoryClass).getDayAsString());
		assertEquals("Day 1", castle1.getFactory(sFactoryClass).getDayAsString());
		for (int i = 0; i < 6; i++)
		{
			player1.endTurn();
		}
		gold += 6 * Constants.GOLD_PER_CASTLE;
		assertEquals("Day 7", castle1.getFactory(gFactoryClass).getDayAsString());
		assertEquals("Day 7", castle1.getFactory(sFactoryClass).getDayAsString());

		player1.endTurn();
		gold += Constants.GOLD_PER_CASTLE;
		assertEquals("Day 1", castle1.getFactory(gFactoryClass).getDayAsString());
		assertEquals("Day 1", castle1.getFactory(sFactoryClass).getDayAsString());
		assertEquals(gold, player1.getCurrentTreasuryAmount("Gold"));

		assertEquals(20, castle1.getFactory(gFactoryClass).getUnitsAvailableToBuild());
		assertEquals(20, castle1.getFactory(sFactoryClass).getUnitsAvailableToBuild());

		assertEquals(0, player2.getCurrentTreasuryAmount("Gold"));
		assertEquals(1, player2.getCastles().size());
		player2.endTurn();
		assertEquals(Constants.GOLD_PER_CASTLE, player2.getCurrentTreasuryAmount("Gold"));
		player2.removeCastle(castle2);
		assertEquals(0, player2.getCastles().size());
		player2.endTurn();
		assertEquals(Constants.GOLD_PER_CASTLE, player2.getCurrentTreasuryAmount("Gold"));
	}
}
