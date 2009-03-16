package tau.heroes.test;
import junit.framework.TestCase;
import tau.heroes.*;

public class ResourcesTest extends TestCase
{
	Player player1 = new Player("Ariel");
	Player player2 = new Player("Ido");
	Board board = new Board(20);
	Resource r = new Resource(ResourceType.WOOD, board, 7, 9);
	Hero h1 = new Hero(player2, board, 1, 1);
	Hero h2 = new Hero(player2, board, 1, 2);
	Hero h3 = new Hero(player1, board, 1, 3);

	public void testOwnership()
	{
		assertEquals(null, r.getOwner());
		player1.displayResources();
		player2.displayResources();
		r.setOwner(h1.player);
		player1.displayResources();
		player2.displayResources();
		assertEquals(1, player2.getQuantity("wood"));
		assertEquals(h1.player, r.getOwner());
		assertEquals(true, r.checkOwner(h2.player));
		assertEquals(false, r.checkOwner(h3.player));		
		r.setOwner(h3.player);
		player1.displayResources();
		player2.displayResources();
		assertEquals(true, r.checkOwner(h3.player));
		assertEquals(1, player1.getQuantity("wood"));
		assertEquals(0, player2.getQuantity("wood"));
	}
	
	public void testPlayerResources()
	{
		Resource r2 = new Resource(ResourceType.GOLD, board, 5, 5);
		
		player1.displayResourcesAmounts();
		player1.displayResources();
		player2.displayResourcesAmounts();
		player2.displayResources();
		
		r.setOwner(player1);
		
		assertEquals(1, player1.getQuantity("wood"));
		assertEquals(0, player1.getCurrentAmount("wood"));
		assertEquals(0, player1.getCurrentAmount("gold"));
		assertEquals(0, player1.getCurrentAmount("stone"));
		
		player1.incrementAmount("wood", 5);
		
		player1.displayResourcesAmounts();
		player2.displayResourcesAmounts();
		
		assertEquals(5, player1.getCurrentAmount("wood"));
		assertEquals(0, player1.getCurrentAmount("gold"));
		assertEquals(0, player1.getCurrentAmount("stone"));
		
		player1.endTurn();
		
		player1.displayResourcesAmounts();
		player2.displayResourcesAmounts();
		
		assertEquals(7, player1.getCurrentAmount("wood"));
		assertEquals(0, player1.getCurrentAmount("gold"));
		assertEquals(0, player1.getCurrentAmount("stone"));
		
		assertEquals(0, player2.getCurrentAmount("wood"));
		assertEquals(0, player2.getCurrentAmount("gold"));
		assertEquals(0, player2.getCurrentAmount("stone"));
		
		player2.endTurn();
		
		player1.displayResourcesAmounts();
		player2.displayResourcesAmounts();
		
		assertEquals(0, player2.getCurrentAmount("wood"));
		assertEquals(0, player2.getCurrentAmount("gold"));
		assertEquals(0, player2.getCurrentAmount("stone"));
		
		r2.setOwner(player2);
		
		player1.displayResourcesAmounts();
		player2.displayResourcesAmounts();
		
		player2.endTurn();
		
		player1.displayResourcesAmounts();
		player2.displayResourcesAmounts();
		
		assertEquals(0, player2.getCurrentAmount("wood"));
		assertEquals(1000, player2.getCurrentAmount("gold"));
		assertEquals(0, player2.getCurrentAmount("stone"));
	}
}