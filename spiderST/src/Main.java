/*
 * Quan Le
 * Tyler Powers
 * Aaron Nelson
 * Seth Kramer
 * Team Robbins Egg Blue Dolphins
 * 05/02/2013
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Main class of the Webcrawler program.
 * Single threaded program.
 * Starts up the program.
 * 
 * @author Quan Le
 * @author Tyler Powers
 * @author Aaron Nelson
 * @author Seth Kramer
 * @version 1.0
 */
public class Main {

	/**
	 * A constant which is the maximum website this program will run. 
	 */
	private static int MAXDEPTH = 10000;

	public static void main(String[] args) throws IOException {

		ArrayDeque<URL> url_queue = new ArrayDeque<>();
		ArrayList<String> wordlist = new ArrayList<String>();
		Map<String, Integer> mainmap = new TreeMap<String, Integer>();

		final Scanner input = new Scanner(System.in);	
		String texts;
		int retrieveCount = 0;
		long totalWordCount = 0;
		long totalTime = 0;
		int totalUrlCount = 0;
		final String main_url;
		int pagelimit;
		int amount = gettingInput("How many words do you want to check? ");
		getInput(input, wordlist, amount);
		System.out.print("Enter the URL: ");
		main_url = input.next();
		int depth = gettingDepth("How many websites would you like to search through? ");

		if (depth == 0) {
			depth = MAXDEPTH;
		}
		pagelimit = depth;

		url_queue.addFirst(new URL(main_url)); 
		long totalParseTime = 0;
		long startTime = System.nanoTime();
		for (String str : wordlist) {
			mainmap.put(str, 0);
		}
		PrintWriter ps = null;
		try  
		{
			File file = new File("spiderRunSingleThread.txt");
		    FileWriter fstream = new FileWriter(file, true); //true tells to append data.
		    ps = new PrintWriter(fstream);
		}
		catch (Exception e)
		{
		    System.err.println("Error: " + e.getMessage());
		}

		while (depth > 0 && !url_queue.isEmpty()) {
			URL url = url_queue.removeFirst();
			BigStruct data = new BigStruct(null, "");

			try {
				// Page retriever section
				Document doc = Jsoup.connect(url.toExternalForm()).get();

				data.setDoc(doc);
				retrieveCount++;
				data.setURL(url.toString());

				//Page parser section
				data.setParseTime(System.nanoTime());
				Elements links = doc.select("a[href]");

				for (Element link : links) {	
					try {
						String http = link.attr("abs:href");		
						if (!http.equals("http://questioneverything.typepad.com/")) {
							url_queue.addLast(new URL(http));
							data.incrementUrlCount();		
						}		
					} catch (IOException e) {
						// Throw away links that don't work
					}
				}

				totalTime = System.nanoTime() - startTime;

				String str;


				try {
					texts = doc.body().text();
					Scanner stringscan = new Scanner(texts);

					while (stringscan.hasNext()) {
						str = stringscan.next().toLowerCase().replaceAll("\\W", "");
						data.incrementWordCount();
						if (mainmap.containsKey(str)) {
							mainmap.put(str, mainmap.get(str)+ 1);
						}
					}	

					totalTime = System.nanoTime() - startTime;
					totalParseTime += System.nanoTime() - data.getParseTime();
					totalWordCount = totalWordCount + data.getWordCount();
					totalUrlCount = totalUrlCount + data.getUrlCount();

					System.out.printf("\n\n\n");
					System.out.println("Parsed: " + data.getUrlName());
					System.out.println("Pages Retrieved: " + retrieveCount);
					System.out.println("Average Words Per Page: " + (totalWordCount / retrieveCount));
					System.out.println("Average URL's per page: " + (totalUrlCount / retrieveCount));
					System.out.printf("Keyword \tAvg. hits per page \tTotal hits\n");

					for (Map.Entry<String, Integer> word : mainmap.entrySet()) {
						System.out.printf("  %-20s %-20d %-20d\n", word.getKey(), word.getValue() / retrieveCount, word.getValue());
					}
					System.out.printf("Average parse time per page: %.4f seconds\n", (totalParseTime / retrieveCount) * (Math.pow(10, -9)));
					System.out.printf("Total running time: %.4f seconds\n", (totalTime * (Math.pow(10, -9))));
					stringscan.close();


				} catch (NullPointerException e) {
					// Throw away docs with empty bodies
				}
				
				ps.printf("\n\n\n");
				ps.println("Parsed: " + data.getUrlName());
				ps.println("Pages Retrieved: " + retrieveCount);
				ps.println("Average Words Per Page: " + (totalWordCount / retrieveCount));
				ps.println("Average URL's per page: " + (totalUrlCount / retrieveCount));
				ps.printf("Keyword \tAvg. hits per page \tTotal hits\n");
				
				for (Map.Entry<String, Integer> word : mainmap.entrySet()) {
					ps.printf("  %-20s %-20d %-20d\n", word.getKey(), word.getValue() / retrieveCount, word.getValue());
				}
				ps.println("Page limit: " + pagelimit);
				ps.printf("Average parse time per page: %.4f seconds\n", (totalParseTime / retrieveCount) * (Math.pow(10, -9)));
				ps.printf("Total running time: %.4f seconds\n", (totalTime * (Math.pow(10, -9))));

				depth--;
				
			} catch (IOException e) {
				// Throw away links that don't work
			}
		}
		ps.println();
		ps.println();
		ps.println("------------------------------------------------------------------------");
		ps.println();
		ps.printf("Average parse time per page: %.4f seconds\n", (totalParseTime / retrieveCount) * (Math.pow(10, -9)));
		ps.println();
		ps.println("Total time: " + totalTime * (Math.pow(10, -9)) + " seconds");
		ps.close();
	}

	/**
	 * getInput() helper method gets the list of words from user input.
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

	/**
	 * Checks for valid input.
	 * If not, it will prompt users for the input again.
	 * @param thetxt the text
	 * @return integer
	 */
	public static int gettingInput(String thetxt) {
		boolean validinput = false;
		int result = 0;
		while (validinput == false) {
			Scanner console = new Scanner(System.in);
			System.out.print(thetxt);
			if (console.hasNextInt()) {
				result = console.nextInt();
				if (result >= 1) {
					validinput = true;
				} else {
					System.out.println("That is not a valid size! Must be a value greater than 0! ");
				}
			} else {
				System.out.println("Invalid entry! Must be an integer value! ");
			}

		}
		return result;
	}

	/**
	 * Another helper method checks for valid input.
	 * @param thetxt the text
	 * @return integer
	 */
	public static int gettingDepth(String thetxt) {
		boolean validinput = false;
		int result = 0;
		while (validinput == false) {
			Scanner console = new Scanner(System.in);
			System.out.print(thetxt);
			if (console.hasNextInt()) {
				result = console.nextInt();
				if (result  <= MAXDEPTH && result  >= 0) {
					validinput = true;
				} else {
					System.out.println("That is not a valid size! Must be a zero or greater, maximum value allowed is 10,000!/n Enter 0 for the max value. ");
				}
			} else {
				System.out.println("Invalid entry! Must be an integer value! ");
			}

		}
		return result;
	}



}

