package tau.heroes;

import java.io.Serializable;

public enum CastleType implements Serializable {

	CASTLE("Castle"),
	DUNGEON("Dungeon"),
	RAMPART("Rampart"),
	TOWER("Tower");
	
	private final String name;
	
	private CastleType(String name){
		this.name = name;
	}
	
	public String castleNameByType(){
		return this.name;
	}
}