package DNSPackage;

public class DNSHeaderFlags {
    public static final char QR_QUERY               = 0 << 15;
    public static final char QR_RESPONSE            = 1 << 15;
    public static final char OPCODE_QUERY           = 0 << 11; /// Standard query
    public static final char OPCODE_IQUERY          = 1 << 11; /// Inverse query
    public static final char OPCODE_STATUS          = 2 << 11; /// Server status request
    public static final char AA_FLAG                = 1 << 10; /// Authoritative Answer
    public static final char TC_FLAG                = 1 <<  9; /// TrunCation
    public static final char RD_FLAG                = 1 <<  8; /// Recursion Desired
    public static final char RA_FLAG                = 1 <<  7; /// Recursion Available

    /// NOTE: RCODE uses the last 4 bits
    public static final char RCODE_NO_ERROR         = 0x0;
    public static final char RCODE_FORMAT_ERROR     = 0x1;
    public static final char RCODE_SERVER_FAILURE   = 0x2;
    public static final char RCODE_NAME_ERROR       = 0x3;
    public static final char RCODE_NOT_IMPLEMENTED  = 0x4;
    public static final char RCODE_REFUSED          = 0x5;

    public static final char STANDARD_QUERY_REQUEST = QR_QUERY | OPCODE_QUERY;
}
