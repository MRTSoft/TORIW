package gombak;

import com.mongodb.client.MongoCursor;
import org.bson.Document;

import javax.print.Doc;
import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class IndexData{
    //public Hashtable<String, Hashtable<String, Integer>> _index;
    //public Hashtable<String, WordRecord> _reverseIndex;
    private MongoAdapter _adapter;
    public IndexData() throws Exception{
        _adapter = new MongoAdapter();
    }


    public void generateReverseIndex(){
        //TODO Make this parallel

        MongoCursor<Document> cursor = _adapter.getAllDocumentCursor();
        while (cursor.hasNext()){
            Document cEntry = cursor.next();
            List<Document> terms = (ArrayList<Document>)cEntry.get("terms");
            for(Document term : terms){
                _adapter.incrementReverseIndexEntry(term.getString("term"), cEntry.getString("document"));
            }
        }
        cursor.close();
        //Calculate df and idf

        //DocumentFreq
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

}
