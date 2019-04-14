package gombak;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.Filters;
import com.mongodb.internal.connection.CommandMessage;
import org.bson.Document;
import org.bson.*;

import javax.print.Doc;
import java.io.FileInputStream;
import java.util.*;

import static com.mongodb.client.model.Updates.combine;

public class MongoAdapter {
    private static String _host = null;
    private static int _port = 0;
    private static String _db_name = null;

    private MongoClient _client;
    private MongoDatabase _database;

    public MongoAdapter() throws Exception{
        if (_host == null){
            throw new Exception("Initialize() first!");
        }
        _client = MongoClients.create(MongoClientSettings.builder().applyToClusterSettings(builder ->
                    builder.hosts(Arrays.asList(new ServerAddress(_host, _port)))
                ).build());
        _database = _client.getDatabase(_db_name);
    }

    ///Set username password port and other stuffs to mongo server
    public static void Initialize(Properties config) throws Exception{
        if (config.containsKey("mongo.server")){
            _host = config.getProperty("mongo.server");
        }
        else {
            throw new Exception("No server!");
        }
        if (config.containsKey("mongo.port")){
            _port = Integer.parseInt(config.getProperty("mongo.port"));
        }
        else {
            throw new Exception("No port specified!");
        }
        if (config.containsKey("mongo.database")){
            _db_name = config.getProperty("mongo.database");
        }
        else {
            throw new Exception("No default collection!");
        }
        MongoAdapter adapter = new MongoAdapter();
        adapter._database.runCommand(new Document().append("ping", 1));
    }

    //Reverse document:
    /*
    {
        "term": "word",
        "idf": <double>,
        "documents": [
            { "name": doc1, "count": c2},....
        ]
    }

     */

    public HashSet<String> SearchTerm(String term) {
        MongoCollection<Document> inverseIndex = _database.getCollection("inverse");

        List<Document> rawResult = inverseIndex.find(Filters.eq("term", term)).into(new ArrayList<Document>());
        HashSet<String> results = new HashSet<String>();
        //Extract only document names from this
        for(Document entry : rawResult){
            List<Document> docs = (List<Document>)entry.get("documents");
            for(Document doc:docs){
                results.add(doc.getString("name"));
            }
        }
        return results;
    }

    public WordRecord getReverseIndexEntry(String term) {
        MongoCollection<Document> inverseIndex = _database.getCollection("inverse");
        List<Document> rawResult = inverseIndex.find(Filters.eq("term", term)).into(new ArrayList<Document>());
        WordRecord record = null;
        if (rawResult.size() != 1){
            return null;
        }
        Document result = rawResult.get(0);
        record = new WordRecord(result.getString("term"));
        record.inverseDocumentFrequency = result.getDouble("idf");
        record.documentList = new Hashtable<String, Integer>();
        List<Document> docs = (List<Document>)result.get("documents");
        for(Document doc : docs){
            record.documentList.put(doc.getString("name"), doc.getInteger("count"));
        }

        return record;
    }


    /*
    Document entry is:
    {
    "document" : <string>,
    "terms" : [
        {"term" : <string>, "count": <integer>},....
    ]
    }
     */
    /// Return nr of terms in the document
    public Integer documentSize(String docName) {
        MongoCollection<Document> inverseIndex = _database.getCollection("direct");
        List<Document> rawResult = inverseIndex.find(Filters.eq("document", docName)).into(new ArrayList<Document>());
        if (rawResult.size() != 1){
            return 0;
        }
        List<Document> terms = (List<Document>) rawResult.get(0).get("terms");
        return terms.size();
    }

    public Hashtable<String, Integer> getDocument(String docName) {
        MongoCollection<Document> inverseIndex = _database.getCollection("direct");
        List<Document> rawResult = inverseIndex.find(Filters.eq("document", docName)).into(new ArrayList<Document>());
        if (rawResult.size() != 1){
            return null;
        }
        List<Document> terms = (List<Document>) rawResult.get(0).get("terms");
        Hashtable<String,Integer> result = new Hashtable<String, Integer>();
        for(Document term: terms){
            result.put(term.getString("term"), term.getInteger("count"));
        }
        return result;
    }

    public void UpdateReverseIndexEntry(String term, String docName, Integer count) {
        WordRecord oldRecord = getReverseIndexEntry(term);
        Boolean foundRecord = true;
        if (oldRecord == null){
            oldRecord = new WordRecord(term);
            foundRecord = false;
        }
        oldRecord.documentList.put(docName, count);
        //Construct the document
        Document newRecord = oldRecord.getBSONDocument();

        // replace the record
        MongoCollection collection = _database.getCollection("inverse");
        if (foundRecord){
            collection.replaceOne(Filters.eq("term", term), newRecord);
        }else {
            collection.insertOne(newRecord);
        }
    }

    public void setIDF(String term, double idf) {
        MongoCollection collection = _database.getCollection("inverse");
        collection.updateOne(Filters.eq("term", term), combine(Updates.set("idf", idf)));
    }

    public double getNoOfDocumentsForWord(String term) {
        MongoCollection collection = _database.getCollection("direct");
        List<Document> rawResult = (List<Document>) collection.find(
                new Document("terms", new Document("term", term))
        ).into(new ArrayList<Document>());
        return rawResult.size();
    }

    public void addDirectIndexEntry(String docName, Hashtable<String, Integer> terms) {
        Document entry = new Document("document",docName);
        List<Document> termData = new ArrayList<Document>();
        for(String key : terms.keySet()){
            termData.add(new Document("term", key).append("count", terms.get(key)));
        }
        entry.append("terms", termData);
        MongoCollection collection = _database.getCollection("direct");
        if (collection.find(Filters.eq("document",docName)).into(new ArrayList<Document>()).size() > 0){
            collection.replaceOne(Filters.eq("document",docName), entry);
        }else {
            collection.insertOne(entry);
        }
    }

    public Double getGrandTotalDocuments() {
        MongoCollection collection = _database.getCollection("direct");
        return new Double(collection.countDocuments());
    }

    public MongoCursor<Document> getAllDocumentCursor(){
        MongoCollection collection = _database.getCollection("direct");
        return collection.find().iterator();
    }


    public MongoCursor<Document> getAllTermNames() {
        MongoCollection collection = _database.getCollection("inverse");
        return collection.find().iterator();
    }

    ///Clear all data in the "direct" and "inverse" collections
    public void ClearIndex(){
        MongoCollection collection = _database.getCollection("inverse");
        collection.deleteMany(new Document()); //Truncate the collection
        collection = _database.getCollection("direct");
        collection.deleteMany(new Document()); //Truncate the collection
    }
}
