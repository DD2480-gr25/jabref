## Task 3 - Refactoring Plan

### GvkParser::parseEntry
1. Reducing code duplication such as for tag 028A and 028B (author) and 028C (editor) by creating separate method handling firstname, lastname related tags.
2. General overview and translation of all german comments (including additions where there are comments missing)
3. Merging lines 327-338 with 369-377
4. 