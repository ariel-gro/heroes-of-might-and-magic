package tau.heroes.test;

import static org.junit.Assert.*;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tau.heroes.Army;
import tau.heroes.Creature;
import tau.heroes.GameState;
import tau.heroes.Goblin;
import tau.heroes.Hero;
import tau.heroes.Player;
import tau.heroes.Soldier;

public class AttackGUITest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	//remove comment to test the fight...(keep the @test) @Test
	public void testOpen()
	{
		GameState state = new GameState(true);
		Player p1 = new Player("Test1");
		Player p2 = new Player("Test2");
		Creature[] c1 = new Creature[5];
		c1[0] = new Soldier(12);//12 will win 11 will lose with 0 (addToDamage).
		Army a1 = new Army(c1);
		Creature[] c2 = new Creature[5];
		c2[0] = new Goblin(10);
		Army a2 = new Army(c2);
		//different types of creatures.
		Hero h1 = new Hero(3,3,a1);
		h1.player = p1;
		Hero h2 = new Hero(3,3,a2);
		h2.player = p2;
		//fight:
		Display d = new Display();
		h1.attack(h2);
        d.dispose();
		//test:
		assertEquals(h1.alive(),true);
		assertEquals(h2.alive(),false);

	}

}
