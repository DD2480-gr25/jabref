
# DD2480 Group 25 Workspace

A working markdown file to write down any findings throughout our work on assignment 3

## Functions with the highest cyclomatic complexity

1. `RTFChars::transformSpecialCharacter`@`208-345@src\main\java\org\jabref\logic\layout\format\RTFChars.java`
2. `RisImporter::importDatabase`@`58-312@src\main\java\org\jabref\logic\importer\fileformat\RisImporter.java`
3. `GvkParser::parseEntry`@`74-442@src\main\java\org\jabref\logic\importer\fileformat\GvkParser.java`
4. `BibEntry::getSourceField`@`139-252@src\main\java\org\jabref\model\entry\BibEntry.java`
5. `FieldNameLabel::getDescription`@`36-250@src\main\java\org\jabref\gui\fieldeditors\FieldNameLabel.java`

## Task 2

We are using the gradle task `verification.jacocoTestReport`

### In the five functions with high cyclomatic complexity...

#### what is the current branch coverage?

1. `RTFChars::transformSpecialCharacter` => 99% branch coverage (1 missed, 293 covered)
2. `RisImporter::importDatabase` => 87% branch coverage (28 missed, 184 covered)
3. `GvkParser::parseEntry` => 73% branch coverage (42 missed, 114 covered)
4. `BibEntry::getSourceField` => 89% branch coverage (17 missed, 141 covered)
5. `FieldNameLabel::getDescription` => 0% branch coverage (101 missed, 0 covered)

#### Is branch coverage higher or lower than in the rest of the code?

The branch coverage appears significantly higher for 4 out of 5 methods than the average coverage for the codebase of approximately 45%.
The only outlier is `FieldNameLabel::getDescription`.

### Identify the requirements that are tested or untested by the given test suite

#### RTFChars::transformSpecialCharacter

Tested:

1. Code transforms unicode representation of special characters into their base equivalent:
   1. All variations of 'A', 'a'
   2. All variations of 'C', 'c'
   3. All variations of 'D', 'd'
   4. All variations of 'E', 'e'
   5. All variations of 'G', 'g'
   6. All variations of 'H', 'h'
   7. All variations of 'I', 'i'
   8. All variations of 'J', 'j'
   9. All variations of 'K', 'k'
   10. All variations of 'L', 'l'
   11. All variations of 'N', 'n'
   12. All variations of 'O'
   13. All variations of 'R', 'r'
   14. All variations of 'S', 's'
   15. All variations of 'T', 't'
   16. All variations of 'U', 'u'
   17. All variations of 'W', 'w'
   18. All variations of 'Y', 'y'
   19. All variations of 'Z', 'z'
   20. 'Æ', 'æ' to 'AE', 'ae'
   21. 'Œ', 'œ' to 'OE', 'oe'
   22. 'Þ' to 'TH'
   23. 'ß' to 'ss'
   24. '¡' to '!'

Untested:

1. All variations of 'o' (characters 242-248 exc. 247, 333, 335)

#### RisImporter::importDatabase

Fully tested:

1. Parsing of RIS tags:
   1. TY
   2. T1, TI
   3. BT
   4. JO, J1, JF
   5. T3
   6. ED
   7. LA
   8. CA
   9. DB
   10. SP
   11. PB
   12. EP
   13. ET
   14. SN
   15. VL
   16. N2, AB
   17. KW
   18. U1, U2, N1
   19. M3, DO
2. Parsing of date tags with correct priority
3. Should throw exception if string input is null
4. Should throw exception if called with null for BufferedReader
5. Fixing author and editor first name and last name ordering

Untested:

1. Parsing of fields that have no direct mapping in the bibtext standard: AV, CN, NV, OP, RI, RP, SE
2. Parsing of RIS tags:
   1. C3
   2. RN
   3. ST
   4. C2
   5. TA
3. When parsing line with tags "JA" and "JF", setting the line value to the journal field if the entry type is not Conference Proceedings

When identifying these requirements we noticed some dead code that we will recommend removing, such as a duplicate `else if` condition for the tag "DB"

#### GvkParser::parseEntry

Tested:

1. Parsing of GVK query tags:
   1. mak tag 002@
   2. ppn tag 003@
   3. author tags 028A, 028B
   4. editor tag 028C
   5. title tag 021A
   6. publisher tag 033A
   7. year tag 011@
   8. overwrite other tags with information from the 036D tag
   9. series tag 036E
   10. note tag 037A
   11. edition tag 032@
   12. isbn tag 004A
   13. page total tag 034D
   14. proceedings tag 030F
