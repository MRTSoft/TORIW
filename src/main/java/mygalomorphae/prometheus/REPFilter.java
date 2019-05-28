package mygalomorphae.prometheus;

import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class REPFilter {
    public static String UserAgent = "RIWEB_CRAWLER";
    private static final String DefaultUserAgent = "*";
    private String host;
    private Hashtable<String, List<REPRule>> rules;

    public REPFilter(){
        host = "";
        rules = new Hashtable<>();
    }

    public Boolean CanAccess(URL target){
        if (rules.isEmpty()){
            return Boolean.TRUE;
        }
        List<REPRule> aRules = rules.get(rules.containsKey(UserAgent)?UserAgent:DefaultUserAgent);
        if (aRules == null){
            return Boolean.TRUE;
        }
        //Parse only standard DISALLOW rules
        for(REPRule r : aRules){
            if ((r.Type == REPRule.RRType.DISALLOW) && (target.getPath().startsWith(r.Path)) && (!r.Path.isEmpty())) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    public void SetRules(String robots_txt){
        List<REPRule> extractedRules = null;
        String agent = null;
        String key, value;
        for(String line : robots_txt.split("\n")){
            int firstDoubleColumn = line.indexOf(":");
            if (firstDoubleColumn > 0){
                key = line.substring(0, firstDoubleColumn).trim();
                value = line.substring(firstDoubleColumn+1).trim();
                if (key.equalsIgnoreCase("User-agent")){
                    if (extractedRules != null && agent != null) {
                        rules.put(agent, extractedRules);
                    }
                    agent = value;
                    extractedRules = new ArrayList<>();
                }
                else {
                    extractedRules.add(new REPRule(key, value));
                }
            }
        }
        if (extractedRules!=null && agent!=null && !extractedRules.isEmpty()){
            rules.put(agent, extractedRules);
        }
    }
}
