

import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;


public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		ArrayDeque<URL> url_queue = new ArrayDeque<>();
		ArrayDeque<BigStruct> pagebuffer = new ArrayDeque<>();
		ArrayDeque<BigStruct> gatherqueue = new ArrayDeque<>();
		ArrayList<String> wordlist = new ArrayList<String>();
		
		Map<String, Integer> mainmap = new TreeMap<String, Integer>();
		
		final Scanner input = new Scanner(System.in);	
		final String main_url;
		
	    int amount;
	    int parsethreads;
	    int retrievethreads;
	    int depth;
	    System.out.print("Number of Page Retriever Threads? ");
	    retrievethreads = input.nextInt();
	    System.out.print("Number of Page Parser Threads? ");
	    parsethreads = input.nextInt();
	    System.out.print("How many words do you want to check? ");
		amount = input.nextInt();
		getInput(input, wordlist, amount);
		System.out.print("Enter the URL: ");
		main_url = input.next();
		System.out.print("How many websites would you like to search through? ");
		depth = input.nextInt();
		while (depth > 10000 || depth <= 0) {
			System.out.print("How many websites would you like to search through? ");
			depth = input.nextInt();
		}
	    	    
	    url_queue.addFirst(new URL(main_url)); //add first URL
	    
	    SlaveInteger linkCount = new SlaveInteger(depth);
	    
		// http://faculty.washington.edu/gmobus
		for (int i = 0; i < retrievethreads; i++) {
			PageRetriever retriever = new PageRetriever(url_queue, pagebuffer, linkCount); //create page retriever
			retriever.start(); //start the page retriever thread
		}
		
		for (int i = 0; i < parsethreads; i++) {
			PageParser pageparser = new PageParser(url_queue, pagebuffer, gatherqueue, linkCount);
			pageparser.start();
		}
		
		DataGatherer data = new DataGatherer(wordlist, gatherqueue, mainmap, System.nanoTime(), depth, linkCount);
			
		data.start();
	}
	
	/**
	 * getInput() helper method.
	 * @param the_input the input
	 * @param the_wordlist list of word
	 * @return the url
	 */
	public static void getInput(final Scanner the_input, final ArrayList<String> the_wordlist, int the_amount) {  
		for (int i = 0; i < the_amount; i++) {
			System.out.print("Enter a word: ");
			the_wordlist.add(the_input.next());
		}
	}
}
