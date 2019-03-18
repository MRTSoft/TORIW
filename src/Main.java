
import indexer.DocParser;
import indexer.IndexGenerator;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        DocParser parser = new DocParser();
        try {
            parser.ParseFolder("data/text/index/", "data/text/site_dump/", true);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        IndexGenerator ig = new IndexGenerator();

        try {
            ig.loadDirectIndexFromFolder("./data/text/index/");
            System.out.println("Loaded the index with success!");
            ig.generateReverseIndex();
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        System.out.println("Direct index is:");
        for (String key : ig._index.keySet()) {
            System.out.println(key);
            for (String subkey : ig._index.get(key).keySet()) {
                System.out.println("\t" + subkey + ": " + ig._index.get(key).get(subkey));
            }
        }
        System.out.println("Reverse index is:");
        for (String word : ig._reverseIndex.keySet()) {
            StringBuilder sb = new StringBuilder();
            sb.append(word).append(" -->");
            for (String doc : ig._reverseIndex.get(word).keySet()) {
                sb.append(" ").append(doc);
                sb.append(":").append(ig._reverseIndex.get(word).get(doc).toString());
            }
            System.out.println(sb.toString());
        }
    }
}
