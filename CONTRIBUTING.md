# Contributing

[See contributing documentation](https://github.com/CSSWENGS18Group9/DB-Poultry/tree/main/docs/contributing).

That directory contains the following files:

- `programming`: Discusses the programming languages (and tools) of the DBMS, their uses, and other important notes.
- `resources`: Contains the documentation of every programming language, tool, and library used for the project. Also contains references, if any.
- `testing`: Notes on Unit Testing and code practices for JUnit.

Also see template files for [GitHub Issues](https://github.com/CSSWENGS18Group9/DB-Poultry/tree/main/.github/ISSUE_TEMPLATE) and [Pull Requests](https://github.com/CSSWENGS18Group9/DB-Poultry/tree/main/.github/PULL_REQUEST_TEMPLATE).

## _Main_ Branches

This project has two main (or long-lived) branches:
- `main`:
        - **Production**-ready code; deployable
- `development`:
        - **Integration** of features.
        - This is usually the branch we're working with. We will only pull request this to `main` once we hit a significant milestone.
        - Contains stable features.

> Long-lived branch refers to a branch that is not merged to the default branch. That is, these branches will remain in the GitHub repository until the end of development.

Then, we have short-lived branches which will be discussed below. All short-lived branches (excluding hotfixes) will be Pull Requested **and deleted** to `developement`.

## Workflow

This project will use **trunk-based** workflow (see Git Flow [here](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow) and [here](https://docs.github.com/en/get-started/using-github/github-flow)).

Hence, we will follow procedure enumerated below:

### Create an Issue

> Notice a problem? Want to request a feature? Want to write documentation? Create an Issue for it!


See [GitHub documentation](https://docs.github.com/issues).

We will follow the naming convention for Issues:
- **Recommend a feature**: `feature/section/name`
- **Something isn't working**: `bug/section/name`
- **Document a section of the codebase**: `docs/filename`
- **Not required but a cool feature**: `enhancement/section/name`

The `section` refers to the specific section of the software or codebase that the issue is for. For instance, if we have a bug to report in the GUI, we may write the issue `bug/GUI/submit-button`

The `name` must be descriptive yet short. While `filename` (for `docs/`) must be the file that requires documentation.

Create an issue using one of the issue templates in the [`ISSUE_TEMPLATES/`](https://github.com/CSSWENGS18Group9/DB-Poultry/tree/main/.github/PULL_REQUEST_TEMPLATE) directory. 

> There isn't an issue template for enhancement. Hence, you may use feature request but explicitly mention that it is an enhancement.

Populate the fields inside the issue template. Ensure that the content is detailed, correct, complete, and relevant.

Remember to **assign people** and add a **label** for the Issue.

### Create a Branch

> Want to work on an issue? Create a branch!

To allow for the developers, UI/UX, and QA people to work independently. We will separate everybody with their own branch (recall that we're working with GitHub Flow which is branch or trunk-based)!

The name of the branch is **the name of the issue it is addressing**. Hence, it follows that, we can only have one branch per issue (or one isssue per branch)!

> Of course we can have sub-issues under an issue. If that is the case, the no need to have multiple branches. But, you may still opt to separate each sub-issue as a branch.

### Solve the Issue and Document your Changes

Remember that you're not working on your own, so document your code! Documentation shouldn't be an essay--you don't need one for every line you write. But your documentation should be complete, relevant, concise, and correct!

Nobody likes undecipherable code with undecipherable documentation!

As a standard, we will be using a **code formatter**. In particular, we'll be using [Spotless](https://github.com/diffplug/spotless). Again, Gradle will handle this automatically, so no need to worry about it!

We will be using proper Java coding practices, see [this](https://blog.jetbrains.com/idea/2024/02/java-best-practices/).

Furthermore, for code readability, we will avoid using **recursive functions** and **obfuscation**!

### Code Review

> Done with the code? Confident with it? Now it's the QAs time to check if you're correct!

See [GitHub documentation](https://docs.github.com/en/pull-requests).

Create a **Pull Request**, our naming convension for PRs is the same with the convension for Branches and Issues.

Read [`documentation/contribution/testing.md`](https://github.com/CSSWENGS18Group9/DB-Poultry/blob/main/docs/contributing/testing.md) and see the pull request template in the [`PULL_REQUEST_TEMPLATE/`](https://github.com/CSSWENGS18Group9/DB-Poultry/blob/main/.github/PULL_REQUEST_TEMPLATE/pull_request.md) directory.

### Pulling and Merging

Once all tests are sucessful. It's time to pull the PR and merge with the development branch!
