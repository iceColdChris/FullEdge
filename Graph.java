/*
 * Chris Fahlin
 * Tcss 342
 * Graphs
 * Graph.java
 */
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

// Graph class: evaluate shortest paths.
//
// CONSTRUCTION: with no parameters.
//
// ******************PUBLIC OPERATIONS**********************
// void addEdge( String v, String w, double cvw )
//                              --> Add additional edge
// void printPath( String w )   --> Print path after alg is run
// void unweighted( String s )  --> Single-source unweighted

// ******************ERRORS*********************************
// Some error checking is performed to make sure graph is ok,
// and to make sure graph satisfies properties needed by each
// algorithm.  Exceptions are thrown if errors are detected.

public class Graph {
	public static final double INFINITY = Double.MAX_VALUE;
	public Map<String,Vertex> vertexMap = new HashMap<String,Vertex>( );
	public Set<FullEdge> fullEdges = new HashSet<FullEdge>();
	public String file;
	private int numberOfVerticies = 0;	


	public Graph(String fileName) {
		FileReader fin;
		try {
			fin = new FileReader(fileName);
			Scanner graphFile = new Scanner(fin);
			file = fileName;

			// Read the edges and insert
			String line;
			while( graphFile.hasNextLine( ) ) {
				line = graphFile.nextLine( );
				StringTokenizer st = new StringTokenizer( line );

				try {
					if( st.countTokens( ) != 3 ) {
						System.err.println( "Skipping ill-formatted line " + line );
						continue;
					}

					String source  = st.nextToken( );
					String dest    = st.nextToken( );
					int    cost    = Integer.parseInt( st.nextToken( ) );
					//					System.out.println(source +" "+ dest +" " + cost);

					addEdge(source, dest, cost);
					fullEdges.add(new FullEdge(source, dest, cost));
					addEdge(dest, source, cost);
				}
				catch( NumberFormatException e ) {
					System.err.println( "Skipping ill-formatted line " + line ); 
				}
			}
			graphFile.close();

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}


	/*TODO rcSubgraph*/
	public Graph rcSubgraph() {
		Set<Set<FullEdge>> powerSet = SetMaker.powerSet(fullEdges);
		PriorityQueue<Graph> pq = new PriorityQueue<Graph>(1, new WeightComparator());

		Graph subGraph;

		for(Set<FullEdge> s : powerSet) {
			if(s.size() < numberOfVerticies){
				s.clear();
			}



			subGraph = new Graph(file);

			subGraph.fullEdges.clear();

			for(Vertex  v :subGraph.vertexMap.values()){
				v.adj.clear();
			}

			for(FullEdge e : s){
				subGraph.addEdge(e.start, e.dest, e.cost);
				subGraph.fullEdges.add(e);
				subGraph.addEdge(e.dest, e.start, e.cost);
			}

			if(subGraph.isRedConnected()){
				pq.add(subGraph);  //add this redundantly connected subgraph to pq
			}
		}
		return pq.poll();

	}

	public double totalEdgeCost()/*TODO Total Edge*/ {
		double totalCost = 0;
		for(FullEdge f : fullEdges)
			totalCost += f.cost;

		return totalCost;
	}

	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for(Vertex v : vertexMap.values()){
			for(Edge e : v.adj){
				if(v.name.compareTo(e.dest.name)<0){
					sb.append(v.name + " " + e.dest.name+ " " + e.cost + "\n");	
				}	
			}
		}

		return sb.toString();
	}
	public boolean isConnected()/*TODO isConnected*/ {
		clearAll();

		/*Begin DFS to check if graph is connected*/
		int traveled = 0;
		List<Vertex> toExplore = new LinkedList<Vertex>();
		if(vertexMap.values().toArray().length > 0){
			Vertex v = (Vertex) vertexMap.values().toArray()[0];
			toExplore.add(v);
			v.scratch = 1;	
		}


		while (!toExplore.isEmpty()) {
			Vertex curr = toExplore.remove(0);
			traveled++;
			for(Edge e : curr.adj) {
				if ( e != null && e.dest != null && e.dest.scratch == 0) { 
					toExplore.add(0,e.dest);
					e.dest.scratch = 1;
				}
			} 
		}//End of DFS Algorithm



		clearAll();

		if(traveled == numberOfVerticies){
			return true;
		} else {
			return false;
		}
	}

