package org.jabref.preferences;

import com.github.javakeyring.BackendNotSupportedException;
import com.github.javakeyring.Keyring;
import com.github.javakeyring.PasswordAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecretStore {
    private static final String PREFIX = "org.jabref";
    private final Keyring keyring;
    private static final Logger LOGGER = LoggerFactory.getLogger(SecretStore.class);

    public SecretStore() throws RuntimeException {
        // Initialize the keyring
        try {
            keyring = Keyring.create();
        } catch (BackendNotSupportedException e) {
            LOGGER.warn("Could not load native keyring: " + e.getMessage(), e);
            throw new RuntimeException("Failed to connect to native keyring");
        }
    }

    public String get(String service) throws RuntimeException {
        try {
            return keyring.getPassword(PREFIX + ":" + service, "");
        } catch (PasswordAccessException e) {
            LOGGER.warn("Could not get secret from keychain: " + e.getMessage(), e);
            return null;
        }
    }

    public void put(String service, String password) {
        put(service, "", password);
    }

    public void put(String service, String account, String password) {
        try {
            keyring.setPassword(PREFIX + ":" + service, account, password);
        } catch (PasswordAccessException e) {
            LOGGER.warn("Could not save secret to keychain: " + e.getMessage(), e);
        }
    }

}
