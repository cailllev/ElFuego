package ch.zhaw.psit3.elfuego.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.List;

import ch.zhaw.psit3.elfuego.ElFuego;
import ch.zhaw.psit3.elfuego.actors.BaseActor;
import ch.zhaw.psit3.elfuego.tools.AppPreferences;
import ch.zhaw.psit3.elfuego.tools.BaseGame;
import ch.zhaw.psit3.elfuego.tools.Sounds;

/**
 * The LevelSelectorScreen displays the different levels that are available and launches the
 * PlayScreen.
 *
 * @author Levi Cailleret
 */
public class LevelSelectorScreen extends BaseScreen {
    private List<ImageTextButton> levels;
    private static final String FILE_NOT_FOUND = "File not found!";
    private static final String UNEXPECTED_EXCEPTION = "Initialization Error!";
    private static final String LEVEL_IS_LOCKED = "Level not unlocked yet!";

    private boolean error = false;
    private String errorTitle;
    private String errorMessage;

    /**
     * Creates a new LevelSelectorScreen with the possibility to pass custom parameters
     *
     * @param object allows to pass custom parameters
     */
    public LevelSelectorScreen(Object object){
        super(object);
    }

    /**
     * Reads in input parameters and adds them to the menu screen
     *
     * @param obj input parameter to be processed
     */
    @Override
    public void processParameter(Object obj) {
        error = true;

        String all = (String) obj;

        String[] s = all.split(",");
        errorTitle = s[0];
        errorMessage = s[1];
    }

    /**
     * Gets called when the LevelSelectorScreen is created. Creates the required level previews.
     */
    @Override
    public void initialize() {
        Skin mySkin = new Skin(Gdx.files.internal("mySkin/levelSelector/levels.json"));

        int width = ElFuego.getResolution()[0];
        int height = ElFuego.getResolution()[1];
        BaseActor background = new BaseActor(0, 0, stage);
        background.loadTexture("mySkin/menu/background.jpg");
        background.setSize(width, height);
        stage.addActor(background);

        levels = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            levels.add(new ImageTextButton("Level " + i, mySkin, "level " + i));
        }

        levelsSetPos(false);
        int progress = AppPreferences.getProgress();
        int levelNr = 1;

        for (ImageTextButton level : levels) {
            level.addListener(myChangeListener(level, levelNr++ <= progress));
            stage.addActor(level);
        }

        if (error) {
            Dialog dialog = new Dialog(errorTitle, skin, "default") {
                public void result(Object obj) {

                }
            };
            int sizeX = width / 4;
            int sizeY = height / 5;

            Button button = new TextButton("Ok", skin, "small");
            Label text = new Label(errorMessage, skin);
            text.setColor(Color.BLACK);

            dialog.button(button, true);
            dialog.text(text);
            dialog.setMovable(false);
            dialog.setPosition((width - sizeX) / 2, (height - sizeY) / 2);
            dialog.setSize(sizeX, sizeY);

            stage.addActor(dialog);
        }
    }

    /**
     * Method is responsible to update the UI after every frame
     *
     * @param dt the interval for a frame
     */
    @Override
    public void update(float dt) {
        handleInput();
        levelsSetPos(true);
    }

    /**
     * Is executed every time an input is recognized. If the escape key is pressed returns to the
     * MenuScreen.
     */
    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            stage.dispose();
            BaseGame.setActiveScreen(new MenuScreen(null));
        }
    }

    private void levelsSetPos(boolean onlyLabel){
        float scale = ElFuego.scale;

        int sizeX = (int) (gameViewport.getScreenWidth() / 2.5);
        int padding = 50;
        int i = 0;
        for (ImageTextButton level : levels){
            // scale y = y_old * x / x_old
            int originalWidth = (int) level.getWidth();
            int originalHeight = (int) level.getHeight();
            double scaleX = (double) sizeX / originalWidth;
            int sizeY = (int) (scaleX * originalHeight);

            if (!onlyLabel)
                level.setSize(sizeX, sizeY);

            int posX;
            if (i%2 == 0){
                posX = padding;
            } else {
                posX = (int) (gameViewport.getScreenWidth() / 2.0) + padding;
            }
            int isoY = (int) (sizeX / (0.866 * 2));
            int posY = gameViewport.getScreenHeight() - padding - isoY - i / 2 * isoY;

            if (!onlyLabel)
                level.setPosition(posX, posY);

            int paddingText = (int) (sizeX / 20.0);
            level.getLabel().setBounds(paddingText, paddingText, sizeX - paddingText, sizeY - paddingText);
            level.getLabel().setAlignment(Align.bottomLeft);
            level.getLabel().setScale(scale * 1.5f);

            i++;
        }
    }

    private ChangeListener myChangeListener(final ImageTextButton level, boolean unlocked) {
        if (unlocked) {
            return new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    String mapName = level.getText().toString().toLowerCase();
                    mapName = mapName.replace(' ', '_');
                    int level = Integer.parseInt(mapName.split("_")[1]);
                    mapName += ".tmx";

                    try {
                        stage.dispose();
                        Sounds.stopMenuTheme();
                        game.setScreen(new PlayScreen(mapName + "#" + level));

                    } catch (com.badlogic.gdx.utils.SerializationException e) {
                        stage.dispose();
                        game.setScreen(new LevelSelectorScreen(FILE_NOT_FOUND + ",File: " + mapName));

                    } catch (Exception e) {
                        stage.dispose();
                        game.setScreen(new LevelSelectorScreen(UNEXPECTED_EXCEPTION + "," + e.getMessage()));
                    }
                }
            };
        } else {
            return new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Dialog dialog = new Dialog(LEVEL_IS_LOCKED, skin, "default") {
                        public void result(Object obj) {

                        }
                    };
                    int width = ElFuego.getResolution()[0];
                    int height = ElFuego.getResolution()[1];
                    int sizeX = width / 5;
                    int sizeY = height / 6;

                    Button button = new TextButton("Ok", skin, "small");

                    dialog.button(button, true);
                    dialog.setMovable(false);
                    dialog.setPosition((width - sizeX) / 2, (height - sizeY) / 2);
                    dialog.setSize(sizeX, sizeY);

                    stage.addActor(dialog);
                }
            };
        }
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
