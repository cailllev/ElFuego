package ch.zhaw.psit3.elfuego.tools;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ch.zhaw.psit3.elfuego.sprites.House;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReferenceArray;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static ch.zhaw.psit3.elfuego.tools.MapLoader.getNumTilesHeight;

/**
 * Manages the current state of the different houses on the map. Randomly sets houses on fire and adds
 * different animations for the current states.
 *
 * @author Selma Wirth
 */
public class BurningController {
    private ArrayList<House> houses;
    private ArrayList<House> intactHouses;
    private AtomicReferenceArray<House> burningHouses;
    private int burningHousesCount;
    private TiledMap map;
    private TiledMapTileSets tileSet;
    private String mapTitle;

    /**
     * Initializes the variables required for the burning controller
     *
     * @param houses   list of all houses on the map
     * @param map      the current map
     * @param tileSet  list of the different tile-types
     * @param mapTitle name of the house's image
     */
    public BurningController(ArrayList<House> houses, TiledMap map, TiledMapTileSets tileSet, String mapTitle){
        this.houses = houses;
        this.intactHouses = new ArrayList<>(houses);
        this.burningHouses = new AtomicReferenceArray<>(houses.size());
        this.burningHousesCount = 0;
        this.map = map;
        this.tileSet = tileSet;
        this.mapTitle = mapTitle;
    }

    private int getRandomIndex(int range){
        Random random = new Random();
        return random.nextInt((range)+1);
    }

    private House getRandomHouse(){
        return intactHouses.get(getRandomIndex((intactHouses.size()-1)));
    }

    /**
     * Provides a list of all burning houses
     *
     * @return a list of all burning houses
     */
    public AtomicReferenceArray<House> getBurningHouses(){
        return burningHouses;
    }

    private String getNamePart(String name, int id){
        String[] split = name.split("_");
        return split[id];
    }

   private HashMap<String, String> getAnimations(String mapTitle, String animationType) throws ParserConfigurationException, IOException, SAXException {
        HashMap<String, String> animations = new HashMap<>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(new File("maps/" + mapTitle));
        NodeList nodeList = document.getElementsByTagName("tileset");
        for(int i=0; i < nodeList.getLength();i++ ){
            if(nodeList.item(i).getAttributes().getNamedItem("name").getNodeValue().contains(animationType)) {
                animations.put(nodeList.item(i).getAttributes().getNamedItem("name").getNodeValue(), nodeList.item(i).getAttributes().getNamedItem("firstgid").getNodeValue());
            }
        }
        return animations;
    }

    private int getAnimationIdByName(String name, String animationType) throws IOException, SAXException, ParserConfigurationException {
        return Integer.parseInt(getAnimations(mapTitle, animationType).get(name));
    }

    /**
     * Sets a random house on fire
     *
     * @throws ParserConfigurationException thrown if tiles can't be parsed
     * @throws SAXException thrown if there is an xml-error
     * @throws IOException thrown if there is an error reading the map
     */
    public void setRandomHouseOnFire() throws ParserConfigurationException, SAXException, IOException {
        House house = getRandomHouse();
        house.setBurning();
        intactHouses.remove(house);
        burningHouses.set(burningHousesCount++, house);
        TiledMapTileLayer backgroundMapTileLayer = (TiledMapTileLayer) map.getLayers().get(0);
        TiledMapTileLayer.Cell houseCell = new TiledMapTileLayer.Cell();
        String orientation = getNamePart(house.getTitle(), 1);
        String type = getNamePart(house.getTitle(),2);

        switch(orientation) {
            case "right":
                houseCell.setTile(tileSet.getTile(getAnimationIdByName("burning_house_right_" + type, "burning")));
                break;
            case "bottom":
                houseCell.setTile(tileSet.getTile(getAnimationIdByName("burning_house_bottom_" + type, "burning")));
                break;
            case "left":
                houseCell.setTile(tileSet.getTile(getAnimationIdByName("burning_house_left_" + type, "burning")));
                break;
            case "top":
                houseCell.setTile(tileSet.getTile(getAnimationIdByName("burning_house_top_" + type, "burning")));
                break;
        }
        backgroundMapTileLayer.setCell(house.getPositionX(), (int)(getNumTilesHeight() - house.getPositionY() - 1), houseCell);
    }

    public void setAgainBurning(House house) throws ParserConfigurationException, SAXException, IOException {
        TiledMapTileLayer backgroundMapTileLayer = (TiledMapTileLayer) map.getLayers().get(0);
        TiledMapTileLayer.Cell houseCell = new TiledMapTileLayer.Cell();
        String orientation = getNamePart(house.getTitle(), 1);
        String type = getNamePart(house.getTitle(),2);

        switch(orientation) {
            case "right":
                houseCell.setTile(tileSet.getTile(getAnimationIdByName("burning_house_right_" + type, "burning")));
                break;
            case "bottom":
                houseCell.setTile(tileSet.getTile(getAnimationIdByName("burning_house_bottom_" + type, "burning")));
                break;
            case "left":
                houseCell.setTile(tileSet.getTile(getAnimationIdByName("burning_house_left_" + type, "burning")));
                break;
            case "top":
                houseCell.setTile(tileSet.getTile(getAnimationIdByName("burning_house_top_" + type, "burning")));
                break;
        }
        backgroundMapTileLayer.setCell(house.getPositionX(), (int)(getNumTilesHeight() - house.getPositionY() - 1), houseCell);
    }

