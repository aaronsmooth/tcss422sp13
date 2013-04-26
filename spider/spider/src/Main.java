/*
 * MObus didnt let me enjoy the sun!!!
 */

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;

import org.jsoup.nodes.Document;


public class Main {

	/**
	 * @param args
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws MalformedURLException {
		ArrayDeque<URL> url_queue = new ArrayDeque<>();
		ArrayDeque<Document> doc_queue = new ArrayDeque<>();
		
		url_queue.addFirst(new URL("http://faculty.washington.edu/gmobus")); //add first URL
		System.out.printf("in url queue: %s\n", url_queue.getFirst()); //URL is now in url_queue
		
		PageRetriever retriever = new PageRetriever(url_queue, doc_queue); //create page retriever
		
		retriever.start(); //start the page retriever thread
		
		System.out.printf("in doc queue: %s\n", url_queue.getFirst()); //URL is in doc queue
		
		//retriever.setURL(new URL("http://faculty.washington.edu/gmobus"));
		//System.out.printf("%s", retriever.getURL().toString());
		
	}

}
