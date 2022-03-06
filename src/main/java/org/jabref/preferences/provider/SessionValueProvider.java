package org.jabref.preferences.provider;

public class SessionValueProvider<T> implements ValueProvider<T> {

    private T value;

    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(T newValue) {
        value = newValue;
    }

}
