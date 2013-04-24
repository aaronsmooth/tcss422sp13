import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.concurrent.locks.Lock;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;



public class PageRetriever implements Runnable {
	
	private URL myURL;
	private Document myDoc;
	private Lock pageToRetrieveLock;
	private Lock pageBufferLock;
	private ArrayDeque<URL> pageToRetrieve;
	private ArrayDeque<Document> pageBuffer;

	public PageRetriever(Lock retrieveLock, Lock bufferLock,
							ArrayDeque<URL> retrieveQueue, ArrayDeque<Document> bufferQueue) {
		pageToRetrieveLock = retrieveLock;
		pageBufferLock = bufferLock;
		pageToRetrieve = retrieveQueue;
		pageBuffer = bufferQueue;
		
		try { 
			myURL = new URL("");
		} catch (MalformedURLException e) {}
	}
	
	public URL getURL() {
		return myURL;
	}
	
	public void run() {
		
		do {
			pageToRetrieveLock.lock();
			myURL = pageToRetrieve.removeFirst();
			pageToRetrieveLock.unlock();
		
			try {
				myDoc = Jsoup.connect(myURL.toString()).get();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
			pageBufferLock.lock();
			pageBuffer.addLast(myDoc);
			pageBufferLock.unlock();
		} while (true);
	}
}
