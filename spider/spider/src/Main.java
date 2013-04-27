/*
 * MObus didnt let me enjoy the sun!!!
 */

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Scanner;

import org.jsoup.nodes.Document;


public class Main {

	/**
	 * @param args
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws MalformedURLException {
		ArrayDeque<URL> url_queue = new ArrayDeque<>();
		ArrayDeque<Document> doc_queue = new ArrayDeque<>();
		final Scanner input = new Scanner(System.in);
	    String main_url;
	    
	    final ArrayList<String> wordlist = new ArrayList<String>();
	    main_url = getInput(input, wordlist);
	    System.out.println(main_url);
	    System.out.println(wordlist);

		url_queue.addFirst(new URL("http://faculty.washington.edu/gmobus")); //add first URL
		System.out.printf("in url queue: %s\n", url_queue.getFirst()); //URL is now in url_queue
		
		PageRetriever retriever = new PageRetriever(url_queue, doc_queue); //create page retriever
		
		retriever.start(); //start the page retriever thread
		
		synchronized (doc_queue) {
			doc_queue.notifyAll();
		}
		
		while (doc_queue.isEmpty()) {
			System.out.println("waiting");
		}
		
		System.out.printf("in doc queue: %s\n", doc_queue.getFirst()); //URL is in doc queue
		
		//retriever.setURL(new URL("http://faculty.washington.edu/gmobus"));
		//System.out.printf("%s", retriever.getURL().toString());
	    input.close();
	}
	/**
	 * getInput() helper method.
	 * @param the_input the input
	 * @param the_wordlist list of word
	 * @return the url
	 */
	public static String getInput(final Scanner the_input, final ArrayList<String> the_wordlist) {  
		System.out.print("Enter the URL: ");
		final String url = the_input.next();
		System.out.print("How many words do you want to check? ");
		final int amount = the_input.nextInt();

		for (int i = 0; i < amount; i++) {
			System.out.print("Enter a word: ");
			the_wordlist.add(the_input.next());
		}

		return url;
	}
}
