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
#### `RTFChars::transformSpecialCharacter`
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

#### `RisImporter::importDatabase`
Tested:
1. a

Untested:
1. b

#### `BibEntry::getSourceField`
Tested:
1. Handle special field mappings, checking author (All 28 branches tested)

Untested:
1. Sort out forbidden fields (1 out of 12 branches missed)
2. Handle special field mappings, checking book title (5 out of 58 branches missed)
3. Handle special field mappings, checking journal title (8 out of 18 branches missed)
