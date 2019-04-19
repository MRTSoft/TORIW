package Utils;

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
                lastIndex = index;
            } else {
                labelLength++;
            }
            index++;
        }
        //Add the last 0
        result[index] = 0x00;
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
        return buffer.toString();
    }

    /**
     * Parse a pointer and return the relative address encoded in it
     * @param pointer The pointer to be parsed
     * @return An offset in the package as defined by RFC1035
     */
    public static int GetPointerAddress(char pointer){
        return 0;
    }

    /**
     * Comodity function to check if the first byte of a
     * @param b1
     * @return
     */
    public static Boolean IsPointer(byte b1){
        return ((b1 & 0xC0) != 0);
    }
}
