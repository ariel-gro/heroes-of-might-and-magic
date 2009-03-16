package tau.heroes;





public enum ResourceType {

	WOOD("wood", 2),
	GOLD("gold", 1000),
	STONE("stone", 2),
	//GEMS("gems", 1)
	;

	private final String type;
	private final int perDay;

	private ResourceType (String type, int perDay){
		this.type = type;
		this.perDay = perDay;
	}

	public String getTypeName(){
		return this.type;
	}

	public int getPerDay() {
		return perDay;
	}

}