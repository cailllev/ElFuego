package ch.zhaw.psit3.elfuego.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import ch.zhaw.psit3.elfuego.ElFuego;
import ch.zhaw.psit3.elfuego.actors.FireDepartment;
import ch.zhaw.psit3.elfuego.actors.FireTruck;
import ch.zhaw.psit3.elfuego.hud.Hud;
import ch.zhaw.psit3.elfuego.logic.EndTurnLogic;
import ch.zhaw.psit3.elfuego.tools.BaseGame;
import ch.zhaw.psit3.elfuego.tools.BurningController;
import ch.zhaw.psit3.elfuego.tools.IsometricMath;
import ch.zhaw.psit3.elfuego.tools.MapLoader;
import ch.zhaw.psit3.elfuego.tools.Sounds;

/**
 * The PlayScreen provides the UI for an active game, where the player needs to move around his
 * Firetrucks to extinguish burning Houses.
 *
 * @author Kevin Winzeler
 */
public class PlayScreen extends BaseScreen {
    private static final int CAMERA_SPEED = 300;

    private MapLoader mapLoader;
    private IsometricTiledMapRenderer mapRenderer;
    private String mapName;

    private Hud hud;
    private static boolean paused;
    private int level;

    private FireTruck activeFireTruck;
    public static final int Y_OFFSET = -24;

    PlayScreen(Object object) {
        super(object);
    }

    /**
     * Allows to pause the game. If the game is paused no interactions with the map can be performed.
     * Used to show different dialogs.
     *
     * @param pause true if the game should be paused
     */
    public static void setPaused(boolean pause) {
        paused = pause;
    }

    /**
     * Processes the input string and extracts the map's name and level.
     *
     * @param object the map's name and level
     */
    @Override
    public void processParameter(Object object) {
        String[] content = ((String) object).split("#");
        mapName = content[0];
        level = Integer.parseInt(content[1]);
    }

    /**
     * Method gets called when a MenuScreen is created. It initializes the required objects to play
     * the game. Loads the map, creates a FireDepartment and a FireTruck and initializes the
     * EndTurnController.
     */
    @Override
    public void initialize() {
        EndTurnLogic.init(level);
        mapLoader = new MapLoader(mapName);
        mapRenderer = new IsometricTiledMapRenderer(mapLoader.getMap());

        BurningController burningController = new BurningController(mapLoader.getHouses(), mapLoader.getMap(), mapLoader.getTileSet(), mapName);
        EndTurnLogic.setBurningController(burningController);

        hud = new Hud(stage, level);

        // Set cam position to center of map
        gameCam.position.set(MapLoader.getMapWidth() / 2, 0, 0);

        FireTruck fireTruck = new FireTruck(stage, "_left_right", burningController);
        fireTruck.setPosition(IsometricMath.mapToScreenX(19, 21), IsometricMath.mapToScreenY(19, 21));
        stage.addActor(fireTruck);

        FireDepartment fireDepartment = new FireDepartment(stage, gameCam, hud, burningController);
        fireDepartment.setPosition(fireDepartment.getX(), fireDepartment.getY());
        stage.addActor(fireDepartment);

        EndTurnLogic.addFireTruck(fireTruck);
        paused = false;
    }

    private Vector3 getWorldCoordinatesFromCursor() {
        Vector3 worldCoordinates = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        return gameCam.unproject(worldCoordinates);
    }

    private void handleInput(float deltaTime) {
        if (!paused) {
            hud.updateMoney();

            if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
                hud.endTurn(new Event());
            }

            if ((Gdx.input.isButtonJustPressed(Input.Buttons.LEFT))) {

                // there is an active truck, test for valid destination
                if (null != activeFireTruck) {

                    //try to set route, if returns true --> route was valid, deactivate firetruck
                    if (activeFireTruck.setOrShowRoute(getWorldCoordinatesFromCursor().x,
                            getWorldCoordinatesFromCursor().y, mapLoader, false)){
                        activeFireTruck = null;
                    }

                    // there is no active firetruck, find the one that was activated
                } else {
                    for (FireTruck f : EndTurnLogic.getFiretrucks()){
                        if (f.isActive()){
                            activeFireTruck = f;

                            // set selected firetruck to last active firetruck
                            f.setLastActive(true);
                        } else {
                            f.setLastActive(false);
                        }
                    }
                }
            }

            if (null != activeFireTruck) {
                activeFireTruck.setOrShowRoute(getWorldCoordinatesFromCursor().x,
                        getWorldCoordinatesFromCursor().y, mapLoader, true);
            }

            if ((Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT))) {
                if (null != activeFireTruck) {
                    activeFireTruck.deactivate();
                    activeFireTruck.resetPreviewRoute();
                    activeFireTruck = null;
                }
            }

            // Zoomen der Karte
            if ((Gdx.input.isKeyPressed(Input.Keys.PLUS) || Gdx.input.isKeyPressed(Input.Keys.PAGE_UP)) && gameCam.zoom >= 0.4) {
                gameCam.zoom -= .01f;
            }

