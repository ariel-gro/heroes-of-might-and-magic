package tau.heroes.test;

import tau.heroes.*;
import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.Test;


public class ArmyTest extends TestCase{

	Army a;
	Creature[] c;
	public ArmyTest()
	{
		c = new Creature[Army.MAX_CREATURES];
		c[0] = new Soldier(10);
		c[1] = new Goblin(10);
		a = new Army(c);
	}
	@Test
	public void testArmy() {

	}

	@Test
	public void testGetCreatures() {
		Creature[] cret = a.getCreatures();
		assertArrayEquals(cret, c);
	}

	@Test
	public void testSetCreature() {
		assertEquals(a.getCreature(2), c[2]);
		assertEquals(a.getCreature(3), c[3]);
		c[2] = new Soldier(10);
		c[3] = new Soldier(10);
		a.setCreature(2, c[2]);
		a.setCreature(3, c[3]);
		assertEquals(a.getCreature(2), c[2]);
		assertEquals(a.getCreature(3), c[3]);

	}

	@Test
	public void testGetCreature() {
		assertEquals(a.getCreature(0), c[0]);
		assertEquals(a.getCreature(1), c[1]);
		assertEquals(a.getCreature(2), c[2]);
		assertEquals(a.getCreature(3), c[3]);
		assertEquals(a.getCreature(4), c[4]);
	}

	@Test
	public void testGetFirstCreature() {
		Creature c1 =  a.getFirstCreature();
		assertEquals(c1, c[0]);
		a.setCreature(0,null);
		c1 =  a.getFirstCreature();
		assertEquals(c1, c[1]);
		a.setCreature(1,null);
		c1 =  a.getFirstCreature();
		assertEquals(null,c1);
		a.setCreature(3,c[0]);
		c1 =  a.getFirstCreature();
		assertEquals(c[0], c1);
	}

	@Test
	public void testCleanDeadCreatures() {
		a.cleanDeadCreatures();
		Creature c1 = a.getFirstCreature();
		assertNotSame(null,c1);

		a.setCreature(0, new Soldier(0));
		a.setCreature(1, new Soldier(-1));
		a.cleanDeadCreatures();
		c1 = a.getFirstCreature();
		assertEquals(null,c1);
	}

	@Test
	public void testToString() {
		String s = "creature number 1: is 10 creatures of Soldier\n"+
		"creature number 2: is 10 creatures of Goblin\n" +
		"creature number 3: is empty\n"+
		"creature number 4: is empty\n"+
		"creature number 5: is empty\n";
		assertEquals(s,a.toString());

	}

}