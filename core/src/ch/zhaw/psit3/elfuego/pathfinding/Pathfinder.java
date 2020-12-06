package ch.zhaw.psit3.elfuego.pathfinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.zhaw.psit3.elfuego.sprites.Road;

/**
 * The Pathfinder class is the main class to calculate the shortest path for a FireTruck with the
 * Dijkstra algorithm.
 *
 * @author Kevin Winzeler
 */
public class Pathfinder {
    private HashMap<String, Road> roads;
    private HashMap<String, Node> nodes;
    private HashMap<String, Dijkstra> paths;

    /**
     * Takes a list of all road tiles and creates the nodes to find paths with the Dijkstra algorithm
     *
     * @param roads a HashMap of the road tiles
     */
    public Pathfinder(HashMap<String, Road> roads) {
        this.roads = roads;
        nodes = new HashMap<>();
        paths = new HashMap<>();

        addNodes();
        addEdges();
    }

    private void addNodes() {
        for (Road road : roads.values()) {
            int roadX = road.getPositionX();
            int roadY = road.getPositionY();
            String startRoad = roadX + "," + roadY;

            nodes.put(startRoad, new Node(startRoad));
        }
    }

    private void addEdges() {
        for (Node node : nodes.values()) {
            Road road = roads.get(node.getName());
            int roadX = road.getPositionX();
            int roadY = road.getPositionY();
            String destinationRoad;

            for (Road.Direction direction : road.getDirections()) {
                if (direction == Road.Direction.LEFT) {
                    destinationRoad = (roadX - 1) + "," + roadY;
                    node.addNeighbour(new Edge(road.getMovementSpeed(), node, nodes.get(destinationRoad)));
                }

                if (direction == Road.Direction.RIGHT) {
                    destinationRoad = (roadX + 1) + "," + roadY;
                    node.addNeighbour(new Edge(road.getMovementSpeed(), node, nodes.get(destinationRoad)));
                }

                if (direction == Road.Direction.TOP) {
                    destinationRoad = roadX + "," + (roadY - 1);
                    node.addNeighbour(new Edge(road.getMovementSpeed(), node, nodes.get(destinationRoad)));
                }

                if (direction == Road.Direction.BOTTOM) {
                    destinationRoad = roadX + "," + (roadY + 1);
                    node.addNeighbour(new Edge(road.getMovementSpeed(), node, nodes.get(destinationRoad)));
                }
            }
        }
    }

    /**
     * Get the fastest path from the specified start to the destination
     *
     * @param start       the tile coordinates of the start as a String
     * @param destination the tile coordinates of the destination as a String
     * @return a list of the Road nodes that need to be passed from start to destination
     */
    public ArrayList<String> findPath(String start, String destination) {
        ArrayList<String> result = new ArrayList<>();
        List<Node> path;

        if (nodes.get(start) != null && nodes.get(destination) != null) {
            Dijkstra dijkstra = new Dijkstra();
            dijkstra.computeShortestPaths(nodes.get(start));
            path = dijkstra.getShortestPathTo(nodes.get(destination));

            for (Node node : path) {
                result.add(node.getName());
            }
        }

        return result;
    }
}




