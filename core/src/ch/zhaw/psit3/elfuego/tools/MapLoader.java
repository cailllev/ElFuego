package ch.zhaw.psit3.elfuego.tools;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import java.util.ArrayList;
import java.util.HashMap;

import ch.zhaw.psit3.elfuego.sprites.House;
import ch.zhaw.psit3.elfuego.sprites.Road;

/**
 * The MapLoader loads a map and iterates through all tiles to get the different objects. These
 * objects in addition with further information can then be requested for further processing.
 *
 * @author Kevin Winzeler
 */
public class MapLoader {
    private TiledMap map;
    private HashMap<String, TiledMapTileSet> tileTypes;
    private HashMap<String, Road> roads;
    private ArrayList<House> houses;
    private int id;

    // Map properties
    private static float tileWidth = 128,
            tileHeight = 64,
            numTilesWidth = 40,
            numTilesHeight = 40,
            mapWidth = tileWidth * numTilesWidth,
            mapHeight = tileHeight * numTilesHeight;

    /**
     * Loads the specified map and sets the properties to provide useful information for other classes.
     * Additionally calls the parseTileSet() and parseTiles() method to read in the different tile-types
     * from the map.
     *
     * @param mapName the name of the map that is contained in the android/assets/maps folder
     */
    public MapLoader(String mapName) {
        TmxMapLoader mapLoader = new TmxMapLoader();
        map = mapLoader.load("maps/" + mapName);

        // Save width and height properties of the map
        MapProperties mapProperties = map.getProperties();
        tileWidth = mapProperties.get("tilewidth", Integer.class);
        tileHeight = mapProperties.get("tileheight", Integer.class);
        numTilesWidth = mapProperties.get("width", Integer.class);
        numTilesHeight = mapProperties.get("height", Integer.class);
        mapWidth = numTilesWidth * tileWidth;
        mapHeight = numTilesHeight * tileHeight;
        id = 1;

        // Read-in roads and houses with their properties
        parseTileSet();
        parseTiles();
    }

    static float getTileWidth() {
        return tileWidth;
    }

    /**
     * Provides the height of an isometric tile
     *
     * @return the height of a single tile
     */
    public static float getTileHeight() {
        return tileHeight;
    }

    private static float getNumTilesWidth() {
        return numTilesWidth;
    }

    /**
     * Provides the number of tiles in y-direction
     *
     * @return the number of tiles in y-direction
     */
    static float getNumTilesHeight() {
        return numTilesHeight;
    }

    /**
     * Provides the map's width
     *
     * @return the map's width in pixels
     */
    public static float getMapWidth() {
        return mapWidth;
    }

    /**
     * Provides the map's height
     *
     * @return the map's height in pixels
     */
    public static float getMapHeight() {
        return mapHeight;
    }

    private void parseTiles() {
        TiledMapTileLayer backgroundMapTileLayer = (TiledMapTileLayer) map.getLayers().get(0);
        roads = new HashMap<>();
        houses = new ArrayList<>();
        for (int y = 0; y < getNumTilesHeight(); y++) {
            for (int x = 0; x < getNumTilesWidth(); x++) {
                TiledMapTileLayer.Cell backgroundCell = backgroundMapTileLayer.getCell(x, y);
                String title = backgroundCell.getTile().getTextureRegion().getTexture().toString()
                        .replace("maps/objects/", "")
                        .replace(".png", "");

                TiledMapTileSet tile = tileTypes.get(title);
                if (title.contains("road")) {
                    Road road = new Road(
                            tile.getName(),
                            (int) tile.getProperties().get("speed"),
                            x,
                            (int) (getNumTilesHeight() - y - 1)
                    );
                    String roadIndex = road.getPositionX() + "," + road.getPositionY();
                    roads.put(roadIndex, road);
                } else if (title.contains("building")) {
                    houses.add(new House(
                            tile.getName(),
                            id,
                            x,
                            (int) (getNumTilesHeight() - y - 1)
                    ));
                    id++;
                }
            }
        }
    }

    private void parseTileSet() {
        tileTypes = new HashMap<>();
        for (TiledMapTileSet tileSet : map.getTileSets()) {
            tileTypes.put(tileSet.getName(), tileSet);
        }
    }

    /**
     * Used to get the currently used TiledMap
     *
     * @return the TiledMap
     */
    public TiledMap getMap() {
        return map;
    }

    /**
     * Provides all tiles that contain roads from the map
     *
     * @return a HashMap containing all road tiles with their coordinates as the key
     */
    public HashMap<String, Road> getRoads() {
        return roads;
    }

    /**
     * Provides all tiles that contain houses from the map
     *
     * @return an ArrayList containing all Houses from the map
     */
    public ArrayList<House> getHouses() {
        return houses;
    }

    /**
     * The tile-sets are used to draw different kinds of objects to the map
     *
     * @return the TiledMapTileSet
     */
    public TiledMapTileSets getTileSet(){return map.getTileSets();}
}
