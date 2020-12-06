package ch.zhaw.psit3.elfuego.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import java.util.logging.Level;

import ch.zhaw.psit3.elfuego.ElFuego;
import ch.zhaw.psit3.elfuego.hud.Hud;
import ch.zhaw.psit3.elfuego.logic.EndTurnLogic;
import ch.zhaw.psit3.elfuego.screens.PlayScreen;
import ch.zhaw.psit3.elfuego.tools.BurningController;
import ch.zhaw.psit3.elfuego.tools.IsometricMath;

/**
 * Creates a new FireDepartment with its settings. FireTrucks can be bought via the menu of the
 * FireDepartment.
 *
 * @author Kevin Winzeler
 */
public class FireDepartment extends BaseActor {
    private Texture fireDepartment;
    private float positionX, positionY;
    private Stage stage;

    private ImageTextButton buyFireTruckButton;

    private OrthographicCamera gameCam;
    private Hud hud;

    private BurningController burningController;

    /**
     * Creates a new fireDepartment and initializes the required variables
     *  @param stage   the stage where the FireDepartment should be placed
     * @param gameCam the cam that shows the PlayScreen
     * @param hud     that provides the current balance of the player
     * @param burningController to assign the burningController to new firetrucks
     */
    public FireDepartment(Stage stage, OrthographicCamera gameCam, Hud hud, BurningController burningController) {
        this.gameCam = gameCam;
        this.hud = hud;
        this.stage = stage;
        this.burningController = burningController;

        fireDepartment = new Texture(Gdx.files.internal("maps/objects/fire_department.png"));
        positionX = IsometricMath.mapToScreenX(19, 20);
        positionY = IsometricMath.mapToScreenY(19, 20);

        float padding = 0;
        setBounds(positionX - padding, positionY - padding, fireDepartment.getWidth() + 2 * padding, fireDepartment.getHeight() + 2 * padding);

        addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                ElFuego.LOGGER.log(Level.INFO, "FireDepartment clicked");
                showMenu();
                return true;
            }
        });
    }

    private void showMenu() {
        TextureAtlas textureAtlas = new TextureAtlas("mySkin/playScreen/fireDepartment/FireDepartment.atlas");
        Skin skin = new Skin(textureAtlas);
        ImageTextButton.ImageTextButtonStyle imageTextButtonStyle = new ImageTextButton.ImageTextButtonStyle();
        imageTextButtonStyle.font = new BitmapFont(Gdx.files.internal("mySkin/font/myFont/myQuicksandFire.fnt"));
        imageTextButtonStyle.font.getData().setScale(.8f);
        imageTextButtonStyle.up = skin.getDrawable("buy_fire_truck_icon");
        buyFireTruckButton = new ImageTextButton(EndTurnLogic.getFireTruckCost() + "$", imageTextButtonStyle);
        buyFireTruckButton.setPosition(getX() - 20, getY() + 80);

        stage.addActor(buyFireTruckButton);

        buyFireTruckButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                ElFuego.LOGGER.log(Level.INFO, "Buy truck clicked");
                buyFireTruckButton.remove();
                stage.act();

                float dialogWidth = 350;
                float dialogHeight = 150;
                Skin skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

                if (EndTurnLogic.getMoney() >= EndTurnLogic.getFireTruckCost()) {
                    final Dialog dialog = new Dialog("Buy Firetruck?", skin, "default") {
                        public void result(Object obj) {
                            if ((boolean) obj) {
                                EndTurnLogic.buyFireTruck();
                                hud.updateMoney();
                                FireTruck fireTruck = new FireTruck(stage, "_left_right", burningController);
                                fireTruck.setPosition(fireTruck.getX(), fireTruck.getY());
                                stage.addActor(fireTruck);

                                EndTurnLogic.addFireTruck(fireTruck);
                            }

                            PlayScreen.setPaused(false);
                            buyFireTruckButton.remove();
                        }
                    };

                    Button confirmButton = new TextButton("Confirm", skin, "small");
                    Button abortButton = new TextButton("Cancel", skin, "small");
                    dialog.button(confirmButton, true);
                    dialog.button(abortButton, false);
                    dialog.pad(20);
                    dialog.setSize(dialogWidth, dialogHeight);
                    dialog.setPosition(gameCam.position.x - (dialogWidth / 2), gameCam.position.y - (dialogHeight / 2));
                    dialog.setScale(gameCam.zoom);

                    PlayScreen.setPaused(true);
                    stage.addActor(dialog);
                } else {
                    final Dialog dialog = new Dialog("Not enough money available!", skin, "default") {
                        public void result(Object obj) {
                            PlayScreen.setPaused(false);
                        }
                    };

                    Button okButton = new TextButton("Ok", skin, "small");
                    dialog.button(okButton);
                    dialog.pad(20);
                    dialog.setSize(dialogWidth, dialogHeight);
                    dialog.setPosition(gameCam.position.x - (dialogWidth / 2), gameCam.position.y - (dialogHeight / 2));
                    dialog.setScale(gameCam.zoom);

                    PlayScreen.setPaused(true);
                    stage.addActor(dialog);
                }

                return true;
            }
        });
    }

    /**
     * Draws the FireDepartment within the specified interval to the screen
     *
     * @param batch       (supplied by Stage draw method)
     * @param alpha the interval to draw the FireDepartment
     */
    public void draw(Batch batch, float alpha) {
        batch.draw(fireDepartment, positionX, positionY);
    }
}
