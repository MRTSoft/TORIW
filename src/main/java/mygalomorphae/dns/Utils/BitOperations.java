package mygalomorphae.dns.Utils;

public final class BitOperations {
    private BitOperations() {}


    /** Extract bits from a byte array into a char
     * @param src               Array with data
     * @param offset            We will start extracting bits starting with the offset byte
     *
     * @return A char representing the value. Please note that the bit at offset will become the MSB
    **/
    public static char extract16Bits(byte[] src, int offset ) {
        char data = 0x0000;
        if ( src == null ) {
            throw new IllegalArgumentException( "You cannot extract from a void source\n" );
        }
        if ( ( src.length - 1 ) <= offset ) {
            throw new IllegalArgumentException( "Invalid offset" );
        }

        data = ( char )( ( src[offset] << 8 ) | ( src[offset + 1] & 0xFF ) );

        return data;
    }

    /**
     * Put a char (16 bits) at the specified destination
     * @param dst       Destination for the value parameter
     * @param offset    Offset (in bytes)
     * @param value     Value to put in the dst
     */
    public static void put16Bits(byte[] dst, int offset, char value ) {
        if ( dst == null ) {
            throw new IllegalArgumentException( "You cannot extract from a void source\n" );
        }
        if ( ( dst.length - 1 ) <= offset ) {
            throw new IllegalArgumentException( "Invalid offset" );
        }
        dst[offset] = ( byte )( ( value >>> 8 ) & 0x00FF );
        dst [offset + 1] = ( byte )( value & 0x00FF );
    }

    public static void printBytes(byte[] data){
        int lines = data.length/12 + 1;
        for(int l=0; l<lines; ++l){
            //Print address
            System.out.print(String.format("%04x: ", l * 16));
            StringBuilder sb = new StringBuilder();
            for(int c=0; c<16; ++c){
                if (l*16+c < data.length){
                    System.out.print(String.format("%02x ",(new Integer((int)data[l*16+c] & 0x00FF))));
                    sb.append((char)data[l*16+c]);
                } else {
                  System.out.print("00 ");
                  sb.append(".");
                }
            }
            System.out.println(sb.toString());
        }
    }

    public static void PrintIpAddress(byte[] data){
        System.out.print(String.format("%d", (int)(data[0]&0xFF)));
        System.out.print(String.format(".%d", (int)(data[1]&0xFF)));
        System.out.print(String.format(".%d", (int)(data[2]&0xFF)));
        System.out.print(String.format(".%d", (int)(data[3]&0xFF)));
    }
}
