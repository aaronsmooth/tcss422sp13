/*
 * Quan Le
 * Tyler Powers
 * Aaron Nelson
 * Seth Kramer
 * Team Robbins Egg Blue Dolphins
 * 05/02/2013
 */

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * PageParser class processes documents from PageBuffer queue.
 * Sends data to DataGatherer and PageToRetrieve queue.
 * 
 * @author Quan Le
 * @author Tyler Powers
 * @author Aaron Nelson
 * @author Seth Kramer
 * @version 1.0
 */
public class PageParser extends Thread {
	
	/**
	 * A document.
	 */
	private Document myDoc;
	
	/**
	 * BigStruct data structure.
	 */
	private BigStruct myBigStruct;
	
	/**
	 * PageToRetrieve queue.
	 */
	private ArrayDeque<URL> myRetrieveQueue;
	
	/**
	 * PageBuffer queue.
	 */
	private ArrayDeque<BigStruct> myPageBufferQueue;
	
	/**
	 * A queue of BigStruct holds data for DataGatherer.
	 */
	private ArrayDeque<BigStruct> myGatherQueue;
	
	/**
	 * Page limit.
	 */
	private SlaveInteger myLinkCount;
	
	/**
	 * Constructor for PageParser.
	 * 
	 * @param retrieveQueue PageToRetrieve queue
	 * @param bufferQueue PageBuffer queue
	 * @param theGatherQueue A queue for DataGatherer
	 * @param theLinkCount the count of the page limit
	 */
	public PageParser(ArrayDeque<URL> retrieveQueue, ArrayDeque<BigStruct> bufferQueue, ArrayDeque<BigStruct> theGatherQueue, SlaveInteger theLinkCount) {
		myBigStruct = new BigStruct(null, "");
		myDoc = new Document("");
		myRetrieveQueue = retrieveQueue;
		myPageBufferQueue = bufferQueue;
		myGatherQueue = theGatherQueue;
		myLinkCount = theLinkCount;
	}
	
	/**
	 * Implements run() method of Thread class.
	 */
	public void run() {
		SlaveInteger accessLinkCount = new SlaveInteger(0);
		do {
			long startTime = System.nanoTime();
			synchronized (myPageBufferQueue) {
				retrieveDoc();
				myBigStruct.setParseTime((System.nanoTime() - startTime));
			}
			
			Elements links = myDoc.select("a[href]");

			synchronized (myRetrieveQueue) {
				try {
					placeInPageRetrieve(links);
				} catch (MalformedURLException e) {
					
					e.printStackTrace();
				}
			}
			
			synchronized (myGatherQueue) {
				myGatherQueue.addLast(myBigStruct);
				myGatherQueue.notifyAll();
			}
			synchronized(myLinkCount) {
				accessLinkCount.setVal(myLinkCount.getVal());
			}
		} while (accessLinkCount.getVal() > 0);
	}
	
	/**
	 * Helper method for run().
	 * Retrieves document from PageBuffer.
	 */
	private synchronized void retrieveDoc() {
		
		while (myPageBufferQueue.isEmpty()) {
			
			try {
				myPageBufferQueue.wait(); 
			} catch (InterruptedException e) {
				
			}
		}
		myBigStruct = myPageBufferQueue.removeFirst();
		myDoc = myBigStruct.getDoc();
		myPageBufferQueue.notifyAll();
	}
	
	/**
	 * Helper method for run().
	 * Puts url links into PageToRetrieve.
	 */
	private synchronized void placeInPageRetrieve(Elements links) throws MalformedURLException {
		for (Element link : links) {	
			try {
				String http = link.attr("abs:href");		
				if (!http.equals("http://questioneverything.typepad.com/")) {
					myRetrieveQueue.addLast(new URL(http));
					myBigStruct.incrementUrlCount();		
				}		
			} catch (IOException e) {
				// Throw away links that don't work
			}
        }
		myRetrieveQueue.notifyAll();
	}
	
}
