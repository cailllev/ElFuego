package ch.zhaw.psit3.elfuego.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import ch.zhaw.psit3.elfuego.actors.FireTruck;
import ch.zhaw.psit3.elfuego.sprites.House;
import ch.zhaw.psit3.elfuego.tools.AppPreferences;
import ch.zhaw.psit3.elfuego.tools.BurningController;
import ch.zhaw.psit3.elfuego.tools.Sounds;

/**
 * This class handles the logic of the ElFuego game. It reads the configuration from an external file
 * and handles all objects on the PlayScreen.
 *
 * @author Sascha Kyburz, Levi Cailleret
 */
public class EndTurnLogic {

    private static List<FireTruck> firetrucks = new ArrayList<>();

    private static int turn = 1;

    // read values from level_values.json

    private static int frequency;
    private static float burningHousesRatioForGameOver;

    private static int fireTruckCost;
    private static int fireTruckCostRise;
    private static int fireTruckMovement;

    private static int houseReward;
    private static int housePunishment;
    private static int houseMaxHealth;
    private static int houseFireHealth;

    private static int bigHouseReward;
    private static int bigHousePunishment;
    private static int bigHouseMaxHealth;
    private static int bigHouseFireHealth;

    private static int skyScraperReward;
    private static int skyScraperPunishment;
    private static int skyScraperMaxHealth;
    private static int skyScraperFireHealth;

    private static int money;
    private static int turnGoal;
    private static int counter = 0;
    private static int everyXFires;

    private static BurningController burningController;

    /**
     * Sets the BurningController to handle burning houses
     *
     * @param bC BurningController
     */
    public static void setBurningController(BurningController bC){
        burningController = bC;
    }

    /**
     * Initializes the game by reading in FireTruck cost, level, turns, difficulty and houses
     *
     * @param level the current level
     */
    public static void init(int level){
        JsonReader json = new JsonReader();
        JsonValue base = json.parse(Gdx.files.internal("maps/level_values.json"));

        frequency = base.get("frequency").asInt();
        everyXFires = base.get("everyXFires").asInt();
        burningHousesRatioForGameOver = base.get("burningHousesRatioForGameOver").asFloat();

        fireTruckCost = base.get("fireTruckCost").asInt();
        fireTruckCostRise = base.get("fireTruckCostRise").asInt();
        fireTruckMovement = base.get("fireTruckMovement").asInt();

        houseReward = base.get("houseReward").asInt();
        housePunishment = base.get("housePunishment").asInt();
        houseMaxHealth = base.get("houseMaxHealth").asInt();
        houseFireHealth = base.get("houseFireHealth").asInt();

        bigHouseReward = base.get("bigHouseReward").asInt();
        bigHousePunishment = base.get("bigHousePunishment").asInt();
        bigHouseMaxHealth = base.get("bigHouseMaxHealth").asInt();
        bigHouseFireHealth = base.get("bigHouseFireHealth").asInt();

        skyScraperReward = base.get("skyScraperReward").asInt();
        skyScraperPunishment = base.get("skyScraperPunishment").asInt();
        skyScraperMaxHealth = base.get("skyScraperMaxHealth").asInt();
        skyScraperFireHealth = base.get("skyScraperFireHealth").asInt();

        money = base.get("level_" + level).get(0).asInt();
        turnGoal = base.get("level_" + level).get(1).asInt();

        String difficulty = AppPreferences.getDifficulty();
        amplifyValues(difficulty);

        turn = 1;
    }

    private static void amplifyValues(String difficulty){
        switch(difficulty){
            case "Easy":
                frequency += 1;
                everyXFires += 1;
                money *= 1.5;
                turnGoal /= 1.5;
                break;

            case "Challenging":
                break;

            case "Hard":
                everyXFires--;
                money /= 1.2;
                turnGoal *= 1;
                fireTruckCostRise += 200;
        }

        //round down next to 100
        money = (money / 100) * 100;
    }

