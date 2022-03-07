package org.jabref.preferences.provider;

import org.jabref.preferences.SecretStore;

public class CredentialValueProvider<T> implements ValueProvider<T> {

    private final String service;

    public CredentialValueProvider(String key) {
        this.service = key;
    }

    @Override
    public T get() {
        SecretStore secretStore = new SecretStore();
        return (T) secretStore.get(service);
    }

    @Override
    public void set(T newValue) {
        SecretStore secretStore = new SecretStore();
        secretStore.put(service, (String) newValue);
    }
}
