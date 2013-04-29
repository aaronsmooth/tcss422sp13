import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import org.jsoup.nodes.Document;


public class DataGatherer extends Thread {
	private int totalWordCount;
	private int totalUrlCount;
	private Map<String, Integer> myMap;
	private ArrayDeque<BigStruct> myGatherer;
	private BigStruct myBigStruct;
	private Document myDoc;
	private int count = 0;
	private long startTime;
	private long totalTime;
	
	/**
	 * Constructor.
	 * @param theList the list of word
	 */
	public DataGatherer(final ArrayList<String> theList, ArrayDeque<BigStruct> theGatherQueue, Map<String, Integer> themap, long theStartTime) {
		totalUrlCount = 0;
		totalWordCount = 0;
		myBigStruct = new BigStruct(null, "");
		myGatherer = theGatherQueue;
		myMap = themap;
		myDoc = new Document("");
		startTime = theStartTime;
		totalTime = 0;
		for (String str : theList) {
			myMap.put(str, 0);
		}
	}
	
	public Map<String, Integer> getMap() {
		return myMap;
	}

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
					myBigStruct.incrementWordCount();
					if (myMap.containsKey(str)) {
						myMap.put(str, myMap.get(str)+ 1);
					}
				}	
				
				System.out.println(myMap);
				++count;
				//System.out.printf("\nDataCount: %d", count);
				//System.out.printf("\nQueueCount: %d", myGatherer.size());
				if (myBigStruct.isDone()) {
					totalTime = System.nanoTime() - startTime;
				}
				
				totalWordCount = totalWordCount + myBigStruct.getWordCount();
				totalUrlCount = totalUrlCount + myBigStruct.getUrlCount();
				//System.out.println(myBigStruct.getUrlName());
				System.out.printf("\nTotal word count: %d", totalWordCount);
				System.out.printf("\nTotal url count: %d\n", totalUrlCount);
				stringscan.close();
			} catch (NullPointerException e) {
				// Throw away docs with empty bodies
			}
		} while (!myBigStruct.isDone());
		//System.out.println("IM DYING!!!!");
		System.out.println("Total time: " + totalTime * (Math.pow(10, -9)) + "seconds");
	
	}

	private synchronized void retrieveDoc() {

		while (myGatherer.isEmpty()) {

			try {
				myGatherer.wait(); 
			} catch (InterruptedException e) {

			}
		}
		myBigStruct = myGatherer.removeFirst();
		myDoc = myBigStruct.getDoc();
		myGatherer.notifyAll();
	}
	
	public int getTotalWordCount() {
		return totalWordCount;
	}
	
	public int getTotalUrlCount() {
		return totalUrlCount;
	}
}
