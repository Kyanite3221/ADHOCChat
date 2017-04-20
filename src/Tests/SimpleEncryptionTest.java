package Tests;

import EncryptionLayer.SimpleEncryptionModule;

/**
 * Created by freem on 4/20/2017.
 */
public class SimpleEncryptionTest {
    public static void main(String[] args) {
        System.out.println("decryption with custom key");
        decryptionWithCustomKey();
        System.out.println("\n\nrepetition code");
        keyLength();

        System.out.println("\n\nstring encoding");
        stringEncodingTest();

        System.out.println("\n\nzero byte test");
        SimpleEncryptionModule modOne = new SimpleEncryptionModule();
        byte[] data = new byte[2];
        data[0]= 0x00;
        data[1] = 0x01;

        byte[] encoded = modOne.encrypt(data);
        System.out.println(Utilities.BytewiseUtilities.printBytes(encoded) + " --> " + Utilities.BytewiseUtilities.printBytes(modOne.decrypt(encoded)));

    }

    private static void stringEncodingTest() {
        SimpleEncryptionModule modOne = new SimpleEncryptionModule();
        String test = "hallo";
        byte[] encryptedData = modOne.encrypt(test.getBytes());
        System.out.println(new String(modOne.decrypt(encryptedData)));
    }

    private static void keyLength() {
        SimpleEncryptionModule modOne = new SimpleEncryptionModule();
        byte[] data = new byte[100];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte)0xff;
        }
        byte[] encryptedData = modOne.encrypt(data);
        System.out.println(Utilities.BytewiseUtilities.printBytes(encryptedData));
    }

    private static void decryptionWithCustomKey() {
        SimpleEncryptionModule modOne = new SimpleEncryptionModule();
        SimpleEncryptionModule modTwo = new SimpleEncryptionModule();
        SimpleEncryptionModule modThree = new SimpleEncryptionModule(14003);

        byte[] data = new byte[14];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (Math.random()*0xff);
        }
        System.out.println("Raw data: " + Utilities.BytewiseUtilities.printBytes(data));
        byte[] encryptedData = modOne.encrypt(data);
        System.out.println("Encrypted data: " + Utilities.BytewiseUtilities.printBytes(encryptedData));

        byte[] decryptedData = modTwo.decrypt(encryptedData);
        System.out.println("Decrypted data: " + Utilities.BytewiseUtilities.printBytes(decryptedData));

        byte[] wrongData = modThree.decrypt(encryptedData);
        System.out.println("Wrong data: " + Utilities.BytewiseUtilities.printBytes(wrongData));
    }
}
