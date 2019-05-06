package mygalomorphae.dns.Package;

import mygalomorphae.dns.Utils.BitOperations;

public class DNSPackage {
    private DNSHeader mHeader;
    private DNSQuestion mQuestion;
    private DNSQuestion mAnswer;
    public byte[] IPv4Address;
    public int TimeToLive = 0;


    public DNSPackage(){
        mAnswer = null;
        IPv4Address = null;
    }

    public DNSPackage(byte [] data) throws Exception{
        mAnswer = null;
        IPv4Address = null;
        mHeader = new DNSHeader(data);
        if (!mHeader.isError()){
            processResponse(data);
        }
    }


    private static char mPackageID = 0xAB;

    public static DNSPackage CreateSimpleQuery(String query){
        return CreateSimpleQuery(query, false);
    }

    public static  DNSPackage CreateSimpleQuery(String query, boolean recursionEnabled){
        DNSPackage request = new DNSPackage();
        request.mHeader = new DNSHeader();
        request.mHeader.setID( mPackageID );
        mPackageID++;
        request.mHeader.setQDCount( (char)1 );
        request.mHeader.setFlags((char)(request.mHeader.getFlags() | DNSHeaderFlags.RD_FLAG));
        request.mQuestion = new DNSQuestion( query );
        return request;
    }

    public String getQuery(){
        return mQuestion.getQuery();
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

    private void processResponse(byte [] data) throws Exception{
        int rc = mHeader.getANCount();
        mQuestion = new DNSQuestion(data, DNSHeader.HEADER_SIZE);
        for(int replyIndex = 0; replyIndex < rc; replyIndex++) {
            mAnswer = new DNSQuestion(data, mQuestion.EndOffset);
            int offset = mAnswer.EndOffset;
            TimeToLive = (BitOperations.extract16Bits(data, offset) << 16) | (BitOperations.extract16Bits(data, offset + 2));
            offset += 4;
            if (mAnswer.isIpAddress()) {
                //Get the first address we can find
                int rdLen = BitOperations.extract16Bits(data, offset) & 0xFF;
                if (rdLen == 4) {
                    //We have an IP address
                    offset += 2;
                    IPv4Address = new byte[4];
                    for (int i = 0; i < 4; ++i) {
                        IPv4Address[i] = data[offset + i];
                    }
                } else {
                    throw new Exception("Not an IP address!");
                }
            }
        }
    }
}
