import java.io.IOException;
import java.util.List;
import java.util.regex.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;
import java.util.HashSet;

public class ShortestPathApp{

	public static void main(String args[]) throws IOException {
		//Read in the provided map file and save it as a Dijkstra graph
		GraphADT<String,Double> walkPathGraphADT = loadGraphData("campus.dot");
		DijkstraGraph<String,Double> walkPathDijkstraGraph = (DijkstraGraph<String,Double>) walkPathGraphADT;
		//Provide a menu for user to choose the applications and
		//execute what the user chose to do. 
		//Allow them to repeat the process till one choose to quit.
		//(1) get the list of all locations in the map read in 
		//(2) find the shortest path for any given starting and ending location (will print out the path)
		//(3) quit the application

		System.out.println("Welcome to iWalk Summer version!");
		System.out.println("===================");
		
		runCommandLoop(walkPathDijkstraGraph);
	
		System.out.println();
		System.out.println("===================");
		System.out.println("Thanks, and Goodbye");

	}

    /**
     * Repeated gives the user an opportunity to issue new commands until
     * they select Q to quit.
     */
    public static void runCommandLoop(DijkstraGraph<String,Double> walkPathDijkstraGraph) {
	//while loop allows user to make selection until quit is chosen
	String inputString = new String();
	Scanner reader = new Scanner(System.in);
	while (inputString.toLowerCase() != "q"){
    		displayMainMenu();
    	
		inputString = reader.nextLine();
		//System.out.println("You entered " + inputString);
		//(1) get list of all locations in the map read in
		if (inputString.toLowerCase().charAt(0) == 'g'){
			List<String> nodeList = getListOfAllLocations(walkPathDijkstraGraph);
			//print out node names
			System.out.println(nodeList.toString());
			continue;
		}
		//(2) find the shortest path for any given starting and ending location
		//(print out the path)
		else if (inputString.toLowerCase().charAt(0) == 'f'){
			//get input parameters
			String rangeString = reader.nextLine();
			//parse input
			int separator = rangeString.indexOf("->");
			if (separator == -1){	
				System.out.println("Invalid input(s). Please try again.");
					continue;
				}
			String start = rangeString.substring(0,separator).trim();
			String end = rangeString.substring(separator+2,rangeString.length()).trim();
			
			//call the shortestPathData method and print out the results
			if (walkPathDijkstraGraph.containsNode(start) == false || walkPathDijkstraGraph.containsNode(end) == false){
				System.out.println("Invalid input(s). Please try again.");
			}
			else {
				List<String> pathList = findShortestPath(walkPathDijkstraGraph,start,end);
				//System.out.println("Results: ");
				System.out.println(pathList.toString());
				continue;
			}
		}
		//(3) quit
		if (inputString.toLowerCase().charAt(0) == 'q'){
			break;
		}
		//invalid input
		else {	
			System.out.println("Please enter a valid input from the menu options.");
		}
	}
    }
    	/**
     	* Displays the menu of command options to the user.
     	* 
     	*/

	public static void displayMainMenu() {
	/*    
		~~~ Command Menu ~~~
	        [G]et list of all possible locations
		[F]ind shortest path for any given starting and ending location start -> end
	        [Q]uit
	 */
		System.out.println("~~~ Command Menu ~~~");
		System.out.println("[G]et list of all possible locations");
		System.out.println("[F]ind shortest path for any given starting and ending location start -> end");
		System.out.println("[Q]uit");
    	}

	
    /**
     * Read in a map file and save it as a Dijkstra graph 
     *
     * @param map file name
     * @return a Dijkstra graph that reprsents the map
     * @throws IOException when there is trouble finding/reading file
     */
	public static GraphADT loadGraphData(String filename) throws IOException {
		DijkstraGraph<String,Double> walkPathDijkstraGraph = new DijkstraGraph<String,Double>();
		//Read in the records in the campus.dot file
		//and store them in a dijkstraGraph
		BufferedReader br = new BufferedReader(new FileReader(filename));
		int k=0; //so we can exclude header row
		String line = "";
		while ((line = br.readLine()) != null){
			//skip header row
			if (k==0) {
				k++;
				continue;
			}
			//skip footer row
			else if (line.equals("}")){
				continue;
			}
			//System.out.println("reading... " + line);
			ArrayList<String> dotLine = lineSplit(line);
			String start = dotLine.get(0);
			String end = dotLine.get(1);
			Double time = Double.valueOf(dotLine.get(2));
			//if start node is not already in the graph, insert it
			if (walkPathDijkstraGraph.containsNode(start)==false){
				walkPathDijkstraGraph.insertNode(start);
			}
			//if end node is not already in the graph, insert it
			if (walkPathDijkstraGraph.containsNode(end)==false){
				walkPathDijkstraGraph.insertNode(end);
			}
			//if there is not already an edge connecting these nodes, insert the edge
			if (walkPathDijkstraGraph.containsEdge(start,end)==false){
				walkPathDijkstraGraph.insertEdge(start,end,time);
			}
			//if opposite-direction edge is not already in the graph, insert it (because this graph is undirected)
			if (walkPathDijkstraGraph.containsEdge(end,start)==false){
				walkPathDijkstraGraph.insertEdge(end,start,time);
			}
			//System.out.println("number of rows inserted: " + Integer.toString(k));
			k++; //count number of rows imported
		}
  	
	return walkPathDijkstraGraph;
	}

    //helper method to parse dot string
    public static ArrayList<String> lineSplit(String inString){
	//System.out.println("string to parse: ~" + inString + "~");

	//get start node
	Pattern nodePattern = Pattern.compile("\"([^\"]*)\"");
	Matcher nodeMatcher = nodePattern.matcher(inString);
	nodeMatcher.find();
	String start = nodeMatcher.group(1);	
	//get end node
	nodeMatcher.find();
	String end = nodeMatcher.group(1);
	//get time
	Pattern edgePattern = Pattern.compile("\\[seconds=([^]]*)\\]");
	Matcher edgeMatcher = edgePattern.matcher(inString);
	edgeMatcher.find();
	String time = edgeMatcher.group(1);
	//add strings to list
	ArrayList<String> lineList = new ArrayList<String>(Arrays.asList(start,end,time));
	//System.out.println("line list length: " + Integer.toString(lineList.size()));
	return lineList;
    }

    /**
     * Return the list of all locations in a map 
     *
     * @param map 
     * @return the list of all possible locations on the map
     */
	public static List<String> getListOfAllLocations(GraphADT map) {
		//TODO
		GraphADT<String,Double> walkPathGraphADT = map;
		DijkstraGraph<String,Double> walkPathDijkstraGraph = (DijkstraGraph<String,Double>) walkPathGraphADT;
		//get nodes
		List<String> nodeList = walkPathDijkstraGraph.keySet();
		return nodeList;
  	}

    /**
     * Return the list of all locations in a shortest path of a map for given start and end locations
     *
     * @param map 
     * @param starting location
     * @param ending location
     * @return the list of locations on the shortest path from start to end 
     */
	//public List<String> findShortestPath(GraphADT map, String startLocation, String endLocation) {
	public static List<String> findShortestPath(GraphADT map, String startLocation, String endLocation) {
		//TODO
		GraphADT<String,Double> walkPathGraphADT = map;
		DijkstraGraph<String,Double> walkPathDijkstraGraph = (DijkstraGraph<String,Double>) walkPathGraphADT;
		//get shortest path
		List<String> pathList = walkPathDijkstraGraph.shortestPathData(startLocation,endLocation);
  		return pathList;
	}


}
