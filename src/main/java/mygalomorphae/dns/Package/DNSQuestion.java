package mygalomorphae.dns.Package;


import mygalomorphae.dns.Utils.BitOperations;
import mygalomorphae.dns.Utils.LabelUtil;

public class DNSQuestion {
    private byte[] mQName;
    private char mQType;
    private char mQClass;
    private String mQuery;

    public String getQuery(){
        return mQuery;
    }

    public Integer EndOffset;

    public byte[] serializeContent(){
        // mQType and mQClass are 4 bytes of data
        int size = mQName.length + 4;
        byte[] payload = new byte[size];
        System.arraycopy( mQName, 0, payload, 0, mQName.length );
        BitOperations.put16Bits( payload, size - 4, mQType );
        BitOperations.put16Bits( payload, size - 2, mQClass );

        return payload;
    }

    public DNSQuestion (String query){
        mQType = 1;
        mQClass = 1;
        mQuery = query;
        mQName = LabelUtil.SerializeLabel( query );
        EndOffset = null;
    }

    public DNSQuestion(byte[] data, int offset){
        int [] fo = new int[1]; //Ugly hack for mutable ints
        mQuery = LabelUtil.DeserializeLabel(data, offset, fo);
        EndOffset = fo[0];
        //TODO: mQName = System.arraycopy();
        mQType = BitOperations.extract16Bits(data, EndOffset);
        EndOffset += 2;
        mQClass = BitOperations.extract16Bits(data, EndOffset);
        EndOffset += 2;
    }

    public boolean isIpAddress(){
        return ((mQClass == 1) && (mQType == 1));
    }
}