2. Should return empty on bad query
3. Should parse subtitles
4. Parsing an entry of "Default" type

Untested:

1. Parsing an "028A" author tag when a previous author tag has already been parsed
2. Parsing an "028B" author tag when no other author tag has preceded it
3. Parsing of GBK query tags
   1. "031A" denoting year, volume, number, pages
   2. Subfield "b" of "037C" for setting value of address, if no "033A" tag was previously found
   3. "027D" denoting the journal or book title + publisher with address
4. Parsing an entry of type "InCollection"
5. Parsing an entry of type "Article"
6. Parsing an entry of type "PhdThesis" (sourced from tag "037C" with subfield "a")

#### BibEntry::getSourceField

Tested:

1. Handle special field mappings, checking author (All 28 branches tested)

Untested:

1. Sort out forbidden fields (1 out of 12 branches missed)
2. Handle special field mappings, checking book title (5 out of 58 branches missed)
3. Handle special field mappings, checking journal title (8 out of 18 branches missed)

#### FieldNameLabel::getDescription

Nothing tested.

Untested:

1. If the input field is a "standard field" the code returns descriptions for the following fields:

   1. ABSTRACT
   2. ADDENDUM
   3. AFTERWORD
   4. ANNOTE
   5. ANNOTATOR
   6. AUTHOR
   7. BOOKSUBTITLE
   8. BOOKTITLE
   9. BOOKTITLEADDON
   10. CHAPTER
   11. COMMENT
   12. COMMENTATOR
   13. DATE
   14. DOI
   15. EDITION
   16. EDITOR
   17. EDITORA
   18. EDITORB
   19. EDITORC
   20. EDITORTYPE
   21. EDITORATYPE
   22. EDITORBTYPE
   23. EDITORCTYPE
   24. EID
   25. EPRINT
   26. PRIMARYCLASS
   27. ARCHIVEPREFIX
   28. EVENTDATE
   29. EVENTTITLE
   30. EVENTTITLEADDON
   31. PDF
   32. FOREWORD
   33. HOWPUBLISHED
   34. SCHOOL
   35. INTRODUCTION
   36. ISBN
   37. ISRN
   38. ISSN
   39. ISSUE
   40. ISSUESUBTITLE
   41. ISSUETITLE
   42. JOURNALSUBTITLE
   43. JOURNAL
   44. LABEL
   45. LANGUAGE
   46. LIBRARY
   47. ADDRESS
   48. MAINSUBTITLE
   49. MAINTITLE
   50. MAINTITLEADDON
   51. MONTH
   52. NAMEADDON
   53. NOTE
   54. NUMBER
   55. ORGANIZATION
   56. ORIGDATE
   57. ORIGLANGUAGE
   58. PAGES
   59. PAGETOTAL
   60. PAGINATION
   61. PART
   62. PUBLISHER
   63. PUBSTATE
   64. SERIES
   65. SHORTTITLE
   66. SUBTITLE
   67. TITLE
   68. TITLEADDON
   69. TRANSLATOR
   70. TYPE
   71. URL
   72. URLDATE
   73. VENUE
   74. VERSION
   75. VOLUME
   76. VOLUMES
   77. YEAR
   78. CROSSREF
   79. GENDER
   80. KEYWORDS
   81. RELATED
   82. XREF
   83. GROUPS
   84. OWNER
   85. TIMESTAMP

   And it will return an empty string for the following fields:

   1. ANNOTATION
   2. EPRINTCLASS
   3. EPRINTTYPE
   4. FILE
   5. INSTITUTION
   6. JOURNALTITLE
   7. LOCATION
2. If the field is an instance of "InternalField" and;

   1. If the field is a "KEY_FIELD" the code returns a description.
   2. Otherwise it returns an empty string.
3. If the field is an instance of "SpecialField" it will return a description if the field is;

   1. PRINTED
   2. PRIORITY
   3. QUALITY
   4. RANKING
   5. READ_STATUS
   6. RELEVANCE
      Otherwise it will return an empty string.

### Improve testing coverage

#### RisImporter::importDatabase

RisImporter is tested through the `RISImporterTestFiles` test class which contains a parameterized test method
`testImportEntries` that will load in test cases in the form of .ris files and compare them against "oracle" files
in the form of .bib files. Thus, in order to add more testing coverage, we need to add additional test files
under `src/test/resources/org/jabref/logic/importer/fileformat`.

