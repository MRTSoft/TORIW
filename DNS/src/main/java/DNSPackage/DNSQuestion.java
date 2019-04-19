package DNSPackage;

public class DNSQuestion {
    private byte[] mQname;
    private char mQType;
    private char mQClass;

    public byte[] serializeContent(){
        // mQType and mQClass are 4 bytes of data
        int size = mQname.length + 4;
        byte[] payload = new byte[size];
        System.arraycopy( mQname, 0, payload, 0, mQname.length );
        Utils.BitOperations.putChar( payload, size - 4, mQType );
        Utils.BitOperations.putChar( payload, size - 2, mQClass );

        return payload;
    }
}
