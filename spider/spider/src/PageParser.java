import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class PageParser extends Thread {
	
	
	private Document myDoc;
	private Elements links;
	private BigStruct myBigStruct;
	private ArrayDeque<URL> myRetrieveQueue;
	private ArrayDeque<BigStruct> myPageBufferQueue;
	private ArrayDeque<BigStruct> myGatherQueue;
	
	private int count = 0;
	
	public PageParser(ArrayDeque<URL> retrieveQueue, ArrayDeque<BigStruct> bufferQueue, ArrayDeque<BigStruct> theGatherQueue) {
		myBigStruct = new BigStruct(null, "");
		myDoc = new Document("");
		myRetrieveQueue = retrieveQueue;
		myPageBufferQueue = bufferQueue;
		myGatherQueue = theGatherQueue;
		links = new Elements();
	}
	
//	public URL getURL() {
//		return myURL;
//	}
	
	public void run() {
		
		do {
			synchronized (myPageBufferQueue) {
				retrieveDoc();
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
				++count;
				//System.out.printf("\nParserCount: %d\n", count);
				myGatherQueue.addLast(myBigStruct);
				myGatherQueue.notifyAll();
			}
			
		} while(!myBigStruct.isDone());
		System.out.println("MY PARSER HAS CANCER!");
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
