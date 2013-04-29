import org.jsoup.nodes.Document;


public class BigStruct {
	private int wordCount;
	private int urlCount;
	private String urlName;
	private Document aDoc;
	private boolean done;
	
	public BigStruct(Document theDoc, String theString) {
		wordCount = 0;
		urlCount = 0;
		aDoc = theDoc;
		urlName = theString;
		done = false;
	}
	
	public void incrementWordCount() {
		wordCount++;
	}
	
	public void incrementUrlCount() {
		urlCount++;
	}
	
	public void setURL(String theURL) {
		urlName = theURL;
	}
	
	public void setDoc(Document theDoc) {
		aDoc = theDoc;
	}
	
	public int getWordCount() {
		return wordCount;
	}
	
	public int getUrlCount() {
		return urlCount;
	}
	
	public String getUrlName() {
		return urlName;
	}
	
	public Document getDoc() {
		return aDoc;
	}
	
	public boolean isDone() {
		return done;
	}
	
	public void setDone() {
		done = true;
	}
	
	@Override
	public String toString() {
		return urlName;
	}
}
