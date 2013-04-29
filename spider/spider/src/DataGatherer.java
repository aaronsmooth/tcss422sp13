import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import org.jsoup.nodes.Document;


public class DataGatherer extends Thread {
	
	private Map<String, Integer> myMap;
	
	private ArrayDeque<Document> myGatherer;
	
	private Document myDoc;
	
	private int count = 0;
	
	/**
	 * Constructor.
	 * @param theList the list of word
	 */
	public DataGatherer(final ArrayList<String> theList, ArrayDeque<Document> theGatherQueue, Map<String, Integer> themap) {
		//myList = theList;
		myGatherer = theGatherQueue;
		myMap = themap;
		myDoc = new Document("");
		for (String str : theList) {
			myMap.put(str, 0);
		}
	}
	
	public Map<String, Integer> getMap() {
		return myMap;
	}
	
//	public void SetList(ArrayList<String> theList) {
//		myList = theList;
//	}
	
	
//	public Map<String, Integer> buildMap() {
//		
//		String theWordinList;
//		String theWordinTextList;
//		
//		for (int i = 0; i < myList.size(); i++) {
//			theWordinList = myList.get(i).toLowerCase().replaceAll("\\W", "");
//			
//			for (int j = 0; j < myTextList.size(); j++) {
//				theWordinTextList = myTextList.get(j).toLowerCase().replaceAll("\\W", "");
//				final Integer occurs = myMap.get(theWordinList);
//				if (theWordinList.equals(theWordinTextList)) {
//					if (occurs == null) {
//						myMap.put(theWordinList, 1);
//					} else {
//						myMap.put(theWordinList, occurs+1);
//					}					
//				}
//				
//
//			}
//			
//		}
//		return myMap;

	public void run() {
		String texts;
		
		
		do {
			synchronized (myGatherer) {
				retrieveDoc();
			}
	
			String str;

			try {
				texts = myDoc.body().text();
				Scanner stringscan = new Scanner(texts);
	        
				while (stringscan.hasNext()) {
					str = stringscan.next().toLowerCase().replaceAll("\\W", "");
					if (myMap.containsKey(str)) {
						myMap.put(str, myMap.get(str)+ 1);
					}
				}	
				//System.out.println(texts);
				System.out.println(myMap);
				++count;
				System.out.printf("\nDataCount: %d", count);
				System.out.printf("\nQueueCount: %d", myGatherer.size());
				stringscan.close();
			} catch (NullPointerException e) {
				// Throw away docs with empty bodies
			}

			
		} while (true);
	
	
	}

	private synchronized void retrieveDoc() {

		while (myGatherer.isEmpty()) {

			try {
				myGatherer.wait(); 
			} catch (InterruptedException e) {

			}
		}

		myDoc = myGatherer.removeFirst();
		myGatherer.notifyAll();
	}
	
}
