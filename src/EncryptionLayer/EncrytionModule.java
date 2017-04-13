package EncryptionLayer;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by freem on 4/13/2017.
 */
public class EncrytionModule {

    private HashMap<String, Key> publicKeys;
    private Key privateKey;
    private BigInteger modulator;
    private HashMap <String, Long > keyMap;

    private boolean canSupportEncryption;

    private static final byte PUBLIC_KEY_EXCHANGE_FLAG = 0x01;
    private static final byte COMMUNICATION_KEY_EXCHANGE_FLAG = 0x02;
    private static final byte MESSAGE_EXCHANGE_FLAG = 0x04;
    private static final byte NON_ENCODED_DATA_FLAG = 0x08;

    public EncrytionModule(){
        publicKeys = new HashMap<>();
        keyMap = new HashMap<>();

        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            KeyPair pair = kpg.generateKeyPair();
            privateKey = pair.getPrivate();
            publicKeys.put("self", pair.getPublic());


        } catch (NoSuchAlgorithmException e) {
            System.out.println("Cry me a river");
            canSupportEncryption = false;
        }


    }
}
