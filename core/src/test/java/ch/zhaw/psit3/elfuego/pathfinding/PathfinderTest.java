package ch.zhaw.psit3.elfuego.pathfinding;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import ch.zhaw.psit3.elfuego.sprites.Road;
import ch.zhaw.psit3.elfuego.tools.MapLoader;

import static org.junit.Assert.assertEquals;

@RunWith(ch.zhaw.psit3.elfuego.GdxTestRunner.class)
public class PathfinderTest {
    private HashMap<String, Road> roads;

    @Before
    public void setUp() {
        MapLoader mapLoader = new MapLoader("level_1.tmx");
        roads = mapLoader.getRoads();
    }

    @Test
    public void pathNotFound() {
        String startingPosition = "20,20";
        String destinationPosition = "1,1";
        Pathfinder pathfinder = new Pathfinder(roads);

        assertEquals(Collections.emptyList(), pathfinder.findPath(startingPosition, destinationPosition));
    }

    @Test
    public void findSimplePath() {
        String startingPosition = "20,20";
        String destinationPosition = "20,24";
        Pathfinder pathfinder = new Pathfinder(roads);

        List<String> expected = new ArrayList<>();
        expected.add("20,20");
        expected.add("20,21");
        expected.add("20,22");
        expected.add("20,23");
        expected.add("20,24");

        assertEquals(expected, pathfinder.findPath(startingPosition, destinationPosition));
    }

    @Test
    public void findLongPath() {
        String startingPosition = "20,24";
        String destinationPosition = "9,13";
        Pathfinder pathfinder = new Pathfinder(roads);

        List<String> expected = new ArrayList<>();
        expected.add("20,24");
        expected.add("20,23");
        expected.add("20,22");
        expected.add("20,21");
        expected.add("20,20");
        expected.add("20,19");
        expected.add("20,18");
        expected.add("20,17");
        expected.add("20,16");
        expected.add("20,15");
        expected.add("20,14");
        expected.add("20,13");
        expected.add("19,13");
        expected.add("18,13");
        expected.add("17,13");
        expected.add("16,13");
        expected.add("15,13");
        expected.add("14,13");
        expected.add("13,13");
        expected.add("12,13");
        expected.add("11,13");
        expected.add("10,13");
        expected.add("9,13");

        assertEquals(expected, pathfinder.findPath(startingPosition, destinationPosition));
    }
}