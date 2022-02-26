package org.jabref.logic.importer.fileformat;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.jabref.logic.importer.Importer;
import org.jabref.logic.importer.ParserResult;
import org.jabref.logic.util.OS;
import org.jabref.logic.util.StandardFileType;
import org.jabref.model.entry.AuthorList;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.Month;
import org.jabref.model.entry.field.Field;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.field.UnknownField;
import org.jabref.model.entry.identifier.DOI;
import org.jabref.model.entry.types.EntryType;
import org.jabref.model.entry.types.IEEETranEntryType;
import org.jabref.model.entry.types.StandardEntryType;

public class RisImporter extends Importer {

    private static final Pattern RECOGNIZED_FORMAT_PATTERN = Pattern.compile("TY  - .*");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
    // stores all the date tags from highest to lowest priority
    private static final List<String> dateTagOrder = List.of("Y1", "PY", "DA", "Y2");

    @Override
    public String getName() {
        return "RIS";
    }

    @Override
    public StandardFileType getFileType() {
        return StandardFileType.RIS;
    }

    @Override
    public String getDescription() {
        return "Imports a Biblioscape Tag File.";
    }

    @Override
    public boolean isRecognizedFormat(BufferedReader reader) throws IOException {
        // Our strategy is to look for the "TY  - *" line.
        return reader.lines().anyMatch(line -> RECOGNIZED_FORMAT_PATTERN.matcher(line).find());
    }

    @Override
    public ParserResult importDatabase(BufferedReader reader) throws IOException {
        List<BibEntry> bibitems = new ArrayList<>();

        // use optional here, so that no exception will be thrown if the file is empty
        String linesAsString = reader.lines().reduce((line, nextline) -> line + "\n" + nextline).orElse("");

        String[] entries = linesAsString.replace("\u2013", "-").replace("\u2014", "--").replace("\u2015", "--")
                                        .split("ER  -.*(\\n)*");

        for (String entryText : entries) {
            bibitems.add(parseEntry(entryText));
        }
        return new ParserResult(bibitems);
    }

