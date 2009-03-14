package tau.heroes;

public class Resource {

	private Player owner;
	private final int xPos;
	private final int yPos;
	private final ResourceType type;

	public Resource (ResourceType type, int xPos, int yPos){
		this.type = type;
		this.owner = null;
		this.xPos = xPos;
		this.yPos = yPos;
	}


	public void setOwner(Player player)
	{
		if (this.owner != null)
		{
			this.owner.decrementQuantity(this.getType().getTypeName());
			System.out.println("player "+this.owner.getName()+
					" lost ownership over "+this.getType().getTypeName()+
					" at place: ("+this.getXPos()+", "+this.getYPos()+")");
			System.out.println();
		}
		this.owner = player;
		player.incrementQuantity(this.getType().getTypeName());
		System.out.println("player "+this.owner.getName()+
				" took ownership over "+this.getType().getTypeName()+
				" at place: ("+this.getXPos()+", "+this.getYPos()+")");
		System.out.println();
	}

	public boolean checkOwner(Player player) {
		return (this.getOwner().equals(player));
	}

	public Player getOwner() {
		return owner;
	}

	public int getXPos() {
		return xPos;
	}

	public int getYPos() {
		return yPos;
	}

	public ResourceType getType() {
		return type;
	}


}
