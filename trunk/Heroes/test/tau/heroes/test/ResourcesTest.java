package tau.heroes.test;
import junit.framework.TestCase;
import tau.heroes.*;

public class ResourcesTest extends TestCase
{
	Player player1 = new Player("Ariel");
	Player player2 = new Player("Ido");
	Resource r = new Resource(ResourceType.WOOD, 7, 9);
	Hero h1 = new Hero(player2);
	Hero h2 = new Hero(player2);
	Hero h3 = new Hero(player1);

	public void testOwnership()
	{
		assertEquals(null, r.getOwner());
		r.setOwner(h1.player);
		assertEquals(1, player2.getQuantity("wood"));
		assertEquals(h1.player, r.getOwner());
		assertEquals(true, r.checkOwner(h2.player));
		assertEquals(false, r.checkOwner(h3.player));
		r.setOwner(h3.player);
		assertEquals(true, r.checkOwner(h3.player));
		assertEquals(1, player1.getQuantity("wood"));
		assertEquals(0, player2.getQuantity("wood"));
	}
}
