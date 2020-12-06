package ch.zhaw.psit3.elfuego.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.logging.Level;

import ch.zhaw.psit3.elfuego.ElFuego;
import ch.zhaw.psit3.elfuego.logic.EndTurnLogic;
import ch.zhaw.psit3.elfuego.pathfinding.Pathfinder;
import ch.zhaw.psit3.elfuego.screens.PlayScreen;
import ch.zhaw.psit3.elfuego.sprites.House;
import ch.zhaw.psit3.elfuego.tools.BurningController;
import ch.zhaw.psit3.elfuego.tools.IsometricMath;
import ch.zhaw.psit3.elfuego.tools.MapLoader;
import ch.zhaw.psit3.elfuego.tools.Sounds;

import static java.lang.Math.abs;
import static java.lang.Math.min;

/**
 * A FireTruck can drive around the map via the Roads and is able to extinguish a burning house.
 *
 * @author Kevin Winzeler
 */
public class FireTruck extends BaseActor {
    private Texture fireTruck;
    private float positionX = IsometricMath.mapToScreenX(19, 21), positionY = IsometricMath.mapToScreenY(19, 21);
    private float targetX, targetY;
    private String direction;
    private final String texturePath = "maps/objects/fire_truck";
    private boolean isActive = false;

    // Create list to save routes
    private AtomicReferenceArray<String> route;
    private AtomicReferenceArray<String> previewRoute;
    private AtomicReferenceArray<String> subroute;

    private ArrayList<BaseActor> checkpoints;
    private ArrayList<BaseActor> path;
    private boolean lastActive;

    //new Datafields for turnlogic
    private final int MAX_MOVES = EndTurnLogic.getFireTruckMovement();
    private int moves;
    private int actionsCount = 1;

    private Stage stage;

    private BurningController burningController;
    private House extinguishingHouse;

