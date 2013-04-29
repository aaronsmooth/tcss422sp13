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
	private ArrayDeque<BigStruct> myPageBufferQueue;
	private SlaveInteger myLinkCount;						// Passed from Main, decrements counter until 0;
	private boolean continueRunning;
	private BigStruct myBigStruct;

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
	
	public void run() {
		do {
			synchronized (myRetrieveQueue) {
				retrievePage();
			}
			
			try {
				myDoc = Jsoup.connect(myURL.toExternalForm()).get();
				
				synchronized (myLinkCount) {
					updateCount();
				}
				
				if (continueRunning) {
					myBigStruct.setDoc(myDoc);
					myBigStruct.setURL(myURL.toString());
					synchronized (myPageBufferQueue) {
						placeInPageBuffer();
					}
				}
				
			} catch (IOException e) {
				// Throw away links that don't work
			}
						
		} while(continueRunning);
		
		System.out.printf("\nI'm dieing! *%s\n" , this.getName());
		System.out.println(myBigStruct.isDone());
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
		myPageBufferQueue.addLast(myBigStruct);
		myPageBufferQueue.notifyAll();
	}
	
	private synchronized void updateCount() {
		System.out.printf("\nmyLinkCount: %d	%s\n", myLinkCount.getVal(), this.getName());
		myLinkCount.decrement();
		
		if (myLinkCount.getVal() < 0) {
			continueRunning = false;
			myBigStruct.setDone();
		} 	
	}
}
