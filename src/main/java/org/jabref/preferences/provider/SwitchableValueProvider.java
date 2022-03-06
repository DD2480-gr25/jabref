package org.jabref.preferences.provider;

public class SwitchableValueProvider<T> implements ValueProvider<T> {

    private final ValueProvider<T> first, second;
    private final T def;

    private boolean isFirst = true;

    public SwitchableValueProvider(ValueProvider<T> first, ValueProvider<T> second, T def) {
        this.first = first;
        this.second = second;
        this.def = def;
    }

    public void setProvider(boolean toFirst) {
        if (toFirst == isFirst) {
            return;
        }

        synchronized (this) {
            T value = current().get();
            current().set(def);
            isFirst = toFirst;
            current().set(value);
        }
    }

    @Override
    public T get() {
        return current().get();
    }

    @Override
    public void set(T newValue) {
        current().set(newValue);
    }

    private ValueProvider<T> current() {
        return isFirst ? first : second;
    }

}
