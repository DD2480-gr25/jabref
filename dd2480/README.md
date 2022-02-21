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


