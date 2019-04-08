package gombak;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

public class SearchEngine {
    private Hashtable<String, WordRecord> _reverseIndex;
    private IndexData _index;
    public SearchEngine(){

    }

    public void addIndexData(IndexData index){
        _index = index;
        _reverseIndex = _index._reverseIndex;
    }

    public HashSet<String> SearchTerm(String term){
        //TODO Make this a simple Mongo query
        HashSet<String> res = new HashSet<String>(_reverseIndex.get(term).documentList.keySet());
        return  res;
    }

    private void queryWord(String word, int operator, SearchResult result){
        Hashtable<String, Integer> sts = Parser.getWordsStatsFromString(word);
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
        Queue<String> words = new LinkedList<String>();
        Queue<Integer> operators = new LinkedList<Integer>();


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
        Hashtable<String, Integer> searchVectorComponents = new Hashtable<String, Integer>();
        if (words.isEmpty() == false){
            searchVectorComponents.put(words.peek(), 1);
            queryWord(words.remove(), OP_NONE, result);
        }
        while (words.isEmpty() == false){
            int operator = OP_NONE;
            String qWord = words.remove();
            if (operators.isEmpty() == false){
                operator = operators.remove();
            }
            if (operator != OP_NOT){
                if (searchVectorComponents.get(qWord) == null){
                    searchVectorComponents.put(qWord, 1);
                } else {
                    searchVectorComponents.put(qWord, searchVectorComponents.get(qWord) + 1);
                }
            }
            queryWord(qWord, operator, result);
        }

        // Use vector search for sorting solutions
        String[] qStringKeys = new String[searchVectorComponents.keySet().size()];
        qStringKeys = searchVectorComponents.keySet().toArray(qStringKeys);

        double[] qVector = new double[searchVectorComponents.size()];

        for(int i=0; i<qVector.length; ++i){
            WordRecord term = _reverseIndex.get(qStringKeys[i]);
            if (term == null){
                term = new WordRecord(qStringKeys[i]);
            }
            qVector[i] = (searchVectorComponents.get(term.Name)/new Double(qVector.length)) * term.inverseDocumentFrequency;
        }

        result.SortedResults = new Result[result.result.size()];
        int k=0;
        for(String document : result.result) {
            double[] documentVector = new double[qVector.length];
            for (int i = 0; i < documentVector.length; ++i) {
                WordRecord term = _reverseIndex.get(qStringKeys[i]);
                if (term == null) {
                    qVector[i] = 0.0;
                    continue;
                }
                Double termFrequency;
                Integer occurences = term.documentList.get(document);
                if (occurences == null){
                    occurences = new Integer(0);
                }
                termFrequency = new Double(occurences) / new Double(_index.DocumentSize(document));//no or occ / size of document
                documentVector[i] = termFrequency * term.inverseDocumentFrequency;
            }
            Double score = DotProduct(qVector, documentVector);
            result.SortedResults[k] = new Result(document, score);
            k++;
        }

        Arrays.sort(result.SortedResults, new SortByScore());
        return result;
    }

    class SortByScore implements Comparator<Result>{
        public int compare(Result a, Result b){
            return (int)Math.signum(b.Score - a.Score); //Sort descending
        }
    }

    /// Calculates a*b/|a|*|b|
    private Double DotProduct(double[] a, double[] b){
        double product = 0.0;
        double size1 = 0.0;
        double size2 = 0.0;

        if (a.length != b.length){
            return null;
        }
        for(int i=0; i<a.length; ++i){
            product += a[i] * b[i];
            size1 += a[i] * a[i];
            size2 += b[i] * b[i];
        }
        return new Double(product/(Math.sqrt(size1) * Math.sqrt(size2)));
    }
}
