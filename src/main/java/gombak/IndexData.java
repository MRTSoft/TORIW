package gombak;

import java.io.*;
import java.util.Hashtable;

public class IndexData{
    public Hashtable<String, Hashtable<String, Integer>> _index;
    public Hashtable<String, WordRecord> _reverseIndex;
    public IndexData(){
        _index = new Hashtable<String, Hashtable<String, Integer>>();
    }

    private void writeIndexFile(Serializable index, String dstFile) throws IOException {
        //TODO Use MongoDB if available
        ObjectOutputStream dst = new ObjectOutputStream(new FileOutputStream(dstFile));
        dst.writeObject(index);
        dst.close();
    }

    public void generateIndexFiles(String outDir) throws IOException{
        Integer i = 0;
        //TODO Actually check that the folder exist and it's a valid path!
        Hashtable<String, String> map = new Hashtable<String, String>();
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
        //TODO Make this parallel
        //TODO Mongo this
        _reverseIndex = new Hashtable<String, WordRecord>();
        for(String doc : _index.keySet()){
            Hashtable<String, Integer> entry = _index.get(doc);
            for(String word : entry.keySet()){
                if (_reverseIndex.get(word) == null){
                    _reverseIndex.put(word, new WordRecord(word));
                }
                _reverseIndex.get(word).documentList.put(doc, entry.get(word));
            }
        }
        //Calculate df and idf

        //DocumentFreq
        Double totalDocs = new Double(_index.size());
        for(String word : _reverseIndex.keySet()){
            _reverseIndex.get(word).inverseDocumentFrequency = Math.log10(totalDocs/(1.0+_reverseIndex.get(word).documentList.size())); // how important each term is
        }
    }

    @Deprecated
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

    @Deprecated
    public void loadDirectIndexFromFolder(String folder) throws IOException {
        Hashtable<String, String> map;
        _index = new Hashtable<String, Hashtable<String, Integer>>();
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

    public void AddDirectEntry(String key, Hashtable<String,Integer> entry){
        //TODO Mongo this
        _index.put(key, entry);
    }

    public Integer DocumentSize(String document){
        //TODO store total numbers of terms in the direct index
        Hashtable<String, Integer> docStats = _index.get(document);
        if (docStats == null){
            return  new Integer(1); //Hack so we don't deal with 0 division
        }
        Integer sum = new Integer(0);
        for(String key : docStats.keySet()){
            sum += docStats.get(key);
        }
        return  sum;
    }
}
