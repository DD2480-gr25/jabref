package org.jabref.manual;

import com.github.javakeyring.PasswordAccessException;
import org.jabref.logic.JabRefException;
import org.jabref.logic.l10n.Localization;
import org.jabref.preferences.JabRefPreferences;
import org.jabref.preferences.SecretStore;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

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
