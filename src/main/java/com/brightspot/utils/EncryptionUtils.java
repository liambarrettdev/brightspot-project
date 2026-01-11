package com.brightspot.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.brightspot.exception.EncryptionException;
import com.psddev.dari.util.Settings;
import org.apache.commons.codec.binary.Base64;

/**
 * Utility class for encryption and decryption operations.
 *
 * <p>This class provides AES encryption/decryption using CBC mode with PKCS5 padding.
 * Encryption keys and initialization vectors are retrieved from application settings.</p>
 *
 * <p>All methods are thread-safe as each call creates its own Cipher instance.</p>
 *
 * <p>FIXME: The encryption key is stored as a UTF-8 string which may not provide sufficient entropy for AES keys - consider using proper key derivation</p>
 * <p>TODO: Consider adding key rotation support for better security</p>
 */
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
     * Encrypts a plain text string using AES encryption.
     *
     * <p>Uses AES/CBC/PKCS5Padding with a secret key and initialization vector retrieved
     * from application settings. The encrypted result is Base64 encoded for safe storage
     * and transmission.</p>
     *
     * <p>This method is thread-safe as each call creates its own Cipher instance.</p>
     *
     * @param input the plain text to encrypt
     * @return the Base64-encoded encrypted text
     * @throws EncryptionException if encryption fails due to configuration issues,
     *         invalid algorithm, or data corruption
     */
    public static String encrypt(String input) throws EncryptionException {
        try {
            SecretKey secretKey = new SecretKeySpec(getSecretKeyAsBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMER);
            Base64 coder = new Base64(32, LINEBREAK, true);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(getIvKeyAsBytes()));
            byte[] cipherText = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));

            return new String(coder.encode(cipherText));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new EncryptionException("Encryption algorithm '" + ALGORITHM + "' or transformation '" + TRANSFORMER + "' is not available", e);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new EncryptionException("Invalid encryption configuration: key or initialization vector may be incorrect or improperly formatted", e);
        } catch (IllegalBlockSizeException e) {
            throw new EncryptionException("Input data size is not valid for the encryption algorithm", e);
        } catch (BadPaddingException e) {
            throw new EncryptionException("Padding error during encryption: data may be corrupted", e);
        }
    }

    /**
     * Decrypts an encrypted string using AES decryption.
     *
     * <p>Expects a Base64-encoded encrypted string and decrypts it using AES/CBC/PKCS5Padding
     * with the same secret key and initialization vector used for encryption. Returns an
     * empty string if the input is null.</p>
     *
     * <p>This method is thread-safe as each call creates its own Cipher instance.</p>
     *
     * @param input the Base64-encoded encrypted text (returns empty string if null)
     * @return the decrypted plain text
     * @throws EncryptionException if decryption fails due to configuration issues,
     *         invalid algorithm, corrupted data, or incorrect key
     */
    public static String decrypt(String input) throws EncryptionException {
        if (input == null) {
            return "";
        }

        try {
            SecretKey secretKey = new SecretKeySpec(getSecretKeyAsBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMER);
            Base64 coder = new Base64(32, LINEBREAK, true);

            byte[] encrypted = coder.decode(input.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(getIvKeyAsBytes()));
            byte[] decrypted = cipher.doFinal(encrypted);

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new EncryptionException("Decryption algorithm '" + ALGORITHM + "' or transformation '" + TRANSFORMER + "' is not available", e);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new EncryptionException("Invalid decryption configuration: key or initialization vector may be incorrect or improperly formatted", e);
        } catch (IllegalBlockSizeException e) {
            throw new EncryptionException("Encrypted data size is not valid for the decryption algorithm", e);
        } catch (BadPaddingException e) {
            throw new EncryptionException("Padding error during decryption: encrypted data may be corrupted or key may be incorrect", e);
        }
    }

    /**
     * Converts a character array to a byte array and clears sensitive data.
     *
     * <p>Converts the character array to UTF-8 bytes and then clears both the character
     * and byte buffers to prevent sensitive data from remaining in memory.</p>
     *
     * <p>TODO: This method is currently unused - consider using it for secure key handling or remove if not needed</p>
     *
     * @param chars the character array to convert
     * @return the byte array representation
     */
    private static byte[] convertToBytes(char[] chars) {
        CharBuffer charBuffer = CharBuffer.wrap(chars);
        ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(charBuffer);
        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());

        Arrays.fill(charBuffer.array(), '\u0000');
        Arrays.fill(byteBuffer.array(), (byte) 0);

        return bytes;
    }

    /**
     * Retrieves the encryption secret key from settings as bytes.
     *
     * <p>FIXME: Converting a UTF-8 string directly to bytes may not provide sufficient entropy for AES keys - consider using proper key derivation (PBKDF2, etc.)</p>
     *
     * @return the secret key as a byte array
     * @throws IllegalStateException if the encryption key is not configured in settings
     */
    private static byte[] getSecretKeyAsBytes() {
        String setting = Settings.getOrError(
            String.class,
            ENCRYPTION_KEY,
            ENCRYPTION_KEY_ERR_MSG
        );

        return setting.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Retrieves the initialization vector from settings as bytes.
     *
     * <p>The IV is expected to be Base64-encoded in the settings and is decoded here.</p>
     *
     * @return the initialization vector as a byte array
     * @throws IllegalStateException if the IV key is not configured in settings
     */
    private static byte[] getIvKeyAsBytes() {
        String setting = Settings.getOrError(
            String.class,
            IV_KEY,
            IV_KEY_ERR_MSG
        );

        return Base64.decodeBase64(setting);
    }
}
