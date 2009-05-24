package tau.heroes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

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
	public static final int appIcon = 0, grassIcon = 1, blueHeroIcon = 2, castleIcon = 3,
		goldMineIcon = 4, stoneIcon = 5, woodIcon = 6, blueInCastleIcon = 7,
		blueInGoldMineIcon = 8, blueInStoneIcon = 9, blueInWoodIcon = 10, blackIcon = 11,
		highscoreIcon = 12, heroesStartScreenIcon = 24, treasureGold = 25, treasureWood = 26,
		treasureStone = 27, dungeonIcon = 28, rampartIcon = 29, towerIcon = 30,
		blueInDungeonIcon = 31, blueInRampartIcon = 32, blueInTowerIcon = 33,
		fireIcon = 58, rockIcon = 59, treeStumpIcon = 60;
	
	public static final int redHeroIcon = 34, blackHeroIcon = 35, purpleHeroIcon = 36,
				redInCastleIcon = 37, blackInCastleIcon = 38, purpleInCastleIcon = 39,
				redInDungeonIcon = 40, blackInDungeonIcon = 41, purpleInDungeonIcon = 42,
				redInRampartIcon = 43, blackInRampartIcon = 44, purpleInRampartIcon = 45,
				redInTowerIcon = 46, blackInTowerIcon = 47, purpleInTowerIcon = 48,
				
				redInGoldMineIcon = 49, blackInGoldMineIcon = 50, purpleInGoldMineIcon = 51,
				redInStoneIcon = 52, blackInStoneIcon = 53, purpleInStoneIcon = 54,
				redInWoodIcon = 55, blackInWoodIcon = 56, purpleInWoodIcon = 57;

	public static final int battleGrassIcon = 13, goblinFaceRightIcon = 14,
		goblinFaceLeftIcon = 15, soldierFaceRightIcon = 16, soldierFaceLeftIcon = 17,
		dwarfFaceRightIcon = 18, dwarfFaceLeftIcon = 19, archerFaceRightIcon = 20,
		archerFaceLeftIcon = 21, fireDragonFaceRightIcon = 22, fireDragonFaceLeftIcon = 23;

	public static final int goblinFactoryIcon = 14, soldierFactoryIcon = 16, dwarfFactoryIcon = 18,
		archerFactoryIcon = 20, fireDragonFactoryIcon = 22;

	public static final int cursorAttackLeft = 2, cursorAttackRight = 3, cursorNo = 4;

	public static final int titleFontIndex = 0;

	public static final String[] stockImageLocations = { "/icons/Heroes-icon.jpg",
			"/icons/grass.png", "/icons/blue_on_grass.jpg", "/icons/Castle.jpg",
			"/icons/GoldMine.jpg", "/icons/Stone.jpg", "/icons/Wood.jpg",
			"/icons/blue_in_Castle.jpg", "/icons/blue_in_GoldMine.jpg", "/icons/blue_in_Stone.jpg",
			"/icons/blue_in_Wood.jpg", "/icons/Black.jpg", "/icons/HighScores.jpg",
			"/icons/battle_grass.jpg", "/icons/battle_goblin_face_right.jpg",
			"/icons/battle_goblin_face_left.jpg", "/icons/battle_soldier_face_right.jpg",
			"/icons/battle_soldier_face_left.jpg", "/icons/battle_dwarf_face_right.png",
			"/icons/battle_dwarf_face_left.png", "/icons/battle_archer_face_right.png",
			"/icons/battle_archer_face_left.png", "/icons/battle_fire_dragon_face_right.png",
			"/icons/battle_fire_dragon_face_left.png", "/icons/HeroesAppMain.png",
			"/icons/treasure_Gold.png", "/icons/treasure_Wood.png", "/icons/treasure_Stone.png",
			"/icons/Dungeon.png", "/icons/Rampart.png", "/icons/Tower.png",
			"/icons/blue_in_Dungeon.png", "/icons/blue_in_Rampart.png",
			"/icons/blue_in_Tower.png", "/icons/red_knight_on_grass.png",
			"/icons/black_knight_on_grass.png", "/icons/purple_knight_on_grass.png",
			"/icons/red_in_Castle.png", "/icons/black_in_Castle.png",
			"/icons/purple_in_Castle.png", "/icons/red_in_Dungeon.png", "/icons/black_in_Dungeon.png",
			"/icons/purple_in_Dungeon.png", "/icons/red_in_Rampart.png", "/icons/black_in_Rampart.png",
			"/icons/purple_in_Rampart.png", "/icons/red_in_Tower.png", "/icons/black_in_Tower.png",
			"/icons/purple_in_Tower.png",
			
			"/icons/red_knight_in_GoldMine.png", "/icons/black_knight_in_GoldMine.png",
			"/icons/purple_knight_in_GoldMine.png",
			"/icons/red_knight_in_Stone.png", "/icons/black_knight_in_Stone.png",
			"/icons/purple_knight_in_Stone.png",
			"/icons/red_knight_in_Wood.png", "/icons/black_knight_in_Wood.png",
			"/icons/purple_knight_in_Wood.png",
			
			"/icons/fire.png", "/icons/rock.png", "/icons/tree_stump.png"
			};
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

	private static Map<ResizedImageDescriptor, Image> resizedImages;

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

		if (resizedImages == null)
		{
			resizedImages = new HashMap<ResizedImageDescriptor, Image>();
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

		if (resizedImages != null)
		{
			for (Iterator<Image> it = resizedImages.values().iterator(); it.hasNext();)
			{
				Image image = it.next();
				image.dispose();
			}
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
			// Copy the font into a temp file
			InputStream is = IconCache.class.getResourceAsStream(fontDesctiptor.path);

			File file = File.createTempFile("temp", ".tmp");
			file.deleteOnExit();

			OutputStream os = new FileOutputStream(file);

			int b;
			while ((b = is.read()) > -1)
				os.write(b);

			os.close();
			is.close();

			// Get the temp file's path
			path = file.getAbsolutePath();
		}
		catch (IOException e)
		{
			return null;
		}

		// Load the font
		if (display.loadFont(path))
			return new Font(display, fontDesctiptor.name, fontDesctiptor.size, fontDesctiptor.style);

		return null;
	}

	/**
	 * Gets a resized version of the original image using provided style.
	 * Resized images are cached to save memory.
	 * 
	 * @param display
	 *            - The display.
	 * @param originalImage
	 *            - Original image.
	 * @param newWidth
	 *            - New image width.
	 * @param newHeight
	 *            - New image height.
	 * @param style
	 *            - New image style.
	 * @return - Resized image
	 */
	public static Image getResizedImage(Display display, Image originalImage, int newWidth,
		int newHeight, int style)
	{
		ResizedImageDescriptor rid = new ResizedImageDescriptor(originalImage, newWidth, newHeight,
			style);

		if (!resizedImages.containsKey(rid))
		{
			Image resizedImage = new Image(display, originalImage.getImageData()
				.scaledTo(newWidth, newHeight));
			if (style != SWT.NONE)
				resizedImage = new Image(display, resizedImage, style);
			resizedImages.put(rid, resizedImage);
		}

		return resizedImages.get(rid);
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

	private static class ResizedImageDescriptor
	{
		Image originalImage;
		Integer newWidth;
		Integer newHeight;
		Integer style;

		public ResizedImageDescriptor(Image originalImage, int newWidth, int newHeight, int style)
		{
			this.originalImage = originalImage;
			this.newWidth = newWidth;
			this.newHeight = newHeight;
			this.style = style;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof ResizedImageDescriptor)
			{
				ResizedImageDescriptor rid = (ResizedImageDescriptor) obj;
				return this.originalImage.equals(rid.originalImage)
					&& this.newWidth == rid.newWidth && this.newHeight == rid.newHeight;
			}
			else
				return super.equals(obj);
		}

		@Override
		public int hashCode()
		{
			return this.originalImage.hashCode() ^ this.newWidth.hashCode()
				^ this.newHeight.hashCode() ^ this.style.hashCode();
		}
	}
}
