package ch.zhaw.psit3.elfuego.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

/**
 * manage sounds / music files, handles logic regarding volume and muted
 *
 * @author Levi Cailleret
 */
@SuppressWarnings("LibGDXStaticResource")
public class Sounds {

    //music
    private static float MUSIC_VOLUME = AppPreferences.getMusicVolume();
    private static final boolean MUSIC_ENABLED = AppPreferences.isMusicEnabled();

    private static final Sound MENU_THEME = Gdx.audio.newSound(Gdx.files.internal("sound/world_on_fire.mp3"));
    private static long soundIdMenuTheme;
    private static boolean isPlayingMenuTheme = false;

    //sounds
    private static float SOUND_VOLUME = AppPreferences.getSoundVolume();
    private static boolean SOUND_ENABLED = AppPreferences.isSoundEnabled();

    public static final Sound KLICK = Gdx.audio.newSound(Gdx.files.internal("sound/klick.mp3"));
    public static final Sound FIRE_ON = Gdx.audio.newSound(Gdx.files.internal("sound/fire_on.mp3"));
    public static final Sound EXTINGUISHING = Gdx.audio.newSound(Gdx.files.internal("sound/extinguishing.mp3"));
    public static final Sound HOUSE_BREAKING = Gdx.audio.newSound(Gdx.files.internal("sound/house_breaking.mp3"));
    public static final Sound TRUCK_SELECTED = Gdx.audio.newSound(Gdx.files.internal("sound/truck.mp3"));

    /**
     * start a sound with preferencial volume if sound is enabled
     * @param sound the sound to play
     */
    public static void play(Sound sound){
        if (!sound.equals(MENU_THEME)){
            if (SOUND_ENABLED){
                sound.play(SOUND_VOLUME);
            }
        }
    }

    /**
     * starts to play "world_on_fire.mp3" with preferencial music volume if music is enabled
     * only if not plaing already
     */
    public static void startMenuTheme(){
        if (!isPlayingMenuTheme) {
            isPlayingMenuTheme = true;

            if (MUSIC_ENABLED) {
                soundIdMenuTheme = MENU_THEME.play(MUSIC_VOLUME);
            }
        }
    }

    /**
     * sets the volume of the MenuTheme
     * @param volume the volume
     */
    public static void updateVolumeMenuTheme(float volume) {
        MENU_THEME.setVolume(soundIdMenuTheme, volume);
    }

    /**
     * stops to play "world_on_fire.mp3"
     * only if it was playing
     */
    public static void stopMenuTheme(){
        if (isPlayingMenuTheme) {
            isPlayingMenuTheme = false;

            MENU_THEME.stop();
        }
    }

    /**
     * activates or deactivates all sounds
     * @param enabled true -> active, false -> mute
     */
    public static void soundsEnabled(boolean enabled){
        SOUND_ENABLED = enabled;
    }

    /**
     * sets the volume of all sound effects
     * @param volume the volume
     */
    public static void updateSoundVolume(float volume){
        SOUND_VOLUME = volume;
    }
}