    /**
     * Changes the image for a burned house and disables further actions
     *
     * @param house the House that is burned down
     * @throws ParserConfigurationException thrown if tiles can't be parsed
     * @throws SAXException thrown if there is an xml-error
     * @throws IOException thrown if there is an error reading the map
     */
    public void burnedHouse(House house) throws ParserConfigurationException, SAXException, IOException {
        int houseIndex = getHouseIndex(house);
        if (houseIndex >= 0) {
            burningHouses.set(houseIndex, null);
            shiftArray(houseIndex);
            burningHousesCount -= 1;
            TiledMapTileLayer backgroundMapTileLayer = (TiledMapTileLayer) map.getLayers().get(0);
            TiledMapTileLayer.Cell houseCell = new TiledMapTileLayer.Cell();
            houseCell.setTile(tileSet.getTile(getAnimationIdByName("burned_house", "burned")));
            backgroundMapTileLayer.setCell(house.getPositionX(), (int) (getNumTilesHeight() - house.getPositionY() - 1), houseCell);
        }
    }

    /**
     * Restore a burned house
     *
     * @param house the house to be restored
     * @throws ParserConfigurationException thrown if tiles can't be parsed
     * @throws SAXException thrown if there is an xml-error
     * @throws IOException thrown if there is an error reading the map
     */
    public void restoreHouse(House house) throws ParserConfigurationException, SAXException, IOException {
        TiledMapTileLayer backgroundMapTileLayer = (TiledMapTileLayer) map.getLayers().get(0);
        TiledMapTileLayer.Cell houseCell = new TiledMapTileLayer.Cell();
        intactHouses.add(house);
        int houseIndex = getHouseIndex(house);
        if (houseIndex >= 0) {
            burningHouses.set(houseIndex, null);
            shiftArray(houseIndex);
            burningHousesCount -= 1;
            String orientation = getNamePart(house.getTitle(), 1);
            String type = getNamePart(house.getTitle(), 2);

            switch (orientation) {
                case "right":
                    houseCell.setTile(tileSet.getTile(getAnimationIdByName("building_right_" + type, "building")));
                    break;
                case "bottom":
                    houseCell.setTile(tileSet.getTile(getAnimationIdByName("building_bottom_" + type, "building")));
                    break;
                case "left":
                    houseCell.setTile(tileSet.getTile(getAnimationIdByName("building_left_" + type, "building")));
                    break;
                case "top":
                    houseCell.setTile(tileSet.getTile(getAnimationIdByName("building_top_" + type, "building")));
                    break;
            }
            backgroundMapTileLayer.setCell(house.getPositionX(), (int) (getNumTilesHeight() - house.getPositionY() - 1), houseCell);
        }
    }

    /**
     * Start to extinguish a house, remove house from burning houses
     *
     * @param house the House to extinguish
     */
    public void extinguishHouse(House house) throws ParserConfigurationException, SAXException, IOException {
        TiledMapTileLayer backgroundMapTileLayer = (TiledMapTileLayer) map.getLayers().get(0);
        TiledMapTileLayer.Cell houseCell = new TiledMapTileLayer.Cell();
        String orientation = getNamePart(house.getTitle(), 1);
        String type = getNamePart(house.getTitle(), 2);

        switch(orientation) {
            case "right":
                houseCell.setTile(tileSet.getTile(getAnimationIdByName("extinguish_house_right_" + type, "extinguish")));
                break;
            case "bottom":
                houseCell.setTile(tileSet.getTile(getAnimationIdByName("extinguish_house_bottom_" + type, "extinguish")));
                break;
            case "left":
                houseCell.setTile(tileSet.getTile(getAnimationIdByName("extinguish_house_left_" + type, "extinguish")));
                break;
            case "top":
                houseCell.setTile(tileSet.getTile(getAnimationIdByName("extinguish_house_top_" + type, "extinguish")));
                break;
        }
        backgroundMapTileLayer.setCell(house.getPositionX(), (int)(getNumTilesHeight() - house.getPositionY() - 1), houseCell);
    }

    /**
     * Provides a list of all Houses
     *
     * @return a list of all houses
     */
    public List<House> getHouses(){
        return houses;
    }

    /**
     * Get the amount of burning houses
     *
     * @return the number of currently burning houses
     */
    public int getBurningHousesCount() {
        return burningHousesCount;
    }

    private int getHouseIndex(House house){
        for (int i = 0; i < burningHousesCount; i++){
            if (burningHouses.get(i).equals(house)){
                return i;
            }
        }
        return -1;
    }

    private void shiftArray(int from){
        for (int i = from; i < burningHousesCount - 1; i++){
            burningHouses.set(i, burningHouses.get(i + 1));
        }
        burningHouses.set(burningHousesCount - 1, null);
    }

    /**
     * searches the house in burning houses
     * @param house the house to search
     * @return true if house was found
     */
    public boolean containsHouse(House house){
        for (int i = 0; i < burningHousesCount; i++){
            if (house.equals(burningHouses.get(i))){
                return true;
            }
        }
        return false;
    }
}
