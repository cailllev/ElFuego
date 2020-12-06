package ch.zhaw.psit3.elfuego.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import ch.zhaw.psit3.elfuego.ElFuego;
import ch.zhaw.psit3.elfuego.tools.BaseGame;

/**
 * BaseScreen extends Screen and adds some functionality required for the ElFuego game. All screens
 * from ElFuego extend this class and get access to stages and cameras required to display the UI.
 *
 * @author Benjamin Burtscher
 */
public abstract class BaseScreen implements Screen, InputProcessor
{
    ElFuego game;

    OrthographicCamera gameCam;
    Viewport gameViewport;

    Stage stage;
    Skin skin;

    //clear screen color
    private float red = 0.9f;
    private float green = 0.9f;
    private float blue = 0.9f;
    private float alpha = 1;

    BaseScreen(Object object) {
        stage = createNewStage();
        skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

        setInputProcessor();
        preprocessParameter(object);
        initialize();
    }

    private Stage createNewStage(){
        game = (ElFuego) BaseGame.getGame();

        gameCam = new OrthographicCamera();
        int[] res = ElFuego.getResolution();
        gameViewport = new ScalingViewport(Scaling.fit, res[0], res[1], gameCam);

        return new Stage(gameViewport, game.getBatch());
    }

    private void setInputProcessor(){
        Gdx.input.setInputProcessor(stage);
    }

    private void preprocessParameter(Object object){
        if (object != null)
            processParameter(object);
    }

    /**
     * Method to process parameters from an external source.
     *
     * @param object input parameter object
     */
    public abstract void processParameter(Object object);

    /**
     * The initialize method is executed upon creation of a class' instance.
     */
    public abstract void initialize();

    /**
     * Method is responsible to update the UI after every frame
     *
     * @param dt the interval for a frame
     */
    public abstract void update(float dt);

    /**
     * Gameloop:
     * (1) process input (discrete handled by listener; continuous in update)
     * (2) update game logic
     * (3) render the graphics
     *
     * @param dt the interval for a frame
     */
    //
    public void render(float dt) {
        // act methods
        stage.act(dt);

        // clear the screen
        Gdx.gl.glClearColor(red, green, blue, alpha);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // defined by user
        update(dt);

        game.getBatch().setProjectionMatrix(stage.getCamera().combined);

        // draw the graphics
        stage.draw();
    }

    void setClearScreenColor(float red, float green, float blue, float alpha){
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    /**
     * Sets the screen to a new size
     *
     * @param width  new screen width
     * @param height new screen height
     */
    public void resize(int width, int height) {
        gameViewport.update(width, height);
    }

    /**
     * Allows to pause the game. If the game is paused no interactions with the map can be performed.
     * Used to show different dialogs.
     */
    public void pause()   { }

    /**
     * Resumes a paused game
     */
    public void resume()  { }

    /**
     * Cleans up the resources after the disposal of the screen has been initiated
     */
    public void dispose() {
        stage.dispose();
    }

    /**
     * Enables the screen to be shown
     */
    public void show() {
    }

    /**
     * Hides the current screen
     */
    public void hide() {
    }
}