    /**
     * Checks if the game is either lost or won
     *
     * @return false if GameOver
     *
     * @throws ParserConfigurationException thrown if tiles can't be parsed
     * @throws SAXException thrown if there is an xml-error
     * @throws IOException thrown if there is an error reading the map
     */
    public static boolean endTurn() throws IOException, SAXException, ParserConfigurationException {

        for (int i = 0; i < burningController.getBurningHousesCount(); i++) {
            // let house burn for one turn, returns true if house is destroyed
            House house = burningController.getBurningHouses().get(i);
            if (house.burnDown()) {
                i -= 1;
                Sounds.play(Sounds.HOUSE_BREAKING);
                money -= house.getPunishment();
                burningController.burnedHouse(house);
            }
        }

        for(FireTruck firetruck: firetrucks) {
            if((null != firetruck.getRoute().get(0)) && firetruck.getMoves() != 0) {
                firetruck.move();
            }

            firetruck.resetMoves();
        }

        // start a fire in turn 2, turn 2 + frequency, turn 2 + 2*frequency, turn 2 + 3*frequency, ...
        if((turn - 1) % frequency == 0) {
            burningController.setRandomHouseOnFire();
            Sounds.play(Sounds.FIRE_ON);

            //increase the frequency of fires every <count> new fires
            if (counter == everyXFires) {
                counter = 0;
                if (frequency > 1){
                    frequency--;
                }
            } else {
                counter++;
            }
        }

        // game over
        if (money < 0 || burningController.getBurningHousesCount() >
                burningController.getHouses().size() * burningHousesRatioForGameOver)
            return false;

        turn++;
        return true;
    }

    /**
     * Gets the current turn
     *
     * @return the current turn
     */
    public static int getTurn(){
        return turn;
    }

    /**
     * Gets the current turn goal
     *
     * @return the current turn goal
     */
    public static int getTurnGoal(){
        return turnGoal;
    }

    /**
     * Checks if the player survived until the turn goal was reached
     *
     * @return true if the turnGoal is reached
     */
    public static boolean isTurnGoalAchieved(){
        return turn >= turnGoal;
    }

    /**
     * Gets the current balance from the player
     *
     * @return the current balance
     */
    public static int getMoney(){
        return money;
    }

    /**
     * Adds a new FireTruck to the game
     *
     * @param fireTruck the FireTruck to add
     */
    public static void addFireTruck(FireTruck fireTruck){
        firetrucks.add(fireTruck);
    }

    /**
     * Gets a list of the available FireTrucks
     *
     * @return a list of the FireTrucks
     */
    public static List<FireTruck> getFiretrucks() {
        return firetrucks;
    }

    /**
     * Allows to buy a new FireTruck if the player can afford it
     */
    public static void buyFireTruck(){
        money -= fireTruckCost;
        fireTruckCost += fireTruckCostRise;
    }

    /**
     * Gets the current cost for a FireTruck
     *
     * @return the current cost for a FireTruck
     */
    public static int getFireTruckCost(){
        return fireTruckCost;
    }

    public static int getFireTruckMovement(){
        return fireTruckMovement;
    }


    /**
     * adds the reward from an extinguished house to the players money and updates the hud
     * @param reward the reward
     */
    public static void addReward(int reward){
        money +=  reward;
    }

    public static int getHouseReward() {
        return houseReward;
    }

    public static int getHousePunishment() {
        return housePunishment;
    }

    public static int getHouseMaxHealth() {
        return houseMaxHealth;
    }

    public static int getHouseFireHealth() {
        return houseFireHealth;
    }

    public static int getBigHouseReward() {
        return bigHouseReward;
    }

    public static int getBigHousePunishment() {
        return bigHousePunishment;
    }

    public static int getBigHouseMaxHealth() {
        return bigHouseMaxHealth;
    }

    public static int getBigHouseFireHealth() {
        return bigHouseFireHealth;
    }

    public static int getSkyScraperReward() {
        return skyScraperReward;
    }

    public static int getSkyScraperPunishment() {
        return skyScraperPunishment;
    }

    public static int getSkyScraperMaxHealth() {
        return skyScraperMaxHealth;
    }

    public static int getSkyScraperFireHealth() {
        return skyScraperFireHealth;
    }
}
