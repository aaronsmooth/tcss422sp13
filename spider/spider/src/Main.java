/*
 * MObus didnt let me enjoy the sun!!!
 */

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		ArrayDeque<URL> url_queue = new ArrayDeque<>();
		ArrayDeque<Document> pagebuffer = new ArrayDeque<>();
		ArrayDeque<Document> gatherqueue = new ArrayDeque<>();
		ArrayList<String> wordlist = new ArrayList<String>();
		Map<String, Integer> mainmap = new TreeMap<String, Integer>();
		final Scanner input = new Scanner(System.in);
		PageParser pageparser = new PageParser(url_queue, pagebuffer, gatherqueue);
		
		final String main_url;
	    int amount;
	    int numthread;
	    System.out.print("Number of threads? ");
	    numthread = input.nextInt();
	    System.out.print("How many words do you want to check? ");
		amount = input.nextInt();
	    main_url = getInput(input, wordlist, amount);
	    
	    System.out.println(wordlist);

		url_queue.addFirst(new URL("http://faculty.washington.edu/gmobus")); //add first URL
		
		
		PageRetriever retriever = new PageRetriever(url_queue, pagebuffer); //create page retriever
		
		retriever.start(); //start the page retriever thread
//		
//		synchronized (pagebuffer) {
//			pagebuffer.notifyAll();
//		}
//		
//		while (pagebuffer.isEmpty()) {
//			System.out.println("waiting");
//		}
		
		
		Document doc = Jsoup.connect("http://faculty.washington.edu/gmobus/").get();
		gatherqueue.add(doc);
		
		DataGatherer data = new DataGatherer(wordlist, gatherqueue, mainmap);
		
		data.start();
		pageparser.start();
//		System.out.println(url_queue);
		//mainmap = data.getMap();
//		int count = 0;
//		do {
//		System.out.println(mainmap);
//		count++;
//		} while (count < 20);
		
	    //input.close();
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
