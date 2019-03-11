
import indexer.IndexGenerator;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        IndexGenerator ig = new IndexGenerator();
        IndexGenerator ig2 = new IndexGenerator();
        ig.addDocument("./data/text/fox.html", "www.example.com");
        ig.addDocument("./data/text/bat.html", "www.example.com");
        try {
            ig.generateIndexFiles("./data/text/index/");
            System.out.println("Created the index with success!");
            ig2.loadDirectIndexFromFolder("./data/text/index/");
            System.out.println("Loaded the index with success!");
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

        for(String key : ig2._index.keySet()){
            System.out.println(key);
            for(String subkey : ig2._index.get(key).keySet()){
                System.out.println("\t"+subkey+": "+ig2._index.get(key).get(subkey));
            }
        }
    }
}
