package tau.heroes;

import java.io.Serializable;

public class Resource implements Serializable
{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Player owner;
	private final int xPos;
	private final int yPos;
	private final ResourceType type;

	public Resource(ResourceType type, Board theBoard, int xPos, int yPos) {
		this.type = type;
		this.owner = null;
		this.xPos = xPos;
		this.yPos = yPos;

		theBoard.placeResource(this, xPos, yPos);
	}

	public void setOwner(Player player) {
		if (this.owner != null) {
			if (!this.owner.equals(player)) {
				this.owner.decrementMineQuantity(this.getType().getTypeName());
				System.out.println("player " + this.owner.getName()
						+ " lost ownership over "
						+ this.getType().getTypeName() + " at place: ("
						+ this.getXPos() + ", " + this.getYPos() + ")\n");
			} else {
				System.out.println("this resource is already yours!");
				return;
			}
		}
		this.owner = player;
		if(player == null)
			return;
		player.incrementMineQuantity(this.getType().getTypeName());
		System.out.println("player " + this.owner.getName()
				+ " took ownership over " + this.getType().getTypeName()
				+ " at place: (" + this.getXPos() + ", " + this.getYPos()
				+ ")\n");
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