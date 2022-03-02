# Report for assignment 3 - group 25

## Project

Name: Jabref

URL: https://www.jabref.org/

A bibliography management program written in Java.

## Onboarding experience

The only build tool we needed was Gradle, which was installed in the IntelliJ IDE by default.

There were instructions for building, running and testing in the README. We first looked in the development dovumentation, however, and there they could not be found. The gradle 'run' script downloaded all the dependencies needed.
The build (and the application) ran without any errors. The tests ran well, too.

We proceeded with the project as planned.

Did it build and run as documented?

## Complexity

_**1. What are your results for ten complex functions?**_

We got very varied results, and we found that online resources, the lecture material and external CCN computation tools all had different ways of counting branches and the final CCN.

The counts we did can be found in issues [#1](https://github.com/DD2480-gr25/jabref/issues/1), [#2](https://github.com/DD2480-gr25/jabref/issues/2), [#3](https://github.com/DD2480-gr25/jabref/issues/3), [#4](https://github.com/DD2480-gr25/jabref/issues/4), [#5](https://github.com/DD2480-gr25/jabref/issues/5).


_**2. Are the functions just complex, or also long?**_

Most of the functions were both complex _and_ long, as a majority of them mainly consisted of a multitude of if-statements or one large switch-statement.


_**3. What is the purpose of the functions?**_

A majority of the functions simply replace a given value or object with a corresponding response. Essentially, they worked as translators.


_**4. Are exceptions taken into account in the given measurements?**_

In a majority of our classes, no exceptions were thrown or caught. Also, when reading about these measurements, there seems to be some discussion going on regarding whether to include exceptions in these measurements or not.

_**5. Is the documentation clear w.r.t. all the possible outcomes?**_

In one class, the documentation was in german. In most cases, the documentation only stated that it translates one value to the other. It did often not provide any further (needed) context.



## Refactoring

Refactoring is described here:
https://github.com/DD2480-gr25/jabref/tree/dd2480/dd2480#readme

## Coverage

### Tools

_**Document your experience in using a "new"/different coverage tool.**_


The JaCoCo coverage tool for java was already integrated in the gradle build environment.
There were settings already made making it easy to just choose to build a JaCoCo coverage report as a xml, csv and html page.


How well was the tool documented? Was it possible/easy/difficult to
integrate it with your build environment?

### Your own coverage tool

--ADD LINK--

We added a flag print to each branch in the code that we identified, so our tool should cover all branches as long as these flags were added correctly.
Of course, some flags were printed multiple times but since our tool filtered out these flags that wasn't much of a problem.

### Evaluation

_**1. How detailed is your coverage measurement?**_


Our coverage tool distinguishes branches by identifying and, or, while, for and if statements. They are the basic symbols we use to reach a new branch.
Since the tool requires manually adding print statements, it can be very detailed.
It also takes into account (albeit unintentionally) how many times a certain branch was reached, although this information is sorted out by the script.

_**2. What are the limitations of your own tool?**_


The obvious limitation is that there is no way to automatically add print statements, so the tool itself is only as detailed as the person who is going through the code.
Another problem is that in order to evaluate if statements with several predicates (i.e. if statements containing && or ||), they have to be rewritten into separate if statements, so that print statements can be added in-between.

_**3. Are the results of your tool consistent with existing coverage tools?**_


The results are not very consistent with other tools, but this is most likely due to differences in counting branches.
Other tools likely evaluate complexity differently, and take other things into account.

## Coverage improvement

_**Show the comments that describe the requirements for the coverage.**_


https://github.com/DD2480-gr25/jabref/blob/dd2480/dd2480/README.md


Report of old coverage: https://github.com/DD2480-gr25/jabref/tree/dd2480/dd2480/coverage_snapshot/original

Report of new coverage: [link]

Test cases added:

git diff ...

Number of test cases added: two per team member (P) or at least four (P+).

## Self-assessment: Way of working

_**Current state according to the Essence standard:**_ Working well

_**Was the self-assessment unanimous? Any doubts about certain items?**_


Yes! We don't really have any regular processes for recurring communications, but it works quite well regardless thanks to communication on Slack.

_**How have you improved so far?**_


Regarding the points in the first two states in Table 8.8 of Essence, not much has changed since the foundation of the group.
The base principles established in the first two meetings have worked well so far. Last time we did this assessment the team was approaching the “In Place” state.
At this point many of the common practises and standards.

_**Where is potential for improvement?**_


Committing to project starting points.
We are getting the work done but we say that we "should start earlier" and rarely do.

## Overall experience

_**What are your main take-aways from this project? What did you learn?**_


Digging through all the branches of a Java method gave an insightful foundation for learning about test coverage later on.
In other words, you learn how the test coverage measurement works by actually manually looking at what the tests cover.

Having used real-world tools for both measuring CCN and test coverage is useful as well.

_**Is there something special you want to mention here?**_


The ease of our onboarding experience was great, and something to keep in mind for future projects.
