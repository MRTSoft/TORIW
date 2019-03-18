package finder;

import java.util.HashSet;
import java.util.Set;

public class SearchResult {
    public HashSet<String> result;
    private SearchEngine _engine;

    public SearchResult(SearchEngine engine){
        _engine = engine;
        result = new HashSet<>();
    }
    public SearchResult SearchTerm(String term){
        result = _engine.SearchTerm(term);
        return this;
    }

    public SearchResult OrSearch(String term){
        HashSet<String> r1 = _engine.SearchTerm(term);
        HashSet<String> r2 = result;
        if (r1.size() > r2.size()) {
            result = r1;
            r1 = r2;
        }

        for(String doc : r1){
            result.add(doc);
        }
        return this;
    }

    public SearchResult AndSearch(String term){
        Set<String> r1 = result;
        Set<String> r2 = _engine.SearchTerm(term);
        Set<String> aux;
        if (r1.size()>r2.size()){
            aux = r1;
            r1 = r2;
            r2 = aux;
        }
        result = new HashSet<>();
        for(String doc : r1){
            if (r2.contains(doc)){
                result.add(doc);
            }
        }
        return this;
    }

    public  SearchResult NotSearch(String term){
        HashSet<String> r1 = result;
        HashSet<String> r2 = _engine.SearchTerm(term);
        //We need all stuff in r1 that are not in r2
        if (r1.size() < r2.size()){
            //delete everything from r1 that we find in r2
            for(String doc : r1){
                if (r2.contains(doc)){
                    r1.remove(doc);
                }
            }
        }else {
            //delete everything from r2 that we find in r1
            for(String doc : r2){
                if (r1.contains(doc)){
                    r1.remove(doc);
                }
            }
        }
        result = r1;
        return this;
    }
}
