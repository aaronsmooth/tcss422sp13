import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;




public class Hello {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		System.out.println("Hello World");
		System.out.println("Aaron IS THE BEST"); //Courtesy of Aaron
		System.out.println("Fuck mobus"); //Courtesy of everyone who ever existed in any universe and dimension
		System.out.println("No, Mobus fucks you");
		
		try {
			Document doc = Jsoup.connect("http://faculty.washington.edu/gmobus").get();
			String title = doc.title();
			System.out.printf("\n%s\n", title);
			Elements links = doc.select("a[href]");
			
			for (Element link : links) {
	            print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
	        }
			
			for (Element link : links) {
				
				try {
					doc = Jsoup.connect(link.attr("abs:href")).get();
					System.out.printf("\n%s\n", doc.title());
				} catch (IOException e) {
					// Throw away links that don't work
				}
	        }
			
		} catch (IOException e){
			System.out.println("\nURL invalid!");
		}
		
	}
	
	private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }
}