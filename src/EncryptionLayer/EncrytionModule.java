package EncryptionLayer;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by freem on 4/13/2017.
 */
public class EncrytionModule {

    private HashMap<String, KeyPair> publicKeys;
    private BigInteger privateKey;
    private BigInteger modulator;
    private HashMap <String, Long > keyMap;

    private static final byte PUBLIC_KEY_EXCHANGE_FLAG = 0x01;
    private static final byte COMMUNICATION_KEY_EXCHANGE_FLAG = 0x02;
    private static final byte MESSAGE_EXCHANGE_FLAG = 0x04;
    private static final byte NON_ENCODED_DATA_FLAG = 0x08;

    public EncrytionModule(){
        publicKeys = new HashMap<>();
        keyMap = new HashMap<>();

        BigInteger primeOne = BigInteger.probablePrime(16, new Random());
        BigInteger primeTwo = BigInteger.probablePrime(16, new Random());
        BigInteger phi = (primeOne.subtract(BigInteger.ONE)).multiply(primeTwo.subtract(BigInteger.ONE));



    }
}
