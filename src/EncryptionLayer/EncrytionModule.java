package EncryptionLayer;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by freem on 4/13/2017.
 */
public class EncrytionModule {

    private HashMap<String, RSAPublicKeySpec> publicKeys;
    private PrivateKey privateKey;
    private HashMap <String, Long > keyMap;
    private LinkedList<byte[]> buffer;

    private boolean canSupportEncryption;

    private static final byte PUBLIC_KEY_EXCHANGE_FLAG = 0x01;
    private static final byte COMMUNICATION_KEY_EXCHANGE_FLAG = 0x02;
    private static final byte MESSAGE_EXCHANGE_FLAG = 0x04;
    private static final byte NON_ENCODED_DATA_FLAG = 0x08;
    private static final byte END_COMMUNICATION_FLAG = 0x0F;

    public EncrytionModule(){
        publicKeys = new HashMap<>();
        keyMap = new HashMap<>();
        buffer = new LinkedList<> ();

        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            KeyPair pair = kpg.generateKeyPair();
            KeyFactory translator = KeyFactory.getInstance("RSA");
            RSAPrivateKeySpec truePrivateKey= translator.getKeySpec(pair.getPrivate(), RSAPrivateKeySpec.class);
            privateKey = translator.generatePrivate(truePrivateKey);
            RSAPublicKeySpec truePublicKey = translator.getKeySpec(pair.getPublic(), RSAPublicKeySpec.class);
            publicKeys.put("self", truePublicKey);

            canSupportEncryption = true;

        } catch (NoSuchAlgorithmException e) {
            System.out.println("Cry me a river");
            canSupportEncryption = false;
        } catch (InvalidKeySpecException e) {
            System.out.println("Cry me a river");
            canSupportEncryption = false;
        }
    }

    public byte[] encryptMessage(byte[] data, String reciever){
        if(!canSupportEncryption){
            byte[] toReturn = new byte[data.length+1];
            toReturn[0] = NON_ENCODED_DATA_FLAG;
            System.arraycopy(data,0,toReturn,1,data.length);
            return toReturn;
        }

        if (!keyMap.containsKey(reciever)){ //we have not yet established a secure connection with this receiver.
            buffer.add(data);
            KeyPairOwn translationStep = new KeyPairOwn(publicKeys.get("self").getModulus(), publicKeys.get("self").getPublicExponent());
            byte[] toReturn = new byte[1+translationStep.toByte().length];
            toReturn[0] = PUBLIC_KEY_EXCHANGE_FLAG; //we will send our own public key to them and add the apropriate flag.
            return Utilities.BytewiseUtilities.arrayInsertion(translationStep.toByte(),toReturn,1);
        }
        byte[] encryptedData = encrypt(data, keyMap.get(reciever));
        byte[] toReturn = new byte[encryptedData.length+1];
        toReturn[0] = MESSAGE_EXCHANGE_FLAG;
        System.arraycopy(encryptedData, 0, toReturn, 1, encryptedData.length);
        return toReturn;

    }

    public byte[] recievedMessage(byte[] data, String reciever){
        if (data[0] == PUBLIC_KEY_EXCHANGE_FLAG){
            KeyPairOwn translator = new KeyPairOwn(Arrays.copyOfRange(data, 1, data.length));
            publicKeys.put(reciever, new RSAPublicKeySpec(translator.getN(), translator.getEOrD()));
            BigInteger key = BigInteger.probablePrime(30, new Random());
            keyMap.put(reciever, key.longValue());
            try {
                Cipher coder = Cipher.getInstance("RSA");
                KeyFactory transformer = KeyFactory.getInstance("RSA");
                PublicKey transformedKey = transformer.generatePublic(publicKeys.get(reciever));
                coder.init(Cipher.ENCRYPT_MODE, transformedKey);
                byte[] toProcess = coder.doFinal(Utilities.BytewiseUtilities.longToByteArray(keyMap.get(reciever)));
                byte[] toReturn = new byte[toProcess.length+1];
                toReturn[0] = COMMUNICATION_KEY_EXCHANGE_FLAG;
                System.arraycopy(toProcess,0,toReturn,1,toProcess.length);
                return toReturn;
            } catch (Exception e) {
                System.out.println("System not RSA capable");
                e.printStackTrace();
                canSupportEncryption = false;
            }


        } else if (data[0] == COMMUNICATION_KEY_EXCHANGE_FLAG) {
            try {
                Cipher decoder = Cipher.getInstance("RSA");
                decoder.init(Cipher.DECRYPT_MODE, privateKey);
                byte[] result = decoder.doFinal(Arrays.copyOfRange(data,1,data.length));
                keyMap.put(reciever, Utilities.BytewiseUtilities.byteArrayToLong(result));

                return null;
            } catch (Exception e) {
                System.out.println("Cry me a river");
                canSupportEncryption = false;
            }


        } else if (data[0] == MESSAGE_EXCHANGE_FLAG){
            if (!keyMap.containsKey(reciever)){
                System.out.println("Wrong communication protocol");
                return null;
            }
            return decrypt(Arrays.copyOfRange(data, 1, data.length), keyMap.get(reciever));
        } else if (data[0] == END_COMMUNICATION_FLAG){
            byte[] encryptedCheckWord = encrypt("EXIT".getBytes(),keyMap.get(reciever));
            boolean cool = true;
            for (int i = 0; i < encryptedCheckWord.length && cool; i++) {
                cool = (encryptedCheckWord[i] == data[i+1]);
            }
            if (cool){
                publicKeys.remove(reciever);
                keyMap.remove(reciever);
                System.out.println("Secure communication with " + reciever + " terminated, checkword was correct.");
            } else{
                System.out.println("Secure communication with " + reciever + " was attempted to shut down, but checkword was wrong.");
            }
            return null;
        } else {
            return Arrays.copyOfRange(data, 1, data.length);
        }
        System.out.println("impossible");
        return null;
    }

    public enum EncryptionDecision {
        RETURN_NEW_TO_SENDER,
        FORWARD_TO_APLICATION,
        CHECK_BUFFER,
        NO_ACTION
    }

    public EncryptionDecision handleIncomming(byte[] data) {
        switch (data[0]){
            case PUBLIC_KEY_EXCHANGE_FLAG:
                //A new message will need to be returned to the sender
                return EncryptionDecision.RETURN_NEW_TO_SENDER;

            case COMMUNICATION_KEY_EXCHANGE_FLAG:
                //the actual data message will need to be sent after this
                return EncryptionDecision.CHECK_BUFFER;

            case MESSAGE_EXCHANGE_FLAG:
                //This data will simply need to be forwarded to the aplication
                return EncryptionDecision.FORWARD_TO_APLICATION;

            case NON_ENCODED_DATA_FLAG:
                //This data will simply need to be forwarded to the aplication
                return EncryptionDecision.FORWARD_TO_APLICATION;

            default:
                //error has occurred
                return EncryptionDecision.NO_ACTION;
        }
    }

    public byte[] decrypt(byte[] data, long key){
        return encrypt(data, key);
    }

    public boolean isBufferEmpty(){
        return (buffer.size() < 1);
    }

    public byte[] encodeFirstBufferItem(String receiver){
        return encryptMessage(buffer.removeFirst(), receiver);
    }

    public byte[] encrypt(byte[] toEncrypt, long key){
        byte[] initKeyArray = Utilities.BytewiseUtilities.longToByteArray(key);

        int zeroBytes = 0; // this is so there are no 0's in the key, meaning better encryption.
        for(byte b: initKeyArray){
            if (b==0x00){
                zeroBytes++;
            }
        }
        byte[] keyArray = new byte[8-zeroBytes];
        int counter=0;
        for (byte b :initKeyArray){
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

    public byte[] endConnection (String receiver){
        byte[] encryptedCheckWord = encrypt("EXIT".getBytes(),keyMap.get(receiver));
        publicKeys.remove(receiver);
        keyMap.remove(receiver);
        byte[] toReturn = new byte[1+encryptedCheckWord.length];
        toReturn[0] = END_COMMUNICATION_FLAG;
        System.arraycopy(encryptedCheckWord,0,toReturn,1,encryptedCheckWord.length);
        return toReturn;
    }

}