	public boolean isRedConnected()/*TODO isRedConnected*/{

		if(!isConnected() || vertexMap.values().isEmpty()) return false;


		for(Vertex v : vertexMap.values()){
			List<Edge> tempList = new LinkedList<Edge>();
			tempList.addAll(v.adj);

			if (v.adj.size() == 0 || tempList == null) return false;

			for(Edge e : tempList){
				Vertex destVertex = e.dest;
				Edge tempEdge = null;
				for(Edge edge : destVertex.adj){
					if(edge.dest.equals(v))
						tempEdge = edge;
				}

				v.adj.remove(tempEdge);
				destVertex.adj.remove(tempEdge);
				if(!isConnected()){
					v.adj.add(tempEdge);
					destVertex.adj.add(tempEdge);
					return false;
				}

				v.adj.add(tempEdge);
				destVertex.adj.add(tempEdge);
			}

		}

		return true;

	}

	/**
	 * Add a new edge to the graph.
	 */
	public void addEdge( String sourceName, String destName, double cost) {
		Vertex v = getVertex( sourceName );
		Vertex w = getVertex( destName );
		v.adj.add( new Edge( w, cost ) );
	}

	/**
	 * Driver routine to handle unreachables and print total cost.
	 * It calls recursive routine to print shortest path to
	 * destNode after a shortest path algorithm has run.
	 */
	public void printPath( String destName ) {
		Vertex w = vertexMap.get( destName );
		if( w == null )
			throw new NoSuchElementException( "Destination vertex not found" );
		else if( w.dist == INFINITY )
			System.out.println( destName + " is unreachable" );
		else {
			System.out.print( "(Cost is: " + w.dist + ") " );
			printPath( w );
			System.out.println( );
		}
	}

	/**
	 * If vertexName is not present, add it to vertexMap.
	 * In either case, return the Vertex.
	 */
	private Vertex getVertex( String vertexName ) {
		Vertex v = vertexMap.get( vertexName );
		if( v == null ) {
			v = new Vertex( vertexName );
			vertexMap.put( vertexName, v);
			numberOfVerticies++;//This is my field 
		}
		return v;
	}

	/**
	 * Recursive routine to print shortest path to dest
	 * after running shortest path algorithm. The path
	 * is known to exist.
	 */
	private void printPath( Vertex dest ) {
		if( dest.prev != null ) {
			printPath( dest.prev );
			System.out.print( " to " );
		}
		System.out.print( dest.name );
	}

	/**
	 * Initializes the vertex output info prior to running
	 * any shortest path algorithm.
	 */
	private void clearAll( ) {
		for( Vertex v : vertexMap.values( ) )
			v.reset( );
	}

	/**
	 * Single-source unweighted shortest-path algorithm.
	 */
	public void unweighted( String startName ) {
		clearAll( ); 

		Vertex start = vertexMap.get( startName );
		if( start == null )
			throw new NoSuchElementException( "Start vertex not found" );

		Queue<Vertex> q = new LinkedList<Vertex>( );
		q.add( start ); start.dist = 0;

		while( !q.isEmpty( ) ) {
			Vertex v = q.remove( );

			for( Edge e : v.adj ) {
				Vertex w = e.dest;
				if( w.dist == INFINITY ) {
					w.dist = v.dist + 1;
					w.prev = v;
					q.add( w );
				}
			}
		}
	}

	/**
	 * Process a request; return false if end of file.
	 */
	public boolean processRequest( Scanner in, Graph g ) {
		try {

			System.out.println( "Unweighted shortest distance:" );        
			System.out.print( "Enter start node:" );
			String startName = in.nextLine( );

			System.out.print( "Enter destination node:" );
			String destName = in.nextLine( );

			g.unweighted( startName );
			g.printPath( destName );
		}
		catch( NoSuchElementException e ) { 
			return false; 
		}
		return true;
	}
}