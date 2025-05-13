# Testing

**Prerequisites**: [`contributing/programming`](https://github.com/CSSWENGS18Group9/DB-Poultry/blob/main/docs/contributing/programming.md) and [Gradle GitHub Actions](https://github.com/gradle/actions).

This section is primarily for the [QAs](https://github.com/orgs/CSSWENGS18Group9/teams/qa).

## Unit Testing

Unit Testing is basically testing each function one-by-one. That is to say, if I have a function `func(a,b)` that adds the two numbers, then our expected return value is `a + b`. If and only if, there is at least one testcase where `a = n` and `b = m` and `func(n,m) != n + m`. Then, `func()` fails the test.

### JUnit

We will be using JUnit for unit testing Java and Kotlin code. No need to think about importing JUnit since Gradle handles that for you already!

Suppose we want to test the following `Calculator` Java class:

```java
package org.DBPoultry;

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

We will create a unit test file inside `src/test/kotlin/org/DBPoultry/calculator_test/` with the file name `CalculatorTest.java`.

> As our standard, for unit test that we will need, its file name is `<ClassName>Test.kt` where `<ClassName>` is the Java or Kotlin class the unit test is testing. 

The unit test for the `Calculator` class is as follows:

```kotlin
package org.DBPoultry.calculator_test;

import org.DBPoultry.Calculator
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class CalculatorTest {
    @Test
    fun testAddition(){
        assertEquals(15, Calculator.addition(10, 5), "10 + 5 = 15")
    }

    @Test
    fun testSubtraction(){
        // this test should fail
        // since as per our code, if the result is negative
        // Calculator.subtraction will return 0
        assertEquals(-7, Calculator.subtraction(3, 10), "3 - 10 = -7")

        // this one will work
        assertEquals(0, Calculator.subtraction(3, 10), "3 - 10 = -7")
    }
}
```

Notice that we test each method inside the `Calculator` class and we use `assertEqual` since our function return value is a number. There are multiple assertions in JUnit, visit the JUnit documentation for more information.

Furthermore, notice that our unit test is in Kotlin. **We will primarily use Kotlin for unit test instead of Java for concise and faster unit testing!**.

### Doing the Unit Tests

Gradle will do all unit tests automatically. Simply run:

```
$ gradlew test
```

This will show if it is SUCCESSFUL (all unit tests passed) or FAILED (at least one unit test failed). In the event of failure, Gradle will generate a report, this report is located in `app/build/reports/tests/test/index.html`.

The test report will look like the image below:
![image](https://github.com/user-attachments/assets/0e852c54-0c9c-459b-82bb-cf3feaacf396)

## Where JUnit will not work

JUnit will not work on functions with no side-effects (functions without a return value, or functions that do not alter the value inside a variable), functions that use external APIs (MongoDB), and asynchronous functions!

## GitHub Actions

Everytime somebody commits or creates a pull request. **All unit tests** will be ran automatically by GitHub through [GitHub Actions](https://github.com/CSSWENGS18Group9/DB-Poultry/actions). In the GitHub action workflow page, we can see all commit messages, their status, and if the run was a failure or a success.

Since we have a unit test earlier that failed, we can see that the GitHub action also shows it failed:

![image](https://github.com/user-attachments/assets/c0546c3b-660e-4ab7-9a18-79342e4480c2)

