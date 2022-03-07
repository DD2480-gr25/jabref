package org.jabref.preferences.provider;

import java.util.Map;
import java.util.prefs.Preferences;

public class ValueProviderFactory {

    private final Preferences prefs;
    private final Map<String, Object> defaults;

    public ValueProviderFactory(Preferences prefs, Map<String, Object> defaults) {
        this.prefs = prefs;
        this.defaults = defaults;
    }

    public PreferencesBooleanValueProvider getPrefsBooleanProvider(String key) {
        return new PreferencesBooleanValueProvider(prefs, key, (Boolean) defaults.get(key));
    }

    public PreferencesStringValueProvider getPrefsStringProvider(String key) {
        return new PreferencesStringValueProvider(prefs, key, (String) defaults.get(key));
    }

    public <T> SwitchableValueProvider<T> getSwitchable(ValueProvider<T> first, ValueProvider<T> second, String key) {
        return new SwitchableValueProvider<>(first, second, (T) defaults.get(key));
    }
}
