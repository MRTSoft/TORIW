package mygalomorphae.dns.Utils;

public final class LabelUtil {
    private LabelUtil() {
    }

    public static byte[] SerializeLabel( String data ) {
        byte[] result = new byte[data.length() + 2];

        int index = 0;
        int labelLength = 0;//length so far
        int lastIndex = 0;

        while ( index < data.length() ) {
            if ( data.charAt( index ) == '.' ) {
                result[lastIndex] = ( byte ) ( labelLength & 0xFF );
                labelLength = 0;
                lastIndex = index + 1;
            } else {
                labelLength++;
                result[index+1] = (byte)data.charAt(index);
            }
            index++;
        }
        //Add the last index
        result[lastIndex] = ( byte ) ( labelLength & 0xFF );
        //Add the last 0
        result[index + 1] = 0x00;
        return result;
    }

    /**
     * Deserialize a label into a standard String
     * @param data      Packaged data
     * @param position  Start of the label
     * @return The decoded label
     *
     */
    public static String DeserializeLabel(byte[] data, int position){
        StringBuilder buffer = new StringBuilder();
        //TODO
        return buffer.toString();
    }

    /**
     * Parse a pointer and return the relative address encoded in it
     * @param pointer The pointer to be parsed
     * @return An offset in the package as defined by RFC1035
     */
    public static int GetPointerAddress(char pointer){
        int offset = 0;
        offset = (((pointer>>8) & 0x3F) | (pointer &0xFF));
        return offset;
    }

    /**
     * Comodity function to check if a byte is a pointer or a label
     * @param b1
     * @return
     */
    public static Boolean IsPointer(byte b1){
        return ((b1 & 0xC0) != 0);
    }
}
