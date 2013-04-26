/*
 * MObus didnt let me enjoy the sun!!!
 */

import java.net.MalformedURLException;
import java.net.URL;


public class Main {

	/**
	 * @param args
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws MalformedURLException {
		PageRetriever retriever = new PageRetriever();
		retriever.setURL(new URL("http://faculty.washington.edu/gmobus"));
		System.out.printf("%s", retriever.getURL().toString());
	}

}