    private BibEntry parseEntry(String entryText) {
        String dateTag = "";
        String dateValue = "";
        int datePriority = dateTagOrder.size();
        int tagPriority;

        EntryType type = StandardEntryType.Misc;
        String author = "";
        String editor = "";
        String startPage = "";
        String endPage = "";
        String comment = "";
        Optional<Month> month = Optional.empty();
        Map<Field, String> fields = new HashMap<>();

        List<String> tagLines = preprocessLines(entryText.split("\n"));

        for (int j = 0; j < tagLines.size(); j++) {
            String entry = tagLines.get(j);

            String tag = entry.substring(0, 2);
            String value = entry.substring(5).trim();

            if ("TY".equals(tag)) {
                type = parseType(value);
            } else if ("T1".equals(tag) || "TI".equals(tag)) {
                fields.put(StandardField.TITLE, parseTitle(value, fields.get(StandardField.TITLE)));
            } else if ("BT".equals(tag)) {
                fields.put(StandardField.BOOKTITLE, value);
            } else if (RisTagMatcher.isSecondaryJournalTag(tag) && (fields.get(StandardField.JOURNAL) == null || fields.get(StandardField.JOURNAL).equals(""))) {
                fields.put(StandardField.JOURNAL, value);
            } else if (RisTagMatcher.isJournalTag(tag)) {
                // if this field appears then this should be the journal title
                fields.put(StandardField.JOURNAL, value);
            } else if ("T3".equals(tag)) {
                fields.put(StandardField.SERIES, value);
            } else if (RisTagMatcher.isAuthorTag(tag)) {
                if ("".equals(author)) {
                    author = value;
                } else {
                    author += " and " + value;
                }
            } else if ("ED".equals(tag)) {
                if (editor.isEmpty()) {
                    editor = value;
                } else {
                    editor += " and " + value;
                }
            } else if ("JA".equals(tag)) {
                if (type.equals(StandardEntryType.InProceedings)) {
                    fields.put(StandardField.BOOKTITLE, value);
                } else {
                    fields.put(StandardField.JOURNAL, value);
                }
            } else if ("LA".equals(tag)) {
                fields.put(StandardField.LANGUAGE, value);
            } else if ("CA".equals(tag)) {
                fields.put(new UnknownField("caption"), value);
            } else if ("DB".equals(tag)) {
                fields.put(new UnknownField("database"), value);
            } else if (RisTagMatcher.isNumberTag(tag)) {
                fields.put(StandardField.NUMBER, value);
            } else if ("SP".equals(tag)) {
                startPage = value;
            } else if ("PB".equals(tag)) {
                if (type.equals(StandardEntryType.PhdThesis)) {
                    fields.put(StandardField.SCHOOL, value);
                } else {
                    fields.put(StandardField.PUBLISHER, value);
                }
            } else if ("AD".equals(tag) || "CY".equals(tag) || "PP".equals(tag)) {
                fields.put(StandardField.ADDRESS, value);
            } else if ("EP".equals(tag)) {
                endPage = value;
                if (!endPage.isEmpty()) {
                    endPage = "--" + endPage;
                }
            } else if ("ET".equals(tag)) {
                fields.put(StandardField.EDITION, value);
            } else if ("SN".equals(tag)) {
                fields.put(StandardField.ISSN, value);
            } else if ("VL".equals(tag)) {
                fields.put(StandardField.VOLUME, value);
            } else if ("N2".equals(tag) || "AB".equals(tag)) {
                String oldAb = fields.get(StandardField.ABSTRACT);
                if (oldAb == null) {
                    fields.put(StandardField.ABSTRACT, value);
                } else if (!oldAb.equals(value) && !value.isEmpty()) {
                    fields.put(StandardField.ABSTRACT, oldAb + OS.NEWLINE + value);
                }
            } else if ("UR".equals(tag) || "L2".equals(tag) || "LK".equals(tag)) {
                fields.put(StandardField.URL, value);
            } else if (((tagPriority = dateTagOrder.indexOf(tag)) != -1) && (value.length() >= 4)) {

                if (tagPriority < datePriority) {
                    String year = value.substring(0, 4);

                    try {
                        Year.parse(year, formatter);
                        // if the year is parsebale we have found a higher priority date
                        dateTag = tag;
                        dateValue = value;
                        datePriority = tagPriority;
                    } catch (DateTimeParseException ex) {
                        // We can't parse the year, we ignore it
                    }
                }
            } else if ("KW".equals(tag)) {
                if (fields.containsKey(StandardField.KEYWORDS)) {
                    String kw = fields.get(StandardField.KEYWORDS);
                    fields.put(StandardField.KEYWORDS, kw + ", " + value);
                } else {
                    fields.put(StandardField.KEYWORDS, value);
                }
            } else if ("U1".equals(tag) || "U2".equals(tag) || "N1".equals(tag)) {
                if (!comment.isEmpty()) {
                    comment = comment + OS.NEWLINE;
                }
                comment = comment + value;
            } else if ("M3".equals(tag) || "DO".equals(tag)) {
                addDoi(fields, value);
            } else if ("C3".equals(tag)) {
                fields.put(StandardField.EVENTTITLE, value);
            } else if ("RN".equals(tag)) {
                fields.put(StandardField.NOTE, value);
            } else if ("ST".equals(tag)) {
                fields.put(StandardField.SHORTTITLE, value);
            } else if ("C2".equals(tag)) {
                fields.put(StandardField.EPRINT, value);
                fields.put(StandardField.EPRINTTYPE, "pubmed");
            } else if ("TA".equals(tag)) {
                fields.put(StandardField.TRANSLATOR, value);

                // fields for which there is no direct mapping in the bibtext standard
            } else if ("AV".equals(tag)) {
                fields.put(new UnknownField("archive_location"), value);
            } else if ("CN".equals(tag) || "VO".equals(tag)) {
                fields.put(new UnknownField("call-number"), value);
            } else if ("NV".equals(tag)) {
                fields.put(new UnknownField("number-of-volumes"), value);
            } else if ("OP".equals(tag)) {
                fields.put(new UnknownField("original-title"), value);
            } else if ("RI".equals(tag)) {
                fields.put(new UnknownField("reviewed-title"), value);
            } else if ("RP".equals(tag)) {
                fields.put(new UnknownField("status"), value);
            } else if ("SE".equals(tag)) {
                fields.put(new UnknownField("section"), value);
            } else if ("ID".equals(tag)) {
                fields.put(new UnknownField("refid"), value);
            }
            fields.put(StandardField.PAGES, startPage + endPage);
        }

        // fix authors
        if (!author.isEmpty()) {
            author = AuthorList.fixAuthorLastNameFirst(author);
            fields.put(StandardField.AUTHOR, author);
        }
        if (!editor.isEmpty()) {
            editor = AuthorList.fixAuthorLastNameFirst(editor);
            fields.put(StandardField.EDITOR, editor);
        }
        if (!comment.isEmpty()) {
            fields.put(StandardField.COMMENT, comment);
        }

        // if we found a date
        if (dateTag.length() > 0) {
            fields.put(StandardField.YEAR, dateValue.substring(0, 4));
            month = processMonth(dateValue);
        }

        // Remove empty fields:
        fields.entrySet().removeIf(key -> (key.getValue() == null) || key.getValue().trim().isEmpty());

        // create one here
        // type is set in the loop above
        BibEntry entry = new BibEntry(type);
        entry.setField(fields);
        // month has a special treatment as we use the separate method "setMonth" of BibEntry instead of directly setting the value
        month.ifPresent(entry::setMonth);
        return entry;
    }

