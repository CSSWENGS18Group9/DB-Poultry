# Testing

**Prerequisites**: Familiarity with [`contributing/programming`](https://github.com/CSSWENGS18Group9/DB-Poultry/blob/main/docs/contributing/programming.md) and [Gradle GitHub Actions](https://github.com/gradle/actions).

This section is primarily intended for the [QAs](https://github.com/orgs/CSSWENGS18Group9/teams/qa).

## Unit Testing

Unit testing involves verifying each function individually. For example, if we have a function `func(a, b)` that adds two numbers, then the expected return value is `a + b`. If there is at least one test case where `a = n`, `b = m`, and `func(n, m) != n + m`, then `func()` fails the test.

### JUnit

We use **JUnit** for unit testing both Java and Kotlin code. You don't need to worry about importing JUnit manually—Gradle handles that for you!

Suppose we want to test the following `Calculator` Java class:

```java
package org.db_poultry;

public class Calculator {
    public static int addition(int x, int y) {
        return x + y;
    }

    public static int subtraction(int x, int y) {
        if (x > y) {
            return x - y;
        } else {
            return 0;
        }
    }
}
```


We will create a unit test file inside `app/src/test/kotlin/org/DBPoultry/calculator_test/` named `CalculatorTest.java`.

> As a standard, the file name of a unit test should be `<ClassName>Test.kt`, where `<ClassName>` is the name of the class being tested.

The unit test for the `Calculator` class is as follows:

```kotlin
package org.db_poultry.calculator_test;

import org.db_poultry.Calculator
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class CalculatorTest {
    @Test
    fun testAddition() {
        assertEquals(15, Calculator.addition(10, 5), "10 + 5 = 15")
    }

    @Test
    fun testSubtraction() {
        // this test should fail
        // since as per our code, if the result is negative
        // Calculator.subtraction will return 0
        assertEquals(-7, Calculator.subtraction(3, 10), "3 - 10 = -7")

        // this one will work
        assertEquals(0, Calculator.subtraction(3, 10), "3 - 10 = -7")
    }
}
```

Notice that we test each method in the `Calculator` class and use `assertEquals` because the functions return numbers. JUnit provides various assertion types—refer to the JUnit documentation for more.

Also note that our unit test is written in Kotlin. **We will primarily use Kotlin for unit tests instead of Java to take advantage of Kotlin’s concise syntax and faster development**.

### Doing the Unit Tests

Gradle will do all unit tests automatically. Simply run:

```
$ gradlew test
```

This command shows whether the test run is SUCCESSFUL (all tests passed) or FAILED (at least one test failed). If there are failures, Gradle will generate a detailed report. You can view it at: `app/build/reports/tests/test/index.html`.

The test report will look like the image below:
![image](https://github.com/user-attachments/assets/0e852c54-0c9c-459b-82bb-cf3feaacf396)

## Where JUnit will not work

JUnit may not be effective in the following scenarios:
- Functions with no side effects (i.e., functions that neither return values nor alter variables).
- Functions interacting with external APIs (e.g., MongoDB).
- Asynchronous functions.

## GitHub Actions

Every time somebody commits or creates a pull request, **all unit tests** will be ran automatically by GitHub through [GitHub Actions](https://github.com/CSSWENGS18Group9/DB-Poultry/actions). In the GitHub action workflow page, we can see all commit messages, their status, and if the run was a failure or a success.

Since we had a unit test that failed earlier, we can observe that the GitHub Actions also shows it as a failure:

![image](https://github.com/user-attachments/assets/c0546c3b-660e-4ab7-9a18-79342e4480c2)

## Regression Testing

Regression testing is a more powerful form of testing, ensuring that recent code changes or commits do not negatively affect the existing codebase. We will also use **JUnit** for regression testing.

Instead of writing new regression tests for every commit, **do not remove or delete unit tests**. This way, all unit tests act as a single regression test. Every time we commit something, all previous unit tests (acting as regression tests) must pass.
