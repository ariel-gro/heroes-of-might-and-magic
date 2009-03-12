package tau.heroes.test;
import junit.framework.TestCase;

import org.junit.Test;
import tau.heroes.*;

public class SoldierTest extends TestCase {
	Soldier s;

	public SoldierTest() {
		s = new Soldier(10);
	}

	@Test
	public void testSoldier() {

	}

	@Test
	public void testCreature() {

	}

	@Test
	public void testGet_AlignmentType() {
		assertEquals(s.get_AlignmentType(), AlignmentType.KNIGHT);
	}

	@Test
	public void testGet_numberOfUnits() {
		assertEquals(s.get_numberOfUnits(), 10);
	}

	@Test
	public void testSet_numberOfUnits() {
		assertEquals(s.get_numberOfUnits(), 10);
		s.set_numberOfUnits(20);
		assertEquals(s.get_numberOfUnits(), 20);

	}

	@Test
	public void testToString() {
		assertEquals(s.toString(), "10 creatures of Soldier");
	}

	@Test
	public void testGet_speed() {
		assertEquals(s.get_speed(), 3);
	}

	@Test
	public void testGet_shoots() {
		assertEquals(s.get_shoots(), 0);
	}

	@Test
	public void testGet_attackSkill() {
		assertEquals(s.get_attackSkill(), 7);
	}

	@Test
	public void testGet_defenseSkill() {
		assertEquals(s.get_defenseSkill(), 7);
	}

	@Test
	public void testGet_hitPoints() {
		assertEquals(s.get_hitPoints(), 10);
	}

	@Test
	public void testGet_damage() {
		assertEquals(s.get_damage(), 3);
	}

	@Test
	public void testGet_name() {
		assertEquals(s.get_name(), "Soldier");
	}

	@Test
	public void testGet_lastUnitHitPoints() {
		assertEquals(s.get_lastUnitHitPoints(), 10);
	}

	@Test
	public void testDefendFromAttack() {
		// Starts with 10 units * 10 hit points
		assertEquals(s.get_numberOfUnits(), 10);
		assertEquals(s.get_lastUnitHitPoints(), 10);
		s.defendFromAttack(1);
		assertEquals(s.get_numberOfUnits(), 10);
		assertEquals(s.get_lastUnitHitPoints(), 9);
		s.defendFromAttack(9);
		assertEquals(s.get_numberOfUnits(), 9);
		assertEquals(s.get_lastUnitHitPoints(), 10);
		s.defendFromAttack(5);
		assertEquals(s.get_numberOfUnits(), 9);
		assertEquals(s.get_lastUnitHitPoints(), 5);
		s.defendFromAttack(8);
		assertEquals(s.get_numberOfUnits(), 8);
		assertEquals(s.get_lastUnitHitPoints(), 7);
		s.defendFromAttack(40);
		assertEquals(s.get_numberOfUnits(), 4);
		assertEquals(s.get_lastUnitHitPoints(), 7);
		s.defendFromAttack(21);
		assertEquals(s.get_numberOfUnits(), 2);
		assertEquals(s.get_lastUnitHitPoints(), 6);
		s.defendFromAttack(26);
		assertEquals(s.get_numberOfUnits(), 0);
		assertEquals(s.get_lastUnitHitPoints(), 10);
		//Test attacker bigger than last point
		s.set_numberOfUnits(2);
		s.defendFromAttack(24);
		assertEquals(s.get_numberOfUnits(), 0);
		assertEquals(s.get_lastUnitHitPoints(), 10);
	}
}
