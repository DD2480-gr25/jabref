package org.jabref.preferences.provider;

import java.util.prefs.Preferences;

public class PreferencesBooleanValueProvider implements ValueProvider<Boolean> {

    private final Preferences prefs;
    private final String key;
    private final Boolean def;

    public PreferencesBooleanValueProvider(Preferences prefs, String key, Boolean def) {
        this.prefs = prefs;
        this.key = key;
        this.def = def;
    }

    @Override
    public Boolean get() {
        return prefs.getBoolean(key, def);
    }

    @Override
    public void set(Boolean newValue) {
        prefs.putBoolean(key, newValue);
    }

    @Override
    public void clear() {
        prefs.remove(key);
    }
}
