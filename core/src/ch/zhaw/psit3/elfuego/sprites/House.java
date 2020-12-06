package ch.zhaw.psit3.elfuego.sprites;

import ch.zhaw.psit3.elfuego.actors.BaseActor;
import ch.zhaw.psit3.elfuego.logic.EndTurnLogic;

/**
 * A house is an actor that can be set on fire and extinguished by a FireTruck. The house can get
 * destroyed if no FireTruck extinguishes the house in time.
 *
 * @author Kevin Winzeler
 */
public class House extends BaseActor {

    private String title;
    private int id;

    private int positionX;
    private int positionY;
    private boolean isBurning;

    private final int MAX_HEALTH;
    private int health;

    private final int MAX_FIRE_HEALTH;
    private int fireHealth;

    private int reward;
    private int punishment;

    /**
     * Creates a new House and sets its initial values
     *  @param title           is the name for the House
     * @param id              unique identifier for the House
     * @param positionX       the House's x-position as nth-tile
     * @param positionY       the House's y-position as nth-tile
     */
    public House(String title, int id, int positionX, int positionY) {
        this.title = title;
        this.id = id;
        this.positionX = positionX;
        this.positionY = positionY;
        this.isBurning = false;

        if (title.contains("building") && title.contains("4")) {
            this.MAX_HEALTH = EndTurnLogic.getSkyScraperMaxHealth();
            this.MAX_FIRE_HEALTH = EndTurnLogic.getSkyScraperFireHealth();

            this.reward = EndTurnLogic.getSkyScraperReward();
            this.punishment = EndTurnLogic.getSkyScraperPunishment();

        } else if (title.contains("building") && (title.contains("6") || title.contains("7"))) {
            this.MAX_HEALTH = EndTurnLogic.getBigHouseMaxHealth();
            this.MAX_FIRE_HEALTH = EndTurnLogic.getBigHouseFireHealth();

            this.reward = EndTurnLogic.getBigHouseReward();
            this.punishment = EndTurnLogic.getBigHousePunishment();

        } else {
            this.MAX_HEALTH = EndTurnLogic.getHouseMaxHealth();
            this.MAX_FIRE_HEALTH = EndTurnLogic.getHouseFireHealth();

            this.reward = EndTurnLogic.getHouseReward();
            this.punishment = EndTurnLogic.getHousePunishment();
        }

        this.health = MAX_HEALTH;
        this.fireHealth = 0;
    }

    /**
     * @return the title of the House
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return The id of the house
     */
    public int getId() {
        return id;
    }

    /**
     * @return the House's x-position as nth-tile
     */
    public int getPositionX() {
        return positionX;
    }

    /**
     * @return the House's y-position as nth-tile
     */
    public int getPositionY() {
        return positionY;
    }

    //methods for turnlogic

    /**
     * If a burning House is extinguished the player gets a reward according to the houses size
     *
     * @return the amount of money a player gets for an extinguished House
     */
    public int getReward() {
        return reward;
    }

    /**
     * If a House burns down the player has to pay a fine according to the houses size
     *
     * @return the amount of money the player has to pay
     */
    public int getPunishment() {
        return punishment;
    }

    /**
     * sets the house on fire, does nothing if house is already on fire
     */
    public void setBurning(){
        if (!isBurning) {
            isBurning = true;
            fireHealth = MAX_FIRE_HEALTH;
        }
    }

    /**
     * @return true if house got destroyed
     */
    public boolean burnDown() {
        if (isBurning) {
            health--;
            if (health <= 0) {
                isBurning = false;
                return true;
            }
        }
        return false;
    }

    /**
     * @return true if house was extigunished
     */
    public boolean extinguish(){
        if (isBurning){
            fireHealth--;
            if (fireHealth <= 0){
                isBurning = false;
                health = MAX_HEALTH;
                return true;
            }
        }
        return false;
    }
}
