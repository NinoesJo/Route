import java.security.InvalidAlgorithmParameterException;
import java.io.*;
import java.util.*;


/**
 * Models a weighted graph of latitude-longitude points
 * and supports various distance and routing operations.
 * To do: Add your name(s) as additional authors
 * @author Brandon Fain
 * @author Owen Astrachan modified in Fall 2023
 *
 */
public class GraphProcessor {
    /**
     * Creates and initializes a graph from a source data
     * file in the .graph format. Should be called
     * before any other methods work.
     * @param file a FileInputStream of the .graph file
     * @throws Exception if file not found or error reading
     */

    // include instance variables here
    private Map<Point, List<Point>> myGraph;
    private List<Point> verticies;

    public GraphProcessor(){
        // TODO initialize instance variables
        myGraph = new HashMap<>();
        verticies = new ArrayList<>();
    }

    /**
     * Creates and initializes a graph from a source data
     * file in the .graph format. Should be called
     * before any other methods work.
     * @param file a FileInputStream of the .graph file
     * @throws IOException if file not found or error reading
     */

    public void initialize(FileInputStream file) throws IOException {
        // TODO implement by reading info and creating graph
        Scanner reader = new Scanner(file);
        int numV = reader.nextInt();
        int numE = reader.nextInt();
        reader.nextLine();
        for (int i = 0; i < numV; i++) {
            String[] vertexList = reader.nextLine().split(" ");
            Point v = new Point(Double.parseDouble(vertexList[1]), Double.parseDouble(vertexList[2]));
            myGraph.put(v, new ArrayList<>());
            verticies.add(v);
        }
        for (int j = 0; j < numE; j++) {
            String[] edgeList = reader.nextLine().split(" ");
            Point one = verticies.get(Integer.parseInt(edgeList[0]));
            Point two = verticies.get(Integer.parseInt(edgeList[1]));
            myGraph.get(one).add(two);
            myGraph.get(two).add(one);
        }
        reader.close();
    }

    /**
     * NOT USED IN FALL 2023, no need to implement
     * @return list of all vertices in graph
     */

    public List<Point> getVertices(){
        return null;
    }

    /**
     * NOT USED IN FALL 2023, no need to implement
     * @return all edges in graph
     */
    public List<Point[]> getEdges(){
        return null;
    }

    /**
     * Searches for the point in the graph that is closest in
     * straight-line distance to the parameter point p
     * @param p is a point, not necessarily in the graph
     * @return The closest point in the graph to p
     */
    public Point nearestPoint(Point p) {
        // TODO implement nearestPoint
        Point nearestP = null;
        double minDist = Double.MAX_VALUE;
        for (Point v: verticies) {
            if (p.distance(v) < minDist) {
                minDist = p.distance(v);
                nearestP = v;
            }
        }
        return nearestP;
    }


    /**
     * Calculates the total distance along the route, summing
     * the distance between the first and the second Points, 
     * the second and the third, ..., the second to last and
     * the last. Distance returned in miles.
     * @param start Beginning point. May or may not be in the graph.
     * @param end Destination point May or may not be in the graph.
     * @return The distance to get from start to end
     */
    public double routeDistance(List<Point> route) {
        double d = 0.0;
        // TODO implement routeDistance
        for (int i = 0; i < route.size() - 1; i++) {
            Point current = route.get(i);
            Point next = route.get(i + 1);
            d += current.distance(next);
        }
        return d;
    }
    

    /**
     * Checks if input points are part of a connected component
     * in the graph, that is, can one get from one to the other
     * only traversing edges in the graph
     * @param p1 one point
     * @param p2 another point
     * @return true if and onlyu if p2 is reachable from p1 (and vice versa)
     */
    public boolean connected(Point p1, Point p2) {
        // TODO implement connected
        Queue<Point> queue = new LinkedList<>();
        Set<Point> visited = new HashSet<>();
        queue.add(p1);
        visited.add(p1);
        while (queue.size() > 0) {
            Point current = queue.remove();
            if (current.equals(p2)) return true;
            for (Point adj: myGraph.get(current)) {
                if (!visited.contains(adj)) {
                    queue.add(adj);
                    visited.add(adj);
                }
            }
        }
        return false;
    }

    /**
     * Returns the shortest path, traversing the graph, that begins at start
     * and terminates at end, including start and end as the first and last
     * points in the returned list. If there is no such route, either because
     * start is not connected to end or because start equals end, throws an
     * exception.
     * @param start Beginning point.
     * @param end Destination point.
     * @return The shortest path [start, ..., end].
     * @throws IllegalArgumentException if there is no such route, 
     * either because start is not connected to end or because start equals end.
     */
    public List<Point> route(Point start, Point end) throws IllegalArgumentException {
        // TODO implement route
        if (!myGraph.containsKey(start) || !myGraph.containsKey(end) || start.equals(end) || !connected(start, end)) {
            throw new IllegalArgumentException("No path between start and end");
        }
        Map<Point, Double> distanceMap = new HashMap<>();
        Map<Point, Point> predMap = new HashMap<>();
        predMap.put(start, null);
        final Comparator<Point> comp = Comparator.comparingDouble(distanceMap::get);
        PriorityQueue<Point> pq = new PriorityQueue<>(comp);
        distanceMap.put(start, 0.0);
        pq.add(start);
        while (pq.size() > 0) {
            Point current = pq.remove();
            if (current.equals(end)) {
                List<Point> pathRoute = new ArrayList<>();
                Point c = end;
                while (c != null) {
                    pathRoute.add(c);
                    c = predMap.get(c);
                }
                Collections.reverse(pathRoute);
                return pathRoute;
            }
            for (Point p: myGraph.get(current)) {
                double weight = current.distance(p);
                double newDist = distanceMap.get(current) + weight;
                if (!distanceMap.containsKey(p) || newDist < distanceMap.get(p)) {
                    distanceMap.put(p, newDist);
                    predMap.put(p, current);
                    pq.add(p);
                }
            }
        }
        return null;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        String name = "data/usa.graph";
        GraphProcessor gp = new GraphProcessor();
        gp.initialize(new FileInputStream(name));
        System.out.println("running GraphProcessor");
    }


    
}
