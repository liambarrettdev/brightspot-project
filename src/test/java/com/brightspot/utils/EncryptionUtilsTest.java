package com.brightspot.utils;

import com.brightspot.TestExecutionException;
import com.psddev.dari.util.Settings;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class EncryptionUtilsTest {

    private static final String TEST_SECRET_KEY = "abcdefgh12345678";
    private static final String TEST_IV_PARAMETER = "abcdefghijkl1234567890";
    private static final String TEST_VALUE = "some plain text";

    @Test
    public void testEncryption_String() {
        try (MockedStatic<Settings> settings = Mockito.mockStatic(Settings.class)) {
            MockedStatic.Verification encryption = () -> Settings.getOrError(
                String.class,
                EncryptionUtils.ENCRYPTION_KEY,
                EncryptionUtils.ENCRYPTION_KEY_ERR_MSG
            );
            settings.when(encryption).thenReturn(TEST_SECRET_KEY);

            MockedStatic.Verification vector = () -> Settings.getOrError(
                String.class,
                EncryptionUtils.IV_KEY,
                EncryptionUtils.IV_KEY_ERR_MSG
            );
            settings.when(vector).thenReturn(TEST_IV_PARAMETER);

            String encrypted = EncryptionUtils.encrypt(TEST_VALUE);
            String decrypted = EncryptionUtils.decrypt(encrypted);

            Assertions.assertNotEquals(TEST_VALUE, encrypted);
            Assertions.assertEquals(TEST_VALUE, decrypted);
        } catch (Exception e) {
            throw new TestExecutionException("Failed to mock statics", e);
        }
    }

    @Test
    public void testEncryption_Bytes() {
        try (MockedStatic<Settings> settings = Mockito.mockStatic(Settings.class)) {
            MockedStatic.Verification encryption = () -> Settings.getOrError(
                String.class,
                EncryptionUtils.ENCRYPTION_KEY,
                EncryptionUtils.ENCRYPTION_KEY_ERR_MSG
            );
            settings.when(encryption).thenReturn(TEST_SECRET_KEY);

            MockedStatic.Verification vector = () -> Settings.getOrError(
                String.class,
                EncryptionUtils.IV_KEY,
                EncryptionUtils.IV_KEY_ERR_MSG
            );
            settings.when(vector).thenReturn(TEST_IV_PARAMETER);

            String encrypted = EncryptionUtils.encrypt(TEST_VALUE.toCharArray());
            String decrypted = EncryptionUtils.decrypt(encrypted);

            Assertions.assertNotEquals(TEST_VALUE, encrypted);
            Assertions.assertEquals(TEST_VALUE, decrypted);
        } catch (Exception e) {
            throw new TestExecutionException("Failed to mock statics", e);
        }
    }

    @Test
    public void testEncryption_Null() {
        try (MockedStatic<Settings> settings = Mockito.mockStatic(Settings.class)) {
            MockedStatic.Verification encryption = () -> Settings.getOrError(
                String.class,
                EncryptionUtils.ENCRYPTION_KEY,
                EncryptionUtils.ENCRYPTION_KEY_ERR_MSG
            );
            settings.when(encryption).thenReturn(TEST_SECRET_KEY);

            MockedStatic.Verification vector = () -> Settings.getOrError(
                String.class,
                EncryptionUtils.IV_KEY,
                EncryptionUtils.IV_KEY_ERR_MSG
            );
            settings.when(vector).thenReturn(TEST_IV_PARAMETER);

            String decrypted = EncryptionUtils.decrypt(null);

            Assertions.assertEquals(StringUtils.EMPTY, decrypted);
        } catch (Exception e) {
            throw new TestExecutionException("Failed to mock statics", e);
        }
    }
}
