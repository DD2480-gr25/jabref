package org.jabref.gui.fieldeditors;

import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.stage.Screen;

import org.apache.tika.sax.StandardReference;
import org.jabref.logic.l10n.Language;
import org.jabref.logic.l10n.Localization;
import org.jabref.model.entry.field.Field;
import org.jabref.model.entry.field.InternalField;
import org.jabref.model.entry.field.SpecialField;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.strings.StringUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldNameLabelTest {


    /**
     * Test getDescription() by comparing the output with
     * all the fields descriptions
     */

    @Test
    void getDescriptionTest(){

        List<Map.Entry <Field, String>> descriptionMap = List.of(
            Map.entry(StandardField.ABSTRACT, "This field is intended for recording abstracts, to be printed by a special bibliography style."),
            Map.entry(StandardField.ADDENDUM, "Miscellaneous bibliographic data usually printed at the end of the entry."),
            Map.entry(StandardField.AFTERWORD,"Author(s) of an afterword to the work."),
            Map.entry(StandardField.ANNOTATION,"This field may be useful when implementing a style for annotated bibliographies."),
            Map.entry(StandardField.ANNOTE,"This field may be useful when implementing a style for annotated bibliographies."),
            Map.entry(StandardField.ANNOTATOR,"Author(s) of annotations to the work."),
            Map.entry(StandardField.AUTHOR,"Author(s) of the work."),
            Map.entry(StandardField.BOOKSUBTITLE,"Subtitle related to the \"Booktitle\"."),
            Map.entry(StandardField.BOOKTITLE,"Title of the main publication this work is part of."),
            Map.entry(StandardField.BOOKTITLEADDON,"Annex to the \"Booktitle\", to be printed in a different font."),
            Map.entry(StandardField.CHAPTER,"Chapter or section or any other unit of a work."),
            Map.entry(StandardField.COMMENT,"Comment to this entry."),
            Map.entry(StandardField.COMMENTATOR,"Author(s) of a commentary to the work.\nNote that this field is intended for commented editions which have a commentator in addition to the author. If the work is a stand-alone commentary, the commentator should be given in the author field."),
            Map.entry(StandardField.DATE,"Publication date of the work."),
            Map.entry(StandardField.DOI,"Digital Object Identifier of the work."),
            Map.entry(StandardField.EDITION,"Edition of a printed publication."),
            Map.entry(StandardField.EDITOR,"Editor(s) of the work or the main publication, depending on the type of the entry."),
            Map.entry(StandardField.EDITORA,"Secondary editor performing a different editorial role, such as compiling, redacting, etc."),
            Map.entry(StandardField.EDITORB,"Another secondary editor performing a different role."),
            Map.entry(StandardField.EDITORC,"Another secondary editor performing a different role."),
            Map.entry(StandardField.EDITORTYPE,"Type of editorial role performed by the \"Editor\"."),
            Map.entry(StandardField.EDITORATYPE,"Type of editorial role performed by the \"Editora\"."),
            Map.entry(StandardField.EDITORBTYPE,"Type of editorial role performed by the \"Editorb\"."),
            Map.entry(StandardField.EDITORCTYPE,"Type of editorial role performed by the \"Editorc\"."),
            Map.entry(StandardField.EID,"Electronic identifier of a work.\nThis field may replace the pages field for journals deviating from the classic pagination scheme of printed journals by only enumerating articles or papers and not pages."),
            Map.entry(StandardField.EPRINT,"Electronic identifier of an online publication.\nThis is roughly comparable to a DOI but specific to a certain archive, repository, service, or system."),
            Map.entry(StandardField.EPRINTCLASS,"Additional information related to the resource indicated by the eprint field.\nThis could be a section of an archive, a path indicating a service, a classification of some sort."),
            Map.entry(StandardField.PRIMARYCLASS,"Additional information related to the resource indicated by the eprint field.\nThis could be a section of an archive, a path indicating a service, a classification of some sort."),
            Map.entry(StandardField.EPRINTTYPE,"Type of the eprint identifier, e. g., the name of the archive, repository, service, or system the eprint field refers to."),
            Map.entry(StandardField.ARCHIVEPREFIX,"Type of the eprint identifier, e. g., the name of the archive, repository, service, or system the eprint field refers to."),
            Map.entry(StandardField.EVENTDATE,"Date of a conference, a symposium, or some other event."),
            Map.entry(StandardField.EVENTTITLE,"Title of a conference, a symposium, or some other event.\nNote that this field holds the plain title of the event. Things like \"Proceedings of the Fifth XYZ Conference\" go into the titleaddon or booktitleaddon field."),
            Map.entry(StandardField.EVENTTITLEADDON,"Annex to the eventtitle field.\nCan be used for known event acronyms."),
            Map.entry(StandardField.FILE,"Link(s) to a local PDF or other document of the work."),
            Map.entry(StandardField.PDF,"Link(s) to a local PDF or other document of the work."),
            Map.entry(StandardField.FOREWORD,"Author(s) of a foreword to the work."),
            Map.entry(StandardField.HOWPUBLISHED,"Publication notice for unusual publications which do not fit into any of the common categories."),
            Map.entry(StandardField.INSTITUTION,"Name of a university or some other institution."),
            Map.entry(StandardField.SCHOOL,"Name of a university or some other institution."),
            Map.entry(StandardField.INTRODUCTION,"Author(s) of an introduction to the work."),
            Map.entry(StandardField.ISBN,"International Standard Book Number of a book."),
            Map.entry(StandardField.ISRN,"International Standard Technical Report Number of a technical report."),
            Map.entry(StandardField.ISSN,"International Standard Serial Number of a periodical."),
            Map.entry(StandardField.ISSUE,"Issue of a journal.\nThis field is intended for journals whose individual issues are identified by a designation such as \"Spring\" or \"Summer\" rather than the month or a number. Integer ranges and short designators are better written to the number field."),
            Map.entry(StandardField.ISSUESUBTITLE,"Subtitle of a specific issue of a journal or other periodical."),
            Map.entry(StandardField.ISSUETITLE,"Title of a specific issue of a journal or other periodical."),
            Map.entry(StandardField.JOURNALSUBTITLE,"Subtitle of a journal, a newspaper, or some other periodical."),
            Map.entry(StandardField.JOURNAL,"Name of a journal, a newspaper, or some other periodical."),
            Map.entry(StandardField.LABEL,"Designation to be used by the citation style as a substitute for the regular label if any data required to generate the regular label is missing."),
            Map.entry(StandardField.LANGUAGE,"Language(s) of the work. Languages may be specified literally or as localisation keys."),
            Map.entry(StandardField.LIBRARY,"Information such as a library name and a call number."),
            Map.entry(StandardField.LOCATION,"Place(s) of publication, i. e., the location of the publisher or institution, depending on the entry type."),
            Map.entry(StandardField.ADDRESS,"Place(s) of publication, i. e., the location of the publisher or institution, depending on the entry type."),
            Map.entry(StandardField.MAINSUBTITLE,"Subtitle related to the \"Maintitle\"."),
            Map.entry(StandardField.MAINTITLE,"Main title of a multi-volume book, such as \"Collected Works\"."),
            Map.entry(StandardField.MAINTITLEADDON,"Annex to the \"Maintitle\", to be printed in a different font."),
            Map.entry(StandardField.MONTH,"Publication month."),
            Map.entry(StandardField.NAMEADDON,"Addon to be printed immediately after the author name in the bibliography."),
            Map.entry(StandardField.NOTE,"Miscellaneous bibliographic data which does not fit into any other field."),
            Map.entry(StandardField.NUMBER,"Number of a journal or the volume/number of a book in a series."),
            Map.entry(StandardField.ORGANIZATION,"Organization(s) that published a manual or an online resource, or sponsored a conference."),
            Map.entry(StandardField.ORIGDATE,"If the work is a translation, a reprint, or something similar, the publication date of the original edition."),
            Map.entry(StandardField.ORIGLANGUAGE,"If the work is a translation, the language(s) of the original work."),
            Map.entry(StandardField.PAGES,"One or more page numbers or page ranges.\nIf the work is published as part of another one, such as an article in a journal or a collection, this field holds the relevant page range in that other work. It may also be used to limit the reference to a specific part of a work (a chapter in a book, for example). For papers in electronic journals with anon-classical pagination setup the eid field may be more suitable."),
            Map.entry(StandardField.PAGETOTAL,"Total number of pages of the work."),
            Map.entry(StandardField.PAGINATION,"Pagination of the work. The key should be given in the singular form."),
            Map.entry(StandardField.PART,"Number of a partial volume. This field applies to books only, not to journals. It may be used when a logical volume consists of two or more physical ones."),
            Map.entry(StandardField.PUBLISHER,"Name(s) of the publisher(s)."),
            Map.entry(StandardField.PUBSTATE,"Publication state of the work, e. g., \"in press\"."),
            Map.entry(StandardField.SERIES,"Name of a publication series, such as \"Studies in...\", or the number of a journal series."),
            Map.entry(StandardField.SHORTTITLE,"Title in an abridged form."),
            Map.entry(StandardField.SUBTITLE,"Subtitle of the work."),
            Map.entry(StandardField.TITLE,"Title of the work."),
            Map.entry(StandardField.TITLEADDON,"Annex to the \"Title\", to be printed in a different font."),
            Map.entry(StandardField.TRANSLATOR,"Translator(s) of the \"Title\" or \"Booktitle\", depending on the entry type. If the translator is identical to the \"Editor\", the standard styles will automatically concatenate these fields in the bibliography."),
            Map.entry(StandardField.TYPE,"Type of a \"Manual\", \"Patent\", \"Report\", or \"Thesis\"."),
            Map.entry(StandardField.URL,"URL of an online publication."),
            Map.entry(StandardField.URLDATE,"Access date of the address specified in the url field."),
            Map.entry(StandardField.VENUE,"Location of a conference, a symposium, or some other event."),
            Map.entry(StandardField.VERSION,"Revision number of a piece of software, a manual, etc."),
            Map.entry(StandardField.VOLUME,"Volume of a multi-volume book or a periodical."),
            Map.entry(StandardField.VOLUMES,"Total number of volumes of a multi-volume work."),
            Map.entry(StandardField.YEAR,"Year of publication."),
            Map.entry(StandardField.CROSSREF,"This field holds an entry key for the cross-referencing feature. Child entries with a \"Crossref\" field inherit data from the parent entry specified in the \"Crossref\" field."),
            Map.entry(StandardField.GENDER,"Gender of the author or gender of the editor, if there is no author."),
            Map.entry(StandardField.KEYWORDS,"Separated list of keywords."),
            Map.entry(StandardField.RELATED,"Citation keys of other entries which have a relationship to this entry."),
            Map.entry(StandardField.XREF,"This field is an alternative cross-referencing mechanism. It differs from \"Crossref\" in that the child entry will not inherit any data from the parent entry specified in the \"Xref\" field."),
            Map.entry(StandardField.GROUPS,"Name(s) of the (manual) groups the entry belongs to."),
            Map.entry(StandardField.OWNER,"Owner/creator of this entry."),
            Map.entry(StandardField.TIMESTAMP,"Timestamp of this entry, when it has been created or last modified."),
            Map.entry(InternalField.KEY_FIELD,"Key by which the work may be cited."),
            Map.entry(SpecialField.PRINTED,"User-specific printed flag, in case the entry has been printed."),
            Map.entry(SpecialField.PRIORITY,"User-specific priority."),
            Map.entry(SpecialField.QUALITY,"User-specific quality flag, in case its quality is assured."),
            Map.entry(SpecialField.RANKING,"User-specific ranking."),
            Map.entry(SpecialField.READ_STATUS,"User-specific read status."),
            Map.entry(SpecialField.RELEVANCE,"User-specific relevance flag, in case the entry is relevant.")
        );

        JFXPanel fxPanel = new JFXPanel();


        for ( var entry : descriptionMap ) {
            assertEquals(entry.getValue(), new FieldNameLabel(entry.getKey()).getDescription(entry.getKey()));
        }

        

    }


    
}
