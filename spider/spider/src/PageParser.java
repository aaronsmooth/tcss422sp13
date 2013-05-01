import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class PageParser extends Thread {
	
	
	private Document myDoc;
	private BigStruct myBigStruct;
	private ArrayDeque<URL> myRetrieveQueue;
	private ArrayDeque<BigStruct> myPageBufferQueue;
	private ArrayDeque<BigStruct> myGatherQueue;
	private SlaveInteger myLinkCount;
	
	
	public PageParser(ArrayDeque<URL> retrieveQueue, ArrayDeque<BigStruct> bufferQueue, ArrayDeque<BigStruct> theGatherQueue, SlaveInteger theLinkCount) {
		myBigStruct = new BigStruct(null, "");
		myDoc = new Document("");
		myRetrieveQueue = retrieveQueue;
		myPageBufferQueue = bufferQueue;
		myGatherQueue = theGatherQueue;
		myLinkCount = theLinkCount;
	}
	
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
