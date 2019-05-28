package mygalomorphae.prometheus;

import com.sun.deploy.util.StringUtils;

public class REPRule {
    public enum RRType {
        ALLOW,
        DISALLOW,
        OTHER
    }
    public String Path;
    public RRType Type;

    public REPRule(){
        Type = RRType.DISALLOW;
        Path = "";
    }

    public REPRule(String path){
        Type = RRType.DISALLOW;
        Path = path;
    }

    public REPRule(String type, String path){
        if (type.equalsIgnoreCase("Allow"))
            Type = RRType.ALLOW;
        else if (type.equalsIgnoreCase("Disallow"))
            Type = RRType.DISALLOW;
        else
            Type = RRType.OTHER;
        Path = path;
    }
}
