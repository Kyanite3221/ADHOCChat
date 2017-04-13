package Utilities;


import java.nio.ByteBuffer;

/**
 * Created by freem on 4/7/2017.
 */
public class BytewiseUtilities {

    /**
     * turns an integer into a byte array
     * @param value the integer to be converted
     * @return the final array.
     */
    public static final byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    public static final byte[] longToByteArray(long value) {
        return new byte[] {
                (byte)(value >>> 56),
                (byte)(value >>> 48),
                (byte)(value >>> 40),
                (byte)(value >>> 32),
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    public static final int byteArrayToInt(byte[] bytes){
        int result = 0;
        for (int i = 0; i < bytes.length; i++) {
            result += bytes[i]*Math.pow(8,3-i);
        }
        return result;
    }

    public static final long byteArrayToLong(byte[] bytes){
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();
        return buffer.getLong();
    }


    /**
     * adds a smaller array into a larger one, commencing at the indicated position.
     * could be used on any object or primitive, but I forgot how to !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     * @param small the array which is to be inserted into the larger one.
     * @param large the array which is to be modified and returned.
     * @param beginPosition the index at which the insertion is to take place.
     */
    public static final byte[] arrayInsertion(byte[] small, byte[] large, int beginPosition){
        if (large.length < small.length+beginPosition){
            return null;
        }

        for (int i = beginPosition; i < small.length + beginPosition ; i++) {
            large[i] = small[i-beginPosition];
        }

        return large;
    }

    public static final <E> E[] allArrayInsertion(E[] small, E[] large, int beginPosition){
        if (large.length < small.length+beginPosition){
            return null;
        }

        for (int i = beginPosition; i < small.length + beginPosition ; i++) {
            large[i] = small[i-beginPosition];
        }

        return large;
    }

    public static final String printBytes(byte[] toPrint) {
        if (toPrint == null){
            return "Empty array";
        }
        String result="";
        for (byte b : toPrint) {
            result += String.format(" 0x%02X",b);
        }
        return result;
    }


    public static final String printBytes(byte[] toPrint, String msg) {
        String result="";
        for (byte b : toPrint) {
            result += String.format(msg + " 0x%02X",b);
        }
        return result;
    }
}
