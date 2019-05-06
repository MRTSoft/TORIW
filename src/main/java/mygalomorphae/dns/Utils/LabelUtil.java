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
    public static String DeserializeLabel(byte[] data, int position, int[] finishOffset){
        StringBuilder buffer = new StringBuilder();
        int offset = position;
        boolean hasJump = false;
        while (data[offset] != 0){
            char pointer = (char)(((0xFF & data[offset]) << 8) | (0xFF & data[offset+1]));
            if (IsPointer(data[offset])){
                if (!hasJump){
                    //Assume we only jump backwards
                    finishOffset[0] = offset+2;
                    hasJump = true;
                }
                offset = GetPointerAddress(pointer);
                if (offset == finishOffset[0]){
                    hasJump = false;
                    //Not sure if I need this
                }
            }
            else {
                int len = data[offset] & 0xFF;
                if (len == 0){
                    break;
                }
                offset++;
                if (!buffer.toString().isEmpty() && len > 0){
                    buffer.append('.');
                }
                for(int i=0; i<len; ++i){
                    buffer.append((char)(0x00FF & data[offset]));
                    offset++;
                }
            }
        }
        if (!hasJump){
            finishOffset[0] = offset+1;//Skip the null pointer at the end
        }
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
