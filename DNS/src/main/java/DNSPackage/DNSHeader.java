package DNSPackage;


import Utils.BitOperations;

public class DNSHeader {

    // byte   ->  8 bits signed
    // short  -> 16 bits signed
    // char   -> 16 bits UNSIGNED!
    // int    -> 32 bits signed

    public final static int HEADER_SIZE = 12;

    private char mID;       /// ID of the request
    private char mFlags;    /// Flags for the request. @see #DNSHeaderFlags
    private char mQDCount;  /// Number of entries in the question section
    private char mANCount;  /// Number or RR in the answer section
    private char mNSCount;  /// Number of name server resource records in the authority records section
    private char mARCount;  /// Number of resource records in the additional records section

    public char getID() {
        return mID;
    }

    public void setID( char id ) {
        this.mID = id;
    }

    public char getFlags() {
        return mFlags;
    }

    public void setFlags( char flags ) {
        this.mFlags = flags;
    }

    public char getQDCount() {
        return mQDCount;
    }

    public void setQDCount( char QDCount ) {
        this.mQDCount = QDCount;
    }

    public char getANCount() {
        return mANCount;
    }

    public void setANCount( char ANCount ) {
        this.mANCount = ANCount;
    }

    public char getNSCount() {
        return mNSCount;
    }

    public void setNSCount( char NSCount ) {
        this.mNSCount = NSCount;
    }

    public char getARCount() {
        return mARCount;
    }

    public void setARCount( char ARCount ) {
        this.mARCount = ARCount;
    }

    public  DNSHeader(){
        mID = 0;
        mFlags = DNSHeaderFlags.STANDARD_QUERY_REQUEST;
        mQDCount = 0;
        mANCount = 0;
        mNSCount = 0;
        mARCount = 0;

    }

    public byte[] getHeader() {

        byte[] header = new byte[HEADER_SIZE];

        BitOperations.putChar(header, 0<<4, mID);
        BitOperations.putChar(header, 1<<4, mFlags);
        BitOperations.putChar(header, 2<<4, mQDCount);
        BitOperations.putChar(header, 3<<4, mANCount);
        BitOperations.putChar(header, 4<<4, mNSCount);
        BitOperations.putChar(header, 5<<4, mARCount);
        return  header;
    }

    public void setHeader(byte[] value) {

        System.arraycopy( value, 0, value, 0, HEADER_SIZE );

        mID      = BitOperations.extractShort( value, 0 << 1 );
        mFlags   = BitOperations.extractShort( value, 1 << 1 );
        mQDCount = BitOperations.extractShort( value, 2 << 1 );
        mANCount = BitOperations.extractShort( value, 3 << 1 );
        mNSCount = BitOperations.extractShort( value, 4 << 1 );
        mARCount = BitOperations.extractShort( value, 5 << 1 );
    }

    public byte[] serializeContent(){
        return getHeader();
    }

}
