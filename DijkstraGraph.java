// === CS400 File Header Information ===
// Name: Kathryn Cole
// Email: kcole9@wisc.ed
// Group and Team: n/a
// Group TA: Rahul
// Lecturer: Jiazhen Zhou
// Notes to Grader: n/a

import java.util.PriorityQueue;
import java.util.List;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Timeout;
import java.util.Arrays;


/**
 * This class extends the BaseGraph data structure with additional methods for
 * computing the total cost and list of node data along the shortest path
 * connecting a provided starting to ending nodes. This class makes use of
 * Dijkstra's shortest path algorithm.
 */
public class DijkstraGraph<NodeType, EdgeType extends Number>
        extends BaseGraph<NodeType, EdgeType>
        implements GraphADT<NodeType, EdgeType> {

    /**
     * While searching for the shortest path between two nodes, a SearchNode
     * contains data about one specific path between the start node and another
     * node in the graph. The final node in this path is stored in its node
     * field. The total cost of this path is stored in its cost field. And the
     * predecessor SearchNode within this path is referened by the predecessor
     * field (this field is null within the SearchNode containing the starting
     * node in its node field).
     *
     * SearchNodes are Comparable and are sorted by cost so that the lowest cost
     * SearchNode has the highest priority within a java.util.PriorityQueue.
     */
    protected class SearchNode implements Comparable<SearchNode> {
        public Node node;
        public double cost;
        public SearchNode predecessor;

        public SearchNode(Node node, double cost, SearchNode predecessor) {
            this.node = node;
            this.cost = cost;
            this.predecessor = predecessor;
        }

        public int compareTo(SearchNode other) {
            if (cost > other.cost)
                return +1;
            if (cost < other.cost)
                return -1;
            return 0;
        }
    }

    /**
     * Constructor that sets the map that the graph uses.
     */
    public DijkstraGraph() {
        super(new BasicMap<>());
    }

    /**
     * This helper method creates a network of SearchNodes while computing the
     * shortest path between the provided start and end locations. The
     * SearchNode that is returned by this method is represents the end of the
     * shortest path that is found: it's cost is the cost of that shortest path,
     * and the nodes linked together through predecessor references represent
     * all of the nodes along that shortest path (ordered from end to start).
     *
     * @param start the data item in the starting node for the path
     * @param end   the data item in the destination node for the path
     * @return SearchNode for the final end node within the shortest path
     * @throws NoSuchElementException when no path from start to end is found
     *                                or when either start or end data do not
     *                                correspond to a graph node
     */
    protected SearchNode computeShortestPath(NodeType start, NodeType end) {
        // implement in step 5.3
        //use java.util.PriorityQueue to greedily explore shorter path possibilities before long ones
	//use MapADT and BasicMap to keep track of the nodes already visited (and found shortest paths for)
	//use the map as a set by inserting graph nodes as keys and values into the map
	//use the containsKey method to check if a node is contained in a set
	//throw NoSuchElementExceptioni when the start or end node is not in the graph
	if (!this.containsNode(start) || !this.containsNode(end)){
		throw new NoSuchElementException("specified start or end node not present in the graph");
	}
	//create a new instance of BasicMap
	BasicMap<NodeType,SearchNode> visitedNodes = new BasicMap<NodeType,SearchNode>();
	//create priority queue to hold nodes not fully explored
	PriorityQueue<SearchNode> queue = new PriorityQueue<SearchNode>(); //add comparator here?
	//add the starting node to the queue
	double startCost = 0;
	SearchNode startPredecessor = null;
	Node startNode = this.nodes.get(start);
	SearchNode startSearchNode = new SearchNode(startNode,startCost,startPredecessor);
	queue.add(startSearchNode);
	visitedNodes.put(start,startSearchNode);
	//while queue not empty, get the next node
	while (!queue.isEmpty()){
		SearchNode currentSearchNode = queue.remove();
		Node currentNode = currentSearchNode.node;
		//perform relaxation procedure on each edge adjacent to currentNode
		for (Edge edge : currentNode.edgesLeaving){
			Node destinationNode = edge.successor;
			//no need to check back to the predecessor node, since we already have this edge
			if (currentSearchNode.predecessor != null && destinationNode == currentSearchNode.predecessor.node){
				continue;
			}
			EdgeType edgeCost = getEdge(currentNode.data,destinationNode.data);
			//System.out.println("from " + currentNode.data.toString() + " to " + destinationNode.data.toString() + " : " + edgeCost.toString());
			if (visitedNodes.containsKey(destinationNode.data) == false){
				SearchNode predecessor = currentSearchNode;
				double destinationNodeCost = edgeCost.doubleValue() + predecessor.cost;
				SearchNode destinationSearchNode = new SearchNode(destinationNode,destinationNodeCost,predecessor);
			        //System.out.println("***New Node visited - data: " + destinationSearchNode.node.data.toString()
				//		+ " predecessor: " + destinationSearchNode.predecessor.node.data.toString()
				//		+ " cost: " + Double.toString(destinationSearchNode.cost)
				//		);	
				//add destinationNode to queue
				queue.add(destinationSearchNode);
				//add this node to the set of visited nodes
				visitedNodes.put(destinationNode.data,destinationSearchNode);
			}
			else if (currentSearchNode.cost + edgeCost.doubleValue() < visitedNodes.get(destinationNode.data).cost){
				SearchNode destinationSearchNode = visitedNodes.get(destinationNode.data);
			        //System.out.println("Flagged for update - currentSearchNodeCost: " + Double.toString(currentSearchNode.cost)
				//	+ " edgeCost: " + Double.toString(edgeCost.doubleValue())
				//	+ " destinationNodeCost: " + Double.toString(destinationSearchNode.cost)
				//	);
				//System.out.println("***Updating Node - data: " + destinationSearchNode.node.data.toString() 
				//		+ " predecessor: " + destinationSearchNode.predecessor.node.data.toString()
				//		+ " cost: " + Double.toString(destinationSearchNode.cost)
				//		);
				destinationSearchNode.cost = currentSearchNode.cost + edgeCost.doubleValue();
				destinationSearchNode.predecessor = currentSearchNode;
			        //System.out.println("***Node Updated  - data: " + destinationSearchNode.node.data.toString() 
				//		+ " predecessor: " + destinationSearchNode.predecessor.node.data.toString()
				//		+ " cost: " + Double.toString(destinationSearchNode.cost)
				//		);
				//change the priority of destinationNode in queue
				queue.remove(destinationSearchNode);
				queue.add(destinationSearchNode);
				//update this node in the set of visited nodes
				visitedNodes.remove(destinationNode.data);
				visitedNodes.put(destinationNode.data,destinationSearchNode);
			}
		}
	}
	//return the ending node, now that we have checked all edges and know that the shortest path is recorded
	if (!visitedNodes.containsKey(end)){
		throw new NoSuchElementException("no path exists between start and end nodes");
	}
	SearchNode endSearchNode = visitedNodes.get(end);
	return endSearchNode;
    }

    /**
     * Returns the list of data values from nodes along the shortest path
     * from the node with the provided start value through the node with the
     * provided end value. This list of data values starts with the start
     * value, ends with the end value, and contains intermediary values in the
     * order they are encountered while traversing this shorteset path. This
     * method uses Dijkstra's shortest path algorithm to find this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end   the data item in the destination node for the path
     * @return list of data item from node along this shortest path
     */
    public List<NodeType> shortestPathData(NodeType start, NodeType end) {
        // implement in step 5.4
	// throws NoSuchElementException when the start and end data passed into it as arguments either 
	// (1) do not correspond to the data held in any nodes within the graph, or 
	// (2) there is no directed path that connects from teh start node to the end node
        //List and LinkedList can be used for the return type
        LinkedList<NodeType> shortestPath = new LinkedList<>();
	// get the ending searchNode, if it exists (exception thrown if either of the above conditions are true)
	SearchNode recentNode = this.computeShortestPath(start,end);
	//step backwards through the predecessor nodes to build a list of nodes along the path
	while (recentNode != null){
		//add element at front of list so nodes will be in correct order once we reach start
		NodeType data = recentNode.node.data;
		shortestPath.push(data);	
		recentNode = recentNode.predecessor;
	}	
	return shortestPath;
	}

    /**
     * Returns the cost of the path (sum over edge weights) of the shortest
     * path freom the node containing the start data to the node containing the
     * end data. This method uses Dijkstra's shortest path algorithm to find
     * this solution.
     *
     * @param start the data item in the starting node for the path
     * @param end   the data item in the destination node for the path
     * @return the cost of the shortest path between these nodes
     */
    public double shortestPathCost(NodeType start, NodeType end) {
        // implement in step 5.4
	// throws NoSuchElementException when the start and end data passed into it as arguments either 
	// (1) do not correspond to the data held in any nodes within the graph, or 
	// (2) there is no directed path that connects from teh start node to the end node
        // get the ending searchNode, if it exists (exception thrown if either of the above conditions are true)
	SearchNode endingNode = this.computeShortestPath(start,end);
	// get the cost from the search node
	double pathCost = (double) endingNode.cost;
	return pathCost;
    }

    // TODO: implement 3+ tests in step 4.1
    //JUnit tests for the DijkstraGraph class

  static final int TIMEOUT = 2; //2 seconds
    
  //helper method to create DijkstraGraph we can use for testing
  private static DijkstraGraph<Integer,Integer> buildDijkstraGraph(Integer[] valuesToInsert) {
    //System.out.println("Building DijkstraGraph");
    //create empty graph 
    DijkstraGraph<Integer,Integer> outputGraph = new DijkstraGraph<Integer,Integer>();
    //insert each value in the input list
    for (int i=0; i<valuesToInsert.length; i++){
    	Integer newNodeValue = valuesToInsert[i];
	outputGraph.insertNode(newNodeValue);
	//System.out.println("added node: " + newNodeValue.toString());
    }
	  //System.out.println("number of nodes in graph: " + String.valueOf(outputGraph.getNodeCount()));
	  return outputGraph;
  }

  //helper method to add edges to DijkstraGraph for testing
  private static DijkstraGraph<Integer,Integer> addEdges(DijkstraGraph inputGraph, Integer[][] valuesToInsert){
  	//System.out.println("Adding edges to DijkstraGraph");
	//insert each edge value in the input list
	for (int i=0;i<valuesToInsert.length;i++){
		Integer[] info = valuesToInsert[i];
		Integer predNode = info[0];
		Integer succNode = info[1];
		Double edgeWeight = Double.valueOf(info[2]);
		inputGraph.insertEdge(predNode,succNode,edgeWeight);
		//System.out.println("added edge: " + predNode.toString() + " -> " + succNode.toString() + " : " + edgeWeight.toString());
	}
	//System.out.println("number of edges in graph: " + String.valueOf(inputGraph.getEdgeCount()));
  	return inputGraph;
  }

  //helper method to build a standard DiijkstraGraph we can use for testing
  private static DijkstraGraph<Integer,Integer> buildGraphFromLecture (){
	//build graph
	Integer[] insertArray = {1,2,3,4,5,6,7,8};
	DijkstraGraph<Integer,Integer> dijkstraGraph = buildDijkstraGraph(insertArray);
	Integer[][] edgeArray = {
		{1,2,9},
		{1,6,14},
		{1,7,15},
		{2,3,23},
		{3,5,2},
		{3,8,19},
		{4,8,6},
		{4,3,6},
		{5,8,16},
		{5,4,11},
		{6,3,18},
		{6,5,30},
		{6,7,5},
		{7,5,20},
		{7,8,44},
		};
	addEdges(dijkstraGraph,edgeArray);
 	return dijkstraGraph; 
  
  }

    /**
    * Confirm cost for shortest path is correct. Example 1. 
    * @return
    */
   @Test
   public void testShortestPathCost(){
	//build graph
	DijkstraGraph<Integer,Integer> dijkstraGraph = buildGraphFromLecture();
	//confirm that shortest path is found correctly
	Integer startingNodeData = 1;
	Integer endingNodeData = 8;
	//correct cost
	double expectedShortestPath = 50.0;
	double actualShortestPath = dijkstraGraph.shortestPathCost(startingNodeData,endingNodeData);
	Assertions.assertEquals(expectedShortestPath,actualShortestPath,"Shortest path is the correct cost.");
   }

    /**
    * Confirm sequence of data for shortest path is correct. Example 1. 
    * @return
    */
   @Test
   public void testShortestPath(){
	//build graph
	DijkstraGraph<Integer,Integer> dijkstraGraph = buildGraphFromLecture();
	//confirm that shortest path is found correctly
	Integer startingNodeData = 1;
	Integer endingNodeData = 8;
	//correct sequence
	List<Integer> expectedShortestPathSequence = new LinkedList<Integer>(Arrays.asList(1,2,3,5,8));
        List<Integer> actualShortestPathSequence = dijkstraGraph.shortestPathData(startingNodeData,endingNodeData);
	Assertions.assertEquals(expectedShortestPathSequence,actualShortestPathSequence,"Shortest path found with the correct sequence.");
   }

    /**
    * Confirm sequence of data for shortest path is correct. Example 2. 
    * @return
    */
   @Test
   public void testShortestPathNotInLecture(){
	//build graph
	DijkstraGraph<Integer,Integer> dijkstraGraph = buildGraphFromLecture();
	//confirm that shortest path is found correctly
	Integer startingNodeData = 6;
	Integer endingNodeData = 7;
	//correct sequence
	List<Integer> expectedShortestPathSequence = new LinkedList<Integer>(Arrays.asList(6,7));
        List<Integer> actualShortestPathSequence = dijkstraGraph.shortestPathData(startingNodeData,endingNodeData);
	Assertions.assertEquals(expectedShortestPathSequence,actualShortestPathSequence,"Shortest path found with the correct sequence.");
   }

    /**
    * Confirm cost for shortest path is correct. Example 2. 
    * @return
    */
   @Test
   public void testShortestPathCostNotInLecture(){
	//build graph
	DijkstraGraph<Integer,Integer> dijkstraGraph = buildGraphFromLecture();
	//confirm that shortest path is found correctly
	Integer startingNodeData = 6;
	Integer endingNodeData = 7;
	//correct cost
	double expectedShortestPath = 5.0;
	double actualShortestPath = dijkstraGraph.shortestPathCost(startingNodeData,endingNodeData);
	Assertions.assertEquals(expectedShortestPath,actualShortestPath,"Shortest path is the correct cost.");
   }
    /**
    * Check behavior when no directed path exists between start and end nodes. 
    * @return
    */
   @Test
   public void testNoPathExists(){ 
	//build graph
	DijkstraGraph<Integer,Integer> dijkstraGraph = buildGraphFromLecture();
	//try to find a path between 2 nodes when a path does not exist
	Integer startingNodeData = 8;
	Integer endingNodeData = 7;
	//getting the sequence of nodes throws no such element exception
	Assertions.assertThrows(NoSuchElementException.class,() -> {
				dijkstraGraph.shortestPathData(startingNodeData,endingNodeData);
			},"Shortest path does not exist."
			);
   }

}
