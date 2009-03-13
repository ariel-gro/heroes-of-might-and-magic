package tau.heroes;

public class Resource {

	public Player owner;
	int xPos;
	int yPos;
	int perDay;
	public ResourceType type;

	public Resource (ResourceType type){
		this.type = type;
		this.owner = null;
		this.xPos = 7;
		this.yPos = 9;
	}

	public void setOwner(Player player) {
		this.owner = player;

	}

	public boolean checkOwner(Hero hero) {
		return (this.owner.equals(hero.player));
	}


}
