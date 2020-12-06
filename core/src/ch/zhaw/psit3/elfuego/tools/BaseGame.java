package ch.zhaw.psit3.elfuego.tools;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;

import ch.zhaw.psit3.elfuego.screens.BaseScreen;

/**
 * Provides extended methods for the Game class that are required for the ElFuego game.
 *
 * @author Benjamin Burtscher
 */
public abstract class BaseGame extends Game
{
    /**
     *  Stores reference to game; used when calling setActiveScreen method.
     */
    private static BaseGame game;

    /**
     *  Called when game is initialized; stores global reference to game object.
     */
    public BaseGame() {
        game = this;
    }

    /**
     * Returns the current game's instance
     *
     * @return the game instance
     */
    public static Game getGame() {
        return game;
    }

    /**
     * Adds an inputMultiplexer to handle different stages upon creation of a game instance.
     */
    public void create() {
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    /**
     *  Used to switch screens while game is running.
     *  Method is static to simplify usage.
     *
     * @param s screen that should be loaded
     */
    public static void setActiveScreen(BaseScreen s) {
        game.setScreen(s);
    }
}

