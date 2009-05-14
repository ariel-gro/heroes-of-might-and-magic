package tau.heroes;

public class GameStringsHelper {
	
	public static final int GettingStarted = 0, CastleWindow = 1, HowToMove = 2, Battles = 3,
			Army = 4, Resources = 5, StatusWindow = 6, NewGameWindow = 7, Highscores = 8,
			About = 9;
	
	public static final String  DefaultHelpString = "helpful text";
	
	public static final String[] helpStrings =
	{
		/*GettingStarted*/
		"Startint to play Heroes of Might and Magic ©  (TAU Version)\n\n"
		+"This game is a turn based starategy game for 2-4 players. The first player must be\n"
		+"a user. All the rest can be either user or computer players.\n\n"

		+"The goal of the game is to be the last player left. To do so, you must build an army\n"
		+"(see more under 'Army'), gather resources (see more under 'Resources'), fight the\n"
		+"other players in battles (see more under 'Battles') and take over their castles.\n\n"

		+"A game is a scenario in which a random map is generated placing map objects such as\n"
		+"castles, and resources on the map."
		+"A player starts with one hero and one castle. The hero starts with a random\n"
		+"amount of soldier units. A player with no castles gets a 7 day period to try and\n"
		+"capture a castle. The 7 days timer is reaset when a castle is captured.\n\n"

		+"The map is located in the main part of the view. On the map you can see where\n"
		+"your castle is, your hero, resource mines and other players' heroes and catles.\n"
		+"The hero is the knight on a horse icon.\n"
		+"A player's view is bounded by the places his hero already been to. Each hero has\n"
		+"a view radius of one square in each direction from where the hero is standing.\n"
		+"Each turn is a day, and when you end a turn (using the 'End Turn' button) the turn\n"
		+"passes to the next player. When all players finished their turn by using 'End Turn'\n"
		+"the turn cycle starts again and a day passed. On each day your hero can move\n"
		+"up to 5 squares on the map (see more under 'How to Move'). By moving your hero\n"
		+"on the map you can capture resources and get into battles with other players' heros\n"
		+"and castles.\n\n"

		+"In order for your hero to win battles, you must have a strong army. To gather a strong\n"
		+"army you need to build creature factories in your castle(s), and build units in those\n"
		+"factories. To do so you need to gather resources. When a hero is in the castle, you can\n"
		+"either move units from the hero to the castle (the hero must have at least 1 unit left) by\n"
		+"using the 'Split' option in the castle's right-click menu, or move units from the castle to the\n"
		+"hero using the 'Join' option in the castle's right-click menu (the castle can be left without\n"
		+"units in it but this will make it easier to capture by enemy heroes). You can also move\n"
		+"units between hero and castle using the castle window.\n"
		+"Each creature factory in the castle has a limited amount of units that can be built. This\n"
		+"quota is regenerated when Day 7 turnes into Day 1. The amount regenerated depends\n"
		+"on the factory. No units are lost (i.e. if 1 unit was left at the end of Day 7 and 10 units\n"
		+"regenerated on Day 1 - 11 units will be available on Day 1).\n\n\n"

		+"Good Luck and we hope you enjoy the game.\n"
		+"The Heroes of Might and Magic (TAU Version) Team\n"
		+ "© 2009",

		/* Castle Window */
		"In this window you can interact with your castle and hero (if one is in it).\n\n"

		+"On the left side of this window you can see the castle's creature facories,\n"
		+"the castle's army and when a hero is present in the castle you will be able\n"
		+"to see the hero's army as well. On the bottom of the left side you will notice\n"
		+"a button 'Create Hero' which will be available if you lose a hero in a battle\n"
		+"(for more about battales see 'Battels' in the help section).\n"
		+"On the right side of this window you will get info about parts of the left side\n"
		+"whenever your cursor is over a part of the left side.\n\n"

		+"Building factories and units:\n"
		+"To build an army you need to purchase army units. To do so you must have a\n"
		+"creature factory first. On the creature factories section of the left side window\n"
		+"you can see the factories available. If a factory is grayed  out that means you\n"
		+"do not have that factory yet. To build a factory double click the image. If you\n"
		+"have the needed resources the factory will be built at once (for more about\n"
		+"resources see 'Resources' under help section).  When the factory image is not\n"
		+"gray you have that factory - only one of each factory can be built in one castle.\n"
		+"After having a creature factory you can purchase army units from that factory,\n"
		+"simply by double clicking the image of the factory. Each double click adds a unit\n"
		+"to the castle's army.\n"
		+"Factories and units prices will show on the right part of this window when your\n"
		+"cursor is over a factory or creature image respectively.\n\n"

		+"Moving units between hero and castle:\n"
		+"When a hero is present in the castle, you can move units between them. To do so\n"
		+"simply double click a creature and one unit will be moved. If you double click on\n"
		+"a creature in the castle a unit will move from the castle to the hero, and vice versa.\n"
		+"Note that a castle can be left with no creatures in it (though not recommanded since\n"
		+"it will be open for enemy to capture with no fight), while a hero must have at least\n"
		+"one unit (of some creature) left at all times.\n\n"

		+"Create Hero button:\n"
		+"Only if you lose your hero in battle, you can purchase a new hero at the castle using\n"
		+"the 'Create Hero' button. A new hero costs 3000 Gold.\n",

		/* How to Move */
		"Each hero (the hero icon is the knight on a horse icon) can move 5 squares on each day (see more\n"
		+"about days under 'Getting Started').\n"
		+"To move your hero on the map, simply click the hero icon once . After clicking your hero once\n"
		+"you will notice that the cursor has changed. Move the cursor to the desiered destination square,\n"
		+"and click that square. The hero will move to that square at once.\n"
		+"Another way to move your hero is to 'drag and drop' it on the map.\n\n"
		+"Some map objects will respond when a hero moves to them:\n"
		+"If you try to move to a castle your hero will enter that castle. If that castle is owned by you,\n"
		+"you will be able to move units to and from that castle.\n"
		+"If the castle is not owned by you, you will try to take over it. If it is defended, a battle will start\n"
		+"automatically (see more about balles under 'Battles'). If it is not defended it will become yours.\n"
		+"If you try to move your hero to a resource (see more under 'Resources') you will get ownership\n"
		+"over that resource. If it was already yours, nothing will change.\n"
		+"If you try to move your hero to another player's hero a battle will start automatically (see more\n"
		+"under 'Battles').",

		/* Battles */
		"To win this game you need to eliminate the other players. To fight another hero,\n"
		+"just walk up to him (see 'How to Move' for more about moving your hero). To fight\n"
		+"another player's castle, just move to his castle. If the castle is protected by an army,\n"
		+"a combat screen will appear automatically. If unguarded you will capture it at once.\n\n"

		+"The Combat Screen:\n"
		+"When a battle starts, you will see a battle field with your army facing the opponenets\n"
		+"army. In each turn, one of your creatures can attack an enemy target of your choice.\n"
		+"For example, if you have 3 different types of creatures you can attack 3 times in each\n"
		+"round. The cursor will change into a sword pointing to the defending army. To attack\n"
		+"just double click on your target.\n\n"

		+"Status:\n"
		+"At the bottom of the screen you will see a status screen. The screen will tell you who's turn\n"
		+"is it, what happend since the battle started, how much damge was done by an attack etc'.\n"
		+"You can hover your cursor over a creature to see how much units remains from it.\n\n"

		+"Winning and losing:\n"
		+"When one hero has no more army units left he is defeated. The other hero is the winner of\n"
		+"the battle. The defeated hero is remove from the map. If you were fighting for a castle, and\n"
		+"won you now own the castle, good for you!\n"
		+"If you lose your hero, a new one can be purchased in a castle for only 3000 gold.\n",

		/* Army */
		"An army is a collection of creature units in a castle or travelling with a hero. To build an army\n"
		+"you need to have creature factories in your castle(s). For more about building creature\n"
		+"factories and creature units see the help under castle window.\n\n"

		+"Each creature has unique traits, such as: attack, defense, hit points, damage etc.\n"
		+"The hit points trait determains how much damage a unit can take before it parishes.\n"
		+"The demage trait determains how much damage a unit can deal to a target with 0 defense.\n"
		+"when attacking an enemy creature, the attacking creature's demage is multiplied by the\n"
		+"amount of units of that same creature in that army.\n"
		+"The attack trait is a bonus modifier on the demage a unit deals when attacking an enemy creature.\n"
		+"The defense trait is a bonus modifier that lowers the amount of demage taken by a unit when\n"
		+"attacked by an enemy creature.",

		/* Resources */
		"To win the game you should build a strong army. To build an army\n"
		+"a player must gather resources.\n"
		+"There are 3 different types of resources in this game: Gold, Wood\n"
		+"and Stone. When a day (turn) starts you recive 1000 Gold for each\n"
		+"castle you own, and for each Gold mine you own. You also get 2\n"
		+"Stone and 2 Wood for each Stone mine and wood-mill you own respectively.\n"
		+"To get ownership over mines you need to look for them on the map\n"
		+"(see more about moving in 'How to Move').\n",

		/* Status Window */
		"In this window you can see the status of the game as well as the status of your hero and castles.\n"
		+"We will go over this window from to to bottom.\n\n"

		+"At the top of this status window you will see the application's icon and a player's name. This is\n"
		+"the name of the player currently playing his turn. Under that you can see what day of the week\n"
		+"it is for your player and the 'End Turn' button. Pressing that button will instantly end your turn and\n"
		+"give the turn to the next player. Don't press that button if you're not sure you want to end your turn.\n\n"

		+"Under the 'End Turn' button you can see your hero's stats: moves left, position on the map, defense\n"
		+"skill, attack skill and army. Attack and defense skills gives you better attack and defense against\n"
		+"enemy units attacks during battles (for more about battles see 'Battels' under the help section).\n"
		+"to learn more about a creature in your hero's army put your cursor on the creature's image.\n\n"

		+"Under the hero stats you can see the stats of your kingdom: resource mines you own, your treasury\n"
		+"and castles you own. To learn more on a resource mine or treasury put your cursor on an image.\n"
		+"To learn more about a castle you own press the castle's button to open the castle window (for more\n"
		+"about the castle window check the help in the castle window).\n",

		/* New Game Window */
		"Starting a new game is easy:\n"
		+"The first player must be a user. Use the text box to enter player name.\n"
		+"A game will have as many players as names entered.\n"
		+"To add a computer player, just click the chec-box near the text line.\n"
		+"After checking that box you can decide whether the computer player will be a 'Novice' or an 'Expert' player.\n"
		+"'Novice' will make random moves on the map, while 'Expert' will also try to build an army.\n"
		+"If you wish to open a previously saved game of Heroes of Might and Magic, \n"
		+"close this window, go to File menu and select 'Open Saved Game'\n\n"
		+"For information about how to play the game check 'Getting Started' in the help section.\n\n\n"
		+"Good Luck and we hope you enjoy the game.\n"
		+"The Heroes of Might and Magic (TAU Version) Team\n"
		+ "© 2009",

		/* Game Score and Highscores */
		"The Heroes of MIght and Magic (TAU Version) has a score board.\n\n"
		+"Only the player who won the scenario (see 'Getting Started' about scenario) gets a score\n"
		+"at the end of the game. The winner gets one point for every resource mine, one point for\n"
		+"each treasury (i.e. 500 Gold equals 500 points) and one point for each unit (in castle\n"
		+"and with hero)\n"
		+"The High-Score table is available through the 'Highscores' menu on the top menu bar.\n"
		+"On the 'Highscores' menu you will find the option to view the highscores,\n"
		+"or reset the highscores table.\n",

		/* About */
		"\n" + "Heroes of Might and Magic (TAU Version)\n\n"
		+ "Developped by the Heroes team for:\n"
		+ "Advanced development of Java based systemes\n"
		+ "course at TAU\n\n"
		+ "© 2009 - All right reserved!",
	};
	
	public static final String[] headerStrings =
	{
		"Getting Started", "Castle Window", "How to Move", "Battles and Battle Window",
		"Army", "Resources", "Status Window", "New Game Window", "Game Score and Highscores",
		"Heroes of Might and Magic (TAU Version)",
	};
	
	public static String getStringByIndex(int StringInd){
		return helpStrings[StringInd];
	}
	
	public static String getHeaderByIndex(int StringInd){
		
		if (StringInd < 9)
			return "Help about: " + headerStrings[StringInd];
		else
			return headerStrings[StringInd];
	}
	
}
