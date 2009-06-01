package tau.heroes.test;

import junit.framework.TestCase;

import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;

import tau.heroes.Archer;
import tau.heroes.Army;
import tau.heroes.Creature;
import tau.heroes.Dwarf;
import tau.heroes.FireDragon;
import tau.heroes.GameState;
import tau.heroes.Goblin;
import tau.heroes.Hero;
import tau.heroes.Player;
import tau.heroes.PlayerColor;
import tau.heroes.Soldier;

public class AttackGUITest extends TestCase
{

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	public void testEmpty()
	{

	}

	// in order to test you need to rename the function testOpen
	public void tes_tOpen()
	{
		@SuppressWarnings("unused")
		GameState state = new GameState(true);
		Player p1 = new Player("Test1", PlayerColor.BLUE);
		Player p2 = new Player("Test2", PlayerColor.BLUE);
		Creature[] c1 = new Creature[5];
		// c1[0] = new Soldier(12);// 12 will win 11 will lose with 0
		// c1[1] = new Dwarf(10);
		c1[2] = new FireDragon(9);
		// c1[3] = new Archer(12);
		// (addToDamage).
		Army a1 = new Army(c1);
		Creature[] c2 = new Creature[5];
		c2[0] = new Goblin(10);
		c2[4] = new Dwarf(10);
		c2[3] = new Soldier(8);
		c2[1] = new Archer(12);
		Army a2 = new Army(c2);
		// different types of creatures.
		Hero h1 = new Hero(3, 3, a1);
		h1.player = p1;
		Hero h2 = new Hero(3, 3, a2);
		h2.player = p2;
		// fight:
		Display d = new Display();
		h1.attack(h2);
		d.dispose();
		// test:
		assertEquals(h1.alive(), true);
		assertEquals(h2.alive(), false);

	}

}
