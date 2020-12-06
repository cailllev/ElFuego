package ch.zhaw.psit3.elfuego.tools;

/**
 * Utility class to convert screen-coordinates to map-coordinates and vice-versa for isometric-tile-maps
 *
 * @author Kevin Winzeler
 */
public class IsometricMath {
    /**
     * Provides the tiles x-coordinate in nth-tile-number from screen input x/y in pixels
     *
     * @param x map's x-coordinates in pixels
     * @param y map's y-coordinates in pixels
     * @return the tile-number in x-direction
     */
    public static float screenToMapX(float x, float y) {
        return (float) ((((x - (MapLoader.getMapWidth() / 2)) / (0.5 * MapLoader.getTileWidth()))
                + (((MapLoader.getMapHeight() / 2) - y) / (0.5 * MapLoader.getTileHeight()))) / 2);
    }

    /**
     * Provides the tiles y-coordinate in nth-tile-number from screen input x/y in pixels
     *
     * @param x map's x-coordinates in pixels
     * @param y map's y-coordinates in pixels
     * @return the tile-number in y-direction
     */
    public static float screenToMapY(float x, float y) {
        return (float) (((((MapLoader.getMapHeight() / 2) - y) / (0.5 * MapLoader.getTileHeight()))
                + (((MapLoader.getMapWidth() / 2) - x) / (0.5 * MapLoader.getTileWidth()))) / 2);
    }

    /**
     * Provides the tiles x-coordinate in pixels from nth-tile-number coordinates e.g. (12/22)
     *
     * @param x map's x-coordinates as nth-tile
     * @param y map's y-coordinates as nth-tile
     * @return the tiles x-location in pixels
     */
    public static float mapToScreenX(float x, float y) {
        return (float) ((MapLoader.getMapWidth() / 2) + 0.5 * MapLoader.getTileWidth() * (x - y));
    }

    /**
     * Provides the tiles y-coordinate in pixels from nth-tile-number coordinates e.g. (12/22)
     *
     * @param x map's x-coordinates as nth-tile
     * @param y map's y-coordinates as nth-tile
     * @return the tiles y-location in pixels
     */
    public static float mapToScreenY(float x, float y) {
        return (float) ((MapLoader.getMapHeight() / 2) - 0.5 * MapLoader.getTileHeight() * (x + y));
    }
}
