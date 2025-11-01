package com.brightspot.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.psddev.dari.util.Settings;
import org.apache.commons.codec.binary.Base64;

public final class EncryptionUtils {

    public static final String ENCRYPTION_KEY = "dari/credentials/encryption-key";
    public static final String IV_KEY = "dari/credentials/initialisation-vector-key";
    public static final String ENCRYPTION_KEY_ERR_MSG = "EncryptDecrypt: getSecretKeyAsBytes: Credentials Encryption Key from Settings is Null";
    public static final String IV_KEY_ERR_MSG = "EncryptDecrypt: getIvKeyAsBytes: Credentials Initialisation Vector from Settings is Null";

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMER = "AES/CBC/PKCS5PADDING";
    private static final byte[] LINEBREAK = {}; // Remove Base64 encoder default linebreak

    private EncryptionUtils() {
    }

    /**
     * Non synchronized - each call has one instance Cipher
     *
     * @param input plain text
     * @return encrypted text
     */
    public static String encrypt(String input) throws Exception {
        SecretKey secretKey = new SecretKeySpec(getSecretKeyAsBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMER);
        Base64 coder = new Base64(32, LINEBREAK, true);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(getIvKeyAsBytes()));
        byte[] cipherText = cipher.doFinal(input.getBytes());

        return new String(coder.encode(cipherText));
    }

    /**
     * Non synchronized - each call has one instance Cipher
     *
     * @param input text as plain char array
     * @return encrypted text
     */
    public static String encrypt(char[] input) throws Exception {
        SecretKey secretKey = new SecretKeySpec(getSecretKeyAsBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMER);
        Base64 coder = new Base64(32, LINEBREAK, true);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(getIvKeyAsBytes()));
        byte[] cipherText = cipher.doFinal(convertToBytes(input));

        return new String(coder.encode(cipherText));
    }

    /**
     * Non synchronized - each call has one instance Cipher
     *
     * @param input encrypted text
     * @return unencrypted text
     */
    public static String decrypt(String input) throws Exception {
        if (input == null) {
            return "";
        }

        SecretKey secretKey = new SecretKeySpec(getSecretKeyAsBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMER);
        Base64 coder = new Base64(32, LINEBREAK, true);

        byte[] encrypted = coder.decode(input.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(getIvKeyAsBytes()));
        byte[] decrypted = cipher.doFinal(encrypted);

        return new String(decrypted);
    }

    private static byte[] convertToBytes(char[] chars) {
        CharBuffer charBuffer = CharBuffer.wrap(chars);
        ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(charBuffer);
        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());

        Arrays.fill(charBuffer.array(), '\u0000');
        Arrays.fill(byteBuffer.array(), (byte) 0);

        return bytes;
    }

    private static byte[] getSecretKeyAsBytes() {
        String setting = Settings.getOrError(
            String.class,
            ENCRYPTION_KEY,
            ENCRYPTION_KEY_ERR_MSG
        );

        return setting.getBytes();
    }

    private static byte[] getIvKeyAsBytes() {
        String setting = Settings.getOrError(
            String.class,
            IV_KEY,
            IV_KEY_ERR_MSG
        );

        return Base64.decodeBase64(setting);
    }
}
