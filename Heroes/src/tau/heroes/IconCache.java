package tau.heroes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

/**
 * Manages icons for the application. This is necessary as we could easily end
 * up creating thousands of icons bearing the same image.
 */
public class IconCache
{
	// Stock images
	public static final int appIcon = 0, grassIcon = 1, heroIcon = 2, castleIcon = 3,
		goldMineIcon = 4, stoneIcon = 5, woodIcon = 6, heroInCastleIcon = 7,
		heroInGlodMineIcon = 8, heroInStoneIcon = 9, heroeInWoodIcon = 10, blackIcon = 11,
		highscoreIcon = 12, heroesStartScreenIcon = 24, treasureGold = 25, treasureWood = 26,
		treasureStone = 27;
	public static final int battleGrassIcon = 13, goblinFaceRightIcon = 14,
		goblinFaceLeftIcon = 15, soldierFaceRightIcon = 16, soldierFaceLeftIcon = 17,
		dwarfFaceRightIcon = 18, dwarfFaceLeftIcon = 19, archerFaceRightIcon = 20,
		archerFaceLeftIcon = 21, fireDragonFaceRightIcon = 22, fireDragonFaceLeftIcon = 23;
	public static final int goblinFactoryIcon = 14, soldierFactoryIcon = 16, dwarfFactoryIcon = 18,
		archerFactoryIcon = 20, fireDragonFactoryIcon = 22;

	public static final int cursorAttackLeft = 2, cursorAttackRight = 3, cursorNo = 4;

	public static final int titleFontIndex = 0;

	public static final String[] stockImageLocations = { "/icons/Heroes-icon.jpg",
			"/icons/Grass3.jpg", "/icons/knight3.jpg", "/icons/Castle.jpg", "/icons/GoldMine.jpg",
			"/icons/Stone.jpg", "/icons/Wood.jpg", "/icons/knight3_in_castle.jpg",
			"/icons/knight3_in_GoldMine.jpg", "/icons/knight3_in_Stone.jpg",
			"/icons/knight3_in_Wood.jpg", "/icons/Black.jpg", "/icons/HighScores.jpg",
			"/icons/battle_grass.jpg", "/icons/battle_goblin_face_right.jpg",
			"/icons/battle_goblin_face_left.jpg", "/icons/battle_soldier_face_right.jpg",
			"/icons/battle_soldier_face_left.jpg", "/icons/battle_dwarf_face_right.png",
			"/icons/battle_dwarf_face_left.png", "/icons/battle_archer_face_right.png",
			"/icons/battle_archer_face_left.png", "/icons/battle_fire_dragon_face_right.png",
			"/icons/battle_fire_dragon_face_left.png", "/icons/HeroesAppMain.png",
			"/icons/treasure_Gold.png", "/icons/treasure_Wood.png", "/icons/treasure_Stone.png" };
	public static final String[] stockCursorLocations = { "/icons/attack_left.gif",
			"/icons/attack_right.gif", };

	public static Image stockImages[];

	public static Cursor stockCursors[];

	public static final FontDescriptor[] stockFontDescriptors = { new FontDescriptor(
		"/fonts/ALGER.TTF", "Algerian", 12, SWT.BOLD) };

	public static Font stockFonts[];

	// Cached icons
	@SuppressWarnings("unchecked")
	private static Hashtable iconCache; /* map Program to Image */

	private IconCache()
	{
	}

	/**
	 * Loads the resources
	 * 
	 * @param display
	 *            - the display
	 */
	@SuppressWarnings("unchecked")
	public static void initResources(Display display)
	{
		if (stockImages == null)
		{
			stockImages = new Image[stockImageLocations.length];

			for (int i = 0; i < stockImageLocations.length; ++i)
			{
				Image image = createStockImage(display, stockImageLocations[i]);
				if (image == null)
				{
					freeResources();
					throw new IllegalStateException("error.CouldNotLoadResources: "
						+ stockImageLocations[i]);
				}
				stockImages[i] = image;
			}
		}
		if (stockCursors == null)
		{
			stockCursors = new Cursor[] {
					null,
					new Cursor(display, SWT.CURSOR_WAIT),
					new Cursor(display, createStockImage(display, stockCursorLocations[0])
						.getImageData(), 0, 0),
					new Cursor(display, createStockImage(display, stockCursorLocations[1])
						.getImageData(), 0, 0), new Cursor(display, SWT.CURSOR_NO),
					new Cursor(display, SWT.CURSOR_UPARROW) };
		}
		iconCache = new Hashtable();

		if (stockFonts == null)
		{
			stockFonts = new Font[stockFontDescriptors.length];

			Font font = createStockFont(display, stockFontDescriptors[titleFontIndex]);
			if (font == null)
			{
				freeResources();
				throw new IllegalStateException("error.CouldNotLoadResources: "
					+ stockFontDescriptors[titleFontIndex]);
			}
			stockFonts[titleFontIndex] = font;
		}
	}

