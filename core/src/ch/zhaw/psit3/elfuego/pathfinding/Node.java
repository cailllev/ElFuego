package ch.zhaw.psit3.elfuego.pathfinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A node contains a list of adjacent nodes and is needed to calculate the shortest path with the
 * Dijkstra algorithm.
 *
 * @author Kevin Winzeler
 */
public class Node implements Comparable<Node> {
    private String name;
    private List<Edge> adjacenciesList;
    private boolean visited;
    private Node predecessor;
    private double distance = Double.MAX_VALUE;

    Node(String name) {
        this.name = name;
        this.adjacenciesList = new ArrayList<>();
    }

    void addNeighbour(Edge edge) {
        this.adjacenciesList.add(edge);
    }

    /**
     * Gets the name of the node
     *
     * @return name of the node
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the node
     *
     * @param name of the node
     */
    public void setName(String name) {
        this.name = name;
    }

    List<Edge> getAdjacenciesList() {
        return adjacenciesList;
    }

    boolean isVisited() {
        return visited;
    }

    void setVisited(boolean visited) {
        this.visited = visited;
    }

    Node getPredecessor() {
        return predecessor;
    }

    void setPredecessor(Node predecessor) {
        this.predecessor = predecessor;
    }

    double getDistance() {
        return distance;
    }

    void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * Returns the name of the node
     *
     * @return name of the node
     */
    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Compares the distance between 2 nodes
     *
     * @param otherNode to compare distance
     * @return 1 if the current node is nearer than the otherNode
     */
    @Override
    public int compareTo(Node otherNode) {
        return Double.compare(this.distance, otherNode.getDistance());
    }
}