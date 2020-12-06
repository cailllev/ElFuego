package ch.zhaw.psit3.elfuego.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.util.Map;

/**
 * AppPreferences is used to create persistent settings. Two files are used to allow to discard
 * changes.
 *
 * @author Levi Cailleret
 */
public class AppPreferences {
    private static final String[] DIFFICULTIES = {"Easy", "Challenging", "Hard"};
    private static final String PREF_DIFFICULTY = "difficulty";
    private static final String PREF_MUSIC_ENABLED = "music.enabled";
    private static final String PREF_MUSIC_VOLUME = "music";
    private static final String PREF_SOUND_ENABLED = "sound.enabled";
    private static final String PREF_SOUND_VOLUME = "sound";

    private static final String PROGRESS = "progress";

    static final String SETTINGS_NAME = "elfuego_settings";
    private static final String SETTINGS_NAME_TEMP = "elfuego_settings_temp";

    private static final int PREFS_COUNT = 5;

    private static Preferences getSettings() {
        return Gdx.app.getPreferences(SETTINGS_NAME);
    }

    private static Preferences getSettingsTemp() {
        return Gdx.app.getPreferences(SETTINGS_NAME_TEMP);
    }

    /**
     * @return unlocked levels, 1 as default
     */
    public static int getProgress(){
        return getSettings().getInteger(PROGRESS, 1);
    }

    /**
     * sets the progress value to level if level is higher than the allready existing value
     * @param level the value to write
     */
    public static void unlockLevel(int level){
        int level_old = getProgress();

        if (level > level_old) {
            getSettings().putInteger(PROGRESS, level);
            getSettings().flush();
        }
    }

    /**
     * sets the players progress to 1 - only level 1 will be playable
     */
    public static void resetProgress(){
        getSettings().putInteger(PROGRESS, 1);
        getSettings().flush();
    }

    /**
     * check if preferences are obsolete
     * if they are obsolete (returns false), init preferences with default values
     * @return  true if up to date
     *          false if obsolete - not all key value pairs are found
     */
    public static boolean checkLocalPrefs(){
        if (getSettings().get().size() < PREFS_COUNT){
            initPrefs();
            return false;
        }
        return true;
    }

    /**
     * clear old preferences, init preferences and temp preferences with default values
     * does not overwrite level
     */
    private static void initPrefs(){
        clearPreferences();

        setDifficulty(getSettings(), "Challenging");
        setMusicEnabled(getSettings(), true);
        setMusicVolume(getSettings(), 0.5f);
        setSoundEnabled(getSettings(), true);
        setSoundVolume(getSettings(), 0.5f);

        setDifficulty(getSettingsTemp(), "Challenging");
        setMusicEnabled(getSettingsTemp(), true);
        setMusicVolume(getSettingsTemp(), 0.5f);
        setSoundEnabled(getSettingsTemp(), true);
        setSoundVolume(getSettingsTemp(), 0.5f);

        unlockLevel(1);
    }

    /**
     * deletes all key and value pairs from the preferences file and the temp file
     */
    private static void clearPreferences(){
        getSettings().clear();
        getSettingsTemp().clear();
    }

    /**
     * writes the values from the source to the target
     * @param source source file
     * @param target target file
     */
    private static void savePreferences(Preferences source, Preferences target){
        Map targetEntries = target.get();
        Map newEntries = source.get();
        for (Object key : newEntries.keySet()){
            if (targetEntries.containsKey(key)){
                Object value = newEntries.get(key);
                switch((String) key) {
                    case PREF_DIFFICULTY:
                        setDifficulty(target, (String) value);
                    case PREF_MUSIC_ENABLED:
                        setMusicEnabled(target, Boolean.parseBoolean((String) value));
                        break;
                    case PREF_MUSIC_VOLUME:
                        setMusicVolume(target, Float.parseFloat((String) value));
                        break;
                    case PREF_SOUND_ENABLED:
                        setSoundEnabled(target, Boolean.parseBoolean((String) value));
                        break;
                    case PREF_SOUND_VOLUME:
                        setSoundVolume(target, Float.parseFloat((String) value));
                }
            }
        }
    }

