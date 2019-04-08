package gombak;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;

public class Gombak {
    public static void main(String [] args){
        //TODO menu and shit
        Engine eng = new Engine();
        try {
            eng.ParseFolder("data/text", true);
            String query;
            System.out.println("Query something: ");
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            query = console.readLine();
            Result[] result = eng.Query(query);
            System.out.println(Integer.toString(result.length) + " results found:");
            for(int i=0; i<result.length; ++i) {
                Result res = result[i];
                System.out.println("\t" + res.Score.toString() + " -- " + res.Record);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }
}
