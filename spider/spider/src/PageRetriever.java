import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.locks.Lock;

import org.jsoup.nodes.Document;



public class PageRetriever implements Runnable {
	
	private URL myURL;
	private Document myDoc;
	private Lock pageToRetrieveLock;
	private Lock pageBufferLock;

	public PageRetriever(Lock retrieveLock, Lock bufferLock) {
		pageToRetrieveLock = retrieveLock;
		pageBufferLock = bufferLock;
		try { 
			myURL = new URL("");
		} catch (MalformedURLException e) {}
	}
	
	public void setURL(URL u) {
		myURL = u;
	}
	public URL getURL() {
		return myURL;
	}
	
	public void run() {
		if (pageToRetrieveLock.tryLock()) {
			
			pageToRetrieveLock.unlock();
	    }
	      
	}
}
