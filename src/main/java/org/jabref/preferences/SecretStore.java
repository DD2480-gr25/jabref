package org.jabref.preferences;

import com.github.javakeyring.BackendNotSupportedException;
import com.github.javakeyring.Keyring;
import com.github.javakeyring.PasswordAccessException;

import java.util.HashMap;
import java.util.Optional;

import static org.jabref.gui.importer.actions.OpenDatabaseAction.LOGGER;

public class SecretStore {
    private final Keyring keyring;
    private final HashMap<String, String> fallback;

    public SecretStore() {
        Keyring kr = null;

        // Initialize the keyring
        try {
            kr = Keyring.create();
        } catch (BackendNotSupportedException e) {
            LOGGER.warn("Could not load native keyring: " + e.getMessage(), e);
        }
        keyring = kr;

        fallback = new HashMap<>();
    }

    public String get(String service) {
        if (keyring == null) {
            return fallback.getOrDefault(service, "");
        }
        try {
            return keyring.getPassword(service, "");
        } catch (PasswordAccessException e) {
            LOGGER.warn("Could not get secret from keychain: " + e.getMessage(), e);
            return fallback.getOrDefault(service, "");
        }
    }

    public void put(String service, String password) {
        if (keyring == null) {
             fallback.put(service, password);
        }
        try {
             keyring.setPassword(service, "", password);
        } catch (PasswordAccessException e) {
            LOGGER.warn("Could not save secret to keychain: " + e.getMessage(), e);
            fallback.put(service, password);
        }
    }

}