    /**
     * Creates a new FireTruck and initializes its variables
     *  @param stage the stage it gets added
     * @param direction either _top_bottom, _bottom_top, _left_right, _right_left
     * @param burningController to start the extiungish animation
     */
    public FireTruck(Stage stage, String direction, BurningController burningController) {
        this.stage = stage;
        this.direction = direction;
        this.burningController = burningController;

        fireTruck = new Texture(Gdx.files.internal(texturePath + direction + ".png"));

        checkpoints = new ArrayList<>();
        path = new ArrayList<>();

        moves = MAX_MOVES;
        route = new AtomicReferenceArray<>(1);
        previewRoute = new AtomicReferenceArray<>(1);
        subroute = new AtomicReferenceArray<>(moves);

        float padding = 20;
        setBounds(positionX - padding, positionY - padding, fireTruck.getWidth() + 2 * padding, fireTruck.getHeight() + 2 * padding);
      
        addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                ElFuego.LOGGER.log(Level.INFO, "FireTruck activated");
                isActive = true;
                Sounds.play(Sounds.TRUCK_SELECTED);

                return true;
            }
        });
    }

    /**
     * Draws the FireTruck in the specified interval to the screen
     *
     * @param batch (supplied by Stage draw method)
     * @param alpha interval to draw the FireTruck
     */
    public void draw(Batch batch, float alpha) {
        batch.draw(fireTruck, positionX, positionY);
    }

    /**
     * Returns true if the FireTruck was selected and is still active
     *
     * @return true if the FireTruck is active
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Resets the available moves for the FireTruck and the actionsCount to 1
     * usually at the end of a round
     */
    public void resetMoves(){
        moves = MAX_MOVES;
        actionsCount = 1;
    }

    /**
     * activates the movement of the truck
     */
    public void move(){
        if (null != route.get(0)){
            int actualMoves = min(moves, route.length());
            for (int i = 0; i < actualMoves; i++) {
                subroute.set(i, route.get(i));
            }
            moves -= actualMoves;

            //update route, delete all fields copied to subroute
            updateRoute();
            setPathAndCheckpoints();
        }
    }

    /**
     * Disables the FireTruck
     */
    public void deactivate(){
        isActive = false;
    }

    /**
     * Sets the last active FireTruck
     *
     * @param wasLastActive true if the FireTruck was last active
     */
    public void setLastActive(boolean wasLastActive){
        lastActive = wasLastActive;
    }

    /**
     * Moves the FireTruck for the amount of moves along the roads.
     *
     * @param delta interval between 2 frames
     */
    @Override
    public void act(float delta) {
        if (!isActive && !lastActive) {
            setActorsToInvisible(path);
            setActorsToInvisible(checkpoints);
        }

        String isSelected = "";
        if (isActive){
            setActorsToVisible(path);
            setActorsToVisible(checkpoints);
            isSelected = "_selected";
        }

        fireTruck = new Texture(Gdx.files.internal(texturePath + direction + isSelected + ".png"));

        if (null != subroute.get(0)) {
            String[] destination = subroute.get(0).split(",");
            targetX = IsometricMath.mapToScreenX(Float.parseFloat(destination[0]), Float.parseFloat(destination[1]) + 1);
            targetY = IsometricMath.mapToScreenY(Float.parseFloat(destination[0]), Float.parseFloat(destination[1]) + 1);

            // Change direction of the firetruck depending on the moving direction
            if (Float.parseFloat(destination[0]) > IsometricMath.screenToMapX(positionX, positionY))
                direction = "_left_right";
            else if (Float.parseFloat(destination[0]) < IsometricMath.screenToMapX(positionX, positionY))
                direction = "_right_left";
            else if (Float.parseFloat(destination[1]) + 1 < IsometricMath.screenToMapY(positionX, positionY))
                direction = "_bottom_top";
            else if (Float.parseFloat(destination[1]) + 1 > IsometricMath.screenToMapY(positionX, positionY))
                direction = "_top_bottom";



            if (positionX - targetX > 0) {
                positionX -= 4;
            } else if (positionX - targetX < 0) {
                positionX += 4;
            }

            if (positionY - targetY > 0) {
                positionY -= 2;
            } else if (positionY - targetY < 0) {
                positionY += 2;
            }
            if (abs(positionX - targetX) == 0 && abs(positionY - targetY) == 0) {
                ElFuego.LOGGER.log(Level.FINE, "Navigated to: " + subroute.get(0));
                ElFuego.LOGGER.log(Level.FINE, "Position X: " + positionX + "\tPosition Y: " + positionY + "\nTarget X: " + targetX + "\tTarget Y: " + targetY);
                ElFuego.LOGGER.log(Level.FINE, "Setting bounds to: X: " + positionX + " Y: " + positionY);

                float padding = 40;
                setBounds(positionX - padding, positionY - MapLoader.getTileHeight() - padding,
                        fireTruck.getWidth() + 2 * padding, fireTruck.getHeight() + 2 * padding);

                removeFirstIndex(subroute);
            }

            // firetruck arrived at target, start extinguish animation if burning house is near
        } else {
            if (actionsCount > 0) {

                //check if a house is adjacent to the firetruck
                int[] pos = getPos();
                House adjacentHouse = null;
                House diagonallyAdjacentHouse = null;
                boolean found = false;

                for (int i = 0; i < burningController.getBurningHousesCount() && !found; i++) {
                    House house = burningController.getBurningHouses().get(i);
                    int posX = house.getPositionX();
                    int posY = house.getPositionY() + 1;

                    //house directly adjacent to firetruck
                    if ((posX + 1 == pos[0] || posX - 1 == pos[0]) && (posY == pos[1])
                            || posX == pos[0] && (posY + 1 == pos[1] || posY - 1 == pos[1])) {
                        adjacentHouse = house;
                        found = true;

                        //house diagonally adjacent to firetruck
                    } else if ((posX + 1 == pos[0] || posX - 1 == pos[0]) &&
                            (posY + 1 == pos[1] || posY - 1 == pos[1])) {
                        diagonallyAdjacentHouse = house;
                    }
                }

                // if !found means no firstPrioHouse was found after a whole loop
                // and one or more secoundPrios were found
                if (!found && null != diagonallyAdjacentHouse) {
                    adjacentHouse = diagonallyAdjacentHouse;
                }

                // a burning house is adjacent to the firetruck
                if (null != adjacentHouse) {

                    try {

                        // check if house is not yet being extinguished
                        if (adjacentHouse != extinguishingHouse) {
                            extinguishingHouse = adjacentHouse;
                            burningController.extinguishHouse(adjacentHouse);
                            Sounds.play(Sounds.EXTINGUISHING);
                        }

                        actionsCount = 0;

                        // extinguish house, returns true if its no longer burning
                        if (adjacentHouse.extinguish()) {
                            extinguishingHouse = null;
                            burningController.restoreHouse(adjacentHouse);

                            EndTurnLogic.addReward(adjacentHouse.getReward());
                        }

                    } catch (Exception e) {
                        ElFuego.LOGGER.log(Level.WARNING, "Animation could not be loaded!");
                    }
                } else {
                    if (extinguishingHouse != null && burningController.containsHouse(extinguishingHouse)){

                        //firetruck isnt at the previos burning house, set again on fire
                        try {
                            burningController.setAgainBurning(extinguishingHouse);
                        } catch (Exception e) {
                            ElFuego.LOGGER.log(Level.WARNING, "Animation could not be loaded!");
                        }

                        extinguishingHouse = null;
                    }
                }
            }
        }
    }

    /**
     * adds Playscreen.Y_OFFSET to y before further calculations
     * sets active to false if and only if the target was valid - firetruck stays active if target was invalid
     * @param x posX target
     * @param y posY target
     * @param mapLoader for the current level
     * @param onlyShow true if path should not be set but be shown
     *
     * @return  true if route was set and stored
     */
    public boolean setOrShowRoute(float x, float y, MapLoader mapLoader, boolean onlyShow) {
        y += PlayScreen.Y_OFFSET;
        if (isActive && (x - positionX < 0 || x - positionX > 128) || (y - positionY > 75 || y - positionY < 0)) {
            int startX = (int) (IsometricMath.screenToMapX(positionX, positionY));
            int destinationX = (int) (IsometricMath.screenToMapX(x, y));

            int startY = (int) IsometricMath.screenToMapY(positionX, positionY) - 1;
            int destinationY = (int) IsometricMath.screenToMapY(x, y);

            String startingPosition = startX + "," + startY;
            String destinationPosition = destinationX + "," + destinationY;

            Pathfinder pathfinder = new Pathfinder(mapLoader.getRoads());
            List<String> list = pathfinder.findPath(startingPosition, destinationPosition);

            // route is valid and route has a length of at least 2, (start, ..., target)
            if (list.size() > 1) {

                list.remove(0);
                previewRoute = listToAtomicArrayReferenceArray(list);

                ElFuego.LOGGER.log(Level.FINE, "Start \t\tx: " + startX + " , y: " + startY + "\n" +
                        "\t\t\t Destination x: " + destinationX + " , y: " + destinationY);

                if (!onlyShow) {
                    ElFuego.LOGGER.log(Level.INFO, "Route set to destination x: " + destinationX
                            + " , y: " + destinationY);
                    ElFuego.LOGGER.log(Level.INFO, this.toString() + " disabled");
                    isActive = false;

                    targetX = (int) IsometricMath.mapToScreenX(IsometricMath.screenToMapX(x, y), IsometricMath.screenToMapY(x, y));
                    targetY = (int) IsometricMath.mapToScreenY(IsometricMath.screenToMapX(x, y), IsometricMath.screenToMapY(x, y));

                    route = previewRoute;
                    return true;
                }
                setPathAndCheckpoints();
                setActorsToVisible(path);
                setActorsToVisible(checkpoints);
            }
        }
        return false;
    }

    /**
     * removes a field in route for each field in subroute
     */
    private void updateRoute(){
        for (int i = 0; i < subroute.length() && null != subroute.get(i) ; i++){
            removeFirstIndex(route);
        }
    }

    /**
     * Resets the preview for the latest specified route
     */
    public void resetPreviewRoute(){
        previewRoute = route;
        setPathAndCheckpoints();
        setActorsToVisible(path);
        setActorsToVisible(checkpoints);
    }

    /**
     * reads the route and calculates path and checkpoints
     */
    private void setPathAndCheckpoints(){
        //set Checkpoints
        ArrayList<Integer> checkpointsInt = new ArrayList<>();
        ArrayList<String> checkpointsString = new ArrayList<>();
        for (int i = 1; (i - 1) < previewRoute.length() / MAX_MOVES; i++){
            checkpointsInt.add(i * MAX_MOVES - 1);
        }

        for (Integer i : checkpointsInt){
            String s = previewRoute.get(i);
            if (null == s)
                break;
            checkpointsString.add(s);
        }

        int last = findLastNotNull(previewRoute);
        if (last >= 0 && !checkpointsString.contains(previewRoute.get(last))) {
            checkpointsString.add(previewRoute.get(last));
        }
        setCheckpoints(checkpointsString);

        //set Path
        setActorsToInvisible(path);
        path = new ArrayList<>();
        for (int i = 0; i < last; i++){
            if (checkpointsInt.contains(i)){
                continue;
            }
            String s = previewRoute.get(i);
            int[] posM = parsePos(s);
            int[] pos = {(int) IsometricMath.mapToScreenX(posM[0], posM[1]),
                    (int) IsometricMath.mapToScreenY(posM[0], posM[1])};
            path.add(newPoint(pos[0], pos[1]));
        }
    }

    /**
     * resets all checkpoints to null, then adds new ones
     *
     * @param points the points to add
     */
    private void setCheckpoints(ArrayList<String> points){
        setActorsToInvisible(checkpoints);
        checkpoints = new ArrayList<>();
        int i = 0;
        for (String s : points){
            i++;
            int[] posM = parsePos(s);
            int[] pos = {(int) IsometricMath.mapToScreenX(posM[0], posM[1]),
                    (int) IsometricMath.mapToScreenY(posM[0], posM[1])};
            checkpoints.add(newDroplet(pos[0], pos[1], i));
        }
    }

    private void setActorsToVisible(ArrayList<BaseActor> list){
        for (BaseActor a : list){
            a.setVisible(true);
        }
    }

    private void setActorsToInvisible(ArrayList<BaseActor> list){
        for (BaseActor a : list){
            a.setVisible(false);
        }
    }

    private AtomicReferenceArray<String> listToAtomicArrayReferenceArray(List<String> list){
        AtomicReferenceArray<String> ara = new AtomicReferenceArray<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            ara.set(i, list.get(i));
        }
        return ara;
    }

    private int findLastNotNull(AtomicReferenceArray<String> list){
        for (int i = list.length() - 1; i >= 0; i--){
            if (null != list.get(i))
                return i;
        }
        return -1;
    }

    /**
     * shifts all elements by -1
     * @param array the array to shift
     */
    private void removeFirstIndex(AtomicReferenceArray<String> array){
        for (int i = 0; i < array.length() - 1; i++){
            array.set(i, array.get(i + 1));
        }
        //set last index to null
        array.set(array.length() - 1, null);
    }

    /**
     * Returns an array of the current route for the FireTruck required for the EndTurnLogic
     *
     * @return an array of the current route
     */
    public AtomicReferenceArray<String> getRoute() {
        return route;
    }

    /**
     * Gets the number of possible moves for the current round
     *
     * @return number of moves
     */
    public int getMoves() {
        return moves;
    }

    /**
     *@return the X and Y coordinates of the actual position
     */
    private int[] getPos() {
        int[] pos = new int[2];

        pos[0] = (int) IsometricMath.screenToMapX(positionX, positionY);
        pos[1] = (int) IsometricMath.screenToMapY(positionX, positionY);

        return pos;
    }

    private int[] parsePos(String target){
        int[] pos = new int[2];
        String[] posS = target.split(",");
        pos[0] = Integer.parseInt(posS[0]);
        pos[1] = Integer.parseInt(posS[1]);

        return pos;
    }

    private BaseActor newDroplet(int posX, int posY, int turns){
        BaseActor droplet = new BaseActor(posX - 105, posY -105, stage);
        droplet.setScale(0.3f);
        if (turns > 9){
            droplet.loadTexture("maps/objects/Tropfen.png");
        } else {
            droplet.loadTexture("maps/objects/drop" + turns + ".png");
        }
        droplet.setVisible(false);

        return droplet;
    }

    private BaseActor newPoint(int posX, int posY){
        BaseActor point = new BaseActor(posX, posY, stage);
        point.loadTexture("maps/objects/Punkt.png");
        point.setSize(10, 10);
        point.setVisible(false);

        return point;
    }
}


