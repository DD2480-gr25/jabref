package org.jabref.gui.fieldeditors;
import org.jabref.logic.l10n.Localization;
import org.jabref.model.entry.field.SpecialField;

class SpecialFieldDescription {
    static String getSpecialFieldDescription(SpecialField specialField){
        switch (specialField) {
            case PRINTED:
                return Localization.lang("User-specific printed flag, in case the entry has been printed.");
            case PRIORITY:
                return Localization.lang("User-specific priority.");
            case QUALITY:
                return Localization.lang("User-specific quality flag, in case its quality is assured.");
            case RANKING:
                return Localization.lang("User-specific ranking.");
            case READ_STATUS:
                return Localization.lang("User-specific read status.");
            case RELEVANCE:
                return Localization.lang("User-specific relevance flag, in case the entry is relevant.");
        }
        return "";
    }
    
}
