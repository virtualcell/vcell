package org.vcell.util.springsalad;

import java.util.*;

/*
 * test graph continuity
 * for undirected graph
 */
public class GraphContinuity {

    public enum Algorithm {
        DFS,
        BFS
    }

    public static class Graph {
        private int nodes;
        private LinkedList<Integer>[] adjList;

        public Graph(int nodes) {
            this.nodes = nodes;
            adjList = new LinkedList[nodes];
            for (int i = 0; i < nodes; i++) {
                adjList[i] = new LinkedList<>();
            }
        }

        // add an edge to the graph
        public void addEdge(int source, int destination) {
            adjList[source].add(destination);
            adjList[destination].add(source); // for undirected graph
        }

        public boolean isConnected(Algorithm algorithm) {
            if(Algorithm.DFS == algorithm) {
                return isConnectedDFS();
            } else {
                return isConnectedBFS();
            }
        }

        private boolean isConnectedDFS() {  // ------------ check continuity using DFS
            boolean[] visited = new boolean[nodes];             // array to track visited nodes
            DFS(0, visited);                               // start from node 0
            for (boolean nodeVisited : visited) {               // check if all nodes are visited
                if (!nodeVisited) {
                    return false;   // if any node is not visited, graph is disconnected
                }
            }
            return true;
        }
        private void DFS(int node, boolean[] visited) {         // depth-first search (DFS) algorithm
            visited[node] = true;                   // mark the current node as visited
            for (int adjacent : adjList[node]) {    // visit all adjacent nodes
                if (!visited[adjacent]) {
                    DFS(adjacent, visited);         // recursively visit unvisited adjacent nodes
                }
            }
        }

        private boolean isConnectedBFS() {  // ------------ check continuity using BFS
            boolean[] visited = new boolean[nodes];
            BFS(0, visited);
            for (boolean nodeVisited : visited) {
                if (!nodeVisited) {
                    return false;
                }
            }
            return true;
        }
        private void BFS(int startNode, boolean[] visited) {    // breadth-first search (BFS) algorithm
            Queue<Integer> queue = new LinkedList<>();
            queue.add(startNode);                       // add start node to the queue
            visited[startNode] = true;                  // mark it as visited
            while (!queue.isEmpty()) {
                int node = queue.poll();                // remove the node from the queue
                for (int adjacent : adjList[node]) {    // visit all adjacent nodes
                    if (!visited[adjacent]) {
                        queue.add(adjacent);            // add unvisited adjacent nodes to the queue
                        visited[adjacent] = true;       // mark them as visited
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Graph graph = new Graph(5);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(3, 4);
        System.out.println("DFS: the graph is " + (graph.isConnected(Algorithm.DFS) ? "connected" : "disconnected"));
        System.out.println("BFS: the graph is " + (graph.isConnected(Algorithm.BFS) ? "connected" : "disconnected"));
    }
}
