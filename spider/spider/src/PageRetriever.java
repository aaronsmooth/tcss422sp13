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

	public PageRetriever(ArrayDeque<URL> retrieveQueue, ArrayDeque<Document> bufferQueue) {

		myRetrieveQueue = retrieveQueue;
		myPageBufferQueue = bufferQueue;
		
		try { 
			myURL = new URL("");
		} catch (MalformedURLException e) {}
	}
	
	public URL getURL() {
		return myURL;
	}
	
	public void run() {
		
		do {
			retrievePage();
		
			try {
				myDoc = Jsoup.connect(myURL.toExternalForm()).get();
			} catch (IOException e) {
				System.out.println("\nCouldnt connect to URL!");
				e.printStackTrace();
			}
		
			placeInPageBuffer();
		} while(true);
	}
	
	private synchronized void retrievePage() {
		
		while (myRetrieveQueue.isEmpty()) {
			try {
				this.wait();
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
}
