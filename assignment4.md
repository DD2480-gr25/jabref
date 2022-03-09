# Report for assignment 4

## Project

Name: Jabref

URL: [https://www.jabref.org/](https://www.jabref.org/)

A bibliography management program written in Java.

## Onboarding experience

We chose the same project as for assignment 3, so most of the onboarding for Gradle was already done. For including the java-keyring dependency we spent quite a bit of time trying different solutions unsuccessfully. We finally landed on using JitPack (jitpack.io) for building the GitHub repository so that it then could be included as a Gradle dependancy.

## Effort spent

For each team member, how much time was spent in

TODO

1. plenary discussions/meetings;

2. discussions within parts of the group;

3. reading documentation;

4. configuration and setup;

5. analyzing code/output;

6. writing documentation;

7. writing code;

8. running code?

For setting up tools and libraries (step 4), enumerate all dependencies
you took care of and where you spent your time, if that time exceeds
30 minutes.

## Overview of issue(s) and work done.

**Title:** Do not save password in Preferences

**URL:** [https://github.com/JabRef/jabref/issues/8055](https://github.com/JabRef/jabref/issues/8055)

Add functionality to prompt the user for a password if none was provided in the preferences file, and add a library for managing credentials. Also implement solutions for storing sensitive information in-memory only.

This affects the packages `preferences`, `gui`, and `logic`.

## Requirements for the new feature or requirements affected by functionality being refactored

All requirements have been defined as github issue and are available here: https://github.com/DD2480-gr25/jabref/issues?q=label%3Arequirement+

Please see the individual issues which trace exactly which tests satisfy each individual requirement.

## Code changes

### Patch

We have raised a pull request with all our changes: https://github.com/DD2480-gr25/jabref/pull/56

We have taken care to keep our code change as clean as possible.

## Test results

Automated tests are run using github actions, all recent test result can be found here: https://github.com/DD2480-gr25/jabref/actions/workflows/tests.yml?query=branch%3Aretry-secure-password

Aside from some of the deployment jobs (where some require OS-specific secret variables setup), all CI jobs successfully pass on our pull request.


## UML class diagram and its description

![Before](DD2480_lab4-Before.svg)

![After](DD2480_lab4-After.svg)

### Key changes/classes affected

Optional (point 1): Architectural overview.

Optional (point 2): relation to design pattern(s).

## Further Improvements

### Requirement R3 - Prompt user for network proxy password on startup

Implementation of this was begun but never finished.

There are a number of things that still need to be implemented. Currently, the buttons in the popup are not linked to actions correctly. The use of JavaFX's TextInputDialog would maybe need to be modified in order to allow the "Disable proxy" button to be linked to any action other than closing the popup. The entered password is also not being stored correctly, for unknown reasons. 

The inputField is also not currently suited for a password. The input would need to be hidden, and potentially also checked for validity.

Apart from this, unittests would also be need to be added. 

### Requirement R4 - Prompt user for shared database password on startup

Implementation of this was never started, but a lot of the functionality would be borrowed from the implementation of R3, above.

## Overall experience

### What are your main take-aways from this project? What did you learn? How did you grow as a team, using the Essence standard to evaluate yourself?

Last time we evaluated the team based on the Essence standard, we were somewhere between the "collaborating" and "performing" stages, still working on filling all requirements in the “collaborating” stage, while also well on their way with several, if not most, of the requirements of the “preforming” stage.

At this point, the group does fulfil all the steps in the “collaborating” stage, and has made very good progress in the “performing” stage as well. While there are still the occasional issue cropping up, the team is generally good at addressing and resolving these continuously. So while improvements could still be done to increase effectiveness and improve communication, there are no specific part of the “performing” stage that the team does not fulfill.



Optional (point 6): How would you put your work in context with best software engineering practice?

Optional (point 7): Is there something special you want to mention here?
