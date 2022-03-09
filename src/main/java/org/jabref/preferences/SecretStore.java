/**
 * @author  Group25
 * @date    06/03/2022
 */

package org.jabref.preferences;

import com.github.javakeyring.BackendNotSupportedException;
import com.github.javakeyring.Keyring;
import com.github.javakeyring.PasswordAccessException;

import java.util.HashMap;
import java.util.Optional;

import static org.jabref.gui.importer.actions.OpenDatabaseAction.LOGGER;

/**
 *  Save and read secret from system.
 */
public class SecretStore {
    private static final String PREFIX = "org.jabref";
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

    /**
     * Read secrets from secret store with according service
     *
     * @param service used service
     * @return secret according to service
     */
    public String get(String service) {
        return  get(service, "");
    }

    /**
     * Read secrets from keychain by identifying service and account
     *
     * @param service used service
     * @param account used account
     * @return secret according to service and account
     */
    public String get(String service, String account) {
        if (keyring == null) {
            return fallback.getOrDefault(service, "");
        }
        try {
            return keyring.getPassword(PREFIX + ":" + service, account);
        } catch (PasswordAccessException e) {
            LOGGER.warn("Could not get secret from keychain: " + e.getMessage(), e);
            return fallback.getOrDefault(account + "@" + service, "");
        }
    }

    /**
     * Put secret into secret store with according service
     */
    public void put(String service, String password) {
        put(service, "", password);
    }

    /**
     * Put secret into secret store with related service and account
     *
     * @param service used service
     * @param account used account
     * @param password secret to be saved in to secret store
     */
    public void put(String service, String account, String password) {
        if (keyring == null) {
             fallback.put(service, password);
        }
        try {
             keyring.setPassword(PREFIX + ":" + service, account, password);
        } catch (PasswordAccessException e) {
            LOGGER.warn("Could not save secret to keychain: " + e.getMessage(), e);
            fallback.put(account + "@" + service, password);
        }
    }

}
