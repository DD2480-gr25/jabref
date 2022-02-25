package org.jabref.gui.fieldeditors;
import org.jabref.logic.l10n.Localization;
import org.jabref.model.entry.field.InternalField;

class InternalFieldDescription {
    static String getInternalFieldDescription(InternalField internalField){
        switch (internalField) {
            case KEY_FIELD:
                return Localization.lang("Key by which the work may be cited.");
        }
        return "";
    }
    
}
