package org.jabref.preferences.provider;

import java.util.Map;
import java.util.prefs.Preferences;

import org.jabref.preferences.SecretStore;

public class ValueProviderFactory {

    private final Preferences prefs;
    private final Map<String, Object> defaults;
    private final SecretStore secretStore;

    public ValueProviderFactory(Preferences prefs, Map<String, Object> defaults) {
        this.prefs = prefs;
        this.defaults = defaults;
        this.secretStore = new SecretStore();
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

    public ValueProvider<String> getCredentialProvider(String key) {
        return new CredentialValueProvider(key, secretStore, (String) defaults.get(key));
    }
}
