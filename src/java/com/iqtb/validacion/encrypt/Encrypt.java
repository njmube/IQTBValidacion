package com.iqtb.validacion.encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author danielromero
 */
public class Encrypt {
    
    public static String getSALT(int size) throws NoSuchAlgorithmException{
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] seed = secureRandom.generateSeed(size);

        secureRandom = null;
        return Base64.encodeBase64String(seed);
    }
    
    public static String getSHA512(String cadena) throws NoSuchAlgorithmException{
        StringBuilder sb = new StringBuilder();

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            md.update(cadena.getBytes());

            byte[] mb = md.digest();

            for (int i = 0; i < mb.length; i++) {
                sb.append(Integer.toString((mb[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException ex) {
           throw ex;
        }

        return sb.toString();
    }
    
    public static boolean verifySHASaltPassword(String testPass, String salt, String passKey) throws NoSuchAlgorithmException {
        String hash = getSHA512(testPass + salt);
        
        return hash.equals(passKey);
    }
    
}