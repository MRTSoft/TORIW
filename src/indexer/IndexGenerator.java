package indexer;

import java.util.ArrayList;
import java.util.Hashtable;

public class IndexGenerator {
    private Hashtable<String, Hashtable<String, Integer>> _index;
    public IndexGenerator(){
        _index = new Hashtable<>();
    }

    public Boolean addDocument(String doc){
        //TODO add class for Document
        DocParser dp = new DocParser();
        StringBuilder title, description, keywords, robots, TextPageContent;
        title = new StringBuilder();
        description = new StringBuilder();
        keywords = new StringBuilder();
        robots = new StringBuilder();
        TextPageContent = new StringBuilder();
        ArrayList<String> externalURLs = new ArrayList<>();
        try {
            dp.ParseDocumentFromFile(doc, "https://www.example.com/", title, description, keywords,
                    robots, externalURLs, TextPageContent);
            Hashtable<String, Integer> words = DocParser.getWordsStatsFromString(TextPageContent.toString());
            _index.put(doc, words);
        }


    }
}