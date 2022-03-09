package org.jabref.manual;

import java.util.prefs.Preferences;

import org.jabref.preferences.JabRefPreferences;
import org.jabref.preferences.SecretStore;

import com.github.javakeyring.PasswordAccessException;

import static org.jabref.gui.importer.actions.OpenDatabaseAction.LOGGER;

public class WritePasswordPref {
    static public void main(String... args) {
        var prefs = Preferences.userRoot().node("/org/jabref");
        prefs.put("proxyPassword", "test-password");
        LOGGER.info("checking if proxy password is stored in plaintext:");
        LOGGER.info(prefs.get("proxyPassword", null));

        JabRefPreferences.getInstance().getProxyPreferences();

        LOGGER.info("checking if proxy password was migrated from prefs (expect empty line)");
        LOGGER.info(prefs.get("proxyPassword", null));

        try {
            LOGGER.info("checking if proxy password was migrated into keychain (expect \"test-password\")");
            LOGGER.info(new SecretStore().get("proxyPassword"));
        } catch (PasswordAccessException e) {
            e.printStackTrace();
        }
    }
}
