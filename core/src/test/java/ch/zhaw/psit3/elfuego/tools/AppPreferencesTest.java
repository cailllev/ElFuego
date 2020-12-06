package ch.zhaw.psit3.elfuego.tools;

import com.badlogic.gdx.Gdx;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static ch.zhaw.psit3.elfuego.tools.AppPreferences.SETTINGS_NAME;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(ch.zhaw.psit3.elfuego.GdxTestRunner.class)
public class AppPreferencesTest {

    @Before
    public void before(){
        AppPreferences.checkLocalPrefs();
    }

    @Test
    public void testInit(){
        // remove one key
        Gdx.app.getPreferences(SETTINGS_NAME).remove("difficulty");
        Gdx.app.getPreferences(SETTINGS_NAME).flush();

        // check should return false, one key less than expected
        assertFalse(AppPreferences.checkLocalPrefs());
        assertTrue(AppPreferences.checkLocalPrefs());
    }

    @Test
    public void testChanges(){
        // set music enabled to true in preferences
        AppPreferences.setMusicEnabledTemp(true);
        AppPreferences.saveTempPrefs();

        // set temp to false and check in preferences music enabled is still true
        AppPreferences.setMusicEnabledTemp(false);
        assertTrue(AppPreferences.isMusicEnabled());

        // save temp prefs to preferences and assert music enabled is now false
        AppPreferences.saveTempPrefs();
        assertFalse(AppPreferences.isMusicEnabled());
    }

    @Test
    public void testDiscardChanges(){
        boolean musicEnabled = AppPreferences.isMusicEnabled();
        AppPreferences.setMusicEnabledTemp(!musicEnabled);

        AppPreferences.resetTempPrefs();
        assertTrue(AppPreferences.comparePrefs());
    }
}
