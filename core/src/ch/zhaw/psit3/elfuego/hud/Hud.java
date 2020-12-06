package ch.zhaw.psit3.elfuego.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;

import ch.zhaw.psit3.elfuego.actors.BaseActor;
import ch.zhaw.psit3.elfuego.logic.EndTurnLogic;
import ch.zhaw.psit3.elfuego.screens.LevelSelectorScreen;
import ch.zhaw.psit3.elfuego.screens.PlayScreen;
import ch.zhaw.psit3.elfuego.tools.AppPreferences;
import ch.zhaw.psit3.elfuego.tools.BaseGame;
import ch.zhaw.psit3.elfuego.tools.Sounds;

/**
 * The Hud provides an overlay to display the level, current balance and turns/turn goal
 *
 * @author Kevin Winzeler
 */
public class Hud extends BaseActor {

    private final Stage stage;

    private Label moneyLabel;
    private Label turnLabel;
    private Label levelLabel;
    private ImageTextButton endRound;

    private int money;
    private int turnGoal;

    /**
     * Initializes the Hud with the required labels and adds them to the screen
     *
     * @param stage the Stage where the Hud should be placed
     * @param level the current level
     */
    public Hud(final Stage stage, final int level) {
        this.stage = stage;

        final Skin skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));
        Skin textSkin = new Skin(Gdx.files.internal("mySkin/levelSelector/levels.json"));
        money = EndTurnLogic.getMoney();
        turnGoal = EndTurnLogic.getTurnGoal();

        moneyLabel = new Label(String.format(Locale.US, "%d $", money), textSkin);
        turnLabel = new Label(String.format(Locale.US, "Turn %s / %s", 1, turnGoal), textSkin);
        levelLabel = new Label(String.format(Locale.US, "Level %s", level), textSkin);

        stage.addActor(moneyLabel);
        stage.addActor(turnLabel);
        stage.addActor(levelLabel);

        //------
        Skin buttonsSkin = new Skin(Gdx.files.internal("mySkin/menu/buttons.json"));
        endRound = new ImageTextButton("", buttonsSkin, "start");

        endRound.setSize(100, 100);
        stage.addActor(endRound);

        endRound.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                try {
                    if (!EndTurnLogic.endTurn()){
                        //Dialog with Score
                        AppPreferences.unlockLevel(level + 1);
                        final Dialog dialog = new Dialog("Game Over", skin, "default") {
                            public void result(Object obj) {
                                if ((boolean) obj) {
                                    stage.dispose();
                                    Sounds.startMenuTheme();
                                    BaseGame.setActiveScreen(new LevelSelectorScreen(null));
                                }
                            }
                        };
                        OrthographicCamera gameCam = (OrthographicCamera) stage.getViewport().getCamera();
                        float zoom = gameCam.zoom;
                        float sizeX = gameCam.viewportWidth / 4f;
                        float sizeY = gameCam.viewportHeight / 6f;

                        int money = EndTurnLogic.getMoney();
                        int turns = EndTurnLogic.getTurn();
                        int trucks = EndTurnLogic.getFiretrucks().size();

                        // game over message
                        String bashing;
                        if (money > 0) {
                            bashing = "You didn't use your " + money + " dollars wisely!";
                        } else {
                            String firetruck;
                            if (trucks == 1) {
                                firetruck = " Firetruck";
                            } else {
                                firetruck = " Firetrucks";
                            }
                            bashing = "You didn't use your " + trucks + firetruck + " wisely!";
                        }

                        Button yesButton = new TextButton("Ok", skin, "small");
                        Label text = new Label("You have lastet " + turns + " turns.\n " + bashing, skin);
                        text.setColor(Color.BLACK);

                        dialog.text(text);

                        dialog.button(yesButton, true);
                        dialog.setMovable(false);
                        dialog.setScale(zoom);

                        dialog.setPosition(gameCam.position.x - sizeX * zoom / 2f, gameCam.position.y - sizeY * zoom / 2f);
                        dialog.setSize(sizeX, sizeY);

                        stage.addActor(dialog);
                    }
                } catch (IOException | SAXException | ParserConfigurationException e) {
                    e.printStackTrace();
                }
                if (EndTurnLogic.isTurnGoalAchieved()){
                    PlayScreen.setPaused(true);

                    //Dialog with Score
                    AppPreferences.unlockLevel(level + 1);
                    final Dialog dialog = new Dialog("Level " + (level + 1) + " unlocked", skin, "default") {
                        public void result(Object obj) {
                            if ((boolean) obj) {
                                stage.dispose();
                                Sounds.startMenuTheme();
                                BaseGame.setActiveScreen(new LevelSelectorScreen(null));
                            }
                        }
                    };
                    OrthographicCamera gameCam = (OrthographicCamera) stage.getViewport().getCamera();
                    float zoom = gameCam.zoom;
                    float sizeX = gameCam.viewportWidth / 4f;
                    float sizeY = gameCam.viewportHeight / 6f;

                    Button yesButton = new TextButton("Ok", skin, "small");

                    dialog.button(yesButton, true);
                    dialog.setMovable(false);
                    dialog.setScale(zoom);

                    dialog.setPosition(gameCam.position.x - sizeX * zoom / 2f, gameCam.position.y - sizeY * zoom / 2f);
                    dialog.setSize(sizeX, sizeY);

                    stage.addActor(dialog);
                }

                int turn = EndTurnLogic.getTurn();
                turnLabel.setText(String.format(Locale.US, "Turn %s / %s", turn, turnGoal));

                return true;
            }
        });
    }

    /**
     * used to end the turn via the playscreen update loop
     */
    public void endTurn(Event event){
        endRound.fire(event);
    }

    /**
     * Updates the position of the different label if the screen was resized.
     */
    public void updatePositions(){
        OrthographicCamera gameCam = (OrthographicCamera) stage.getViewport().getCamera();
        float zoom = gameCam.zoom;
        float vpWidth = gameCam.viewportWidth * zoom;
        float vpHeight = gameCam.viewportHeight * zoom;
        moneyLabel.setPosition(gameCam.position.x - vpWidth / 2f + 30 * zoom, gameCam.position.y + vpHeight / 2f - 50 * zoom);
        turnLabel.setPosition(gameCam.position.x - 80 * zoom, gameCam.position.y + vpHeight / 2f - 50 * zoom);
        levelLabel.setPosition(gameCam.position.x + vpWidth / 2f - 120 * zoom, gameCam.position.y + vpHeight / 2f - 50 * zoom);
        endRound.setPosition(gameCam.position.x + vpWidth / 2f - 170 * zoom, gameCam.position.y - vpHeight / 2f + 20 * zoom);

        moneyLabel.setFontScale(zoom);
        turnLabel.setFontScale(zoom);
        levelLabel.setFontScale(zoom);
        endRound.setSize(100 * zoom, 100* zoom);
    }

    /**
     * Updates the current balance of the player
     */
    public void updateMoney() {
        this.money = EndTurnLogic.getMoney();
        moneyLabel.setText(String.format(Locale.US, "%d $", money));
    }
}