package ch.zhaw.psit3.elfuego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.logging.Logger;

import ch.zhaw.psit3.elfuego.screens.MenuScreen;
import ch.zhaw.psit3.elfuego.tools.BaseGame;
import ch.zhaw.psit3.elfuego.tools.Sounds;

/**
 * Entry point for the game. Defines different resolutions, logger and redirects to the menu screen.
 * The class extends BaseGame which provides the main method to launch the game with LibGDX.
 *
 * @author Kevin Winzeler
 */
public class ElFuego extends BaseGame {
    public static final Logger LOGGER = Logger.getLogger("ElFuegoLogger");

    public static final String TITLE = "El Fuego";

    private static final int[][] resolutions = {{3840, 2160}, {2560, 1440}, {1920, 1080}, {1600, 900}, {1280, 720}, {640, 360}};
    private static final float[] scales = {2, 1.33f, 1, 0.83f, 0.66f, 0.33f};

    private static int[] resolution = new int[2];
    public static float scale;

    private SpriteBatch batch;

    /**
     * The default constructor calls the super-class' constructor and launches with the default resolution
     */
    public ElFuego(){
        super();
    }

    /**
     * This constructor creates the game with the specified resolution.
     *
     * @param width  the initial width of the game window
     * @param height the initial height of the game window
     */
    public ElFuego(int width, int height) {
        super();
        resolution[0] = width;
        resolution[1] = height;

        scale = (scales[0] / resolutions[0][0]) * width;
    }

    /**
     * Provides the current window resolution settings
     *
     * @return an int array containing the current window's width and height
     */
    public static int[] getResolution(){
        int[] res = new int[2];
        res[0] = resolution[0];
        res[1] = resolution[1];
        return res;
    }

    /**
     * Allows to set a different solution for the game
     *
     * @param index for the resolution contained in the resolutions variable
     */
    public static void setResolution(int index) {
        resolution = resolutions[index];
        scale = scales[index];
    }

    /**
     * Converts the resolution to a string
     *
     * @return the resolution as a string. e.g. "1920, 1080"
     */
    public static String resolutionToString(){
        return resolution[0] + ", " + resolution[1];
    }

    /**
     * Converts all possible resolutions to a string
     *
     * @return an array containing all the different resolutions as a String
     */
    public static String[] resolutionsToString(){
        int l = resolutions.length;
        String[] resToString = new String[l];

        for (int i = 0; i < l; i++){
            resToString[i] = resolutions[i][0] + ", " + resolutions[i][1];
        }

        return resToString;
    }

    /**
     * Creates a MenuScreen and redirects the user to it.
     */
    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        Sounds.startMenuTheme();
        setScreen(new MenuScreen(null));
    }

    /**
     * Cleans up unneeded resources
     */
    @Override
    public void dispose() {
        batch.dispose();
    }

    /**
     * Returns the single instance of the SpriteBatch that is used throughout all Screens
     *
     * @return SpriteBatch that is used to draw different objects to the screen
     */
    public SpriteBatch getBatch() {
        return batch;
    }
}
