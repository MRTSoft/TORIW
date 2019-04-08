package gombak;

import java.util.Hashtable;

public class WordRecord {
    public Hashtable<String, Integer> documentList;
    //public Double termFrequency; -- this is per document and we will calculate it on the fly
    String Name;
    public Double inverseDocumentFrequency;

    public WordRecord(String name){
        documentList = new Hashtable<String, Integer>();
        Name = new String(name);
        inverseDocumentFrequency = 0.0;
    }
}
