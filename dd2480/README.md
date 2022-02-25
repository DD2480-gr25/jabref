## Task 3 - Refactoring Plan

### GvkParser::parseEntry
1. Creating helper method for checking and setting StandardFields. Replaces lines 369-377 and 389-433 consisting of many if statements with method calls, reduces code duplication.
2. Reducing code duplication for tag 028A and 028B (author) and 028C (editor) by creating separate helper method.
3. Move the null-check into the RemoveSortCharacters method. Reduces code duplication.
4. Create method for checking tag and getting single subfield. Removes several if statements and reduces code duplication.

### Implementation
Reduced complexity with 38% From CCN=79 to CCN=49