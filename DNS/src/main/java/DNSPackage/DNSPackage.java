package DNSPackage;

import java.util.ArrayList;

public class DNSPackage {
    private DNSHeader mHeader;
    private DNSQuestion mQuestion;
    private ArrayList<DNSResourceRecord> mAnswer;
    private ArrayList<DNSResourceRecord> mAuthority;
    private ArrayList<DNSResourceRecord> mAdditional;
}
