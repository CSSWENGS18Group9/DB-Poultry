# Errors

We will format the errors in the **entire codebase** as follows:

Kotlin:
```kt
println("Error at `<functionName>()` in `<file>.kt`")
println("<Description>.")
```

Java:
```java
System.out.println("Error at `<functionName>()` in `<file>.java`")
System.out.println("<Description>.")
```

> Example:
> ```java
> println("Error at `getDotEnv()` in `App.kt`")
> println("Failed to load environment variables: not found.")          
> ```

## If you're in a `try-catch` statement

Add the following line to the line below the error description.

```java
e.printStackTrace()
```

## When to use error checking?

When we're working with `try-catch` statements, opening a file, working with data structures, and other sections of the codebase that may be error prone.