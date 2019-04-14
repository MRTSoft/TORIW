package gombak;

import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.util.Hashtable;
import java.util.Queue;

public class IndexData{
    //public Hashtable<String, Hashtable<String, Integer>> _index;
    //public Hashtable<String, WordRecord> _reverseIndex;
    private MongoAdapter _adapter;
    public IndexData() throws Exception{
        _adapter = new MongoAdapter();
    }


    public void generateReverseIndex(Queue<String> insertedDocuments){
        //MongoCursor<Document> cursor = _adapter.getAllDocumentCursor();
        //We should only get the new documents!
        while (insertedDocuments.isEmpty() == false){
            String docName = insertedDocuments.remove();
            Hashtable<String, Integer> doc = _adapter.getDocument(docName);
            for(String term : doc.keySet()){
                _adapter.UpdateReverseIndexEntry(term, docName, doc.get(term));
            }
        }

        //Calculate idf
        //DocumentFreq
        // We need to update this for the whole collection :(
        Double totalDocs = _adapter.getGrandTotalDocuments();
        MongoCursor<Document> allTerms = _adapter.getAllTermNames();
        while (allTerms.hasNext()) {
            Document term = allTerms.next();
            double idf = Math.log10(totalDocs / (1.0 + _adapter.getNoOfDocumentsForWord(term.getString("term"))));
            _adapter.setIDF(term.getString("term"), idf);
        }
        allTerms.close();
    }

    public void AddDirectEntry(String key, Hashtable<String,Integer> entry){
        _adapter.addDirectIndexEntry(key, entry);
    }

    /// Clear all data in the index
    public void clear(){
        _adapter.ClearIndex();
    }
}
