package com.service.video_data_service.utils.EncryptTools;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Random;
public class CryptoUtils {
    private static final String ENCODE="utf-8";
    private static final String ALGORITHM="AES";
    private static final String PATTERN="AES/ECB/PKCS5PADDING";
    private static final String ALLCHAR="0123456789qwertyuioplkjhgfdsazxcvbnmQWERTYUIOPLKJHGFDSAZXCVBNM";
    public static String GenerateKey()
    {
        StringBuffer buffer=new StringBuffer();
        Random random=new Random();
        for (int i = 0; i < 16; i++) {
            buffer.append(ALLCHAR.charAt(random.nextInt(ALLCHAR.length())));
        }
        return buffer.toString();
    }
    public static int GenerateIntKey()
    {
        Random random=new Random();
        return random.nextInt(999999999);
    }
    public static String encrypt_msg(String txt,String key) throws Exception {
        SecretKey secretKey=new SecretKeySpec(key.getBytes(ENCODE),ALGORITHM);
        Cipher cipher=Cipher.getInstance(PATTERN);
        cipher.init(Cipher.ENCRYPT_MODE,secretKey);
        byte[] encrypt_data= cipher.doFinal(txt.getBytes(ENCODE));
        return Base64.getEncoder().encodeToString(encrypt_data);
    }
    public static String decrypt_msg(String txt,String key) throws Exception {
        SecretKey secretKey=new SecretKeySpec(key.getBytes(ENCODE),ALGORITHM);
        Cipher cipher=Cipher.getInstance(PATTERN);
        cipher.init(Cipher.DECRYPT_MODE,secretKey);
        byte[] decrypt_data= cipher.doFinal(Base64.getDecoder().decode(txt));
        return new String(decrypt_data, ENCODE);
    }
    public static byte[] Encrypt_MD5(byte[]data) throws NoSuchAlgorithmException {
        MessageDigest digest=MessageDigest.getInstance("MD5");
        digest.update(data);
        return digest.digest();
    }
    public static byte[] Encrypt_SHA(byte[]data) throws NoSuchAlgorithmException {
        MessageDigest digest=MessageDigest.getInstance("SHA");
        digest.update(data);
        return digest.digest();
    }
    public static KeyPairGenerator generator;
    public static KeyPair keyPair;
    public static final class keystore{
        public byte[] public_key;
        public byte[] private_key;
    }
    static {
        try {
            generator=KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            keyPair=generator.generateKeyPair();
        }catch (Exception err)
        {
            err.printStackTrace();
        }
    }
    public static keystore get_key_pair(){
        keystore key_value=new keystore();
        key_value.public_key=keyPair.getPublic().getEncoded();
        key_value.private_key=keyPair.getPrivate().getEncoded();
        return key_value;
    }
    public static byte[] encrypt_data_by_public(byte[] msg, byte[] key) throws Exception {
        KeyFactory keyFactory=KeyFactory.getInstance("RSA");
        X509EncodedKeySpec x509EncodedKeySpec=new X509EncodedKeySpec(key);
        PublicKey publicKey=keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher=Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE,publicKey);
        return cipher.doFinal(msg);
    }
    public static byte[] decrypt_data_by_private(byte[] msg, byte[] key) throws Exception{
        KeyFactory keyFactory=KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec=new PKCS8EncodedKeySpec(key);
        PrivateKey privateKey=keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher=Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE,privateKey);
        return cipher.doFinal(msg);
    }
    public static byte[] encrypt_data_by_private(byte[] msg, byte[] key) throws Exception
    {
        KeyFactory keyFactory=KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec=new PKCS8EncodedKeySpec(key);
        PrivateKey privateKey=keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher=Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE,privateKey);
        return cipher.doFinal(msg);
    }
    public static byte[] decrypt_data_by_public(byte[] msg,byte[] key) throws Exception
    {
        KeyFactory keyFactory=KeyFactory.getInstance("RSA");
        X509EncodedKeySpec x509EncodedKeySpec=new X509EncodedKeySpec(key);
        PublicKey publicKey=keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher=Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE,publicKey);
        return cipher.doFinal(msg);
    }
    public static String Base64ToString(byte[]data)
    {
        return Base64.getEncoder().encodeToString(data);
    }
    public static String BytesToString(byte[]data,String encode) throws UnsupportedEncodingException {
        return new String(data,encode);
    }
}