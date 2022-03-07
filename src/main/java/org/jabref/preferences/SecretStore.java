package org.jabref.preferences;

import com.github.javakeyring.BackendNotSupportedException;
import com.github.javakeyring.Keyring;
import com.github.javakeyring.PasswordAccessException;

import java.util.HashMap;
import java.util.Optional;

import static org.jabref.gui.importer.actions.OpenDatabaseAction.LOGGER;

public class SecretStore {
    private static final String PREFIX = "org.jabref";
    private final Keyring keyring;

    public SecretStore() {
        Keyring kr = null;

        // Initialize the keyring
        try {
            kr = Keyring.create();
        } catch (BackendNotSupportedException e) {
            throw new RuntimeException("Could not load native keyring: " + e.getMessage());
        }
        keyring = kr;
    }

    public String get(String service) {
        return get(service, "");
    }

    public String get(String service, String account) {
        try {
            return keyring.getPassword(PREFIX + ":" + service, account);
        } catch (PasswordAccessException e) {
            throw new RuntimeException("Could not load native keyring: " + e.getMessage());
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
