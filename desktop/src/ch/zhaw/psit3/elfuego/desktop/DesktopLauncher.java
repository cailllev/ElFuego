package ch.zhaw.psit3.elfuego.desktop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import ch.zhaw.psit3.elfuego.ElFuego;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = ElFuego.TITLE;

        //normal use
        System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
        config.width = LwjglApplicationConfiguration.getDesktopDisplayMode().width;
        config.height = LwjglApplicationConfiguration.getDesktopDisplayMode().height;


        //debugging
        boolean isDebug =
                java.lang.management.ManagementFactory.getRuntimeMXBean().
                        getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
        if (isDebug) {
            config.width = 1280;
            config.height = 780;
        }

        config.fullscreen = false;
        config.resizable = false;
        config.forceExit = false;

        new LwjglApplication(new ElFuego(config.width, config.height), config);

        // LOG_DEBUG logs all messages
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
    }
}
