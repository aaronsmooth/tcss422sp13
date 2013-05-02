/*
 * Quan Le
 * Tyler Powers
 * Aaron Nelson
 * Seth Kramer
 * Team Robbins Egg Blue Dolphins
 * 05/02/2013
 */

import org.jsoup.nodes.Document;

/**
 * BigStruct holds all of the data.
 * It is used to provide information to DataGatherer.
 * 
 * @author Quan Le
 * @author Tyler Powers
 * @author Aaron Nelson
 * @author Seth Kramer
 * @version 1.0
 */
public class BigStruct {
	/**
	 * Private field for amount of words.
	 */
	private int wordCount;
	
	/**
	 * Private field for number of url links.
	 */
	private int urlCount;
	
	/**
	 * Private field for url links.
	 */
	private String urlName;
	
	/**
	 * Private field for documents.
	 */
	private Document aDoc;
	
	/**
	 * Condition variable.
	 */
	private boolean done;
	
	/**
	 * Private field for parse time.
	 */
	private long myParseTime;
	
	/**
	 * Constructor of BigStruct.
	 * 
	 * @param theDoc the document
	 * @param theString the url string
	 */
	public BigStruct(Document theDoc, String theString) {
		wordCount = 0;
		urlCount = 0;
		aDoc = theDoc;
		urlName = theString;
		myParseTime = 0;
		done = false;
	}
	
	/**
	 * Sets parse time.
	 * @param theTime the parse time
	 */
	public void setParseTime(long theTime) {
		myParseTime = theTime;
	}
	
	/**
	 * Gets parse time.
	 * @return the parse time
	 */
	public long getParseTime() {
		return myParseTime;
	}
	
	/**
	 * Increments amount of words.
	 */
	public void incrementWordCount() {
		wordCount++;
	}
	
	/**
	 * Increments amount of url links.
	 */
	public void incrementUrlCount() {
		urlCount++;
	}
	
	/**
	 * Sets url links.
	 * @param theURL the url
	 */
	public void setURL(String theURL) {
		urlName = theURL;
	}
	
	/**
	 * Sets the documents.
	 * @param theDoc the document
	 */
	public void setDoc(Document theDoc) {
		aDoc = theDoc;
	}
	
	/**
	 * Gets number of words.
	 * @return number of words.
	 */
	public int getWordCount() {
		return wordCount;
	}
	
	/**
	 * Gets number of url links.
	 * @return number of url links
	 */
	public int getUrlCount() {
		return urlCount;
	}
	
	/**
	 * Gets url links.
	 * @return the url link
	 */
	public String getUrlName() {
		return urlName;
	}
	
	/**
	 * Gets documents.
	 * @return the document
	 */
	public Document getDoc() {
		return aDoc;
	}
	
	/**
	 * Checks if the process is done.
	 * @return true if it is done, false if not
	 */
	public boolean isDone() {
		return done;
	}

	/**
	 * Sets the condition variable.
	 */
	public void setDone() {
		done = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return urlName;
	}
}
