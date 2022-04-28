package com.ruoyi.common.utils.security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA 工具类
 */
public class RSAUtils {
    /**
     * 生成RSA密钥对
     */
    public static RSAKeyPair generateKeyPair(Integer length) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(length);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        String publicKeyString = Base64.getEncoder().encodeToString(rsaPublicKey.getEncoded());
        String privateKeyString = Base64.getEncoder().encodeToString(rsaPrivateKey.getEncoded());
        return new RSAKeyPair(publicKeyString, privateKeyString);
    }

    /**
     * RSA密钥对对象
     */
    public static class RSAKeyPair
    {
        private final String publicKey;
        private final String privateKey;

        public RSAKeyPair(String publicKey, String privateKey)
        {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public String getPublicKey()
        {
            return publicKey;
        }

        public String getPrivateKey()
        {
            return privateKey;
        }
    }

    /**
     *  从文件读取公钥文件，尝试先将文件作为证书来处理(不带注视符号)
     */
    public static RSAPublicKey loadPublicKey(File file) throws Exception {
        try{
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate)cf.generateCertificate(new FileInputStream(file));
            return loadPublicKey(cert.getPublicKey().getEncoded(), "RSA");
        }catch (Exception ignored){}

        String collect = String.join("", Files.readAllLines(file.toPath()));
        return loadPublicKey(collect);
    }

    /**
     * 从字符串读取公钥
     */
    public static RSAPublicKey loadPublicKey(String publicKeyStr) throws Exception {
        byte[] buffer = Base64.getDecoder().decode(publicKeyStr);
        return loadPublicKey(buffer, "RSA");
    }

    /**
     * 从字节中加载公钥
     * @param publicKeyBytes
     */
    public static RSAPublicKey loadPublicKey(byte[] publicKeyBytes, String algorithm)throws Exception
    {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法");
        } catch (NullPointerException e) {
            throw new Exception("公钥数据为空");
        }
    }

    /**
     *  从文件读取私钥文件(不带注视符号)
     */
    public static RSAPrivateKey loadPrivateKey(File file) throws Exception {
        String collect = String.join("", Files.readAllLines(file.toPath()));
        return loadPrivateKey(collect);
    }

    /**
     * 从字符串读取私钥
     */
    public static RSAPrivateKey loadPrivateKey(String privateKeyStr) throws Exception {
        byte[] buffer = Base64.getDecoder().decode(privateKeyStr);
        return loadPrivateKey(buffer, "RSA");
    }

    /**
     * 从字节数组加载私钥
     */
    public static RSAPrivateKey loadPrivateKey(byte[] privateKeyBytes, String algorithm)throws Exception
    {
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("私钥非法");
        } catch (NullPointerException e) {
            throw new Exception("私钥数据为空");
        }
    }

    /**
     * 默认使用utf8的字符集加密
     */
    public static String encrypt(String publicKey, String plainTextData) throws Exception
    {
        return encrypt(publicKey, plainTextData, StandardCharsets.UTF_8);
    }

    /**
     * 公钥加密，返回base64结果
     */
    public static String encrypt(String publicKey, String plainTextData, Charset charSet) throws Exception
    {
        byte[] data = plainTextData.getBytes(charSet);
        RSAPublicKey rsaPublicKey = loadPublicKey(publicKey);
        byte[] rsaBytes = encrypt(rsaPublicKey, data, "RSA");
        return Base64.getEncoder().encodeToString(rsaBytes);
    }

    /**
     * 公钥加密
     */
    public static byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData, String algorithm) throws Exception {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(plainTextData);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此加密算法");
        } catch (NoSuchPaddingException e) {
            throw new Exception("填充异常");
        } catch (InvalidKeyException e) {
            throw new Exception("加密公钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("明文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("明文数据已损坏");
        }
    }

    /**
     * 默认使用utf8的字符集加密
     */
    public static String privateEncrypt(String publicKey, String plainTextData) throws Exception
    {
        return privateEncrypt(publicKey, plainTextData, StandardCharsets.UTF_8);
    }

    /**
     * 私钥加密，返回base64结果
     */
    public static String privateEncrypt(String publicKey, String plainTextData, Charset charSet) throws Exception
    {
        byte[] data = plainTextData.getBytes(charSet);
        RSAPrivateKey rsaPrivateKey = loadPrivateKey(publicKey);
        byte[] rsaBytes = privateEncrypt(rsaPrivateKey, data, "RSA");
        return Base64.getEncoder().encodeToString(rsaBytes);
    }

    /**
     * 私钥加密
     */
    public static byte[] privateEncrypt(RSAPrivateKey privateKey, byte[] plainTextData, String algorithm) throws Exception {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return cipher.doFinal(plainTextData);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此加密算法");
        } catch (NoSuchPaddingException e) {
            throw new Exception("填充异常");
        } catch (InvalidKeyException e) {
            throw new Exception("加密私钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("明文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("明文数据已损坏");
        }
    }

    /**
     * 解密base64编码字符串
     */
    public static String decrypt(String privateKey, String base64CipherData, Charset charset) throws Exception {
        byte[] data = Base64.getDecoder().decode(base64CipherData);
        RSAPrivateKey rsaPrivateKey = loadPrivateKey(privateKey);
        byte[] rsaBytes = decrypt(rsaPrivateKey, data, "RSA");
        return new String(rsaBytes, charset);
    }
    /**
     * 私钥解密
     */
    public static byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData, String algorithm) throws Exception {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(cipherData);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此解密算法");
        } catch (NoSuchPaddingException e) {
            throw new Exception("填充异常");
        } catch (InvalidKeyException e) {
            throw new Exception("解密私钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("密文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("密文数据已损坏");
        }
    }

    /**
     * 公钥解密base64编码字符串
     */
    public static String publicDecrypt(String publicKey, String base64CipherData, Charset charset) throws Exception {
        byte[] data = Base64.getDecoder().decode(base64CipherData);
        RSAPublicKey rsaPublicKey = loadPublicKey(publicKey);
        byte[] rsaBytes = publicDecrypt(rsaPublicKey, data, "RSA");
        return new String(rsaBytes, charset);
    }

    /**
     * 公钥解密过程
     */
    public static byte[] publicDecrypt(RSAPublicKey publicKey, byte[] cipherData, String algorithm)throws Exception {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return cipher.doFinal(cipherData);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此解密算法");
        } catch (NoSuchPaddingException e) {
            throw new Exception("填充异常");
        } catch (InvalidKeyException e) {
            throw new Exception("解密公钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("密文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("密文数据已损坏");
        }
    }

    /**
     * 为签名内容使用默认字符集
     */
    public static String sign(String privateKey, String content)throws Exception
    {
        return sign(privateKey, content, StandardCharsets.UTF_8);
    }

    /**
     * 字符串签名和返回，默认使用 SHA1WithRSA算法
     */
    public static String sign(String privateKey, String content, Charset charset)throws Exception
    {
        RSAPrivateKey rsaPrivateKey = loadPrivateKey(privateKey);
        byte[] signed = sign(rsaPrivateKey, content.getBytes(charset), "SHA1WithRSA");
        return Base64.getEncoder().encodeToString(signed);
    }

    /**
     * 私钥签名
     */
    public static byte[] sign(PrivateKey privateKey, byte[] plainText, String algorithm)throws Exception
    {
        Signature signature = Signature.getInstance(algorithm);
        signature.initSign(privateKey);
        signature.update( plainText);
        return signature.sign();
    }

    /**
     * 使用默认字符集作为内容编码
     */
    public static boolean doCheck(String publicKey, String content, String base64Sign)throws Exception
    {
        return doCheck(publicKey, content, StandardCharsets.UTF_8, base64Sign);
    }

    /**
     * 字符串验签
     */
    public static boolean doCheck(String publicKey, String content, Charset charset, String base64Sign)throws Exception
    {
        RSAPublicKey rsaPublicKey = loadPublicKey(publicKey);
        return doCheck(rsaPublicKey, content.getBytes(charset), Base64.getDecoder().decode(base64Sign), "SHA1WithRSA");
    }

    /**
     * 公钥验签
     */
    public static boolean doCheck(RSAPublicKey publicKey, byte[] content, byte[] sign, String algorithm)throws Exception
    {
        Signature signature = Signature.getInstance(algorithm);
        signature.initVerify(publicKey);
        signature.update(content);
        return signature.verify(sign);
    }

}
