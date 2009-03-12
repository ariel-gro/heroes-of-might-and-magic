package tau.heroes;

public class World {
	private Resource[][] resourcesGrid;
	private Hero[][] herosGrid;

	public World(int x, int y){
		resourcesGrid = new Resource[x][y];
		herosGrid = new Hero[x][y];
	}

	public void setResourcesGrid(Resource resource, int x, int y) {
		this.resourcesGrid[x][y] = resource;
	}

	public Resource getResourcesGrid(int x, int y) {
		return resourcesGrid[x][y];
	}

	public void setHerosGrid(Hero hero, int x, int y) {
		this.herosGrid[x][y] = hero;
	}

	public Hero getHerosGrid(int x, int y) {
		return herosGrid[x][y];
	}

}
