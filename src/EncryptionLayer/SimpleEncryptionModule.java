package EncryptionLayer;

/**
 * Created by freem on 4/19/2017.
 */
public class SimpleEncryptionModule {

    private byte[] key;

    public SimpleEncryptionModule(){
        key = new byte[12];
        key[0] = 0x0F;
        key[1] = (byte)0xAC;

    }
}
