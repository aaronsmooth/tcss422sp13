import java.util.ArrayList;
import java.util.Map;


public class DataGatherer {
	
	private Map<String, Integer> myMap;
	
	private ArrayList<String> myList;
	
	/**
	 * Constructor.
	 * @param theList the list of word
	 */
	public DataGatherer(final ArrayList<String> theList) {
		myList = theList;
		myMap = buildMap(myList);
	}
	
	public Map<String, Integer> getMap() {
		return myMap;
	}
	
	@SuppressWarnings("null")
	private Map<String, Integer> buildMap(final ArrayList<String> theList) {
		Map<String, Integer> themap = null;
		String theWord;
		
		for (int i = 0; i < theList.size(); i++) {
			theWord = theList.get(i).toLowerCase().replaceAll("\\W", "");
			final Integer occurs = themap.get(theWord);
			if (occurs == null) {
				themap.put(theWord, 1);
			} else {
				themap.put(theWord, occurs+1);
			}
			
		}
		return themap;
	}
}
