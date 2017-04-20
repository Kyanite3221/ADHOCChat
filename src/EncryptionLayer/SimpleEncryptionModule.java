package EncryptionLayer;

/**
 * Created by freem on 4/19/2017.
 */
public class SimpleEncryptionModule {

    private byte[] key;

    public SimpleEncryptionModule(){
        key = Utilities.BytewiseUtilities.longToByteArray(828162663); //just a large prime number
    }

    public SimpleEncryptionModule(long keyValue){
        key = Utilities.BytewiseUtilities.longToByteArray(keyValue);
    }

    public byte[] encrypt(byte[] toEncrypt){
        int zeroBytes = 0; // this is so there are no 0's in the key, meaning better encryption.
        for(byte b: key){
            if (b==0x00){
                zeroBytes++;
            }
        }
        byte[] keyArray = new byte[key.length-zeroBytes];
        int counter=0;
        for (byte b :key){
            if (b!=0x00){
                keyArray[counter] = b;
                counter++;
            }
        } //the key array now contains only non-zero elements.

        byte[] finalArray = new byte[toEncrypt.length];
        for (int i = 0; i < toEncrypt.length; i++) {
            finalArray[i] = (byte)(toEncrypt[i]^keyArray[i%keyArray.length]);
        }
        return finalArray;
    }

    public byte[] decrypt(byte[] data){
        return encrypt(data);
    }

    protected void soutKey(){
        System.out.println(Utilities.BytewiseUtilities.printBytes(key));
    }
}
