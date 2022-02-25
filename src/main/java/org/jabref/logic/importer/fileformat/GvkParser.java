package org.jabref.logic.importer.fileformat;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jabref.logic.importer.ParseException;
import org.jabref.logic.importer.Parser;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.field.UnknownField;
import org.jabref.model.entry.types.EntryType;
import org.jabref.model.entry.types.StandardEntryType;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GvkParser implements Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(GvkParser.class);

    @Override
    public List<BibEntry> parseEntries(InputStream inputStream) throws ParseException {
        try {
            DocumentBuilder dbuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document content = dbuild.parse(inputStream);
            return this.parseEntries(content);
        } catch (ParserConfigurationException | SAXException | IOException exception) {
            throw new ParseException(exception);
        }
    }

    private List<BibEntry> parseEntries(Document content) {
        List<BibEntry> result = new LinkedList<>();

        // used for creating test cases
        // XMLUtil.printDocument(content);

        // Namespace srwNamespace = Namespace.getNamespace("srw","http://www.loc.gov/zing/srw/");

        // Schleife ueber alle Teilergebnisse
        // Element root = content.getDocumentElement();
        Element root = (Element) content.getElementsByTagName("zs:searchRetrieveResponse").item(0);
        Element srwrecords = getChild("zs:records", root);
        if (srwrecords == null) {
            // no records found -> return empty list
            return result;
        }
        List<Element> records = getChildren("zs:record", srwrecords);
        for (Element record : records) {
            Element e = getChild("zs:recordData", record);
            if (e != null) {
                e = getChild("record", e);
                if (e != null) {
                    BibEntry bibEntry = parseEntry(e);
                    // TODO: Add filtering on years (based on org.jabref.logic.importer.fetcher.transformers.YearRangeByFilteringQueryTransformer.getStartYear)
                    result.add(bibEntry);
                }
            }
        }
        return result;
    }

    private BibEntry parseEntry(Element e) {
        String author = null;
        String editor = null;
        String title = null;
        String publisher = null;
        String year = null;
        String address = null;
        String series = null;
        String edition = null;
        String isbn = null;
        String issn = null;
        String number = null;
        String pagetotal = null;
        String volume = null;
        String pages = null;
        String journal = null;
        String ppn = null;
        String booktitle = null;
        String url = null;
        String note = null;

        String quelle = "";
        String mak = "";
        String subtitle = "";

        EntryType entryType = StandardEntryType.Book; // Default

        // Alle relevanten Informationen einsammeln

        List<Element> datafields = getChildren("datafield", e);
        for (Element datafield : datafields) {
            String tag = datafield.getAttribute("tag");
            LOGGER.debug("tag: " + tag);

            // mak
            if ("002@".equals(tag)) {
                mak = getSubfield("0", datafield);
                if (mak == null) {
                    mak = "";
                }
            }

            // ppn
            ppn = checkAndGetSingleSubfield("003@", tag, "0", datafield, ppn);
            // author
            if ("028A".equals(tag)) {
                author = checkAndGetAuthorOrEditorNames(datafield, author);
            }
            // author (weiterer)
            if ("028B".equals(tag)) {
                author = checkAndGetAuthorOrEditorNames(datafield, author);
            }
            // editor
            if ("028C".equals(tag)) {
                editor = checkAndGetAuthorOrEditorNames(datafield, editor);
            }
            
            // title and subtitle (subtitle must not be null)
            title = checkAndGetSingleSubfield("021A", tag, "a", datafield, title);
            subtitle = checkAndGetSingleSubfield("021A", tag, "d", datafield, subtitle);

            // publisher and address
            if ("033A".equals(tag)) {
                publisher = getSubfield("n", datafield);
                address = getSubfield("p", datafield);
            }

            // year
            year = checkAndGetSingleSubfield("011@", tag, "a", datafield, year);

            // year, volume, number, pages (year bei Zeitschriften (evtl. redundant mit 011@))
            if ("031A".equals(tag)) {
                year = getSubfield("j", datafield);
                volume = getSubfield("e", datafield);
                number = getSubfield("a", datafield);
                pages = getSubfield("h", datafield);
            }

            // 036D seems to contain more information than the other fields
            // overwrite information using that field
            // 036D also contains information normally found in 036E
            if ("036D".equals(tag)) {
                // 021 might have been present
                if (title != null) {
                    // convert old title (contained in "a" of 021A) to volume
                    if (title.startsWith("@")) {
                        // "@" indicates a number
                        title = title.substring(1);
                    }
                    number = title;
                }
                // title and subtitle
                title = getSubfield("a", datafield);
                subtitle = getSubfield("d", datafield);
                volume = getSubfield("l", datafield);
            }

            // series and number
            if ("036E".equals(tag)) {
                series = getSubfield("a", datafield);
                number = getSubfield("l", datafield);
                String kor = getSubfield("b", datafield);

                if (kor != null) {
                    series = series + " / " + kor;
                }
            }

            // note
            if ("037A".equals(tag)) {
                note = getSubfield("a", datafield);
            }

            // edition
            edition = checkAndGetSingleSubfield("032@", tag, "a", datafield, edition);

            // isbn
            if ("004A".equals(tag)) {
                final String isbn10 = getSubfield("0", datafield);
                final String isbn13 = getSubfield("A", datafield);

                if (isbn10 != null) {
                    isbn = isbn10;
                }

                if (isbn13 != null) {
                    isbn = isbn13;
                }
            }

            // Hochschulschriftenvermerk
            // Bei einer Verlagsdissertation ist der Ort schon eingetragen
            if ("037C".equals(tag)) {
                if (address == null) {
                    address = checkAndRemoveSortCharacters(getSubfield("b", datafield));
                }

                String st = getSubfield("a", datafield);
                if ((st != null) && st.contains("Diss")) {
                    entryType = StandardEntryType.PhdThesis;
                }
            }

            // journal oder booktitle

            /* Problematiken hier: Sowohl für Artikel in
             * Zeitschriften als für Beiträge in Büchern
             * wird 027D verwendet. Der Titel muß je nach
             * Fall booktitle oder journal zugeordnet
             * werden. Auch bei Zeitschriften werden hier
             * ggf. Verlag und Ort angegeben (sind dann
             * eigentlich überflüssig), während bei
             * Buchbeiträgen Verlag und Ort wichtig sind
             * (sonst in Kategorie 033A).
             */
            if ("027D".equals(tag)) {
                journal = getSubfield("a", datafield);
                booktitle = getSubfield("a", datafield);
                address = getSubfield("p", datafield);
                publisher = getSubfield("n", datafield);
            }

            // pagetotal
            if ("034D".equals(tag)) {
                pagetotal = getSubfield("a", datafield);

                if (pagetotal != null) {
                    // S, S. etc. entfernen
                    pagetotal = pagetotal.replaceAll(" S\\.?$", "");
                }
            }

            // Behandlung von Konferenzen
            if ("030F".equals(tag)) {
                address = getSubfield("k", datafield);

                if (!"proceedings".equals(entryType)) {
                    subtitle = getSubfield("a", datafield);
                }

                entryType = StandardEntryType.Proceedings;
            }

            // Wenn eine Verlagsdiss vorliegt
            if (entryType.equals(StandardEntryType.PhdThesis) && (isbn != null)) {
                entryType = StandardEntryType.Book;
            }

            // Hilfskategorien zur Entscheidung @article
            // oder @incollection; hier könnte man auch die
            // ISBN herausparsen als Erleichterung für das
            // Auffinden der Quelle, die über die
            // SRU-Schnittstelle gelieferten Daten zur
            // Quelle unvollständig sind (z.B. nicht Serie
            // und Nummer angegeben werden)
            quelle = checkAndGetSingleSubfield("039B", tag, "8", datafield, quelle);
            if ("046R".equals(tag) && ((quelle == null) || quelle.isEmpty())) {
                quelle = getSubfield("a", datafield);
            }

            // URLs behandeln
            if ("009P".equals(tag) && ("03".equals(datafield.getAttribute("occurrence"))
                    || "05".equals(datafield.getAttribute("occurrence"))) && (url == null)) {
                url = getSubfield("a", datafield);
            }
        }

        // Abfangen von Nulleintraegen
        if (quelle == null) {
            quelle = "";
        }

        // Nichtsortierzeichen entfernen
        author = checkAndRemoveSortCharacters(author);
        editor = checkAndRemoveSortCharacters(editor);
        title = checkAndRemoveSortCharacters(title);
        subtitle = checkAndRemoveSortCharacters(subtitle);

        // Dokumenttyp bestimmen und Eintrag anlegen
        if (mak.startsWith("As")) {
            entryType = BibEntry.DEFAULT_TYPE;

            if (quelle.contains("ISBN")) {
                entryType = StandardEntryType.InCollection;
            }
            if (quelle.contains("ZDB-ID")) {
                entryType = StandardEntryType.Article;
            }
        } else if (mak.isEmpty()) {
            entryType = BibEntry.DEFAULT_TYPE;
        } else if (mak.startsWith("O")) {
            entryType = BibEntry.DEFAULT_TYPE;
            // FIXME: online only available in Biblatex
            // entryType = "online";
        }

        /*
         * Wahrscheinlichkeit, dass ZDB-ID
         * vorhanden ist, ist größer als ISBN bei
         * Buchbeiträgen. Daher bei As?-Sätzen am besten immer
         * dann @incollection annehmen, wenn weder ISBN noch
         * ZDB-ID vorhanden sind.
         */
        BibEntry result = new BibEntry(entryType);

        // Zuordnung der Felder in Abhängigkeit vom Dokumenttyp
        result = checkAndSetStandardField(result, StandardField.AUTHOR, author);
        result = checkAndSetStandardField(result, StandardField.EDITOR, editor);
        result = checkAndSetStandardField(result, StandardField.TITLE, title);

        if (!Strings.isNullOrEmpty(subtitle)) {
            // ensure that first letter is an upper case letter
            // there could be the edge case that the string is only one character long, therefore, this special treatment
            // this is Apache commons lang StringUtils.capitalize (https://commons.apache.org/proper/commons-lang/javadocs/api-release/org/apache/commons/lang3/StringUtils.html#capitalize%28java.lang.String%29), but we don't want to add an additional dependency  ('org.apache.commons:commons-lang3:3.4')
            StringBuilder newSubtitle = new StringBuilder(
                    Character.toString(Character.toUpperCase(subtitle.charAt(0))));
            if (subtitle.length() > 1) {
                newSubtitle.append(subtitle.substring(1));
            }
            result.setField(StandardField.SUBTITLE, newSubtitle.toString());
        }
        result = checkAndSetStandardField(result, StandardField.PUBLISHER, publisher);
        result = checkAndSetStandardField(result, StandardField.YEAR, year);
        result = checkAndSetStandardField(result, StandardField.ADDRESS, address);
        result = checkAndSetStandardField(result, StandardField.SERIES, series);
        result = checkAndSetStandardField(result, StandardField.EDITION, edition);
        result = checkAndSetStandardField(result, StandardField.ISBN, isbn);
        result = checkAndSetStandardField(result, StandardField.ISSN, issn);
        result = checkAndSetStandardField(result, StandardField.NUMBER, number);
        result = checkAndSetStandardField(result, StandardField.PAGETOTAL, pagetotal);
        result = checkAndSetStandardField(result, StandardField.PAGES, pages);
        result = checkAndSetStandardField(result, StandardField.VOLUME, volume);
        result = checkAndSetStandardField(result, StandardField.JOURNAL, journal);
        if (ppn != null) {
            result.setField(new UnknownField("ppn_GVK"), ppn);
        }        
        result = checkAndSetStandardField(result, StandardField.URL, url);
        result = checkAndSetStandardField(result, StandardField.NOTE, note);
        
        if ("article".equals(entryType) && (journal != null)) {
            result.setField(StandardField.JOURNAL, journal);
        } else if ("incollection".equals(entryType) && (booktitle != null)) {
            result.setField(StandardField.BOOKTITLE, booktitle);
        }

        return result;
    }

    private BibEntry checkAndSetStandardField(BibEntry result, StandardField field, String fieldString){
        if (fieldString != null) {
            result.setField(field, fieldString);
        }
        return result;
    }

    private String checkAndGetAuthorOrEditorNames(Element datafield, String nameHolder) {
        String firstname = getSubfield("d", datafield);
        String lastname = getSubfield("a", datafield);
        if (nameHolder == null) {
            nameHolder = "";
        } else {
            nameHolder = nameHolder.concat(" and ");
        }
        nameHolder = nameHolder.concat(firstname + " " + lastname);
        return nameHolder;
    }

    private String checkAndGetSingleSubfield(String gvkCode, String tag, String subFieldCode, Element datafield, String dataHolder){
        if (gvkCode.equals(tag)) {
            return getSubfield(subFieldCode, datafield);
        }
        return dataHolder;
    }

    private String getSubfield(String a, Element datafield) {
        List<Element> liste = getChildren("subfield", datafield);

        for (Element subfield : liste) {
            if (subfield.getAttribute("code").equals(a)) {
                return (subfield.getTextContent());
            }
        }
        return null;
    }

    private Element getChild(String name, Element e) {
        if (e == null) {
            return null;
        }
        NodeList children = e.getChildNodes();

        int j = children.getLength();
        for (int i = 0; i < j; i++) {
            Node test = children.item(i);
            if (test.getNodeType() == Node.ELEMENT_NODE) {
                Element entry = (Element) test;
                if (entry.getTagName().equals(name)) {
                    return entry;
                }
            }
        }
        return null;
    }

    private List<Element> getChildren(String name, Element e) {
        List<Element> result = new LinkedList<>();
        NodeList children = e.getChildNodes();

        int j = children.getLength();
        for (int i = 0; i < j; i++) {
            Node test = children.item(i);
            if (test.getNodeType() == Node.ELEMENT_NODE) {
                Element entry = (Element) test;
                if (entry.getTagName().equals(name)) {
                    result.add(entry);
                }
            }
        }

        return result;
    }

    private String checkAndRemoveSortCharacters(String input) {
        if (input != null) {
            return input.replaceAll("\\@", "");
        }
        return input;
    }
}
