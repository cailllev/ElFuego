package ch.zhaw.psit3.elfuego.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import ch.zhaw.psit3.elfuego.ElFuego;
import ch.zhaw.psit3.elfuego.actors.BaseActor;
import ch.zhaw.psit3.elfuego.tools.BaseGame;
import ch.zhaw.psit3.elfuego.tools.Sounds;

/**
 * The MenuScreen is the start screen that provides buttons to start/options/exit
 *
 * @author Levi Cailleret
 */
public class MenuScreen extends BaseScreen {

    /**
     * Creates a new MenuScreen with the possibility to pass custom parameters
     *
     * @param object allows to pass custom parameters
     */
    public MenuScreen(Object object){
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
     * Gets called when the MenuScreen is created. Creates the required buttons and sets the background.
     */
    @Override
    public void initialize() {
        Skin mySkin = new Skin(Gdx.files.internal("mySkin/menu/buttons.json"));

        ImageTextButton start = new ImageTextButton("Start", mySkin, "start");
        ImageTextButton options = new ImageTextButton("Options", mySkin, "options");
        ImageTextButton quit = new ImageTextButton("Quit", mySkin, "quit");

        int width = ElFuego.getResolution()[0];
        int height = ElFuego.getResolution()[1];

        int logoSize = width / 9;
        int buttonSize = width / 12;
        int distance = width / 9;

        start.setSize(buttonSize, buttonSize);
        start.setPosition((width - buttonSize) / 2 - distance, (height - buttonSize) / 4);

        options.setSize(buttonSize, buttonSize);
        options.setPosition((width - buttonSize) / 2, (height - buttonSize) / 4);

        quit.setSize(buttonSize, buttonSize);
        quit.setPosition((width - buttonSize) / 2 + distance, (height - buttonSize) / 4);

        BaseActor background = new BaseActor(0, 0, stage);
        background.loadTexture("mySkin/menu/background.jpg");
        background.setSize(width, height);

        BaseActor logo = new BaseActor(0, 0, stage);
        logo.loadTexture("mySkin/menu/elfuego-logo.png");
        logo.setSize(logoSize, logoSize);
        logo.setPosition((width - logoSize) / 2, (height - logoSize) / 2);

        stage.addActor(background);
        stage.addActor(logo);
        stage.addActor(start);
        stage.addActor(options);
        stage.addActor(quit);

        start.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Sounds.play(Sounds.KLICK);

                stage.dispose();
                BaseGame.setActiveScreen(new LevelSelectorScreen(null));
            }
        });

        options.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Sounds.play(Sounds.KLICK);

                stage.dispose();
                BaseGame.setActiveScreen(new OptionsScreen(null));
            }
        });

        quit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Sounds.play(Sounds.KLICK);
                Sounds.stopMenuTheme();

                Gdx.app.exit();
            }
        });
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
