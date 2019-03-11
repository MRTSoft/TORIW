package indexer;


import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;

public class IndexGenerator {
    public Hashtable<String, Hashtable<String, Integer>> _index;
    public Hashtable<String, Hashtable<String, Integer>> _reverseIndex;
    public IndexGenerator(){
        _index = new Hashtable<>();
    }

    public void addDocument(String doc, String address){
        //TODO add class for Document
        DocParser dp = new DocParser();
        StringBuilder title, description, keywords, robots, TextPageContent;
        title = new StringBuilder();
        description = new StringBuilder();
        keywords = new StringBuilder();
        robots = new StringBuilder();
        TextPageContent = new StringBuilder();
        ArrayList<String> externalURLs = new ArrayList<>();

        dp.ParseDocumentFromFile(doc, address, title, description, keywords,
                robots, externalURLs, TextPageContent);
        Hashtable<String, Integer> words = DocParser.getWordsStatsFromString(TextPageContent.toString());
        _index.put(doc, words);
    }


    private void writeIndexFile(Serializable index, String dstFile) throws IOException {
        ObjectOutputStream dst = new ObjectOutputStream(new FileOutputStream(dstFile));
        dst.writeObject(index);
        dst.close();
    }

    public void generateIndexFiles(String outDir) throws IOException{
        Integer i = 0;
        Hashtable<String, String> map = new Hashtable<>();
        for(String key : _index.keySet()){
            i++;
            String entryName = outDir+"direct_"+i.toString()+".obj";
            writeIndexFile(_index.get(key), entryName);
            java.io.File entryFile = new File(entryName);
            map.put(key, entryFile.getAbsolutePath());
        }
        writeIndexFile(map, outDir + "map_direct.obj");
    }

    public void generateReverseIndex(){
        _reverseIndex = new Hashtable<>();
        for(String doc : _index.keySet()){
            Hashtable<String, Integer> entry = _index.get(doc);
            for(String word : entry.keySet()){
                if (_reverseIndex.get(word) == null){
                    Hashtable<String, Integer> newEntry = new Hashtable<>();
                    _reverseIndex.put(word, newEntry);
                }
                _reverseIndex.get(word).put(doc, entry.get(word));
            }
        }
    }

    private Serializable deserializeObject(String src) throws IOException{
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream((src)));
        Serializable obj = null;
        try {
             obj = (Serializable) ois.readObject();
        }
        catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }
        return  obj;
    }

    public void loadDirectIndexFromFolder(String folder) throws IOException {
        Hashtable<String, String> map;
        _index = new Hashtable<>();
        try {
            map = (Hashtable<String,String>)deserializeObject(folder+"map_direct.obj");
            for(String key : map.keySet()){
                Hashtable<String, Integer> value = (Hashtable<String, Integer>)deserializeObject(map.get(key));
                _index.put(key, value);
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
            _index = null;
        }
    }
}