package mygalomorphae.dns.Package;

import java.util.ArrayList;

public class DNSPackage {
    private DNSHeader mHeader;
    private DNSQuestion mQuestion;
    private ArrayList<DNSResourceRecord> mAnswer;
    private ArrayList<DNSResourceRecord> mAuthority;
    private ArrayList<DNSResourceRecord> mAdditional;

    public DNSPackage(){
        mAnswer = new ArrayList<DNSResourceRecord>(  );
        mAuthority = new ArrayList<DNSResourceRecord>(  );
        mAdditional = new ArrayList<DNSResourceRecord>(  );
    }

    private static char mPackageID = 1;

    public static DNSPackage CreateSimpleQuery(String query){
        DNSPackage request = new DNSPackage();
        request.mHeader = new DNSHeader();
        request.mHeader.setID( mPackageID );
        mPackageID++;
        request.mHeader.setQDCount( (char)1 );
        request.mQuestion = new DNSQuestion( query );
        return request;
    }

    public byte[] serializeContent(){
        //1. compute length
        int len = 0;

        byte[] header = mHeader.serializeContent();
        byte[] question = mQuestion.serializeContent();
        len = header.length + question.length;
        byte[] result = new byte[len];

        System.arraycopy( header, 0, result, 0, header.length );
        System.arraycopy( question, 0, result, header.length, question.length );

        return result;
    }
}
