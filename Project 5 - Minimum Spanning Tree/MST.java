package apps;

import structures.*;
import java.util.ArrayList;

public class MST {
	
	/**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
		
		// Initializing Variables
		PartialTreeList ptList = new PartialTreeList(); //empty PartialTreeList
		int gvLength = graph.vertices.length; //number of vertices in the graph
		
		//System.out.println("# of Graph's Vertices: " + gvLength);
		
		// Looping through Graph's Vertices
		for(int index = 0; index < gvLength; index++){
			
			// Step 1 -> Graph's Vertex at 'index'
			Vertex gvAtIndex = graph.vertices[index]; //graph's vertex at index
			//System.out.println("Vertex: " + gvAtIndex + " (index = " + index + ")");
			//System.out.println();
			
			// Step 2 -> Creating a PartialTreeList with given vertex at index
			PartialTree PTL = new PartialTree(gvAtIndex);
			
			// Step 3 -> Neighbor (outgoing edges) of given vertex at index
			Vertex.Neighbor nbrPtr = gvAtIndex.neighbors;
			
			// Step 4 -> Adding values to PartialTree PTL
			while(nbrPtr != null){
				
				// Initializing Variables
				Vertex nbrVertex = nbrPtr.vertex; //neighbor's vertex
				int nbrWeight = nbrPtr.weight; //neighbor's weight
				
				// Creating a PartialTree with parameters: graph's vertex, neighbor's vertex, and neighbor's weight
				PartialTree.Arc temp = new PartialTree.Arc(graph.vertices[index], nbrVertex, nbrWeight);
				
				// Inserting arcs (edges) connected to temp to PTL
				PTL.getArcs().insert(temp);
				
				nbrPtr = nbrPtr.next; //advancing neighbor
				
			}
			
			ptList.append(PTL); //adding partial tree 'PTL' to list 'ptList'
			
		}
		
		return ptList; //initial partial tree list
		
	} //end of initialize method

	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<PartialTree.Arc> execute(PartialTreeList ptlist) {
		
		// Step 1 - Initializing Empty Arcs List
		ArrayList<PartialTree.Arc> arcsList = new ArrayList<PartialTree.Arc>(); //empty list
		
		// Step 2 - Loops until there is only one tree remaining (MST)
		while(ptlist.size() > 1){
			
			//System.out.println("Size: " + ptlist.size());
			
			// Step 3
			PartialTree ptx = ptlist.remove(); //remove first partial tree (ptx) from ptlist (L)
			MinHeap<PartialTree.Arc> pqx = ptx.getArcs(); //let pqx be ptx's priority queue
			
			// Step 4
			PartialTree.Arc hpa = pqx.deleteMin(); //remove highest-priority arc from pqx
			Vertex v2 = hpa.v2; //connect vertex 2 (v2) to hpa (a)
			//Vertex v1 = ptx.getRoot(); //vertex 1 (v1) belongs to ptx
			
			//System.out.println("v2: " + v2);
			//System.out.println("v1: " + v1);
			
			// Step 5
			while(v2.getRoot() == ptx.getRoot()){
				
				// Re-doing step 4
				hpa = pqx.deleteMin(); //remove highest-priority arc from pqx
				v2 = hpa.v2; //connect vertex 2 (v2) to hpa (a)
				//v1 = ptx.getRoot(); //vertex 1 (v1) belongs to ptx
				
				//System.out.println("Step 5 - v2: " + v2);
				//System.out.println("Step 5 - v1: " + v1);
				
			}
			
			// Step 6
			arcsList.add(hpa); //hpa a component of the MST -> add to array list
			
			//System.out.println(hpa);
			
			// Step 7
			PartialTree pty = ptlist.removeTreeContaining(v2); //removing pty from ptlist (L)
			
			// Step 8
			pty.getRoot().parent = ptx.getRoot(); //changing the root of all of pty's vertices to the root of ptx
			ptx.merge(pty); //combining/merging ptx and pty
			ptlist.append(ptx); //appending resulting tree to the end of ptlist (L)
			
		}
		
		return arcsList; //array list of all arcs in MST
		
	} //end of execute method
}