    /**
     * Preprocess all lines, merging lines that do not start with a RIS tag into the previous RIS tag line
     */
    private List<String> preprocessLines(String[] unprocessedLines) {
        List<String> processedLines = new ArrayList<>();
        if (unprocessedLines.length == 0) {
            return processedLines;
        }

        StringBuilder currentLine = new StringBuilder(unprocessedLines[0]);
        for (int i = 1; i < unprocessedLines.length; ++i) {
            String nextLine = unprocessedLines[i].trim();

            if (containsRisTag(nextLine)) {
                String current = currentLine.toString();
                if (containsRisTag(current)) {
                    processedLines.add(current);
                }
                currentLine = new StringBuilder(nextLine);
            } else {
                // Continue on previous tag
                currentLine.append(' ');
                currentLine.append(nextLine);
            }
        }
        processedLines.add(currentLine.toString());
        return processedLines;
    }

    private boolean containsRisTag(String line) {
        return line.matches("^[A-Z0-9]{2}  -.*");
    }

    private EntryType parseType(String value) {
        return switch (value) {
            case "BOOK" -> StandardEntryType.Book;
            case "JOUR", "MGZN" -> StandardEntryType.Article;
            case "THES" -> StandardEntryType.PhdThesis;
            case "UNPB" -> StandardEntryType.Unpublished;
            case "RPRT" -> StandardEntryType.TechReport;
            case "CONF" -> StandardEntryType.InProceedings;
            case "CHAP" -> StandardEntryType.InCollection;
            case "PAT" -> IEEETranEntryType.Patent;
            default -> StandardEntryType.Misc;
        };
    }

    private String parseTitle(String value, String currentTitle) {
        String newTitle = currentTitle;
        if (currentTitle == null) {
            newTitle = value;
        } else {
            if (currentTitle.endsWith(":") || currentTitle.endsWith(".") || currentTitle.endsWith("?")) {
                newTitle += " " + value;
            } else {
                newTitle += ": " + value;
            }
        }

        return newTitle.replaceAll("\\s+", " "); // Normalize whitespaces
    }

    private Optional<Month> processMonth(String dateValue) {
        String[] parts = dateValue.split("/");
        if ((parts.length > 1) && !parts[1].isEmpty()) {
            try {
                int monthNumber = Integer.parseInt(parts[1]);
                return Month.getMonthByNumber(monthNumber);
            } catch (NumberFormatException ex) {
                // The month part is unparseable, so we ignore it.
            }
        }
        return Optional.empty();
    }

    private void addDoi(Map<Field, String> hm, String val) {
        Optional<DOI> parsedDoi = DOI.parse(val);
        parsedDoi.ifPresent(doi -> hm.put(StandardField.DOI, doi.getDOI()));
    }
}
