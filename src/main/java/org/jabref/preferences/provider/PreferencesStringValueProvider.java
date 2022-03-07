package org.jabref.preferences.provider;

import java.util.prefs.Preferences;

public class PreferencesStringValueProvider implements ValueProvider<String> {

    private final Preferences prefs;
    private final String key;
    private final String def;

    public PreferencesStringValueProvider(Preferences prefs, String key, String def) {
        this.prefs = prefs;
        this.key = key;
        this.def = def;
    }

    @Override
    public String get() {
        return prefs.get(key, def);
    }

    @Override
    public void set(String newValue) {
        prefs.put(key, newValue);
    }

}
