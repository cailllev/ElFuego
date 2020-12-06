package ch.zhaw.psit3.elfuego.pathfinding;

/**
 * Defines an edge that is used to calculate the shortest path with the Dijkstra algorithm
 *
 * @param <N> in ElFuego usually a Road edge
 * @author Kevin Winzeler
 */
public class Edge<N> {
    private double weight;
    private Node startNode;
    private Node targetNode;

    Edge(double weight, Node startNode, Node targetNode) {
        this.weight = weight;
        this.startNode = startNode;
        this.targetNode = targetNode;
    }

    double getWeight() {
        return weight;
    }

    Node getTargetNode() {
        return targetNode;
    }
}