            if ((Gdx.input.isKeyPressed(Input.Keys.MINUS) || Gdx.input.isKeyPressed(Input.Keys.PAGE_DOWN)) && gameCam.zoom <= 2.2) {
                gameCam.zoom += .01f;
                gameCam.update();
            }

            int width = ElFuego.getResolution()[0];
            int height = ElFuego.getResolution()[1];

            // Allow moving around the map and limit it to the corners of the map
            if ((Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)  || Gdx.input.getY() < 10)
                    && getWorldCoordinatesFromCursor().y < MapLoader.getMapHeight() / 2 - gameViewport.getScreenHeight() / 2.0 + 300)
                gameCam.position.y += CAMERA_SPEED * deltaTime * gameCam.zoom * 1.5;

            if ((Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)  || height - Gdx.input.getY() < 10)
                    && getWorldCoordinatesFromCursor().y > -MapLoader.getMapHeight() / 2 + gameViewport.getScreenHeight() / 2.0)
                gameCam.position.y -= CAMERA_SPEED * deltaTime * gameCam.zoom * 1.5;

            if ((Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)  || Gdx.input.getX() < 10)
                    && getWorldCoordinatesFromCursor().x > 0 + gameViewport.getScreenWidth() / 2.0 - 400)
                gameCam.position.x -= CAMERA_SPEED * deltaTime * gameCam.zoom * 1.5;

            if ((Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)  || width - Gdx.input.getX() < 10)
                    && getWorldCoordinatesFromCursor().x < MapLoader.getMapWidth() - gameViewport.getScreenWidth() / 2.0)
                gameCam.position.x += CAMERA_SPEED * deltaTime * gameCam.zoom * 1.5;

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                final Dialog dialog = new Dialog("Exit to Menu?", skin, "default") {
                    public void result(Object obj) {
                        if ((boolean) obj) {
                            stage.dispose();
                            Sounds.startMenuTheme();
                            BaseGame.setActiveScreen(new MenuScreen(null));
                        } else {
                            stage.act();
                            paused = false;
                        }
                    }
                };
                float zoom = gameCam.zoom;
                float sizeX = gameCam.viewportWidth / 4f;
                float sizeY = gameCam.viewportHeight / 6f;

                Button yesButton = new TextButton("Yes", skin, "small");
                Button noButton = new TextButton("No", skin, "small");

                dialog.button(yesButton, true);
                dialog.button(noButton, false);
                dialog.setMovable(false);
                dialog.setScale(zoom);

                dialog.setPosition(gameCam.position.x - sizeX * zoom / 2f, gameCam.position.y - sizeY * zoom / 2f);
                dialog.setSize(sizeX, sizeY);

                stage.addActor(dialog);
                paused = true;
            }
        }
    }

    /**
     * Method is responsible to update the UI after every frame
     *
     * @param deltaTime the interval for a frame
     */
    public void update(float deltaTime) {
        gameCam.update();

        handleInput(deltaTime);
        hud.updatePositions();

        mapRenderer.setView(gameCam);
        mapRenderer.render();
    }

    /**
     * Cleans up the resources after the disposal of the screen has been initiated
     */
    @Override
    public void dispose() {
        mapLoader.getMap().dispose();
        mapRenderer.dispose();
        super.dispose();
    }

    /**
     * Handles a pressed key
     *
     * @param keycode the key that is pressed
     * @return boolean if event should be passed down to the next event
     */
    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    /**
     * Handles a released key
     *
     * @param keycode the key that was pressed
     * @return boolean if event should be passed down to the next event
     */
    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    /**
     * Handles a typed character
     *
     * @param character the character that was pressed
     * @return boolean if event should be passed down to the next event
     */
    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    /**
     * Handles a touch down on the screen
     *
     * @param screenX the finger's destination x-coordinate
     * @param screenY the finger's destination y-coordinate
     * @param pointer the index of the finger for multitouch
     * @param button  the index of the button if one was pressed
     * @return boolean if event should be passed down to the next event
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    /**
     * Handles if a finger released the touchscreen
     *
     * @param screenX the finger's destination x-coordinate
     * @param screenY the finger's destination y-coordinate
     * @param pointer the index of the finger for multitouch
     * @param button the index of the button if one was pressed
     * @return boolean if event should be passed down to the next event
     */
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    /**
     * Handles if the user moved over the touchscreen
     *
     * @param screenX the finger's destination x-coordinate
     * @param screenY the finger's destination y-coordinate
     * @param pointer the index of the finger for multitouch
     * @return boolean if event should be passed down to the next event
     */
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    /**
     * Handles if the mouse was moved
     *
     * @param screenX the mouse's destination x-coordinate
     * @param screenY the mouse's destination y-coordinate
     * @return boolean if event should be passed down to the next event
     */
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    /**
     * Handles if the user scrolled with the mouse-wheel
     *
     * @param amount the distance the user scrolled
     * @return boolean if event should be passed down to the next event
     */
    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}