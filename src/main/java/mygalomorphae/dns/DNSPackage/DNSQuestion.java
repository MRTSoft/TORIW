package DNSPackage;

import Utils.LabelUtil;

public class DNSQuestion {
    private byte[] mQName;
    private char mQType;
    private char mQClass;

    public byte[] serializeContent(){
        // mQType and mQClass are 4 bytes of data
        int size = mQName.length + 4;
        byte[] payload = new byte[size];
        System.arraycopy( mQName, 0, payload, 0, mQName.length );
        Utils.BitOperations.putChar( payload, size - 4, mQType );
        Utils.BitOperations.putChar( payload, size - 2, mQClass );

        return payload;
    }

    public DNSQuestion (String query){
        mQType = 1;
        mQClass = 1;
        mQName = LabelUtil.SerializeLabel( query );
    }
}
