package ch.zhaw.psit3.elfuego.sprites;

import java.util.ArrayList;

/**
 * A Road is a tile on a tiled map that is used for the FireTrucks.
 *
 * @author Kevin Winzeler
 */
public class Road {
    private ArrayList<Direction> directions;
    private String title;
    private int movementSpeed;
    private int positionX;
    private int positionY;

    /**
     * Generate a road with its attrributes and possible directions.
     *
     * @param title         is the name for the road
     * @param movementSpeed the speed a FireTruck can ride on the street
     * @param positionX     the Road's x-coordinate as nth-tile
     * @param positionY     the Road's y-coordinate as nth-tile
     */
    public Road(String title, int movementSpeed, int positionX, int positionY) {
        this.title = title;
        this.movementSpeed = movementSpeed;
        this.positionX = positionX;
        this.positionY = positionY;

        generateDirections(title);
    }

    private void generateDirections(String title) {
        directions = new ArrayList<>();
        if (title.toLowerCase().contains("crossing_all")) {
            directions.add(Direction.TOP);
            directions.add(Direction.LEFT);
            directions.add(Direction.RIGHT);
            directions.add(Direction.BOTTOM);
        }

        if (title.toLowerCase().contains("top")) {
            directions.add(Direction.TOP);
            if (title.contains("crossing")) {
                directions.add(Direction.LEFT);
                directions.add(Direction.RIGHT);
            }
        }

        if (title.toLowerCase().contains("bottom")) {
            directions.add(Direction.BOTTOM);
            if (title.contains("crossing")) {
                directions.add(Direction.LEFT);
                directions.add(Direction.RIGHT);
            }
        }

        if (title.toLowerCase().contains("left")) {
            directions.add(Direction.LEFT);
            if (title.contains("crossing")) {
                directions.add(Direction.TOP);
                directions.add(Direction.BOTTOM);
            }
        }

        if (title.toLowerCase().contains("right")) {
            directions.add(Direction.RIGHT);
            if (title.contains("crossing")) {
                directions.add(Direction.TOP);
                directions.add(Direction.BOTTOM);
            }
        }
    }

    /**
     * @return the Road's title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the Road's movement speed
     */
    public int getMovementSpeed() {
        return movementSpeed;
    }

    /**
     * The different directions a Road leads to
     *
     * @return a list of all possible directions
     */
    public ArrayList<Direction> getDirections() {
        return directions;
    }

    /**
     * @return the Road's x-position as nth-tile
     */
    public int getPositionX() {
        return positionX;
    }

    /**
     * @param positionX the Road's x-position as nth-tile
     */
    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    /**
     * @return the Road's y-position as nth-tile
     */
    public int getPositionY() {
        return positionY;
    }

    /**
     * @param positionY the Road's y-position as nth-tile
     */
    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    /**
     * Possible directions for a Road tile
     */
    public enum Direction {TOP, BOTTOM, LEFT, RIGHT}
}
