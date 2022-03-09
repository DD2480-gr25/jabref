package org.jabref.preferences;

import java.util.HashMap;
import java.util.prefs.Preferences;

import org.jabref.preferences.provider.SessionValueProvider;
import org.jabref.preferences.provider.ValueProviderFactory;
import org.jabref.testutils.MockPreferences;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JabRefPreferencesTest {

    private static final String secret = "very-secret";

    private MockPreferences mockPreferences = new MockPreferences();
    private Preferences preferences = mockPreferences.createMock();

    private ValueProviderFactory providerFactory;

    private JabRefPreferences jabPrefs;

    @BeforeEach
    void setUp() {
        HashMap<String, Object> defaults = new HashMap<>();
        providerFactory = spy(new ValueProviderFactory(preferences, defaults));
        jabPrefs = JabRefPreferences.getInstance(preferences, providerFactory, defaults);
    }

    // FIXME: This test is not testing anything. prefs.getDefaultEncoding() will always return StandardCharsets.UTF_8
    //  because of setUp(): when(prefs.getDefaultEnconding()).thenReturn(StandardCharsets.UTF_8);
    @Test
    void getDefaultEncodingReturnsPreviouslyStoredEncoding() {
        // prefs.setDefaultEncoding(StandardCharsets.UTF_8);
        // assertEquals(StandardCharsets.UTF_8, prefs.getDefaultEncoding());
    }

    // This test satisfies requirement R1 (issue #47). TODO: Remove this comment once documented in report
    @Test
    void mustNotStoreProxyPassword() {
        SessionValueProvider<String> fakeCredentialProvider = new SessionValueProvider<>();
        when(providerFactory.getCredentialProvider(anyString())).thenReturn(fakeCredentialProvider);

        var proxyPreferences = jabPrefs.getProxyPreferences();

        proxyPreferences.setStorePassword(false);
        proxyPreferences.setPassword(secret);

        assertFalse(mockPreferences.getBoolean("storeProxyPassword", true));
        assertNull(mockPreferences.get("proxyPassword", null));
        verify(preferences, never()).put("proxyPassword", secret);
    }

    //This test satisfies requirement R5 (issue #52). TODO: Remove this comment once documented in report
    @Test
    void mustStoreProxyPasswordAsSecureCredential() {
        SessionValueProvider<String> fakeSecureProvider = new SessionValueProvider<>();
        when(providerFactory.getCredentialProvider("proxyPassword")).thenReturn(fakeSecureProvider);
        when(providerFactory.getCredentialProvider(argThat(key -> !key.equals("proxyPassword")))).thenReturn(new SessionValueProvider<>());

        var proxyPreferences = jabPrefs.getProxyPreferences();

        proxyPreferences.setStorePassword(true);
        proxyPreferences.setPassword(secret);

        assertEquals(secret, fakeSecureProvider.get());
    }

    //This test satisfies requirement R5 (issue #52). TODO: Remove this comment once documented in report
    @Test
    void mustRetrieveProxyPasswordAsSecureCredential() {
        String storedPassword = "another password";
        SessionValueProvider<String> fakeSecureProvider = new SessionValueProvider<>();
        fakeSecureProvider.set(storedPassword);

        when(providerFactory.getCredentialProvider("proxyPassword")).thenReturn(fakeSecureProvider);
        when(providerFactory.getCredentialProvider(argThat(key -> !key.equals("proxyPassword")))).thenReturn(new SessionValueProvider<>());

        var proxyPreferences = jabPrefs.getProxyPreferences();
        proxyPreferences.setStorePassword(true);

        assertEquals(storedPassword, proxyPreferences.getPassword());
    }
}
