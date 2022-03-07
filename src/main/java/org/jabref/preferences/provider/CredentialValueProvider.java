package org.jabref.preferences.provider;

import com.github.javakeyring.PasswordAccessException;
import org.jabref.preferences.SecretStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CredentialValueProvider<T> implements ValueProvider<T> {
    private final String service;
    private SecretStore secretStore;
    private static final Logger LOGGER = LoggerFactory.getLogger(CredentialValueProvider.class);

    public CredentialValueProvider(String service) {
        this.service = service;
        this.secretStore = new SecretStore();
    }

    @Override
    public T get(){
        try {
            return (T) secretStore.get(service);
        } catch (PasswordAccessException e) {
            LOGGER.error("Could not get credential from key ring: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void set(T newValue){
        try {
            secretStore.put(service, (String) newValue);
        } catch (PasswordAccessException e) {
            LOGGER.warn("Could not save secret to keychain: " + e.getMessage(), e);
        }
    }
}
