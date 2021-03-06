package Tests;

import EncryptionLayer.KeyPairOwn;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created by freem on 4/13/2017.
 */
public class KeyPairTest {
    public static void main(String[] args) {
        BigInteger one = BigInteger.probablePrime(32,new Random());
        BigInteger two = BigInteger.probablePrime(33,new Random());
        KeyPairOwn test = new KeyPairOwn(one, two);
        System.out.println("one is                  : " + one);
        System.out.println("keypair says that one is: " + test.getN());
        System.out.println("two is                  : " + two);
        System.out.println("keypair says that two is: " + test.getEOrD());

        KeyPairOwn conversionTest = new KeyPairOwn(test.toByte());
        System.out.println("one is                  : " + one);
        System.out.println("keypair says that one is: " + conversionTest.getN());
        System.out.println("two is                  : " + two);
        System.out.println("keypair says that two is: " + conversionTest.getEOrD());
    }
}
