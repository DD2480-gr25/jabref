package org.jabref.logic.importer.fileformat;

public class RisTagMatcher {

    public static boolean isJournalTag(String tag) {
        return switch (tag) {
            case "JO", "J1", "JF" -> true;
            default -> false;
        };
    }

    public static boolean isSecondaryJournalTag(String tag) {
        return switch (tag) {
            case "T2", "J2", "JA" -> true;
            default -> false;
        };
    }

    public static boolean isAuthorTag(String tag) {
        return switch(tag) {
            case "AU", "A1", "A2", "A3", "A4" -> true;
            default -> false;
        };
    }

    public static boolean isNumberTag(String tag) {
        return switch (tag) {
            case "IS", "AN", "C7", "M1" -> true;
            default -> false;
        };
    }
}
