package org.jabref.logic.shared.prefs;

import org.jabref.logic.shared.DatabaseConnectionProperties;
import org.jabref.preferences.provider.CredentialValueProvider;
import org.jabref.preferences.provider.SessionValueProvider;
import org.jabref.preferences.provider.SwitchableValueProvider;
import org.jabref.preferences.provider.ValueProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Optional;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class SharedDatabasePreferences {

    private static final String DEFAULT_NODE = "default";
    private static final String PREFERENCES_PATH_NAME = "/org/jabref-shared";

    private static final String SHARED_DATABASE_TYPE = "sharedDatabaseType";
    private static final String SHARED_DATABASE_HOST = "sharedDatabaseHost";
    private static final String SHARED_DATABASE_PORT = "sharedDatabasePort";
    private static final String SHARED_DATABASE_NAME = "sharedDatabaseName";
    private static final String SHARED_DATABASE_USER = "sharedDatabaseUser";
    private static final String SHARED_DATABASE_PASSWORD = "sharedDatabasePassword";
    private static final String SHARED_DATABASE_REMEMBER_PASSWORD = "sharedDatabaseRememberPassword";
    private static final String SHARED_DATABASE_USE_SSL = "sharedDatabaseUseSSL";
    private static final String SHARED_DATABASE_KEYSTORE_FILE = "sharedDatabaseKeyStoreFile";
    private static final String SHARED_DATABASE_SERVER_TIMEZONE = "sharedDatabaseServerTimezone";

    // This {@link Preferences} is used only for things which should not appear in real JabRefPreferences due to security reasons.
    private final Preferences internalPrefs;
    private final SwitchableValueProvider<String> passwordValueProvider;

    private static final Logger LOGGER = LoggerFactory.getLogger(SharedDatabasePreferences.class);


    public SharedDatabasePreferences() {
        this(DEFAULT_NODE);
    }

    public SharedDatabasePreferences(String sharedDatabaseID) {
        internalPrefs = Preferences.userRoot().node(PREFERENCES_PATH_NAME).node(sharedDatabaseID);

        ValueProviderFactory valueProviderFactory = new ValueProviderFactory(internalPrefs, new HashMap<>());
        CredentialValueProvider credentialValueProvider = valueProviderFactory.getCredentialProvider(SHARED_DATABASE_PASSWORD);
        if (getRememberPassword()) {
            credentialValueProvider.migrateFromPref(internalPrefs, SHARED_DATABASE_PASSWORD);
            LOGGER.info("checking if shared database password is stored in plaintext, following line is from plaintext file");
            LOGGER.info(internalPrefs.get(SHARED_DATABASE_PASSWORD, null));
        }

        passwordValueProvider = valueProviderFactory.getSwitchable(credentialValueProvider, new SessionValueProvider<>(), SHARED_DATABASE_PASSWORD);
        passwordValueProvider.setProvider(getRememberPassword());
    }

    public Optional<String> getType() {
        return getOptionalValue(SHARED_DATABASE_TYPE);
    }

    public Optional<String> getHost() {
        return getOptionalValue(SHARED_DATABASE_HOST);
    }

    public Optional<String> getPort() {
        return getOptionalValue(SHARED_DATABASE_PORT);
    }

    public Optional<String> getName() {
        return getOptionalValue(SHARED_DATABASE_NAME);
    }

    public Optional<String> getUser() {
        return getOptionalValue(SHARED_DATABASE_USER);
    }

    public Optional<String> getPassword() {
        return Optional.ofNullable(passwordValueProvider.get());
    }

    public Optional<String> getKeyStoreFile() {
        return getOptionalValue(SHARED_DATABASE_KEYSTORE_FILE);
    }

    public Optional<String> getServerTimezone() {
        return getOptionalValue(SHARED_DATABASE_SERVER_TIMEZONE);
    }

    public boolean getRememberPassword() {
        return internalPrefs.getBoolean(SHARED_DATABASE_REMEMBER_PASSWORD, false);
    }

    public boolean isUseSSL() {
        return internalPrefs.getBoolean(SHARED_DATABASE_USE_SSL, false);
    }

    public void setType(String type) {
        internalPrefs.put(SHARED_DATABASE_TYPE, type);
    }

    public void setHost(String host) {
        internalPrefs.put(SHARED_DATABASE_HOST, host);
    }

    public void setPort(String port) {
        internalPrefs.put(SHARED_DATABASE_PORT, port);
    }

    public void setName(String name) {
        internalPrefs.put(SHARED_DATABASE_NAME, name);
    }

    public void setUser(String user) {
        internalPrefs.put(SHARED_DATABASE_USER, user);
    }

    public void setPassword(String password) {
        passwordValueProvider.set(password);
    }

    public void setRememberPassword(boolean rememberPassword) {
        internalPrefs.putBoolean(SHARED_DATABASE_REMEMBER_PASSWORD, rememberPassword);
        passwordValueProvider.setProvider(rememberPassword);
    }

    public void setUseSSL(boolean useSSL) {
        internalPrefs.putBoolean(SHARED_DATABASE_USE_SSL, useSSL);
    }

    public void setKeystoreFile(String keystoreFile) {
        internalPrefs.put(SHARED_DATABASE_KEYSTORE_FILE, keystoreFile);
    }

    public void setServerTimezone(String serverTimezone) {
        internalPrefs.put(SHARED_DATABASE_SERVER_TIMEZONE, serverTimezone);
    }

    public void clearPassword() {
        passwordValueProvider.clear();
    }

    public void clear() throws BackingStoreException {
        clearPassword();
        internalPrefs.clear();
    }

    private Optional<String> getOptionalValue(String key) {
        return Optional.ofNullable(internalPrefs.get(key, null));
    }

    public static void clearAll() throws BackingStoreException {
        Preferences.userRoot().node(PREFERENCES_PATH_NAME).clear();
    }

    public void putAllDBMSConnectionProperties(DatabaseConnectionProperties properties) {
        assert (properties.isValid());

        setType(properties.getType().toString());
        setHost(properties.getHost());
        setPort(String.valueOf(properties.getPort()));
        setName(properties.getDatabase());
        setUser(properties.getUser());
        setPassword(properties.getPassword());
        setUseSSL(properties.isUseSSL());
        setKeystoreFile(properties.getKeyStore());
        setServerTimezone(properties.getServerTimezone());
    }
}
