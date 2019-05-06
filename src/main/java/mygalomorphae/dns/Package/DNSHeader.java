package mygalomorphae.dns.Package;

import mygalomorphae.dns.Utils.BitOperations;

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

    public boolean isError() {
        return ((mFlags & 0x000F) != DNSHeaderFlags.RCODE_NO_ERROR);
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

    public DNSHeader(byte[] udpData){
        // We extract the first 12 bytes from the array and set the header accordingly
        mID = 0;
        mFlags = DNSHeaderFlags.STANDARD_QUERY_REQUEST;
        mQDCount = 0;
        mANCount = 0;
        mNSCount = 0;
        mARCount = 0;
        setHeader(udpData);
    }

    public byte[] getHeader() {

        byte[] header = new byte[HEADER_SIZE];

        BitOperations.put16Bits(header,  0, mID);
        BitOperations.put16Bits(header,  2, mFlags);
        BitOperations.put16Bits(header,  4, mQDCount);
        BitOperations.put16Bits(header,  6, mANCount);
        BitOperations.put16Bits(header,  8, mNSCount);
        BitOperations.put16Bits(header, 10, mARCount);
        return  header;
    }

    public void setHeader(byte[] value) {
        mID      = BitOperations.extract16Bits( value, 0 );
        mFlags   = BitOperations.extract16Bits( value, 2 );
        mQDCount = BitOperations.extract16Bits( value, 4 );
        mANCount = BitOperations.extract16Bits( value, 6 );
        mNSCount = BitOperations.extract16Bits( value, 8 );
        mARCount = BitOperations.extract16Bits( value, 10);
    }

    public byte[] serializeContent(){
        return getHeader();
    }
}
