/*
 * MObus didnt let me enjoy the sun!!!
 */

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

import org.jsoup.nodes.Document;


public class Main {

	/**
	 * @param args
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws MalformedURLException {
		ArrayDeque<URL> url_queue = new ArrayDeque<>();
		ArrayDeque<Document> doc_queue = new ArrayDeque<>();
		ArrayList<String> wordlist = new ArrayList<String>();
		final Scanner input = new Scanner(System.in);
	    
		final String main_url;
	    int amount;
	    int numthread;
	    System.out.print("Number of threads? ");
	    numthread = input.nextInt();
	    System.out.print("How many words do you want to check? ");
		amount = input.nextInt();
	    main_url = getInput(input, wordlist, amount);
	    System.out.println(main_url);
	    System.out.println(wordlist);

		url_queue.addFirst(new URL("http://faculty.washington.edu/gmobus")); //add first URL
		System.out.printf("in url queue: %s\n", url_queue.getFirst()); //URL is now in url_queue
		
		PageRetriever retriever = new PageRetriever(url_queue, doc_queue); //create page retriever
		
		retriever.start(); //start the page retriever thread
		
		synchronized (doc_queue) {
			doc_queue.notifyAll();
		}
		
//		while (doc_queue.isEmpty()) {
//			System.out.println("waiting");
//		}
		
//		System.out.printf("in doc queue: %s\n", doc_queue.getFirst()); //URL is in doc queue
		
		//retriever.setURL(new URL("http://faculty.washington.edu/gmobus"));
		//System.out.printf("%s", retriever.getURL().toString());
		
		
		//Test gatherer, 
		Map<String, Integer> testmap = new TreeMap<String, Integer>();
		ArrayList<String> gatherlist = new ArrayList<>();
		gatherlist.add("the");
		gatherlist.add("the");
		gatherlist.add("this");
		gatherlist.add("that");
		gatherlist.add("these");
		gatherlist.add("those");
		DataGatherer data = new DataGatherer(wordlist, gatherlist);		
		testmap=data.buildMap();

		System.out.println(testmap);
	    input.close();
	}
	
	/**
	 * getInput() helper method.
	 * @param the_input the input
	 * @param the_wordlist list of word
	 * @return the url
	 */
	public static String getInput(final Scanner the_input, final ArrayList<String> the_wordlist, int the_amount) {  
		for (int i = 0; i < the_amount; i++) {
			System.out.print("Enter a word: ");
			the_wordlist.add(the_input.next());
		}
		System.out.print("Enter the URL: ");
		final String url = the_input.next();
		return url;
	}
}
