package gombak;

import java.io.*;
import java.util.Properties;

public class Gombak {
    private static Properties _properties;
    public static void main(String [] args){
        //TODO menu and shit
        LoadConfig("data/gombak.conf");
        if (_properties.containsKey("app.logs")){
            try {
                FileOutputStream stderr = new FileOutputStream(new File(_properties.getProperty("app.logs")));
                PrintStream ps = new PrintStream(stderr);
                System.setErr(ps);
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        }
        Engine eng = null;
        try {
            MongoAdapter.Initialize(_properties);
            eng = new Engine();
        }
        catch (Exception ex){
            System.out.println("Error on loading MongoDB: ");
            ex.printStackTrace();
            return;
        }
        try {
            String dataFolder = _properties.getProperty("app.data");
            eng.ParseFolder(dataFolder, true);
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

    private static void LoadConfig(String configFile){
        _properties = new Properties();
        InputStream is = null;
        try {
            is = new FileInputStream(configFile);
        } catch (FileNotFoundException ex) {
            System.out.println("Config file not found!");
            ex.printStackTrace();
        }
        try {
            _properties.load(is);
        } catch (IOException ex) {
            System.out.println("Failed to load config file! (BAD format)");
            ex.printStackTrace();
        }
        System.out.println("=====================================");
        System.out.println(_properties.getProperty("app.name"));
        System.out.println(_properties.getProperty("app.version"));
    }

}