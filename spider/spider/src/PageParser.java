import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class PageParser extends Thread {
	
	
	private Document myDoc;
	private Elements links;
	
	private ArrayDeque<URL> myRetrieveQueue;
	private ArrayDeque<Document> myPageBufferQueue;
	private ArrayDeque<Document> myGatherQueue;
	
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
					placeInPageRetrieve();
				} catch (MalformedURLException e) {
					
					e.printStackTrace();
				}
			}
			
			synchronized (myGatherQueue) {
				myGatherQueue.addLast(myDoc);
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
	
	private synchronized void placeInPageRetrieve() throws MalformedURLException {
		System.out.println("in pageparser before for");
		System.out.println(myRetrieveQueue);
		for (Element link : links) {
			String http = link.attr("abs:href");
			if (http.substring(0, 4).equals("http") 
        			&& !http.substring(http.length()-4,http.length()).equals(".m4v")
        			&& !http.equals("http://questioneverything.typepad.com/")) {
				URL url = new URL(http);
				myRetrieveQueue.addLast(url);
			}
        }
		System.out.println("in pageparser");
		System.out.println(myRetrieveQueue);
		
		myRetrieveQueue.notifyAll();
	}
	
}
