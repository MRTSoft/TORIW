package gombak;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Queue;

public class Parser {



    /// Extract from the specified file the following stuff
    public void ParseDocumentFromFile(String file, // in
                                      String absPath, // in
                                      StringBuilder title, // out
                                      StringBuilder description, // out
                                      StringBuilder keywords, // out
                                      StringBuilder robots, // out
                                      ArrayList<String> externalURLs, // out
                                      StringBuilder TextPageContent) {
        File input = new File(file);
        Document doc = null;
        try {
            doc = Jsoup.parse(input, null, absPath);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Elements titleElems = doc.getElementsByTag("title");
        for (Element titleElem : titleElems) {
            title.append(titleElem.text());
        }

        Elements metadatas = doc.getElementsByTag("meta");
        for (Element meta : metadatas) {
            if (meta.hasAttr("name")) {
                if (meta.attr("name").equalsIgnoreCase("robots")) {
                    robots.append(meta.attr("content"));
                }
                if (meta.attr("name").equalsIgnoreCase("description")) {
                    description.append(meta.attr("content"));
                }
                if (meta.attr("name").equalsIgnoreCase("keywords")) {
                    keywords.append(meta.attr("content"));
                }
            }
        }

        Elements urls = doc.getElementsByTag("a");
        for (Element url : urls) {
            String parsed = ParseUrl(url);
            if (parsed != null) {
                externalURLs.add(parsed);
            }
        }

        TextPageContent.append(doc.select("body").first().text());
    }

    private String ParseUrl(Element url) {
        StringBuilder content = new StringBuilder();
        String absHref = url.attr("abs:href");
        if (url.hasAttr("href")) {
            int pozOfDiez = absHref.indexOf('#');
            if (pozOfDiez >= 0) {
                content.append(absHref.substring(0, pozOfDiez));
            } else {
                content.append(absHref);
            }

        } else {
            return null;
        }
        if (content.length() == 0) {
            return null;
        }
        return content.toString();
    }

    private static Hashtable<String, Integer> _exceptionWords = new Hashtable<String, Integer>();
    private static Hashtable<String, Integer> _stopWords = new Hashtable<String, Integer>();

    public static Boolean IsExceptionWord(String word) {
        if (_exceptionWords.isEmpty()) {
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(
                        "./data/EXCEPTIONS"));
                String line = reader.readLine();
                while (line != null) {
                    //System.out.println(line);
                    // read next line
                    _exceptionWords.put(line, 1);
                    line = reader.readLine();
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("FATAL: Exception list not found!");
            }
        }
        Integer value = _exceptionWords.get(word);
        return (value != null);
    }

    public static Boolean IsStopWord(String word) {
        if (_stopWords.isEmpty()) {
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(
                        "./data/STOPWORDS"));
                String line = reader.readLine();
                while (line != null) {
                    // read next line
                    _stopWords.put(line, 1);
                    line = reader.readLine();
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Integer value = _stopWords.get(word);
        return (value != null);
    }

    private static void  processWord(String word, Hashtable<String, Integer> stats){
        if (word.isEmpty()) {
            return;
        }
        Integer value = stats.get(word);

        if (value != null) {
            stats.put(word, value + 1);
        } else {
            //if it's an exception and not a stop word
            if (IsExceptionWord(word)) {
                //Keep the word
                stats.put(word, 1);
            } else {
                if (IsStopWord(word)) {
                    //Skip the word
                }
                else {
                    //Dictionary word
                    //Generate short form of the word
                    Stemmer stm = new Stemmer();
                    stm.add(word.toCharArray(), word.length());
                    stm.stem();
                    String key = stm.toString();
                    //Keep the word
                    stats.put(key, 1);
                }
            }
        }
    }

    public static  Hashtable<String, Integer> getWordsStatsFromString(String content){
        Hashtable<String, Integer> stats = null;
        StringBuilder currentWord = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new ByteArrayInputStream(content.getBytes()),
                            Charset.forName("UTF-8")));
            stats = getWordsStatsFromStream(reader);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to open file");
        }
        processWord(currentWord.toString(), stats);
        return stats;
    }

    public static Hashtable<String, Integer> getWordsStatsFromStream(BufferedReader reader) throws IOException {
        Hashtable<String, Integer> stats = new Hashtable<String, Integer>();
        StringBuilder currentWord = new StringBuilder();
        int c;
        String separators = " ,.;':\"[] {}()-=+!@#$%^&*`~<>/?\\|\t\n\r";

        while ((c = reader.read()) != -1) {
            char character = (char) c;
            // Process the char
            if (separators.indexOf(character) == -1) {
                currentWord.append(character);
            } else {
                // Process the current word
                String key = currentWord.toString();
                processWord(key, stats);
                currentWord = new StringBuilder();
            }
        }
        reader.close();
        processWord(currentWord.toString(), stats);
        return stats;
    }
}

