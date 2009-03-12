package tau.heroes.test;
import junit.framework.TestCase;
import tau.heroes.*;

public class ResourcesTest extends TestCase
{
	Player player1 = new Player("Ariel");
	Player player2 = new Player("Ido");
	Resource r = new Resource(ResourceType.WOOD);
	Hero h1 = new Hero(player2);
	Hero h2 = new Hero(player2);
	Hero h3 = new Hero(player1);
	World w = new World(20, 20);

	public ResourcesTest()
	{
		w.setResourcesGrid(r, 7, 9);
	}

	public void testOwnership()
	{
		assertEquals(null, r.owner);
		r.setOwner(h1.player);
		assertEquals(h1.player, r.owner);
		assertEquals(true, r.checkOwner(h2));
		assertEquals(false, r.checkOwner(h3));
		r.setOwner(h3.player);
		assertEquals(true, r.checkOwner(h3));
		//assertEquals(true, );
		assertEquals(1, player1.getQuantity("wood"));
	}

	public void testOccupation()
	{
		h1.moveTo(7, 9, w);
		assertEquals(true, r.checkOwner(h1));
		h1.moveTo(5, 4, w);
		h3.moveTo(7, 9, w);
		assertEquals(true, w.getResourcesGrid(7, 9).checkOwner(h3));
	}

}
