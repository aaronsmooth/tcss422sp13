import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;


public class DataGatherer {
	
	private Map<String, Integer> myMap;
	
	private ArrayList<String> myTextList;
	
	private ArrayList<String> myList;
	
	/**
	 * Constructor.
	 * @param theList the list of word
	 */
	public DataGatherer(final ArrayList<String> theList, ArrayList<String> theTextList) {
		myList = theList;
		myTextList = theTextList;
		myMap = new TreeMap<String, Integer>();
	}
	
	public Map<String, Integer> getMap() {
		return myMap;
	}
	
	public void SetList(ArrayList<String> theList) {
		myList = theList;
	}
	
	public void setTextList(ArrayList<String> theTextList) {
		myTextList = theTextList;
	}
	
	public Map<String, Integer> buildMap() {
		
		String theWordinList;
		String theWordinTextList;
		
		for (int i = 0; i < myList.size(); i++) {
			theWordinList = myList.get(i).toLowerCase().replaceAll("\\W", "");
			
			for (int j = 0; j < myTextList.size(); j++) {
				theWordinTextList = myTextList.get(j).toLowerCase().replaceAll("\\W", "");
				final Integer occurs = myMap.get(theWordinList);
				if (theWordinList.equals(theWordinTextList)) {
					if (occurs == null) {
						myMap.put(theWordinList, 1);
					} else {
						myMap.put(theWordinList, occurs+1);
					}					
				}
				

			}
			
		}
		return myMap;
	}
}
