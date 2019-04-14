package gombak;

import java.io.*;
import java.util.Objects;
import java.util.Properties;

public class Gombak {
    private static Properties _properties;
    private static Engine _engine = null;
    public static void main(String [] args){
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
        try {
            MongoAdapter.Initialize(_properties);
            _engine = new Engine();
        }
        catch (Exception ex){
            System.out.println("Error on loading MongoDB: ");
            ex.printStackTrace();
            return;
        }
        try {
            ParseCommandLineArgs(args);
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
        //System.out.println("=====================================");
        //System.out.println(_properties.getProperty("app.name"));
        //System.out.println(_properties.getProperty("app.version"));
    }

    private static void RunQuery(String query) throws IOException {
        if (_engine == null) {
            return;
        }
        Result[] result = _engine.Query(query);
        System.out.println(Integer.toString(result.length) + " results found:");
        for (int i = 0; i < result.length; ++i) {
            Result res = result[i];
            System.out.println("\t" + res.Score.toString() + " -- " + res.Record);
        }
    }
    private static void RunQuery(Boolean runForever) throws IOException{
        if (_engine == null){
            return;
        }
        do{
            String query;
            System.out.println("Query something: ");
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            query = console.readLine();
            Result[] result = _engine.Query(query);
            System.out.println(Integer.toString(result.length) + " results found:");
            for(int i=0; i<result.length; ++i) {
                Result res = result[i];
                System.out.println("\t" + res.Score.toString() + " -- " + res.Record);
            }
        }while (runForever);
    }

    private static void ParseCommandLineArgs(String [] args) throws IOException{
        if (args.length == 0){
            //Print usage
            PrintUsage();
            return;
        }
        if (args.length == 1){
            if (Objects.equals(args[0].toLowerCase().trim(), "-h")){
                PrintUsage();
                return;
            }
            if (Objects.equals(args[0].toLowerCase().trim(), "-i")){
                RunQuery(true);
                return;
            }
            if (Objects.equals(args[0].toLowerCase().trim(), "-q")){
                RunQuery(false);
                return;
            }
            if (Objects.equals(args[0].toLowerCase().trim(), "--clear-index")){
                _engine.ClearIndex();
                System.out.println("Index was deleted.");
                return;
            }
            System.out.println("I have no idea what " + args[0] + " should do -_-");
            PrintUsage();
            return;
        }
        if (args.length >= 2){
            if (
                    Objects.equals(args[0].toLowerCase().trim(), "--index-folder") ||
                    Objects.equals(args[0].toLowerCase().trim(), "-f")
            ) {
                for(int i=1; i<args.length; ++i) {
                    AddFolderToIndex(args[i], false);
                }
                System.out.println("Added to index");
                return;
            }

            if (
                    Objects.equals(args[0].toLowerCase().trim(), "--index-folder-recursive") ||
                    Objects.equals(args[0].toLowerCase().trim(), "-fr")
            ) {
                for(int i=1; i<args.length; ++i) {
                    AddFolderToIndex(args[i], true);
                }
                System.out.println("Added to index");
                return;
            }
            if (Objects.equals(args[0].toLowerCase().trim(), "-q")){
                StringBuilder query = new StringBuilder();
                for(int i=1; i<args.length; ++i){
                    query.append(args[i]);
                    if (i+1 < args.length){
                        query.append(" ");
                    }
                }
                RunQuery(query.toString());
                return;
            }
            System.out.println("I have no idea what " + args[0] + " " + args[1] + " should do -_-");
            PrintUsage();
            return;
        }

    }

    private static void PrintUsage(){
        StringBuilder usage = new StringBuilder();
        usage.append("\nUsage:\n");
        usage.append("-h             : Display this help\n");
        usage.append("-q [<query>]     : Run a single query \n");
        usage.append("-i             : Interactive mode (run multiple queries\n");
        usage.append("--clear-index  : Clear the mongoDB index\n");
        usage.append("--index-folder | -f <folder> : NON-RECURSIVE Add .txt files in the folder to the index\n");
        usage.append("--index-folder-recursive | -fr <folder> : RECURSIVE Add all .txt files in the folder to the index");
        System.out.println(usage.toString());
    }

    private static void AddFolderToIndex(String folder, Boolean parseRecursive) throws IOException{
        Boolean isVerbose = Boolean.valueOf((String)_properties.getOrDefault("app.debug", "false"));
        _engine.ParseFolder(folder, parseRecursive, isVerbose);
    }
}
