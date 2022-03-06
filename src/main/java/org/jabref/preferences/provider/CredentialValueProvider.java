package org.jabref.preferences.provider;

public class CredentialValueProvider<T> implements ValueProvider<T> {
    @Override
    public T get() {
        // TODO: Integrate with credential framework
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void set(T newValue) {
        // TODO: Integrate with credential framework
        throw new RuntimeException("Not implemented");
    }
}
