package org.jabref.testutils;

import java.util.HashMap;
import java.util.prefs.Preferences;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

public class MockPreferences {

    private HashMap<String, Object> map = new HashMap<>();

    public void put(String key, String value) {
        map.put(key, value);
    }

    public String get(String key, String def) {
        return (String) map.getOrDefault(key, def);
    }

    public void putBoolean(String key, boolean value) {
        map.put(key, value);
    }

    public boolean getBoolean(String key, boolean def) {
        return (Boolean) map.getOrDefault(key, def);
    }

    public Preferences createMock() {
        Preferences prefs = mock(Preferences.class);
        lenient().when(prefs.get(any(), any())).then(invocation -> get(invocation.getArgument(0), invocation.getArgument(1)));
        lenient().when(prefs.getBoolean(any(), anyBoolean())).then(invocation -> getBoolean(invocation.getArgument(0), invocation.getArgument(1)));
        lenient().doAnswer(invocation -> {
            put(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(prefs).put(anyString(), anyString());

        lenient().doAnswer(invocation -> {
            putBoolean(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(prefs).putBoolean(anyString(), anyBoolean());
        return prefs;
    }
}
