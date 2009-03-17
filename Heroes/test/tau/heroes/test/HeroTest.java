package tau.heroes.test;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import tau.heroes.*;
/**
 * @author ran
 *
 */
public class HeroTest extends TestCase {

	public HeroTest(String s)
	{
		super(s);
	}
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	public void testAttack1()
	{
	//	Hero h1 = new Hero(5,10);//Strong
	//	Hero h2 = new Hero(1,2);//weak
	//	h1.battle(h2);
	//	assertEquals(h1.alive(),false);
	//	assertEquals(h2.alive(),true);
	}
	public void testAttack2()
	{
	//	Hero h1 = new Hero(4,2,null);//weak
	//	Hero h2 = new Hero(6,7,null);//strong
	//	h1.battle(h2);
	//	assertEquals(h1.alive(),false);
	//	assertEquals(h2.alive(),true);
	}

	public void testAttack3()
	{
		Creature[] c1 = new Creature[1];
		c1[0] = new Soldier(10);
		Army a1 = new Army(c1);
		Creature[] c2 = new Creature[2];
		c2[0] = new Soldier(10);
		c2[1] = new Soldier(10);
		Army a2 = new Army(c2);

		Hero h1 = new Hero(0,0,a1);//few of creatures
		Hero h2 = new Hero(0,0,a2);//lots of creatures
		h1.setAutoFight(true);
		h2.setAutoFight(true);
		h1.attack(h2);
		assertEquals(h1.alive(),false);
		assertEquals(h2.alive(),true);
	}

	public void testAttack4()
	{
		Creature[] c1 = new Creature[5];
		c1[0] = new Soldier(20);//20 will win 19 will lose with 2 addToDamage
		Army a1 = new Army(c1);
		Creature[] c2 = new Creature[5];
		c2[0] = new Soldier(10);
		c2[1] = new Soldier(10);
		c2[4] = new Soldier(10);
		Army a2 = new Army(c2);

		Hero h1 = new Hero(5,5,a1);//few of creatures stronger hero
		Hero h2 = new Hero(3,3,a2);//more creatures weaker hero.
		h1.setAutoFight(true);
		h2.setAutoFight(true);
		h1.attack(h2);
		assertEquals(h1.alive(),true);
		assertEquals(h2.alive(),false);
	}

	public void testAttack5()
	{
		Creature[] c1 = new Creature[5];
		c1[0] = new Soldier(12);//12 will win 11 will lose with 0 (addToDamage).
		Army a1 = new Army(c1);
		Creature[] c2 = new Creature[5];
		c2[0] = new Goblin(10);
		Army a2 = new Army(c2);
		//different types of creatures.
		Hero h1 = new Hero(3,3,a1);
		Hero h2 = new Hero(3,3,a2);
		h1.setAutoFight(true);
		h2.setAutoFight(true);
		//fight:
		h1.attack(h2);
		//test:
		assertEquals(h1.alive(),true);
		assertEquals(h2.alive(),false);
	}
	public void testSoldier()
	{
		Creature s = new Soldier(10);
		assertEquals(s.get_shoots(),0);
		assertEquals(s.get_numberOfUnits(),10);
	}
	public void testGoblins()
	{
		Creature s = new Goblin(10);
		assertEquals(s.get_shoots(),0);
		assertEquals(s.get_numberOfUnits(),10);
	}

	public void testHero()
	{
		//Hero with an Army
		Creature[] c1 = new Creature[1];
		c1[0] = new Soldier(10);
		Army a1 = new Army(c1);
		Hero h1 = new Hero(0,0,a1);
		assertEquals(h1.alive(),true);
		h1.kill();
		assertEquals(h1.alive(),false);
		//Hero without an Army
		Hero h2 = new Hero(0,0,null);
		assertEquals(h2.alive(),false);
	}

	public void testArmy()
	{
		//Army with some creatures
		Creature[] c1 = new Creature[5];
		c1[4] = new Soldier(10);
		Army a1 = new Army(c1);
		assertEquals(a1.getFirstCreature(),c1[4]);
		c1[2] = new Soldier(10);
		Army a2 = new Army(c1);
		assertEquals(a2.getFirstCreature(),c1[2]);
	}
}
