package org.jabref.preferences.provider;

import java.util.prefs.Preferences;

import org.jabref.preferences.SecretStore;

import com.github.javakeyring.PasswordAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CredentialValueProvider implements ValueProvider<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CredentialValueProvider.class);

    private final String credentialKey;
    private final SecretStore secretStore;
    private final String def;

    public CredentialValueProvider(String credentialKey, SecretStore secretStore, String def) {
        this.credentialKey = credentialKey;
        this.secretStore = secretStore;
        this.def = def;
    }

    @Override
    public String get() {
        try {
            return secretStore.get(credentialKey);
        } catch (PasswordAccessException e) {
            LOGGER.warn("Could not get credential from key ring: " + e.getMessage());
            return def;
        }
    }

    @Override
    public void set(String newValue) {
        if (newValue == null || newValue.isEmpty()) {
            clear();
            return;
        }

        try {
            secretStore.put(credentialKey, newValue);
        } catch (PasswordAccessException e) {
            LOGGER.warn("Could not save secret to keychain: " + e.getMessage(), e);
        }
    }

    @Override
    public void clear() {
        try {
            secretStore.delete(credentialKey);
        } catch (PasswordAccessException e) {
            LOGGER.warn("Could not delete secret from keychain: " + e.getMessage(), e);
        }
    }

    public void migrateFromPref(Preferences prefs, String key) {
        String preferencevalue = prefs.get(key, null);
        if (preferencevalue == null) {
            return; //  no stored preference
        }

        try {
            secretStore.get(key); // throws exception if key not present
            prefs.remove(key); // key exists, remove preference
            return;
        } catch (PasswordAccessException e) {
            LOGGER.warn("Could not access to secret store: " + e.getMessage(), e);

            try {
                secretStore.put(key, preferencevalue);
                prefs.remove(key); // key migrated, remove preference
            } catch (PasswordAccessException ex) {
                LOGGER.warn("Could not migrate to secret store: " + e.getMessage(), e);
            }
        }
    }
}
