import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;



public class PageRetriever extends Thread {
	
	private URL myURL;
	private Document myDoc;
	private ArrayDeque<URL> myRetrieveQueue;
	private ArrayDeque<Document> myPageBufferQueue;
	private Integer myLinkCount;
	private boolean continueRunning;

	public PageRetriever(ArrayDeque<URL> retrieveQueue, ArrayDeque<Document> bufferQueue,
							Integer linkCount) {

		myRetrieveQueue = retrieveQueue;
		myPageBufferQueue = bufferQueue;
		myLinkCount = linkCount;
		continueRunning = true;
		
		try { 
			myURL = new URL("");
		} catch (MalformedURLException e) {}
	}
	
	public void run() {
		
		do {
			synchronized (myRetrieveQueue) {
				retrievePage();
			}
			
			try {
				myDoc = Jsoup.connect(myURL.toExternalForm()).get();
				
				synchronized (myLinkCount) {
					continueRunning = updateCount();
				}
			} catch (IOException e) {
				System.out.println("\nCouldnt connect to URL!");
				e.printStackTrace();
			}
			
			synchronized (myPageBufferQueue) {
				placeInPageBuffer();
			}
			
		} while(true);
	}
	
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
	
	private synchronized void placeInPageBuffer() {
		myPageBufferQueue.addLast(myDoc);
		myPageBufferQueue.notifyAll();
	}
	
	private synchronized boolean updateCount() {
		myLinkCount++;
		
	}
}
