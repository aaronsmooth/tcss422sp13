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
	private int count;
	private long startTime;
	private long totalTime;
	private long totalParseTime;
	private SlaveInteger linkCount;
	private boolean continueRunning;
	
	/**
	 * Constructor.
	 * @param theList the list of word
	 */
	public DataGatherer(final ArrayList<String> theList, ArrayDeque<BigStruct> theGatherQueue, Map<String, Integer> themap, long theStartTime, int theTotalLinks, SlaveInteger theLinkCount) {
		totalUrlCount = 0;
		totalWordCount = 0;
		myBigStruct = new BigStruct(null, "");
		myGatherer = theGatherQueue;
		myMap = themap;
		myDoc = new Document("");
		startTime = theStartTime;
		totalTime = 0;
		linkCount = theLinkCount;
		count = theTotalLinks;
		totalParseTime = 0;
		continueRunning = true;
		for (String str : theList) {
			myMap.put(str, 0);
		}
	}
	
	public Map<String, Integer> getMap() {
		return myMap;
	}

	public void run() {
		String texts;
		int retrieveCount = 0;
		do {
			synchronized (myGatherer) {
				retrieveDoc();
				retrieveCount++;
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
				
				totalTime = System.nanoTime() - startTime;
				totalParseTime += myBigStruct.getParseTime();
				totalWordCount = totalWordCount + myBigStruct.getWordCount();
				totalUrlCount = totalUrlCount + myBigStruct.getUrlCount();
				/*Parsed: www.tacoma.washington.edu/calendar/
					Pages Retrieved: 12
					Average words per page: 321
					Average URLs per page: 11
					Keyword               Ave. hits per page       Total hits
					  albatross               0.001                     3
					  carrots                 0.01                      5
					  everywhere              1.23                      19
					  etc..........
					
					  intelligence            0.000                     0
					
					Page limit: 5000
					Average parse time per page .001msec
					Total running time:       0.96 sec
									 */
				System.out.printf("\n\n\n");
				System.out.println("Parsed: " + myBigStruct.getUrlName());
				System.out.println("Pages Retrieved: " + retrieveCount);
				System.out.println("Average Words Per Page: " + (totalWordCount / retrieveCount));
				System.out.println("Average URL's per page: " + (totalUrlCount / retrieveCount));
				System.out.printf("Keyword \tAvg. hits per page \tTotal hits\n");
				
				for (Map.Entry<String, Integer> word : myMap.entrySet()) {
					System.out.printf("  %-20s %-20d %-20d\n", word.getKey(), word.getValue() / retrieveCount, word.getValue());
				}
				System.out.println("Page limit: " + count);
				System.out.printf("Average parse time per page: %.4f seconds\n", (totalParseTime / retrieveCount) * (Math.pow(10, -9)));
				System.out.printf("Total running time: %.4f seconds\n", (totalTime * (Math.pow(10, -9))));
				stringscan.close();
				
			} catch (NullPointerException e) {
				// Throw away docs with empty bodies
			}
			synchronized (linkCount) {
				updateCount();
			}
		} while (continueRunning);
		//System.out.println("Total time: " + totalTime * (Math.pow(10, -9)) + "seconds");
	
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
	
	private synchronized void updateCount() {
		linkCount.decrement();
		
		if (linkCount.getVal() <= 0) {
			continueRunning = false;
			//myBigStruct.setDone();
		} 	
	}
}