    /**
     * writes the values from the temp file to the preferences file
     */
    public static void saveTempPrefs(){
        savePreferences(getSettingsTemp(), getSettings());
    }

    /**
     * writes the values from the preferences file to the temp file
     */
    public static void resetTempPrefs(){
        savePreferences(getSettings(), getSettingsTemp());
    }

    /**
     * @return true if preferences and temp prefs are equal
     */
    public static boolean comparePrefs(){
        Map existing = getSettings().get();
        Map temp = getSettingsTemp().get();

        for (Object key : existing.keySet()){
            if (key.equals(PROGRESS)){
                continue;
            }
            if (!temp.containsKey(key)){
                return false;
            } else if (!temp.get(key).equals(existing.get(key))){
                return false;
            }
        }

        return true;
    }

    /**
     * @return all possible difficulties
     */
    public static String[] getDifficulties() {
        return DIFFICULTIES;
    }

    /**
     * @return the difficulty in the preferences file
     */
    public static String getDifficulty() {
        return getSettings().getString(PREF_DIFFICULTY);
    }

    private static void setDifficulty(Preferences prefs, String difficulty) {
        prefs.putString(PREF_DIFFICULTY, difficulty);
        prefs.flush();
    }

    /**
     * sets the difficulty in the temp file
     * @param difficulty the difficulty to save in the temp file
     */
    public static void setDifficultyTemp(String difficulty) {
        getSettingsTemp().putString(PREF_DIFFICULTY, difficulty);
        getSettingsTemp().flush();
    }

    /**
     * @return wether music is enabled in the preferences file
     */
    public static boolean isMusicEnabled() {
        return getSettings().getBoolean(PREF_MUSIC_ENABLED);
    }

    private static void setMusicEnabled(Preferences prefs, boolean musicEnabled) {
        prefs.putBoolean(PREF_MUSIC_ENABLED, musicEnabled);
        prefs.flush();
    }

    /**
     * sets the musicEnabled in the temp file
     * @param musicEnabled the boolean to save in the temp file
     */
    public static void setMusicEnabledTemp(boolean musicEnabled) {
        getSettingsTemp().putBoolean(PREF_MUSIC_ENABLED, musicEnabled);
        getSettingsTemp().flush();
    }

    /**
     * @return the music volume value in the preferences file
     */
    public static float getMusicVolume() {
        return getSettings().getFloat(PREF_MUSIC_VOLUME);
    }

    private static void setMusicVolume(Preferences prefs, float volume) {
        prefs.putFloat(PREF_MUSIC_VOLUME, volume);
        prefs.flush();
    }

    /**
     * sets the musicVolume in the temp file
     * @param musicVolume the value to save in the temp file
     */
    public static void setMusicVolumeTemp(float musicVolume) {
        getSettingsTemp().putFloat(PREF_MUSIC_VOLUME, musicVolume);
        getSettingsTemp().flush();
    }

    /**
     * @return wether sound is enabled in the preferences file
     */
    public static boolean isSoundEnabled() {
        return getSettings().getBoolean(PREF_SOUND_ENABLED);
    }

    private static void setSoundEnabled(Preferences prefs, boolean soundEnabled) {
        prefs.putBoolean(PREF_SOUND_ENABLED, soundEnabled);
        prefs.flush();
    }

    /**
     * sets the soundEnabled in the temp file
     * @param soundEnabled the boolean to save in the temp file
     */
    public static void setSoundEnabledTemp(boolean soundEnabled) {
        getSettingsTemp().putBoolean(PREF_SOUND_ENABLED, soundEnabled);
        getSettingsTemp().flush();
    }

    /**
     * @return the sound volume value in the preferences file
     */
    public static float getSoundVolume() {
        return getSettings().getFloat(PREF_SOUND_VOLUME);
    }

    private static void setSoundVolume(Preferences prefs, float volume) {
        prefs.putFloat(PREF_SOUND_VOLUME, volume);
        prefs.flush();
    }

    /**
     * sets the soundVolume in the temp file
     * @param soundVolume the value to save in the temp file
     */
    public static void setSoundVolumeTemp(float soundVolume) {
        getSettingsTemp().putFloat(PREF_SOUND_VOLUME, soundVolume);
        getSettingsTemp().flush();
    }
}
