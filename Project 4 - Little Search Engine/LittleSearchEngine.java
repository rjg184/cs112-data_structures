package search;

import java.io.*;
import java.util.*;

/**
 * This class encapsulates an occurrence of a keyword in a document. It stores the
 * document name, and the frequency of occurrence in that document. Occurrences are
 * associated with keywords in an index hash table.
 * 
 * @author Sesh Venugopal
 * 
 */
class Occurrence {
	/**
	 * Document in which a keyword occurs.
	 */
	String document;
	
	/**
	 * The frequency (number of times) the keyword occurs in the above document.
	 */
	int frequency;
	
	/**
	 * Initializes this occurrence with the given document,frequency pair.
	 * 
	 * @param doc Document name
	 * @param freq Frequency
	 */
	public Occurrence(String doc, int freq) {
		document = doc;
		frequency = freq;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + document + "," + frequency + ")";
	}
}

/**
 * This class builds an index of keywords. Each keyword maps to a set of documents in
 * which it occurs, with frequency of occurrence in each document. Once the index is built,
 * the documents can searched on for keywords.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in descending
	 * order of occurrence frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash table of all noise words - mapping is from word to itself.
	 */
	HashMap<String,String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashMap<String,String>(100,2.0f);
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.put(word,word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeyWords(docFile);
			mergeKeyWords(kws);
		}
		
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeyWords(String docFile) 
	throws FileNotFoundException {
		
		// Creating Hash Table (HashMap)
		HashMap<String,Occurrence> kwList = new HashMap<String,Occurrence>();
		
		// Loading the document file to be scanned
		Scanner sc = new Scanner(new File(docFile));
		
		// Scanning/iterating through the document file
		while(sc.hasNext()){
			
			// Getting words from document file (calls getKeyWord method - has to pass keyword test)
			String token = sc.next();
			String keyword = getKeyWord(token);
					//passes -> word
					//does NOT pass -> null
			
			// Word is a keyword (passed keyword test)
			if(keyword != null){
				
				if(kwList.containsKey(keyword) == false){
					Occurrence occDCF = new Occurrence(docFile, 1);
					kwList.put(keyword, occDCF);
				} else {
					Occurrence kw = kwList.get(keyword);
					kw.frequency = kw.frequency + 1;
				}
			}
			
		}
		
		return kwList; //list of keywords in the given document
		
	} //end of loadKeyWords method
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeyWords(HashMap<String,Occurrence> kws) {
		
		// Creating Occurrence ArrayList
		ArrayList<Occurrence> mkwList = new ArrayList<Occurrence>();
		
		// Iterating
		for(String index : kws.keySet()){
			
			// Initializing Variables
			mkwList = new ArrayList<Occurrence>();
			Occurrence ocr = kws.get(index);
			
			if(keywordsIndex.containsKey(index) == false){
				mkwList.add(ocr);
				keywordsIndex.put(index, mkwList);
			} else {
				mkwList = keywordsIndex.get(index);
				mkwList.add(ocr);
				insertLastOccurrence(mkwList);
			}
			
		}
		
	} //end of mergeKeyWords method
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * TRAILING punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyWord(String word) {
		
		// Cleaning up input -> lower-case and trimming leading spaces
		word = word.toLowerCase().trim();
		
		// Initializing Variables
		String[] temp;
		String delims = "[.,\\?:;!]";
		
		// Remove trailing punctuation
		temp = word.split(delims);
		int tL = temp.length;
		
		// --- DEBUGGING ---
		//for(int i = 0; i < temp.length; i++){
		//	System.out.println(i + ": " + temp[i]);
		//}
		// -----------------
		
		// Case 1 (return null) - temp array has multiple words after split
		if(tL != 1){
			return null;
		}
		
		// Word becomes the word at temp's index 0 (if temp length is 1)
		word = temp[0];
		
		// Case 2 (return null) - checks if word is null or empty
		if(word == null || word == "" || word.isEmpty()){
			return null;
		}
		
		// Case 3 (return null) - checks for non-alphabetical characters
		for(int index = 0; index < word.length(); index++){
			
			if(Character.isLetter(word.charAt(index)) == false){
				return null;
			}
			
		}
		
		// Case 4 (return null) - checks if the word is a noise word
		if(noiseWords.containsKey(word)){
			return null;
		}
		
		//System.out.println("Word (passed): " + word);
		
		return word; //returns word if it passes keyword test
		
	} //end of getKeyWord method
	
	/**
	 * Helps insertLastOccurrence method - finishing touches.
	 * 
	 * @param key
	 * @param keyPtr
	 * @param middle
	 * @param occs
	 * @param occs2
	 */
	private void iloHelper(int key, Occurrence keyPtr, int middle, ArrayList<Occurrence> occs, ArrayList<Occurrence> occs2){
		
		// Getting data value
		int data = occs2.get(middle).frequency;
		int index = occs2.size() - 1;
		
		// Case 1 - key is GREATER THAN data
		if(key > data){
			
			occs2.add(middle, keyPtr);
			//System.out.println(middle + ", " + keyPtr);
		
		// Case 2 - key is EQUAL TO or LESS THAN data
		} else {
			
			// Initializing Variables
			int x = middle + 1;
			
			// Add keyPtr to end of list
			if(middle == index){
				
				occs2.add(keyPtr);
				//System.out.println(middle + ", " + keyPtr);
			
			// Add keyPtr at position x in the list (1 after middle)
			} else {
				
				occs2.add(x, keyPtr);
				//System.out.println(middle + ", " + keyPtr);
				
			}
			
		}
		
		// Assign occs2's occurrence value to occs
		occs = occs2;
		
	} //end of iloHelper method
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * same list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion of the last element
	 * (the one at index n-1) is done by first finding the correct spot using binary search, 
	 * then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		
		// Creating midpoint ArrayList
		ArrayList<Integer> midpoint = new ArrayList<Integer>();
		
		// Modified Binary Search - P1 (Initializing Variables)
		int s1 = occs.size() - 1;
		int key = occs.get(s1).frequency;
		Occurrence keyPtr = occs.get(s1);
		ArrayList<Occurrence> occs2 = occs;
		occs2.remove(occs2.size() - 1);
		int high = occs2.size() - 1; //lowest value at highest index
		int middle = 0;
		int low = 0; //highest value at lowest index
		//String dcF = occs.get(s1).document;
		
		// Modified Binary Search - P2 (Finding Middle)
		while(high >= low){
			
			// Locating middle value
			middle = (low + high) / 2;
			midpoint.add(middle);
			
			// Getting data value
			int data = occs2.get(middle).frequency;
			
			// data is EQUAL TO key
			if(data == key){
				break;
			// data is LESS THAN key
			} else if(data < key){
				high = middle - 1;	
			// data is GREATER THAN key
			} else if(data > key){
				low = middle + 1;
			}
			
		}
	
		// Call iloHelper (insertLastOccurrence Helper method)
		this.iloHelper(key, keyPtr, middle, occs, occs2);

		return midpoint; //arrayList filled with sequence of mid point indexes
		
	} //end of insertLastOccurrence method
	
	/**
	 * Either kw1 or kw2 is null or not found in any of the documents. In this case, 
	 * the list of docs with the other keyword is added to documents ArrayList.
	 * 
	 * @param list
	 * @param documents
	 */
	private void atdlOne(ArrayList<Occurrence> list, ArrayList<String> documents){
		
		// Loops through documents list of the keyword that is NOT null
		for(int index = 0; index < list.size() && index < 5; index++){
			
			String listDoc = list.get(index).document; //document at current index
			//System.out.println(listDoc);
			
			documents.add(listDoc); //adds listDoc to documents ArrayList
			
		}
		
	} //end of atdlOne method
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of occurrence frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will appear before doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matching documents, the result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of NAMES of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matching documents,
	 *         the result is null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		
		// Creating documents ArrayList (list of documents that contain either kw1 or kw2)
		ArrayList<String> documents = new ArrayList<String>();
		
		// Creating Occurrence ArrayLists (kw1 and kw2)
		ArrayList<Occurrence> kwOcc1 = keywordsIndex.get(kw1);
		ArrayList<Occurrence> kwOcc2 = keywordsIndex.get(kw2);
		
		// Case 1 - Words (kw1 and kw2) are NOT in document -> return null
		if(kwOcc1 == null && kwOcc2 == null){
			
			//System.out.println(kwOcc1);
			return null; //words don't exist in either documents
			
		
		// Case 2 - Only one of the words is in the document
		} else if(kwOcc1 == null || kwOcc2 == null){
			
			// Initializing Temporary Occurrence ArrayList
			ArrayList<Occurrence> list = new ArrayList<Occurrence>();
			
			// kwOcc1 is null
			if(kwOcc2 != null){
				list = kwOcc2;
			// kwOcc2 is null
			} else {
				list = kwOcc1;
			}
			
			// Add either kwOcc1 or kwOcc2 to documents
			this.atdlOne(list, documents);
			//System.out.println(list);
			
			return documents; //words from only one of the inputs
		}
		
		// Case 3 - Both words (kw1 and kw2) are in the documents -- w/ or w/o common frequencies
		
		// Initializing Variables (kw1 and kw2)
		int kwSize1 = keywordsIndex.get(kw1).size();
		int kwPtr1 = 0; //pointer1
		int kwSize2 = keywordsIndex.get(kw2).size();
		int kwPtr2 = 0; //pointer2
		
		// Loops
		while(kwPtr1 < kwSize1 && kwPtr2 < kwSize2){
			
			// Getting frequencies
			int freq1 = keywordsIndex.get(kw1).get(kwPtr1).frequency;
			int freq2 = keywordsIndex.get(kw2).get(kwPtr2).frequency;
			
			// kw1's frequency is GREATER THAN or EQUAL TO kw2's frequency
			if(freq1 >= freq2){
				this.addToDocsList(kw1, kwPtr1, documents); //adds to documents list (kw1)
				kwPtr1 = kwPtr1 + 1; //incrementing
			// kw1's frequency is LESS THAN kw2's frequency
			} else {
				this.addToDocsList(kw2, kwPtr2, documents); //adds to documents list (kw2)
				kwPtr2 = kwPtr2 + 1; //incrementing
			}
		}
		
		// Add to documents list (if it's not already in there) - loop
		this.atdlLoop(kw1, kwPtr1, documents);
		this.atdlLoop(kw2, kwPtr2, documents);
		
		// Remove extra documents (keeps ONLY top 5 documents)
		this.removeExtraDocs(documents);
		
		return documents; //ArrayList of document names in which either 
						  //kw1 or kw2 occurs (highest to lowest frequencies)
		
	} //end of top5search method
	
	/**
	 * While keyword's pointer is less than keyword's size, 
	 * calls addToDocsList method and increments pointer by one.
	 * 
	 * @param kw
	 * @param kwPtr
	 * @param documents
	 */
	private void atdlLoop(String kw, int kwPtr, ArrayList<String> documents){
		
		// Initializing Variables
		int kwSize = keywordsIndex.get(kw).size();
		
		// Loops as long as keyword's pointer is LESS THAN keyword's size
		while(kwPtr < kwSize){
			
			this.addToDocsList(kw, kwPtr, documents); //adds to documents list (checks beforehand)
			kwPtr++; //incrementing pointer
			
		}
		
	} //end of atdlLoop method
	
	/**
	 * Checks if the documents list contains the docFile. If it
	 * does not, then the docFile is added to documents list.
	 * 
	 * @param kw
	 * @param kwPtr
	 * @param documents
	 */
	private void addToDocsList(String kw, int kwPtr, ArrayList<String> documents){
		
		// Initializing Variables
		String documentFile = keywordsIndex.get(kw).get(kwPtr).document;
		
		// Checks if documents list contains docFile
		if(documents.contains(documentFile) == false){
			
			documents.add(documentFile); //adds docFile to documents list
			
		}
		
	} //end of addToDocsList method
	
	/**
	 * Removes the extra documents from the list (only keeps the top five).
	 * 
	 * @param documents
	 */
	private void removeExtraDocs(ArrayList<String> documents){
		
		// Checks if documents list 
		if(documents.size() >= 5){
			
			// Initializing Variables
			int docSize = documents.size() - 1; //document size
			
			// Starts at end of documents list and removes extra document names
			for(int index = docSize; index > 4; index--){
				
				documents.remove(index); //removes document name at index
				
			}
			
		}
		
	} //end of removeExtraDocs method
	
}

