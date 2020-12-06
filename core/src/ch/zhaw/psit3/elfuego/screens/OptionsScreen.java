package ch.zhaw.psit3.elfuego.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import ch.zhaw.psit3.elfuego.ElFuego;
import ch.zhaw.psit3.elfuego.tools.AppPreferences;
import ch.zhaw.psit3.elfuego.tools.BaseGame;
import ch.zhaw.psit3.elfuego.tools.Sounds;

/**
 * The OptionsScreen lets the user set the preferred resolution, difficulty and sound settings.
 *
 * @author Levi Cailleret
 */
public class OptionsScreen extends BaseScreen {

    private Table table;
    private boolean noDialogOpened = true;

    OptionsScreen(Object object){
        super(object);
    }

    /**
     * Reads in input parameters and adds them to the option screen
     *
     * @param object input parameter to be processed
     */
    @Override
    public void processParameter(Object object) {
    }

    /**
     * Method that initializes the option screen width default values or the user-specific saved
     * data if those are available.
     */
    @Override
    public void initialize() {
        AppPreferences.checkLocalPrefs();

        setClearScreenColor(0, 0, 0, 1);

        //music slider
        final Slider musicSlider = new Slider( 0f, 1f, 0.1f,false, skin );
        musicSlider.setValue(AppPreferences.getMusicVolume());
        musicSlider.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                AppPreferences.setMusicVolumeTemp(musicSlider.getValue());
                Sounds.updateVolumeMenuTheme(musicSlider.getValue());

                return false;
            }
        });

        //music checkbox
        final CheckBox musicCheckbox = new CheckBox(null, skin);
        musicCheckbox.setChecked(AppPreferences.isMusicEnabled());
        musicCheckbox.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                boolean enabled = musicCheckbox.isChecked();
                AppPreferences.setMusicEnabledTemp(enabled);
                if (enabled) {
                    Sounds.startMenuTheme();
                } else {
                    Sounds.stopMenuTheme();
                }
                return false;
            }
        });

        //sound slider
        final Slider soundSlider = new Slider( 0f, 1f, 0.1f,false, skin );
        soundSlider.setValue(AppPreferences.getSoundVolume());
        soundSlider.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                AppPreferences.setSoundVolumeTemp(soundSlider.getValue());
                Sounds.updateSoundVolume(soundSlider.getValue());
                return false;
            }
        });

        //sound checkbox
        final CheckBox soundCheckbox = new CheckBox(null, skin);
        soundCheckbox.setChecked(AppPreferences.isSoundEnabled());
        soundCheckbox.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                boolean enabled = soundCheckbox.isChecked();
                AppPreferences.setSoundEnabledTemp(enabled);
                Sounds.soundsEnabled(enabled);
                return false;
            }
        });

        //difficulty
        final SelectBox<String> difficultySelectBox = new SelectBox<>(skin);
        final String[] difficulties = AppPreferences.getDifficulties();
        difficultySelectBox.setItems(difficulties);
        difficultySelectBox.setSelected(AppPreferences.getDifficulty());
        difficultySelectBox.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                int index = difficultySelectBox.getSelectedIndex();
                AppPreferences.setDifficultyTemp(difficulties[index]);
                return false;
            }
        });

        //difficulty
        final SelectBox<String> resolutionSelectBox = new SelectBox<>(skin);

        final String[] resolutionsStrings = ElFuego.resolutionsToString();
        final String resolutionString = ElFuego.resolutionToString();

        resolutionSelectBox.setItems(resolutionsStrings);
        resolutionSelectBox.setSelected(resolutionString);
        resolutionSelectBox.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                return false;
            }
        });

        // return to main screen button
        final TextButton resetProgress = new TextButton("Reset Progress", skin, "small");
        resetProgress.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                final Dialog dialog = new Dialog("Do you really want to reset your progress?", skin, "default") {
                    public void result(Object obj) {
                        if ((boolean) obj) {
                            AppPreferences.resetProgress();
                        }
                    }
                };
                Button yesButton = new TextButton("Yes", skin, "small");
                Button noButton = new TextButton("No", skin, "small");

                dialog.button(yesButton, true);
                dialog.button(noButton, false);
                dialog.setMovable(false);

                int width = ElFuego.getResolution()[0];
                int height = ElFuego.getResolution()[1];
                int sizeX = width / 4;
                int sizeY = height / 6;
                dialog.setPosition((width - sizeX) / 2, (height - sizeY) / 2);
                dialog.setSize(sizeX, sizeY);

                stage.addActor(dialog);
            }
        });

        // return to main screen button
        final TextButton saveButton = new TextButton("Save", skin);
        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Sounds.play(Sounds.KLICK);

                //save changes
                int index = resolutionSelectBox.getSelectedIndex();
                ElFuego.setResolution(index);

                AppPreferences.saveTempPrefs();

                if (AppPreferences.isMusicEnabled()) {
                    Sounds.startMenuTheme();
                } else {
                    Sounds.stopMenuTheme();
                }

                stage.dispose();
                BaseGame.setActiveScreen(new MenuScreen(null));
            }
        });

        table = new Table(skin);

        Label titleLabel = new Label("Settings", skin, "big");
        Label musicSliderLabel = new Label("Music Volume", skin);
        Label musicCheckboxLabel = new Label("Music", skin);
        Label soundSliderLabel = new Label("Sound Volume", skin);
        Label soundCheckboxLabel = new Label("Sound", skin);
        Label difficultyLabel = new Label("Difficulty", skin);
        Label resolutionLabel = new Label("Resolution", skin);

        Label empty = new Label("      ", skin);

        int padBig = 40;
        int padMedium = 20;
        int pad = 10;

        table.add(titleLabel).colspan(4);

        table.row().pad(2 * padBig, 0, 0, padMedium);
        table.add(musicCheckboxLabel);
        table.getCell(musicCheckboxLabel).align(Align.left);
        table.add(musicCheckbox);
        table.add(empty);
        table.add(difficultyLabel);

        table.row().pad(pad, 0, 0, padMedium);
        table.add(musicSliderLabel);
        table.add(musicSlider);
        table.add(empty);
        table.add(difficultySelectBox);

        table.row().pad(padMedium, 0, 0, padMedium);
        table.add(soundCheckboxLabel);
        table.getCell(soundCheckboxLabel).align(Align.left);
        table.add(soundCheckbox);
        table.add(empty);
        table.add(resolutionLabel);

        table.row().pad(pad, 0, 0, padMedium);
        table.add(soundSliderLabel);
        table.add(soundSlider);
        table.add(empty);
        table.add(resolutionSelectBox);

        table.row().padTop(padMedium);
        table.add(resetProgress).colspan(2);

        table.row().padTop(padBig);
        table.add(saveButton).colspan(4);

        //table.setDebug(true);

        stage.addActor(table);
    }

    /**
     * Rerenders the UI after every frame.
     *
     * @param dt interval between two frames
     */
    @Override
    public void update(float dt) {
        int width = ElFuego.getResolution()[0];
        int height = ElFuego.getResolution()[1];
        table.setPosition(width / 2, height / 2);

        handleInput();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && noDialogOpened) {
            if (AppPreferences.comparePrefs()){
                stage.dispose();
                BaseGame.setActiveScreen(new MenuScreen(null));
            } else {
                noDialogOpened = false;
                final Dialog dialog = new Dialog("Discard changes?", skin, "default") {
                    public void result(Object obj) {
                        if ((boolean) obj) {
                            AppPreferences.resetTempPrefs();
                            stage.dispose();
                            BaseGame.setActiveScreen(new MenuScreen(null));
                        } else {
                            noDialogOpened = true;
                        }
                    }
                };
                Button yesButton = new TextButton("Yes", skin, "small");
                Button noButton = new TextButton("No", skin, "small");

                dialog.button(yesButton, true);
                dialog.button(noButton, false);
                dialog.setMovable(false);

                int width = ElFuego.getResolution()[0];
                int height = ElFuego.getResolution()[1];
                int sizeX = width / 4;
                int sizeY = height / 6;
                dialog.setPosition((width - sizeX) / 2, (height - sizeY) / 2);
                dialog.setSize(sizeX, sizeY);

                stage.addActor(dialog);
            }
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
