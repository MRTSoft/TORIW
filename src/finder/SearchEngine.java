package finder;

import indexer.DocParser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Hashtable;

public class SearchEngine {
    public Hashtable<String, Hashtable<String, Integer>> _reverseIndex;
    public SearchEngine(){

    }

    public void addIndexData(Hashtable<String, Hashtable<String, Integer>> index){
        _reverseIndex = (Hashtable<String, Hashtable<String, Integer>>) index.clone();
    }

    public HashSet<String> SearchTerm(String term){
        HashSet<String> res = new HashSet<>(_reverseIndex.get(term).keySet());
        return  res;
    }

    public SearchResult RunQuery(String query){
        SearchResult result = new SearchResult(this);
        int OP_OR = (int)' ';
        int OP_AND = (int)'+';
        int OP_NOT = (int)'-';
        String operators = " +-";
        int lastOperator = -1;
        StringBuilder word = new StringBuilder();
        try{
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new ByteArrayInputStream(query.getBytes()),
                            Charset.forName("UTF-8")
                    )
            );
            int c;
            while ((c=reader.read()) != -1){
                if (operators.indexOf(c) == -1)
                    word.append(c);
                else{
                    //process the word
                    lastOperator = c;
                    Hashtable<String, Integer> sts = DocParser.getWordsStatsFromString(word.toString());
                    for(String term : sts.keySet()){
                        //TODO use the operator
                        result = result.OrSearch(term);
                    }
                }
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return result;
    }

}
