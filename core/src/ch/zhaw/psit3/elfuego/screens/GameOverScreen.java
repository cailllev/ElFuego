package ch.zhaw.psit3.elfuego.screens;

/**
 * The GameOverScreen is displayed if the player failed to complete the level.
 *
 * @author LeviCailleret
 */
public class GameOverScreen extends BaseScreen {
    /**
     * Creates a new GameOverScreen with the possibility to pass custom parameters
     *
     * @param object allows to pass custom parameters
     */
    public GameOverScreen(Object object) {
        super(object);
    }

    /**
     * Reads in input parameters and adds them to the menu screen
     *
     * @param object input parameter to be processed
     */
    @Override
    public void processParameter(Object object) {

    }

    /**
     * Gets called when the LevelSelectorScreen is created. Creates the buttons and background.
     */
    @Override
    public void initialize() {

    }

    /**
     * Method is responsible to update the UI after every frame
     *
     * @param dt the interval for a frame
     */
    @Override
    public void update(float dt) {

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
