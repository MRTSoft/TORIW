package gombak;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class WordRecord {
    public Hashtable<String, Integer> documentList;
    //public Double termFrequency; -- this is per document and we will calculate it on the fly
    public String Name;
    public Double inverseDocumentFrequency;

    public WordRecord(String name){
        documentList = new Hashtable<String, Integer>();
        Name = new String(name);
        inverseDocumentFrequency = 0.0;
    }

    public Document getBSONDocument() {
        Document doc = new Document();
        doc.append("term", Name).append("idf", inverseDocumentFrequency);
        List<Document> docs = new ArrayList<Document>();
        for(String docName: documentList.keySet()){
            docs.add(new Document()
                    .append("name", docName)
                    .append("count",documentList.get(docName)));
        }
        doc.append("documents", docs);
        return doc;
    }
}
