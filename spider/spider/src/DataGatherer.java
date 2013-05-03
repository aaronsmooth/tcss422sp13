/*
 * Quan Le
 * Tyler Powers
 * Aaron Nelson
 * Seth Kramer
 * Team Robbins Egg Blue Dolphins
 * 05/02/2013
 */

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import org.jsoup.nodes.Document;

/**
 * DataGatherere class will collect data from PageRetriever and PageParser.
 * It will then produce the data report.
 * 
 * @author Quan Le
 * @author Tyler Powers
 * @author Aaron Nelson
 * @author Seth Kramer
 * @version 1.0
 */
public class DataGatherer extends Thread {
	/**
	 * Total number of words.
	 */
	private int totalWordCount;
	
	/**
	 * Total of url links.
	 */
	private int totalUrlCount;
	
	/**
	 * Map holds words and how many times they occur.
	 */
	private Map<String, Integer> myMap;
	
	/**
	 * BigStruct queue.
	 */
	private ArrayDeque<BigStruct> myGatherer;
	
	/**
	 * BigStruct hold data for DataGatherer.
	 */
	private BigStruct myBigStruct;
	
	/**
	 * A document.
	 */
	private Document myDoc;
	
	/**
	 * A count integer.
	 */
	private int count;
	
	/**
	 * Start time of the program.
	 */
	private long startTime;
	
	/**
	 * Total running time of the program.
	 */
	private long totalTime;
	
	/**
	 * Total parse time of the program.
	 */
	private long totalParseTime;
	
	/**
	 * Page limit.
	 */
	private SlaveInteger linkCount;
	
	/**
	 * Condition variable.
	 */
	private boolean continueRunning;
	
	/**
	 * Constructor of the DataGatherer.
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
	
	/**
	 * Returns a map contains words and their amount.
	 * @return a map
	 */
	public Map<String, Integer> getMap() {
		return myMap;
	}

	/**
	 * Implements run() method of Thread class.
	 * Collects data and produces data reports.
	 */
	public void run() {
		String texts;
		int retrieveCount = 0;
		
		//FileOutputStream out; 
		//PrintStream ps = null; 
		PrintWriter ps = null;
		
		/*          For creating a file and printing to it
		try {
			 // Create a new file output stream
			  out = new FileOutputStream("spiderRunST.txt");

			  // Connect print stream to the output stream
			  ps = new PrintStream(out);
		}
		  catch (Exception e){
		  System.err.println ("Error in writing to file");
		 }
		 */
		
		try  
		{
			File file = new File("collected_data.txt");
		    FileWriter fstream = new FileWriter(file, true); //true tells to append data.
		    ps = new PrintWriter(fstream);
		}
		catch (Exception e)
		{
		    System.err.println("Error: " + e.getMessage());
		}
		  
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
				
				
				// Printing the data to a file
				
				ps.printf("\n\n\n");
				ps.println("Parsed: " + myBigStruct.getUrlName());
				ps.println("Pages Retrieved: " + retrieveCount);
				ps.println("Average Words Per Page: " + (totalWordCount / retrieveCount));
				ps.println("Average URL's per page: " + (totalUrlCount / retrieveCount));
				ps.printf("Keyword \tAvg. hits per page \tTotal hits\n");
				
				for (Map.Entry<String, Integer> word : myMap.entrySet()) {
					ps.printf("  %-20s %-20d %-20d\n", word.getKey(), word.getValue() / retrieveCount, word.getValue());
				}
				ps.println("Page limit: " + count);
				ps.printf("Average parse time per page: %.4f seconds\n", (totalParseTime / retrieveCount) * (Math.pow(10, -9)));
				ps.printf("Total running time: %.4f seconds\n", (totalTime * (Math.pow(10, -9))));
						
				
				
			} catch (NullPointerException e) {
				// Throw away docs with empty bodies
			}
			synchronized (linkCount) {
				updateCount();
			}
		} while (continueRunning);
		ps.println();
		ps.println();
		ps.println("------------------------------------------------------------------------");
		ps.println();
		ps.printf("Average parse time per page: %.4f seconds\n", (totalParseTime / retrieveCount) * (Math.pow(10, -9)));
		ps.println();
		ps.println("Total time: " + totalTime * (Math.pow(10, -9)) + " seconds");
		ps.close();
	
	}

	/**
	 * Helper method for run().
	 * Retrieves documents from url links.
	 */
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
	
	/**
	 * Returns the total word count.
	 * @return the total word count
	 */
	public int getTotalWordCount() {
		return totalWordCount;
	}
	
	/**
	 * Returns the total url link count.
	 * @return
	 */
	public int getTotalUrlCount() {
		return totalUrlCount;
	}
	
	/**
	 * Helper method for run(), updating the count to stop the program.
	 */
	private synchronized void updateCount() {
		linkCount.decrement();
		
		if (linkCount.getVal() <= 0) {
			continueRunning = false;
			//myBigStruct.setDone();
		} 	
	}
}
