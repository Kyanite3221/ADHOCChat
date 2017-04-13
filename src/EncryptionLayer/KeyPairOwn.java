package EncryptionLayer;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * Created by freem on 4/13/2017.
 */
public class KeyPairOwn {
    private BigInteger N;
    private BigInteger eOrD;
    private int sizeOfN;
    private int sizeOfKey;

    public KeyPairOwn(BigInteger modulo, BigInteger exponent){
        N = modulo;
        sizeOfN = N.toByteArray().length;
        eOrD = exponent;
        sizeOfKey = eOrD.toByteArray().length;
    }

    public KeyPairOwn(byte[] data){
        sizeOfN = Utilities.BytewiseUtilities.byteArrayToInt(Arrays.copyOfRange(data, 0, 4));
        sizeOfKey = Utilities.BytewiseUtilities.byteArrayToInt(Arrays.copyOfRange(data, 4, 8));
        N = new BigInteger(Arrays.copyOfRange(data, 8, 8+sizeOfN));
        eOrD = new BigInteger(Arrays.copyOfRange(data, 8+sizeOfN, 8+sizeOfN+sizeOfKey));
    }

    public BigInteger getN() {
        return N;
    }

    public BigInteger getEOrD(){
        return eOrD;
    }

    public byte[] toByte(){
        byte[] toReturn = new byte[8+sizeOfKey+sizeOfN];
        toReturn = Utilities.BytewiseUtilities.arrayInsertion(Utilities.BytewiseUtilities.intToByteArray(sizeOfN),toReturn,0);
        toReturn = Utilities.BytewiseUtilities.arrayInsertion(Utilities.BytewiseUtilities.intToByteArray(sizeOfKey),toReturn,4);
        toReturn = Utilities.BytewiseUtilities.arrayInsertion(N.toByteArray(),toReturn,8);
        toReturn = Utilities.BytewiseUtilities.arrayInsertion(eOrD.toByteArray(),toReturn,8+sizeOfN);
        return toReturn;
    }
}
