# 1. Project Proposal, Standards, and Plan

**DB-Poultry Development Team**:

- Vienn Balcita* [`Viennbalcita`](https://github.com/Viennbalcita),  
- Stephen Borja* [`OutForMilks`](https://github.com/OutForMilks),  
- Justin Ching† [`JustinChing30`](https://github.com/JustinChing30),  
- Monica Cu† [`keishoo4`](https://github.com/keishoo4),  
- Megan Dasal§ [`megandasal`](https://github.com/megandasal),  
- Sean Dimaunahan† [`Dattebayo2505`](https://github.com/Dattebayo2505),  
- Zhean Ganituen* [`zrygan`](https://github.com/zrygan),  
- Jaztin Jimenez‡ [`jazjimenez`](https://github.com/jazjimenez),  
- Job Lozada†◊ [`lozadajobd`](https://github.com/lozadajobd).

‡ Analyst  
† Development Team  
§ Product Owner  
\* Quality Assurance Team  
◊ Scrum Master

---
GitHub Organization: [`DB-Poultry`](https://github.com/DB-Poultry)

- [1. Project Proposal, Standards, and Plan](#1-project-proposal-standards-and-plan)
  - [1.1. Background, Software Specifications, \& Solution Stack](#11-background-software-specifications--solution-stack)
  - [1.2. Software Development Methodology](#12-software-development-methodology)
  - [1.3. Team Standard and Workflow](#13-team-standard-and-workflow)
    - [1.3.1. Branching Strategy](#131-branching-strategy)
    - [1.3.2. Issues](#132-issues)
    - [1.3.3. Branching](#133-branching)
    - [1.3.4. Development Standards](#134-development-standards)
    - [1.3.5. Pull Requests and Review](#135-pull-requests-and-review)
    - [1.3.6. Merging](#136-merging)
  - [1.4. Gradle](#14-gradle)
    - [1.4.1. Testing via Gradle](#141-testing-via-gradle)
  - [1.5. Continuous Integration and Continious Deployment](#15-continuous-integration-and-continious-deployment)
    - [1.5.1. Pipeline Configuration File (via GitHub actions)](#151-pipeline-configuration-file-via-github-actions)
  - [1.6. References](#16-references)

## 1.1. Background, Software Specifications, & Solution Stack

**Background** |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;DB Poultry is a poultry farm based
in Tanauan City, Batangas. Founded by William Balahadia and Kenneth Dasal in 
2017, DB Poultry raises chickens with a commitment to quality and care. In 
addition to their own poultry operations, DB Poultry also partners with major 
companies such as San Miguel as a contract grower following strict guidelines 
on animal care, feeding, and housing. Once the chickens reach market weight, 
they are returned to the partner companies for processing and distribution. 
This setup allows DB Poultry to concentrate on poultry production while the 
partner company handles marketing and sales.

One of the key parts of the business is management, which Mr. Balahadia is in 
charge of. Management heavily relies on people to track various maintenance 
supplies such as chicken feeds and medicine. Aside from inventory, the company 
also monitors key metrics such as the chickens’ mortality rate to maintain a 
healthy flock and efficient operations. 

Their current database software developed by the same team has a flock and 
supply tracking system.

**Software Specifications** |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;...

> 👷🚧 Working on software specifications... <br>
> ~From the specs:
> > Proposed Application
> > Include a brief description of the application, the intended users and a 
> > list of the features or functionalities.
> Assigned to: @Megan Dasal

**Solution Stack** |&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;_Java_ and _Kotlin_ for 
their portability via the Java Virtual Machine (JVM). Hence, the DBMS is 
operating system-agnostic. The group will primarily use Java for the 
performance-sensitive sections of the DBMS. In this case, the 
performance-sensitive sections are the GUI and the database operations. For the 
GUI, the group will be using _JavaFX_ due to its modern media and graphics 
packages. While for the database, the group will be using PostgreSQL due to its 
speed and modern features. 

In summary, our goal characteristics for the DBMS are the following:

- **Performant**: can handle real-time database operations with minimal delay.
- **Secure**: ensures data confidentiality by user authentication.
- **Usable**: usable without much training; intuitive and user-friendly GUI.
- **Complete**: ensures data organization is complete and relevant.
- **Portable**: operating system-agnostic.

## 1.2. Software Development Methodology

> 👷🚧 Working on software development methodology... <br>
> ~From the specs:
> > Discuss here in detail what software development methodology your group 
> > plans to follow and provide an explanation why this is what you’re 
> > committing to do for your project. Also mention here who is in charge of 
> >updating the status of each requirement card when tracking the user 
> > stories/features.
> Assigned to: @Job Lozada



## 1.3. Team Standard and Workflow

This section outlines the team workflow and standards we follow for contributing to the DB-Poultry project. It is based on **trunk-based development** (inspired by Git Flow) and is designed to ensure clarity, consistency, and maintainability.

### 1.3.1. Branching Strategy

We use both **long-lived** and **short-lived** branches:

**Long-Lived Branches**

- **`main`**: Production-ready code. Always stable and deployable.
- **`development`**: Used for integrating new features. We merge into `main` only after significant milestones or stable releases.

**Short-Lived Branches**

- Created for individual issues or tasks.
- Always based on the naming of the Issue it addresses.
- Merged into `development` and deleted after successful integration.

### 1.3.2. Issues
All development tasks must begin with the creation of a GitHub Issue. This includes bug reports, feature requests, documentation efforts, and enhancements.

Issues must follow the established naming conventions:
- `feature/section/name` – For new feature requests
- `bug/section/name` – For reporting bugs
- `docs/filename` – For documentation-related work
- `enhancement/section/name` – For non-essential improvements (must be labeled explicitly as enhancements)

Each issue should clearly describe the task, include necessary details, and be assigned to the appropriate contributors. Labels should be applied to indicate the nature and priority of the issue. Contributors are encouraged to use the templates provided in the `ISSUE_TEMPLATES/` directory to ensure consistency and completeness.

### 1.3.3. Branching
A dedicated branch must be created for each issue. The branch name must **exactly match** the name of the corresponding issue to maintain traceability. Only one branch should exist per issue. In cases where sub-tasks are defined under a single issue, contributors may optionally create sub-branches for better organization.

### 1.3.4. Development Standards
All implementation must adhere to the project's coding standards and guidelines. The following practices are mandatory:

- Code must follow the Java/Kotlin standards defined in the project's documentation.
- Recursive functions and obfuscated logic must be avoided unless explicitly justified.
- Code must pass all **unit tests** and **integration tests** written using **JUnit**. Failing tests must be resolved before moving forward.

### 1.3.5. Pull Requests and Review
Once development on a branch is complete and the contributor has verified correctness, a **Pull Request (PR)** must be opened.

- The PR title must follow the same naming convention as the issue and branch.
- The corresponding template in `PULL_REQUEST_TEMPLATE/` must be used and completed fully.
- All relevant tests must pass before the PR is eligible for review.
- The QA team will review the submitted work to verify correctness, adherence to standards, and functionality.

### 1.3.6. Merging
Upon successful review and testing, the Pull Request may be merged into the `development` branch. After merging, the short-lived branch associated with the issue must be deleted to keep the repository clean and organized.

---

By adhering to this structured workflow, the development team ensures high-quality code, maintains a clear project history, and supports effective collaboration among all project contributors.

## 1.4. Gradle

_Gradle_ will be used as the build automation tool for the project. 
Specifically, Gradle will be used for building the project, conducting the 
automated unit and integration tests.

### 1.4.1. Testing via Gradle

Testing in this project is built around JUnit and automated through Gradle, ensuring that every commit and pull request maintains the reliability of the codebase.

At the core, we use unit testing to verify the behavior of individual functions. Unit tests are placed under `app/src/test/kotlin`, and while the codebase includes both Java and Kotlin, we primarily write our tests in Kotlin for better readability and conciseness.

Lastly, we're using unit tests as **regression tests**. By not deleting or bypassing existing tests, we ensure that any new change doesn't break existing features. This makes the test suite increasingly valuable over time, acting as a safeguard for the whole codebase.

## 1.5. Continuous Integration and Continious Deployment

The team will use GitHub Actions for CI/CD.

The CI/CD pipeline for this project is designed to automate the build and 
release process of a Java + Kotlin application that depends on a PostgreSQL 
DBMS. Triggered on every **push** or **pull request** to `main`, the pipeline 
begins by provisioning a PostgreSQL 14 service to emulate the test environment. 
Once the database service is up and healthy, the workflow proceeds to set up the 
Java 21 environment using the Temurin (Adoptium) distribution. After checking 
out the repository and ensuring proper permissions on the Gradle wrapper, the 
application is built into a fat JAR using the `./gradlew clean fatJar` (see code 
sections below) command. The pipeline also verifies the existence of the 
generated artifact in the `../app/build/libs` directory.

Once the build is successful, a Git tag is created using a versioning scheme 
based on the run number to uniquely identify each release. Finally, a GitHub 
Release is published using the newly created tag, and the fat JAR artifact is 
uploaded automatically for distribution. This setup ensures a streamlined CI/CD 
process that handles code integration, testing, artifact creation, version 
tagging, and deployment—all without manual intervention.

**Listing: `fatJar` task in Gradle build script**
```kotlin
tasks.register<Jar>("fatJar") {
    group = "build"
    description = "Creates a fat jar including all dependencies"

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    })

    manifest {
        attributes["Main-Class"] = appMainClass
    }

    archiveBaseName.set("db-poultry-all")

    doLast {
        println("Fat jar created at: ${archiveFile.get().asFile.absolutePath}")
    }
}
```

### 1.5.1. Pipeline Configuration File (via GitHub actions)
```yaml
name: CI (FatJar + PostgreSQL)

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest

    services:
      postgres:
        image: postgres:14
        env:
          POSTGRES_USER: postgres
          POSTGRES_PASSWORD: postgres
          POSTGRES_DB: db_poultry_test
        ports:
          - 5432:5432
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    steps:
      - name: Checkout repository
        uses: actions/checkout@v4

      - name: Setup Java 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Wait for PostgreSQL
        run: |
          for i in {1..10}; do
            pg_isready -h localhost -p 5432 -U postgres && break
            echo "Waiting for PostgreSQL..."
            sleep 5
          done

      - name: Make gradlew executable
        run: chmod +x gradlew

      - name: Build project (with fatJar)
        run: ./gradlew clean fatJar
        env:
          DB_URL: jdbc:postgresql://localhost:5432/db_poultry_test
          DB_USERNAME: postgres
          DB_PASSWORD: postgres

      - name: List app/build/libs directory
        run: ls -l app/build/libs || echo "UH OH~ app/build/libs does not exist"


      - name: Create Git tag
        run: |
          git config user.name "github-actions"
          git config user.email "github-actions@github.com"
          git tag v5.0.${{ github.run_number }}
          git push origin v5.0.${{ github.run_number }}

      - name: Create GitHub Release and upload FatJar
        uses: softprops/action-gh-release@v2
        with:
          tag_name: v5.0.${{ github.run_number }}
          files: app/build/libs/db-poultry-all-*.jar
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

## 1.6. References
<!-- List in alphabetical order -->

**Programming Languages**: 
[Java 21 (Adoptium)](https://adoptium.net/), 
[Kotlin](https://kotlinlang.org/)

**Tools**: [Gradle](https://gradle.org/), 
[Github Actions](https://github.com/features/actions)

**Java Libraries and Frameworks**:
[JavaFX](https://openjfx.io/)
[JUnit 5](https://junit.org/), 
