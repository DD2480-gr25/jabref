package org.jabref.gui.fieldeditors;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.stage.Screen;

import org.jabref.logic.l10n.Localization;
import org.jabref.model.entry.field.Field;
import org.jabref.model.entry.field.InternalField;
import org.jabref.model.entry.field.SpecialField;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.strings.StringUtil;

public class FieldNameLabel extends Label {

    public FieldNameLabel(Field field) {
        super(field.getDisplayName());

        setPadding(new Insets(4, 0, 0, 0));
        setAlignment(Pos.CENTER);
        setPrefHeight(Double.POSITIVE_INFINITY);

        String description = getDescription(field);
        if (StringUtil.isNotBlank(description)) {
            Screen currentScreen = Screen.getPrimary();
            double maxWidth = currentScreen.getBounds().getWidth();
            Tooltip tooltip = new Tooltip(description);
            tooltip.setMaxWidth(maxWidth * 2 / 3);
            tooltip.setWrapText(true);
            this.setTooltip(tooltip);
        }
    }

    public String getDescription(Field field) {
        if (field.isStandardField()) {
            StandardField standardField = (StandardField) field;
            return StandardFieldDescription.getStandardFieldDescription(standardField);
        } else if (field instanceof InternalField) {
            InternalField internalField = (InternalField) field;
            return InternalFieldDescription.getInternalFieldDescription(internalField);
        } else if (field instanceof SpecialField) {
            SpecialField specialField = (SpecialField) field;
            return SpecialFieldDescription.getSpecialFieldDescription(specialField);
        }
        return "";
    }
}
