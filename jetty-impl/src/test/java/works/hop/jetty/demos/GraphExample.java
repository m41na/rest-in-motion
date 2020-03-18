package works.hop.jetty.demos;

import org.junit.Test;

import java.util.*;

public class GraphExample {

    @Test
    public void testPrintGraph() {
        Graph graph = new Graph();
        graph.addEdge(0, 1);
        graph.addEdge(0, 4);
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(1, 4);
        graph.addEdge(2, 3);
        graph.addEdge(3, 4);
        graph.printGraph();
        System.out.println("done with print graph");
    }

    @Test
    public void testPrintBfs() {
        Graph graph = new Graph();
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(2, 3);
        graph.addEdge(3, 3);
        graph.printBFS(2);
        System.out.println("done with dfs");
    }

    static class Node {
        int data;

        public Node(int data) {
            this.data = data;
        }
    }

    static class Graph {
        Map<Integer, List<Node>> adjacency = new LinkedHashMap<>();

        void addEdge(int src, int dest) {
            adjacency.putIfAbsent(src, new LinkedList<>());
            adjacency.get(src).add(new Node(dest));
            adjacency.putIfAbsent(dest, new LinkedList<>());
            adjacency.get(dest).add(new Node(src));
        }

        void printGraph() {
            for (Integer key : adjacency.keySet()) {
                System.out.print(key + " -> [");
                for (Node node : adjacency.get(key)) {
                    System.out.print(node.data + ",");
                }
                System.out.println();
            }
        }

        void printBFS(int start) {
            Set<Integer> visited = new HashSet<>();
            Queue<Integer> queue = new LinkedList();
            visited.add(start);
            queue.add(start);

            while (!queue.isEmpty()) {
                int head = queue.poll();
                System.out.print(head + " -> ");
                for (Iterator<Node> iter = adjacency.get(head).iterator(); iter.hasNext(); ) {
                    int next = iter.next().data;
                    if (!visited.contains(next)) {
                        visited.add(next);
                        queue.add(next);
                    }
                }
            }
            System.out.println();
        }
    }
}
