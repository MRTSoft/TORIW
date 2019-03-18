package finder;

import indexer.DocParser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Queue;

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

    private void queryWord(String word, int operator, SearchResult result){
        Hashtable<String, Integer> sts = DocParser.getWordsStatsFromString(word);
        for(String term : sts.keySet()){
            switch (operator){
                case OP_AND:
                    result.AndSearch(term);
                    break;
                case OP_NOT:
                    result.NotSearch(term);
                    break;
                case OP_OR:
                    result.OrSearch(term);
                    break;
                case OP_NONE:
                    result.SearchTerm(term);
                    break;
                default:
                    result.OrSearch(term);
            }
        }
    }

    private static final int OP_OR = (int)' ';
    private static final int OP_AND = (int)'+';
    private static final int OP_NOT = (int)'-';
    private static final int OP_NONE = -1;

    public SearchResult RunQuery(String query) {
        SearchResult result = new SearchResult(this);

        String allOperators = " +-";
        Queue<String> words = new ArrayDeque<>();
        Queue<Integer> operators = new ArrayDeque<>();

        StringBuilder word = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new ByteArrayInputStream(query.getBytes()),
                            Charset.forName("UTF-8")
                    )
            );
            int c;
            while ((c = reader.read()) != -1) {
                if (allOperators.indexOf(c) == -1)
                    word.append((char) c);
                else {
                    //enqueue the word
                    words.add(word.toString());
                    operators.add(new Integer(c));
                    word = new StringBuilder();
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (!word.toString().isEmpty()) {
            words.add(word.toString());
        }
        if (words.isEmpty() == false){
            queryWord(words.remove(), OP_NONE, result);
        }
        while (words.isEmpty() == false){
            int operator = OP_NONE;
            String qWord = words.remove();
            if (operators.isEmpty() == false){
                operator = operators.remove();
            }
            queryWord(qWord, operator, result);
        }


        return result;
    }

}
