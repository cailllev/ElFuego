package ch.zhaw.psit3.elfuego.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Traverses the nodes, starting from the source node to find the shortest paths
 *
 * @author Kevin Winzeler
 */
class Dijkstra {
    void computeShortestPaths(Node sourceNode) {
        sourceNode.setDistance(0);
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>();
        priorityQueue.add(sourceNode);
        sourceNode.setVisited(true);

        while (!priorityQueue.isEmpty()) {
            Node actualNode = priorityQueue.poll();

            for (Edge edge : actualNode.getAdjacenciesList()) {

                Node node = edge.getTargetNode();
                if (!node.isVisited()) {
                    double newDistance = actualNode.getDistance() + edge.getWeight();

                    if (newDistance < node.getDistance()) {
                        priorityQueue.remove(node);
                        node.setDistance(newDistance);
                        node.setPredecessor(actualNode);
                        priorityQueue.add(node);
                    }
                }
            }
            actualNode.setVisited(true);
        }
    }

    List<Node> getShortestPathTo(Node targetNode) {
        List<Node> path = new ArrayList<>();

        for (Node node = targetNode; node != null; node = node.getPredecessor()) {
            path.add(node);
        }

        Collections.reverse(path);
        return path;
    }
}
