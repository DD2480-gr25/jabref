package org.jabref.preferences.provider;

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
}
