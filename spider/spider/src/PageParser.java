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
	
	private ArrayDeque<URL> myRetrieveQueue;
	private ArrayDeque<Document> myPageBufferQueue;
	private ArrayDeque<Document> myGatherQueue;
	
	private int count = 0;
	
	public PageParser(ArrayDeque<URL> retrieveQueue, ArrayDeque<Document> bufferQueue, ArrayDeque<Document> theGatherQueue) {
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
				System.out.printf("\nParserCount: %d", count);
				myGatherQueue.addLast(myDoc);
				myGatherQueue.notifyAll();
			}
			
		} while(true);
	}
	
	private synchronized void retrieveDoc() {
		
		while (myPageBufferQueue.isEmpty()) {
			
			try {
				myPageBufferQueue.wait(); 
			} catch (InterruptedException e) {
				
			}
		}
		
		myDoc = myPageBufferQueue.removeFirst();
		myPageBufferQueue.notifyAll();
	}
	
	private synchronized void placeInPageRetrieve(Elements links) throws MalformedURLException {

		for (Element link : links) {
			
			try {
				String http = link.attr("abs:href");	
				
				if (!http.equals("http://questioneverything.typepad.com/")) {
					myRetrieveQueue.addLast(new URL(http));
					//System.out.println(http);
				}
					
			} catch (IOException e) {
				// Throw away links that don't work
			}
        }
		
		myRetrieveQueue.notifyAll();
	}
	
}
