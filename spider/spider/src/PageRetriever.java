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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * PageRetriever class processes the information from the PageToRetrieve queue and puts in PageBuffer queue.
 * 
 * @author Quan Le
 * @author Tyler Powers
 * @author Aaron Nelson
 * @author Seth Kramer
 * @version 1.0
 */
public class PageRetriever extends Thread {
	/**
	 * URL field.
	 */
	private URL myURL;
	
	/**
	 * A document.
	 */
	private Document myDoc;
	
	/**
	 * PageToRetrieve queue.
	 */
	private ArrayDeque<URL> myRetrieveQueue;
	
	/**
	 * PageBuffer queue.
	 */
	private ArrayDeque<BigStruct> myPageBufferQueue;
	
	/**
	 * Page limit.
	 */
	private SlaveInteger myLinkCount;						// Passed from Main, decrements counter until 0;
	
	/**
	 * Condition variable.
	 */
	private boolean continueRunning;
	
	/**
	 * BigStruct hold data for DataGatherer.
	 */
	private BigStruct myBigStruct;

	/**
	 * Constructor for PageRetriever.
	 * 
	 * @param retrieveQueue the PageToRetrieve Queue
	 * @param bufferQueue the PageBuffer Queue
	 * @param linkCount the count of the page limit
	 */
	public PageRetriever(ArrayDeque<URL> retrieveQueue, ArrayDeque<BigStruct> bufferQueue,
							SlaveInteger linkCount) {
		
		myRetrieveQueue = retrieveQueue;
		myPageBufferQueue = bufferQueue;
		myLinkCount = linkCount;
		continueRunning = true;
		try { 
			myURL = new URL("");
		} catch (MalformedURLException e) {}
		
		myBigStruct =  new BigStruct(null, "");
	}
	
	/**
	 * Implements run() method of Thread class.
	 */
	public void run() {
		do {
			synchronized (myRetrieveQueue) {
				retrievePage();
			}
			
			try {
				myDoc = Jsoup.connect(myURL.toExternalForm()).get();
				
				if (continueRunning) {
					myBigStruct.setDoc(myDoc);
					myBigStruct.setURL(myURL.toString());
					synchronized (myPageBufferQueue) {
						placeInPageBuffer();
					}
				}

				synchronized (myLinkCount) {
					updateCount();
				}
				
			} catch (IOException e) {
				// Throw away links that don't work
			}
						
		} while(continueRunning);
		
	}
	
	/**
	 * Helper method for run().
	 * Retrieves website from PageToRetrieve.
	 */
	private synchronized void retrievePage() {
		
		while (myRetrieveQueue.isEmpty()) {
			
			try {
				myRetrieveQueue.wait(); 
			} catch (InterruptedException e) {
				
			}
		}
		
		myURL = myRetrieveQueue.removeFirst();
		myRetrieveQueue.notifyAll();
	}
	
	/**
	 * Helper method for run().
	 * Puts documents into PageBuffer.
	 */
	private synchronized void placeInPageBuffer() {
		myPageBufferQueue.addLast(myBigStruct);
		myPageBufferQueue.notifyAll();
	}
	
	/**
	 * Helper method for run().
	 * Update the count of page limit.
	 */
	private synchronized void updateCount() {
		if (myLinkCount.getVal() <= 0) {
			continueRunning = false;
			myBigStruct.setDone();
		} 	
	}
}