	/**
	 * Frees the resources
	 */
	@SuppressWarnings("unchecked")
	public static void freeResources()
	{
		if (stockImages != null)
		{
			for (int i = 0; i < stockImages.length; ++i)
			{
				final Image image = stockImages[i];
				if (image != null)
					image.dispose();
			}
			stockImages = null;
		}

		if (iconCache != null)
		{
			for (Enumeration it = iconCache.elements(); it.hasMoreElements();)
			{
				Image image = (Image) it.nextElement();
				image.dispose();
			}
		}

		if (stockCursors != null)
		{
			for (int i = 0; i < stockCursors.length; ++i)
			{
				final Cursor cursor = stockCursors[i];
				if (cursor != null)
					cursor.dispose();
			}
			stockCursors = null;
		}

		if (stockFonts != null)
		{
			for (int i = 0; i < stockFonts.length; i++)
			{
				final Font font = stockFonts[i];
				if (font != null)
					font.dispose();
			}
			stockFonts = null;
		}
	}

	/**
	 * Creates a stock image
	 * 
	 * @param display
	 *            the display
	 * @param path
	 *            the relative path to the icon
	 */
	private static Image createStockImage(Display display, String path)
	{
		InputStream stream = IconCache.class.getResourceAsStream(path);
		ImageData imageData = new ImageData(stream);
		ImageData mask = imageData.getTransparencyMask();
		Image result = new Image(display, imageData, mask);
		try
		{
			stream.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Load a font from the file system
	 * 
	 * @param display
	 *            - The display
	 * @param fontDesctiptor
	 *            - Font descriptor
	 * @return Font object if succeeds, otherwise false
	 */
	private static Font createStockFont(Display display, FontDescriptor fontDesctiptor)
	{
		// Convert the relative path to an absolute one
		String path = null;
		try
		{
			URL url = IconCache.class.getResource(fontDesctiptor.path);
			File file = new File(url.toURI());
			path = file.getAbsolutePath();
		}
		catch (URISyntaxException e)
		{
			return null;
		}

		// Load the font
		if (display.loadFont(path))
			return new Font(display, fontDesctiptor.name, fontDesctiptor.size, fontDesctiptor.style);

		return null;
	}

	public static Image getCreatureImage(Class<? extends Creature> creatureClass)
	{
		if (creatureClass == null)
			return stockImages[battleGrassIcon];
		else if (creatureClass.equals(Goblin.class))
			return stockImages[goblinFaceRightIcon];
		else if (creatureClass.equals(Soldier.class))
			return stockImages[soldierFaceRightIcon];
		else if (creatureClass.equals(Dwarf.class))
			return stockImages[dwarfFaceRightIcon];
		else if (creatureClass.equals(Archer.class))
			return stockImages[archerFaceRightIcon];
		else if (creatureClass.equals(FireDragon.class))
			return stockImages[fireDragonFaceRightIcon];
		else
			return stockImages[battleGrassIcon];
	}

	public static Image getCreatureImage(Creature creature)
	{
		if (creature != null)
			return getCreatureImage(creature.getClass());
		else
			return getCreatureImage((Class<? extends Creature>) null);
	}

	public static Image getMineImage(String name)
	{
		if (name == null)
			return stockImages[blackIcon];
		else if (name.equals(ResourceType.GOLD.getTypeName()))
			return stockImages[goldMineIcon];
		else if (name.equals(ResourceType.WOOD.getTypeName()))
			return stockImages[woodIcon];
		else if (name.equals(ResourceType.STONE.getTypeName()))
			return stockImages[stoneIcon];
		else
			return stockImages[blackIcon];
	}

	public static Image getResourceImage(String name)
	{
		if (name == null)
			return stockImages[blackIcon];
		else if (name.equals(ResourceType.GOLD.getTypeName()))
			return stockImages[treasureGold];
		else if (name.equals(ResourceType.WOOD.getTypeName()))
			return stockImages[treasureWood];
		else if (name.equals(ResourceType.STONE.getTypeName()))
			return stockImages[treasureStone];
		else
			return stockImages[blackIcon];
	}

	public static Image getCreatureFactoryImage(Class<? extends CreatureFactory> factoryClass)
	{
		if (factoryClass == null)
			return stockImages[battleGrassIcon];
		else if (factoryClass.equals(GoblinFactory.class))
			return stockImages[goblinFactoryIcon];
		else if (factoryClass.equals(SoldierFactory.class))
			return stockImages[soldierFactoryIcon];
		else if (factoryClass.equals(DwarfFactory.class))
			return stockImages[dwarfFactoryIcon];
		else if (factoryClass.equals(ArcherFactory.class))
			return stockImages[archerFactoryIcon];
		else if (factoryClass.equals(FireDragonFactory.class))
			return stockImages[fireDragonFactoryIcon];
		else
			return stockImages[battleGrassIcon];
	}

	private static class FontDescriptor
	{
		String path;
		String name;
		int size;
		int style;

		FontDescriptor(String path, String name, int size, int style)
		{
			this.path = path;
			this.name = name;
			this.size = size;
			this.style = style;
		}
	}
}