The following test cases i.e. test files were added:F

1. `RisImporterTestPeriodicalAbbrv.ris`: Tests the following requirement: "When parsing a line with tags "JA" and "JF", setting the line's value should be set to the journal field only if the entry type is not Conference Proceedings"
2. `RisImporterTest10`: Tests parsing of previously untested RIS tags C3, N1, and ST
3. `RisImporterTest11`: Tests parsing of previously untested RIS tags RN, C2, and TA
4. `RisImporterTest12`: Tests parsing of previously untested RIS tags SE and NV
5. `RisImporterTestUnmappedTags`: Tests RIS tags with no direct bibtext-mapping namely RP, AV, CN, OP, RI



## Task 3 - Refactoring Plan

### RTFChars::transformSpecialCharacter
This function is used for converting special unicode characters to their base character counterpart. Example: é -> e.
This is done using if-statements. I identified two main ways to reduce the number of branches in this method:
1. Set up a lookup table (HashMap?) that stores all special character codes and their base character counterparts
2. Split the function into two methods; one for lower case letters and one for upper case, just like in RTFCharsTest (the unit test class).

The first refactor was implemented. It decreased the CCN from 148 to 3, with the drawback of extra memory complexity and a more heavy instance construction of RTFChars. Furthermore, the implementation was not bulletproof as some RTFChar-tests fail after implementation.
These need to be debugged if to be used in production.

### GvkParser::parseEntry
1. Creating helper method for checking and setting StandardFields. Replaces lines 369-377 and 389-433 consisting of many if statements with method calls, reduces code duplication.
2. Reducing code duplication for tag 028A and 028B (author) and 028C (editor) by creating separate helper method.
3. Move the null-check into the RemoveSortCharacters method. Reduces code duplication.
4. Create method for checking tag and getting single subfield. Removes several if statements and reduces code duplication.

### RisImporter::importDatabase

There are many opportunities to split up the code into smaller functions. We would:

1. Extract all logic for processing a single RIS entry into a separate method called `parseEntry(entryText)`
2. Simplify and extract line "preprocessing" (on lines 90-103)
   1. Simplify logic by leveraging regex matching and removing unnecessary inner loop
   2. Extract logic to a separate method called `preprocessLines(String[] lines)`
   3. Run `preprocessLines` method once on all lines ahead of the for loop starting on line 88
3. Extract the if/else nest on lines 110-128 for handling the "TY" tag into method `parseType`
4. Remove redundant predicate for tag "JF"on line 163
5. Remove redundant predicate for tag "N1" on line 238
6. Remove redundant `else if` for tag "DB" on line 253
7. Extract the month parsing logic
8. Extract multi-predicate `else if` statements with methods

### FieldNameLabel::getDescription

This function is used to get the description message of each fields.
The decriptions of three fields are combined in this function.

The refactoring plan is to split the methods into three parts for each field.
The decriptions for each field are indiviually stored in a new class. So in the target method,
it would call other methods for specific field to get the descriptions which avoid lots of
switch statements and reduce complexity.

### BibEntry::getSourceField

One way of refactoring this function is to split the many if statements into separate functions. In places where an if statement first evaluates if something is true regarding the variables `sourceEntry` and `targetEntry` and the body of the if statement consists of additional if functions evaluating `targetField`, the nested if functions could be cut out into a separate function. The problem with this is that in the original function, if the first if statement evaluates to true, but none of the nested ones do, the function will simply proceeed to the next statement, which can't really be acheived when excangilng the nested if statements for something like: `return newFunction(targetField);`. Thus, this is only trivial when none of the specific requirements for entering the if statement body reoccur in some other if statement later the function. 

In a similar vein, there are several if statements with the format: 

`if ((sourceEntry == x && targetEntry == a) || (sourceEntry == x && targetEntry == b) || (sourceEntry == x && targetEntry == c))`

which can be refactored into:

`if (sourceEntry == x) {evaluateTargetEntry(targetEntry)}`

## Task 3 - Refactoring Implementation

#### GvkParser::parseEntry
The refactoring reduces complexity with 38% From CCN=79 to CCN=49

### RisImporter::importDatabase
By applying the refactoring plan, we have managed to reduce CCN from 110 down to 71 which constitutes a 35% reduction

### BibEntry::getSourceField
By applying the refactoring plan, we have managed to reduce CCN from 63 down to 38, which constitutes a 39% reduction
