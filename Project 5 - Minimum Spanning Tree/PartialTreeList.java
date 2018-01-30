package apps;

import java.util.Iterator;
import java.util.NoSuchElementException;

import structures.Vertex;


public class PartialTreeList implements Iterable<PartialTree> {
    
	/**
	 * Inner class - to build the partial tree circular linked list 
	 * 
	 */
	public static class Node {
		/**
		 * Partial tree
		 */
		public PartialTree tree;
		
		/**
		 * Next node in linked list
		 */
		public Node next;
		
		/**
		 * Initializes this node by setting the tree part to the given tree,
		 * and setting next part to null
		 * 
		 * @param tree Partial tree
		 */
		public Node(PartialTree tree) {
			this.tree = tree;
			next = null;
		}
	}

	/**
	 * Pointer to last node of the circular linked list
	 */
	private Node rear;
	
	/**
	 * Number of nodes in the CLL
	 */
	private int size;
	
	/**
	 * Initializes this list to empty
	 */
    public PartialTreeList() {
    	rear = null;
    	size = 0;
    }

    /**
     * Adds a new tree to the end of the list
     * 
     * @param tree Tree to be added to the end of the list
     */
    public void append(PartialTree tree) {
    	Node ptr = new Node(tree);
    	if (rear == null) {
    		ptr.next = ptr;
    	} else {
    		ptr.next = rear.next;
    		rear.next = ptr;
    	}
    	rear = ptr;
    	size++;
    }

    /**
     * Removes the tree that is at the front of the list.
     * 
     * @return The tree that is removed from the front
     * @throws NoSuchElementException If the list is empty
     */
    public PartialTree remove() 
    throws NoSuchElementException {
    	
    	// Initializing Variables
    	PartialTree deletedTree;
    	
    	// Case 1 - List is empty (rear is null) -> throw exception
    	if(rear == null || size == 0){
    		
    		throw new NoSuchElementException("List is empty");
    	
    	// Case 2 - List only has a rear (pointing to itself) -> delete rear
    	} else if(rear.next == rear || size == 1){
    		
    		deletedTree = rear.tree; //deleted tree
			rear = null;
			size = 0;
			
		// Case 3 - Removing tree that is at the front of the list -> return deleted tree
		} else {
			
			//Node prev = rear;
			//Node ptr = rear.next;
			
			deletedTree = rear.next.tree; //deleted tree
			rear.next = rear.next.next;
			size--;
			
		}
    	
    	return deletedTree; //tree that is removed from the front
    	
    } //end of remove method

    /**
     * Removes the tree in this list that contains a given vertex.
     * 
     * @param vertex Vertex whose tree is to be removed
     * @return The tree that is removed
     * @throws NoSuchElementException If there is no matching tree
     */
    public PartialTree removeTreeContaining(Vertex vertex) 
    throws NoSuchElementException {
    	
    	// Initializing Variables
    	PartialTree deletedTree;
    	
    	// Case 1 - List is empty (rear is null) -> throw exception
    	if(rear == null || size == 0){
    		
    		throw new NoSuchElementException("List is empty -> no matching tree");
    		
    	// Case 2 - List only has a rear (1 node)
    	} else if(rear.next == rear || size == 1){
    		
    		// Initializing Variables
    		Vertex rearRt = rear.tree.getRoot();
    		Vertex vertexRt = vertex.getRoot();
    		boolean containsVertex = rearRt.equals(vertexRt);
    		
    		// Tree (rear) contains target (a given vertex) -> delete tree
			if(containsVertex == true){
				
				deletedTree = rear.tree;
				rear = null;
				size = 0;
				
				return deletedTree;
			
			// Target (vertex) not found -> throw exception
			} else {
				
				throw new NoSuchElementException("No matching tree");
				
			}
			
		// Case 3 - Removing the tree that contains the target (a given vertex)
		} else {
			
			// Initializing Pointers
			Node prev = rear;
			Node ptr = rear.next;
			
			// Loops through each node in CLL
			do {
				
				// Initializing Variables
	    		Vertex ptrRt = ptr.tree.getRoot();
	    		Vertex vertexRt = vertex.getRoot();
	    		boolean containsVertex = ptrRt.equals(vertexRt);
				
	    		// Case 3a - Tree (ptr) contains target (a given vertex)
				if (containsVertex == true){
					
					deletedTree = ptr.tree;
					prev.next = ptr.next;
					size--;
					
					// Removing rear (last node in CLL) -> prev becomes new rear
					if(ptr == rear){
						
						rear = prev; //prev becomes new rear b/c ptr (original rear) was removed
						
					}
					
					return deletedTree;
				
				// Case 3b - Target (vertex) not found -> advance pointers
				} else {
					
					prev = ptr; //prev takes ptr's value
					ptr = ptr.next; //ptr advances to next value
					
				}
				
			} while(ptr != rear.next); //goes through whole CLL once
			
			// Target (vertex) not found -> throws exception
			throw new NoSuchElementException("No matching tree");
			
		}
			
     } //end of removeTreeContaining method
    
    /**
     * Gives the number of trees in this list
     * 
     * @return Number of trees
     */
    public int size() {
    	return size;
    }
    
    /**
     * Returns an Iterator that can be used to step through the trees in this list.
     * The iterator does NOT support remove.
     * 
     * @return Iterator for this list
     */
    public Iterator<PartialTree> iterator() {
    	return new PartialTreeListIterator(this);
    }
    
    private class PartialTreeListIterator implements Iterator<PartialTree> {
    	
    	private PartialTreeList.Node ptr;
    	private int rest;
    	
    	public PartialTreeListIterator(PartialTreeList target) {
    		rest = target.size;
    		ptr = rest > 0 ? target.rear.next : null;
    	}
    	
    	public PartialTree next() 
    	throws NoSuchElementException {
    		if (rest <= 0) {
    			throw new NoSuchElementException();
    		}
    		PartialTree ret = ptr.tree;
    		ptr = ptr.next;
    		rest--;
    		return ret;
    	}
    	
    	public boolean hasNext() {
    		return rest != 0;
    	}
    	
    	public void remove() 
    	throws UnsupportedOperationException {
    		throw new UnsupportedOperationException();
    	}
    	
    }
}



