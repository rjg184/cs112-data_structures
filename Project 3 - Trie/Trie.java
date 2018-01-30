package structures;

import java.util.ArrayList;

/**
 * This class implements a compressed trie. Each node of the tree is a CompressedTrieNode, with fields for
 * indexes, first child and sibling.
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {
	
	/**
	 * Words indexed by this trie.
	 */
	ArrayList<String> words;
	
	/**
	 * Root node of this trie.
	 */
	TrieNode root;
	
	/**
	 * Initializes a compressed trie with words to be indexed, and root node set to
	 * null fields.
	 * 
	 * @param words
	 */
	public Trie() {
		root = new TrieNode(null, null, null);
		words = new ArrayList<String>();
	}
	
	/**
	 * Uses TrieNode's word index to find the starting and ending substring of the word.
	 * 
	 * @param commonNode
	 * @return String of word's substring
	 */
	private String getNodeSubstring(TrieNode commonNode) {
		
		// Initializing Variables
		int index = commonNode.substr.wordIndex; //word index
		short indexStart = commonNode.substr.startIndex; //starting index of word or set of characters
		short indexEnd = (short)(commonNode.substr.endIndex + 1); //ending index of word or set of characters
		
		// Substring -> start to end
		String commonNodeSubstring = words.get(index).substring(indexStart, indexEnd);
		
		return commonNodeSubstring; //starting index to ending index
		
	} //end of getNodeSubstring method
	
	/**
	 * Searches ArrayList for specified word.
	 * 
	 * @param word
	 * @return True if word is found; false otherwise
	 */
	private boolean searchArrList(String word){
		
		for(int index = 0; index < words.size(); index++){
			if(words.get(index).equals(word)){
				return true;
			}
		}
		
		return false;
		
	} //end of searchArrList method
	
	/**
	 * Creates 'indexes' for the newly created nodes.
	 * 
	 * @param insWord, node
	 * @return the 'indexes' for the common prefix
	 */
	private Indexes createIndexes(String insWord, TrieNode node) {
		
		// Initializing Variables
		String existingWord = getNodeSubstring(node); //passes node to getNodeSubstring method -> commonNodeSubstring
		int nsi = node.substr.startIndex; //starting index of passed in node
		String insertedWord = insWord.substring(nsi); //substring of nsi
		short count = -1; //if this number doesn't change -> no common prefix
		short index = 0; //character at certain index for the while loop
		
		//System.out.println("- - - - - - - - - - ");
		//System.out.println("existing:  " + existingWord);
		//System.out.println("inserting: " + insertedWord);
		//System.out.println("- - - - - - - - - - ");
		
		// Compares words with common prefix -> gets longest common prefix
		while(index < existingWord.length()){
			
			// Initializing Variables -> characters at certain index
			char nStringChar = existingWord.charAt(index);
			char sStringChar = insertedWord.charAt(index);
			
			// Characters don't match -> break out of while loop
			if(nStringChar != sStringChar){
				//System.out.println("Difference: " + nStringChar + " -|- " + sStringChar);
				//System.out.println("- - - - - - - - - - ");
				//System.out.println("");
				break; //breaks out of while loop
			
			// Otherwise -> make count equal to index
			} else {
				//System.out.println("Count (before): " + count);
				count = index;
				//System.out.println("Count (after): " + count);
			}
			
			index++; //increment index by 1
		}
		
		// No common prefix between the words
		if(count == -1){
			
			return null; //no common prefix -> return null
		
		// Creates indexes based on common prefix
		} else {
			
			// Initializing Indexes Parameters (Word Index, Start Index, End Index)
			int indexWord = node.substr.wordIndex;
			short indexStart = node.substr.startIndex;
			short indexEnd = (short)(node.substr.startIndex + count);
			
			// Creating Indexes -> adjusting ending index
			Indexes commonPrefix = new Indexes(indexWord, indexStart, indexEnd);
			
			return commonPrefix; //indexes of the common prefix between the words
			
		}
		
	} //end of createIndexes method
	
	/**
	 * Obtains word at a certain index from the 'words' ArrayList.
	 * 
	 * @param index
	 * @return word found at index
	 */
	private String getWordFromIndex(int index){
		
		String foundWord = words.get(index);
		
		return foundWord;
		
	} //end of getWordFromIndex method
	
	/**
	 * Recursively inserts word in the trie.
	 * 
	 * @param root, origWord, insWord
	 */
	private void insertWordRecurs(TrieNode root, String origWord, String insWord) {
		
		// Initializing Variables
		Indexes commPref = null;
		boolean execute = true;
			
		// Initializing Pointers
		TrieNode ptr = root.firstChild;
		TrieNode pvHolder = null;
		
		// Always executed
		if(execute == true){
			commPref = createIndexes(insWord, ptr);
			//System.out.println("- if statement: " + commPref);
			pvHolder = ptr;
			ptr = ptr.sibling;
		}
		
		// 
		while(ptr != null && commPref == null){
			commPref = createIndexes(insWord, ptr);
			//System.out.println("- while loop: " + commPref);
			pvHolder = ptr;
			ptr = ptr.sibling;
		}
		
		// pvHolder's first child
		TrieNode pvHfc = pvHolder.firstChild;
		
		// No Common Prefix -> create siblings of root's firstChild
		if(commPref == null && ptr == null){
			
			//System.out.println("no common prefix - also executed in recursive statement");
			
			// Initializing Indexes Parameters (Word Index, Start Index, End Index)
			int indexWord = words.indexOf(insWord);
			short indexStart = pvHolder.substr.startIndex;
			short indexEnd = (short)(insWord.length() - 1);
			
			// Creating Indexes
			Indexes sib = new Indexes(indexWord, indexStart, indexEnd);
			
			//System.out.println("- - - - - - - - - - ");
			//System.out.println("sibling indexes (word pos, start, end): ");
			//System.out.println(sib.toString());
			//System.out.println("- - - - - - - - - - ");
			//System.out.println("");
			
			// Creating sibling of firstChild
			pvHolder.sibling = new TrieNode(sib, null, null);
			
		}
		
		// Common Prefix (no child) -> adjust indexes and create node's first child
		else if(pvHfc == null){
			
			//System.out.println("common prefix, but no first child");
			//System.out.println("");
			
			// Initializing Indexes Parameters (Word Index, Start Index, End Index)
			short indexEnd = pvHolder.substr.endIndex;
			pvHolder.substr = commPref;
			short indexStart = (short)(commPref.endIndex + 1);    
			
			// Creating Indexes 1 (insW) and TrieNode 1 (insNode)
			int indexWord = words.indexOf(insWord); //word index of inserted word
			short insWordIndexEnd = (short)(insWord.length() -1); //end index of inserted word
			Indexes insW = new Indexes (indexWord, indexStart, insWordIndexEnd); //inserted word 'indexes'
			TrieNode insNode = new TrieNode (insW, null, null); //inserted word node
			//System.out.println(insW.toString() + " - insW");
			
			// Creating Indexes 2 (cpW) and TrieNode 2 (cpNode)
			int commPrefIndex = commPref.wordIndex; //index of commPref	
			Indexes cpW = new Indexes (commPrefIndex, indexStart, indexEnd); //common prefix 'indexes'
			TrieNode cpNode = new TrieNode (cpW, null, insNode); //common prefix node
			//cpNode.sibling = insNode; //make insNode the sibling of cpNode
			//System.out.println(cpW.toString() + " - cpW");
			
			// Advancing Pointers
			//System.out.println("");
			pvHolder.firstChild = cpNode; //make cpNode pvHolder's first child
			//System.out.println("");
		
		// Common Prefix (sibling)
		} else {
			
			// Initializing Variables
			int pvHolderEnd = pvHolder.substr.endIndex; //pvHolder's ending index
			int commonPrefEnd = commPref.endIndex; //commonPref's ending index
			
			// Common Prefix (sibling) -> adjusts indexes of child prefix node and creates/adds sibling to child node
			if(pvHolderEnd != commonPrefEnd){
				
				//System.out.println("common prefix - sibling of changed common prefix child");
				//System.out.println("");
				
				// Updating pvHolder's Indexes (commPref becomes pvHolder's substring)
				Indexes oldCommNode = pvHolder.substr; //holds original indexes
				pvHolder.substr = commPref; //substring of pvHolder becomes new indexes

				// Creating Indexes 1 (insNewIndexes)
				int ss = (commPref.endIndex + 1);
				short s = (short)(ss); //starting index
				int w = words.indexOf(insWord);
				//System.out.println("start: " + start);
				int ee = (insWord.length() - 1);
				short e = (short)(ee); //ending index
				//System.out.println("end: " + end);
				
				Indexes insNewIndexes = new Indexes (w, s, e); //indexes 1 - new
				//System.out.println(insNewIndexes.toString());
				
				// Creating Indexes 2 (oldRemIndexes)
				int oldCommNodeIndex = oldCommNode.wordIndex; //index of old common node
				short cpref2 = (short)(commPref.endIndex + 1); //ending of commPref plus one (2)
				short oldCNendIndex = oldCommNode.endIndex; //ending index of old common node
				
				Indexes oldRemIndexes = new Indexes (oldCommNodeIndex, cpref2, oldCNendIndex); //indexes 2 - remaining of old
				
				// Creating TrieNode 1 (insNewNode)
				TrieNode insNewNode = new TrieNode(insNewIndexes, null, null); //TrieNode 1 = new
				
				// Creating TrieNode 2 (oldRemNode)
				TrieNode oldRemNode = new TrieNode(oldRemIndexes, null, null); //TrieNode 2 = remaining of old
				
				oldRemNode.firstChild = pvHolder.firstChild; //pvHolder's first child becomes oldRemNode's first child
				oldRemNode.sibling = insNewNode; //insNewNode becomes oldRemNode's sibling
				pvHolder.firstChild = oldRemNode; //oldRemNode becomes pvHolder's first child
				
			// Recursive -> passes itself new/modified parameters
			} else {
				
				// Initializing Variables
				int cpref1 = (commPref.endIndex + 1); //commPref ending index plus one
				String nadeshot = origWord.substring(cpref1); //original word's substring from beginning to cpref1's ending index
				
				//System.out.println("common prefix (sibling - else): " + nadeshot);
				//System.out.println("");
				
				//Recursion
				insertWordRecurs(pvHolder, nadeshot, insWord);
				
			} 
			
		}
		
	} //end of insertWordRecurs method
	
	/**
	 * Searches ArrayList to find the index of a given word.
	 * 
	 * @param word 
	 * @return index of a given word
	 */
	private int findWordIndex(String word) {
		
		for(int index = 0; index < words.size(); index++){
			if(words.get(index).equals(word)){
				return index;
			}
		}
		
		return -1;
		
	} //end of findWordIndex method
	
	/**
	 * Finds the node that is the root node for all the words containing the common prefix.
	 * 
	 * 
	 * @param ptr, prefix
	 * @return root node of all common prefix words
	 */
	private TrieNode getCommonRNode(TrieNode ptr, String prefix){

		// Initializing Variables
		String ptrS = "";
		int ptrLength = 0;
		int prefLength = 0;
		int length = 0;
		boolean ace = true;
		boolean noscope = true;
		TrieNode worstCase = null;
		
		// Converts common node's indexes to string - e.g (0, 0, 1) -> ca
		ptrS = getNodeSubstring(ptr);
		
		// Getting Lengths of Common Root Node (ptrS) and Prefix (prefix)
		ptrLength = ptrS.length();
		prefLength = prefix.length();
		
		// Determining which length is bigger -> makes 'length' either ptrLength or prefLength
		if(ptrLength > prefLength){
			length = prefLength;
		} else {
			length = ptrLength;
		}

		// Checking for similar characters -> booleans 'noscope' and 'ace'
		for(int index = 0; index < length; index++){
			
			// Initializing Variables (characters)
			char ptrSChar = ptrS.charAt(index);
			char prefChar = prefix.charAt(index);
			
			if(ptrSChar == prefChar){
				noscope = false;
			} else {
				ace = false;
			}
			
		}
		
		// Case 1 - ace == true and noscope == false
		if(ace == true && noscope == false){
			
			// Case 1A - ptrLength > prefLength [LESS THAN]
			if(ptrLength < prefLength){
				
				// Initializing Variables
				TrieNode ptrFC = ptr.firstChild;
				String updatedPref = "";
				
				// Getting new prefix based on ptrLength's length
				updatedPref = prefix.substring(ptrLength);
				
				// Common prefix found -> recursion
				if(ptrFC != null){
					return getCommonRNode(ptrFC, updatedPref); //recursion
				
				// No common prefix found -> return null
				} else {
					return null;
				}
				
			// Case 1B && 1C - ptrLength > OR == prefLength [GREATER THAN OR EQUAL TO[
			} else if(ptrLength > prefLength || ptrLength == prefLength){
				
				return ptr;
			}
		
		// Case 2 - ace == false
		} else if(ace == false){
			
			// Case 2A & 2B - noscope == true or false
			if(noscope == true || noscope == false){
				
				// Initializing Pointers
				TrieNode ptrSibling = ptr.sibling;
				
				if(ptrSibling != null){
					
					return getCommonRNode(ptrSibling, prefix);
				} else {
					
					return null;
				}

			}
		
		// Case 3 - otherwise, return null
		} else {
			
			return null;
		}
		
		//System.out.println("reached worst case: " + worstCase);
		
		return worstCase; //worst case scenario -> returns null (*HOPEFULLY* should NEVER reach this)
		
	} //end of getCommonRNode method
	
	/**
	 * Adds words with the prefix to a completion list ArrayList
	 * 
	 * 
	 * @param ptr, rootNode, compList
	 * @return ArrayList containing all the words that share the prefix
	 */
	private void addToCL(TrieNode ptr, TrieNode rootNode, ArrayList<String> compList){

		// Initializing Variables
		short iStart = 0;
		short iEnd = (short)(ptr.substr.endIndex + 1);
		int wordIndex = ptr.substr.wordIndex;
		String wordCL = words.get(wordIndex).substring(iStart, iEnd);
		
		//System.out.println("-> Word: " + wordCL);
		
		// Single common prefix word - is always executed once
		if(ptr == rootNode){
			
			// Initializing Pointers
			TrieNode ptrFC = ptr.firstChild;
			
			if(ptrFC != null){
				addToCL(ptrFC, rootNode, compList);
			} else {
				compList.add(wordCL); //adds word to completion list ArrayList
			}
		
		// Multiple common prefix words
		} else {
			
			// Initializing Pointers
			TrieNode ptrFC = ptr.firstChild;
			TrieNode ptrSibling = ptr.sibling;
			
			if(ptrFC == null){
				compList.add(wordCL); //adds word to completion list ArrayList
				
			} else if(ptrFC != null){
				addToCL(ptrFC, rootNode, compList);
			}
			
			if(ptrSibling != null){
				addToCL(ptrSibling, rootNode, compList);
			}
			
		}
		
	} //end of addToCL method
	
	/**
	 * Inserts a word into this trie. Converts to lower case before adding.
	 * The word is first added to the words array list, then inserted into the trie.
	 * 
	 * @param word Word to be inserted.
	 */
	public void insertWord(String word) {
		
		// Clean up input -> convert to lower case and trim spaces
		word = word.toLowerCase().trim();
		
		// Initializing Variables
		String insWord = word;
		words.add(insWord); //adding word to 'words' ArrayList
		short indexStart;
		short indexEnd;
		int firstWordIndex = 0;
		
		// Finds/prints inserted word and its ArrayList index
		//int wordIndex = this.findWordIndex(insWord);
		//System.out.println("Word: " + insWord + " (Array Index: " + wordIndex + ")");
		
		// Root's First Child is empty -> create new firstChild
		if(root.firstChild == null){
			
			// Word's Character Index -> Start & End
			indexStart = 0;
			indexEnd = (short)(insWord.length() - 1);
			
			// Creating root's firstChild -> Indexes & TrieNode
			Indexes childIndexes = new Indexes(firstWordIndex, indexStart, indexEnd);
			TrieNode childNode = new TrieNode(childIndexes, null, null);
			
			// Root's firstChild -> childNode
			root.firstChild = childNode;
			
			//System.out.println("-> [IF] Root's firstChild: " + childNode.substr);
			//System.out.println("");
		
		// Otherwise -> send to private recursive insert method
		} else {
			
			//System.out.println("-> [ELSE] firstChild's Siblings or Common Prefixes");
			//System.out.println("");
			
			// Recursively Insert Word
			insertWordRecurs(root, insWord, insWord);	
			
		}
		
	} //end of insertWord method
	
	/**
	 * Given a string prefix, returns its "completion list", i.e. all the words in the trie
	 * that start with this prefix. For instance, if the tree had the words bear, bull, stock, and bell,
	 * the completion list for prefix "b" would be bear, bull, and bell; for prefix "be" would be
	 * bear and bell; and for prefix "bell" would be bell. (The last example shows that a prefix can be
	 * an entire word.) The order of returned words DOES NOT MATTER. So, if the list contains bear and
	 * bell, the returned list can be either [bear,bell] or [bell,bear]
	 * 
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all words in tree that start with the prefix, order of words in list does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public ArrayList<String> completionList(String prefix) {
		
		// Clean up input -> convert to lower case and trim spaces
		prefix = prefix.toLowerCase().trim();
		
		// Initializing Variables
		ArrayList<String> compList = new ArrayList<String>();
		String optic = "";
		
		// Initializing Pointers
		TrieNode ptr = root.firstChild;
		TrieNode cpNode = null;
		
		// Case 1 -> Trie is empty (return null)
		if(root.firstChild == null){
			
			//System.out.println("-> Trie is empty");
			return null;
			
		}
		
		// Gets the root node with the common prefix
		cpNode = this.getCommonRNode(ptr, prefix);

		// Case 2 -> Prefix found (return ArrayList of words with common prefixes)
		if(cpNode != null){
			
			//Finding words with common prefixes
			optic = getNodeSubstring(cpNode); //turns indexes of cpNode into a string (the root node containing the common prefix)
			this.addToCL(cpNode, cpNode, compList);
			
			return compList; //ArrayList filled with words that share the prefix
		
		// Case 3 -> Prefix not found (return null)
		} else {
			
			return null;
			
		}

	} //end of completionList method
	
	
	public void print() {
		print(root, 1, words);
	}
	
	private static void print(TrieNode root, int indent, ArrayList<String> words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			System.out.println("      " + words.get(root.substr.wordIndex));
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		System.out.println("(" + root.substr + ")");
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }

