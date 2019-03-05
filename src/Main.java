import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import indexer.DocParser;

public class Main {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        DocParser dp = new DocParser();
        StringBuilder title, description, keywords, robots, TextPageContent;
        title = new StringBuilder();
        description = new StringBuilder();
        keywords = new StringBuilder();
        robots = new StringBuilder();
        TextPageContent = new StringBuilder();
        ArrayList<String> externalURLs = new ArrayList<String>();
        dp.ParseDocumentFromFile("./data/java.html", "https://moz.com/", title, description, keywords,
                robots, externalURLs, TextPageContent);
        System.out.println(title);
        System.out.println(description);
        System.out.println(keywords);
        System.out.println(robots);
        for (String url : externalURLs) {
            //System.out.print('\t');
            //System.out.println(url);
        }

        //Write the page content to data/text/name-of-page.txt
        try {
            PrintWriter out = new PrintWriter("./data/text/java.txt");
            out.write(TextPageContent.toString());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Hashtable<String, Integer> v = DocParser.getWordsStatsForFile("./data/text/java.txt");
        Set<String> keys = v.keySet();
        for(String key : keys) {
            System.out.println(String.format("%s : %s", key, v.get(key)));
        }
        System.out.println("Finished!");
    }
}
