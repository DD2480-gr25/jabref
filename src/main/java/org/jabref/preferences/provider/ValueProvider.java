package org.jabref.preferences.provider;

public interface ValueProvider<T> {

    T get();

    void set(T newValue);

}
