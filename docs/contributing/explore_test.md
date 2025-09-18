<!--
 - Exploratory Testing Document format
 - By:  Zhean Ganituen (zrygan)
 - On:  2025
 - For: DB-Poultry Database Software

 THE FILENAME of this document must be the ETR Code (described below; in 
 section Exploratory Test Specifications).
 -->
# DB-Poultry Database Software Exploratory Testing Report

<!--
 Exploratory Test Specifications

 Fill in the information below with CORRECT information. Specific instructions
 are enumerated below.
 
 - ETR Code
 The shorthand code for the exploratory test. Suggested ETR Code format:
    <ShortPurpose>_<ShortVersion>_<Date as MMDDYYYY>
 
 Example:
    Version: (SHA) 26760d8c95a99d5e9bb4808b567b9139e3443cc2
    Scope: I want to create a new Member
    
    Possible ETR Code: NewMember_26760d8_10012004

 - Tester 
 Tester's name
 
 - Date
 The date the exploratory test was conducted. For date ranges use a single 
 dash (e.g.,October 1-12, 2025) 
 
 - Purpose 
 Specific software feature or user story the exploratory test targets: 
    example feature: "Create Function for Member Table"
    example user story: "I want to create a new Member"

 - Version
 The specific version of the software under test. Specify as:
    - internal version number: (int) 3.2
    - Git Commit SHA:          (sha) 872b363d2ecb6ae911cfc27c5ed3fa871250d8bd
    - Git Tag:                 (tag) v5.0.42

 - Also See
 If there are other exploratory tests that are SIMILAR or RELATED to this 
 exploratory test add them here. Use the ETR Code of those exploratory tests; 
 separating ETR Codes with a semicolon (;) if multiple are needed.

 Example: 
 NewMember_26760d8_10012004; NewMember_872b363_10262004
 -->
**ETR Code**:   %ExploratoryTestCode%

**Tester**:     %TesterName%

**Date**:       %DateConducted%

**Scope**:      %Feature/Story%

**Version**:    (%type%) %PointerTag%

**Also See**:   %OtherExploratoryTestCodes%

## Assertions/Preconditions
<!-- 
 List any conditions that must be true before the exploratory test begins. 
 If there are none, write `NONE`.
 
 Examples
    Technical Requirements: 
        Java version, 
        PostgreSQL version, etc.

    Software Requirements: 
        Test database loaded, 
        User is logged in, 
        Tables are populated, etc.
 -->

## Scenario
<!-- 
 A NON-TECHNICAL and FREE-FORM description of the test steps. There should only
 be ONE scenario for an exploratory test report.

 Each step is formatted as:
    **Action #**. <description>
    **Expect 1**. <description>

 "Action" specifies the action to be performed by the tester.
 "Expect" specifies what the tester expects to happen after the action.

 Example Scenario:
    Action 1. I want to add a new member
    Action 2. I click the button that shows "Add Member"
    Action 3. I populate the information of the new Member.
    ...

 "Final Expectation" specifies the expected result of the scenario when all
 actions succeed.
 -->

**Action 1**. %Description%
**Expect 1**. %Description%

**Action 2**. %Description%
**Expect 2**. %Description%

**Action 3**. %Description%
**Expect 3**. %Description%

**Final Expectation**. %ExpectedResult%

## Observation
<!-- 
 A NON-TECHNICAL and FREE-FORM description of your observations throughout
 the exploratory test. This can include one of the following:
    UI quirks           (something moves around)
    Performance quirks  (it gets slow when I do ...)

 This SHOULD NOT INCLUDE bugs or issues. See the Issues section below.

 Format:
 | Doing | Event | Attachments |
 | ----- | ----- | ----------- |
 | %ActionNumber% | %Description% | %Attachments% |

 - Doing: a NON-TECHNICAL and FREE-FORM description of the action being 
 performed when the observed event occured. Provide a short description. 
 Example: "hovering on Add Member button"

 - Event: a NON-TECHNICAL and FREE-FORM description of the observed event.
 Example: "the button grew bigger"

 - Attachments: images (links or actual), videos, etc.
 -->

| Doing | Event | Attachments |
| ----- | ----- | ----------- |
| %ActionNumber% | %Description% | %Attachments% |

## Issues
<!-- 
 A NON-TECHNICAL and FREE-FORM description of your issues and bugs that occered 
 throughout the exploratory test. 

 Format:
 | Issue | Action | Event | Compare |
 | ----- | ------ | ----- | ------- |
 | %number% | %ActionNumber% | %Description% | %Analysis% |

 - Issue: the Issue number from GitHub (you SHOULD publish the Issue in the 
 GitHub repository)
 
 - Action: action being performed when the observed event occured. Indicate
 the action number.

 - Event: a NON-TECHNICAL and FREE-FORM description of the observed event
 or result after performing the action.
 
 Example A1: New member was not added.
 Example B1: New member was added with the incorrect name.

 - Compare: a NON-TECHNICAL and FREE-FORM description and comparative analysis
 of the expectation and actual event.

 Example A2: Expected the new member to be added, actual result did not add this 
 new member.

 Example B2: Expected new member with name "John Doe" to be added, actual result
 added "JOHNDOE". There is no space and is in all capital letters.
 -->

| Issue | Action | Event | Compare |
| ----- | ------ | ----- | ------- |
| %number% | %ActionNumber% | %Description% | %Analysis% |
