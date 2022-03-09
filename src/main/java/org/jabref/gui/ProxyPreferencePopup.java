package org.jabref.gui;

import org.jabref.logic.l10n.Localization;
import org.jabref.preferences.PreferencesService;

import java.util.Optional;

public class ProxyPreferencePopup {

    public static void askForPasswordAndWait(DialogService dialogService, PreferencesService preferences) {
        Optional<String> password = dialogService.showInputDialogWithDisableAndWait(
                Localization.lang("No proxy password found"),
                Localization.lang("Please enter password"),
                Localization.lang("Disable proxy"),
                preferences.getProxyPreferences()::setUseProxy);
        password.ifPresent(s -> preferences.getPasswordProvider().set(s));

    }

}
