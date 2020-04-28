package works.hop.scrum.demo;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;

public class TestTextEncryptor {

    public static void main(String[] args) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword("united_code"); // could be got from web, env variable...
        encryptor.setAlgorithm("PBEWithHMACSHA512AndAES_256");
        encryptor.setIvGenerator(new RandomIvGenerator());

        String word = "ripples";
        String encrypted = encryptor.encrypt(word);
        System.out.printf("word: %s, encrypted: %s%n", word, encrypted);
        String decrypted = encryptor.decrypt("OFtd6dO/JB2YaPFBCaWn09/zVaYPtewG9IkRovauIXmxkvmZz5J6nU9PMHQV/t1D");
        System.out.printf("decrypted: %s%n", decrypted);
    }
}
