# Programming Languages

This project will use both Java and Kotlin.

Download the latest LTS release from [Adoptium (jdk-21.0.7+6)](https://adoptium.net/) and Kotlin 21.20 from [Kotlin's Github Repository](https://github.com/JetBrains/kotlin/releases/tag/v2.1.20).

> Remember to add `JAVA_HOME` (the root directory of the JDK), and the binary files of both Java and Kotlin in your system `PATH` variables.

## When to use Java and Kotlin

**Sections of the code that need to be fast**. Since the database may get big and handling a lot of database operations may be performance intensive, we will use **Java** for those sections via MongoDB.

**The frontend**. We will be using **Java** for the GUI using the JavaFX platform.

**The rest of the codebase** will be using **Kotlin** to take advantage of its human-readability and conciseness.

**For testing**, we may also use **Kotlin**.



## Java + Kotlin (Interoperability)

> This section is very important for: <br>
> @Dattebayo2505 @JustinChing30 @zrygan (devs); @jazjimenez @OutForMilks (QAs).

This section discusses Java and Kotlin interoperability (fancy word for using Kotlin in Java, and the other way around).

### Packages
**Recall.** Java packages (see also [this documentation section](https://docs.oracle.com/javase/tutorial/java/package/index.html)).

Our Application folder is given by the directory tree:

```
app/
├─ build/           ▷ we don't care about this
├─ src/
│  ├─ main/
│  │  ├─ java/
│  │  │  ├─ org/DBPoultry 
│  │  │  │  ├─ package_a/ 
│  │  │  │  └─ package_b/ 
│  │  ├─ kotlin/
│  │  │  ├─ org/DBPoultry 
│  │  │  │  ├─ package_a/ 
│  │  │  │  └─ package_b/ 
│  │  ├─ resources/ ▷ we don't care about this
│  └─ test/         ▷ we don't care about this
└─ build.gradle.kts
```

All `.java` files will be placed in the `java/` directory, while all `.kt` (not `.kts`) files will be placed in the `kotlin/` directory.

All Java files inside the directory `java/org/DBPoultry/<package name>` will have the package:

```java
package org.db_poultry.<package name>;
```

While, all Kotlin files inside the directory `kotlin/org/DBPoultry/<package name>` will have the package:

```kotlin
package org.db_poultry.<package name >
```

**Notice** that if you have the two directories, one in `java/` and the other in `kotlin/`. They will have the *same package*! 

For instance, if we have a file `MyKotlin.kt` inside the package `kotlin/../MyPackage` and `MyJava.java` inside the package `java/../MyPackage`, the two files are in the same package! B

```
app/
├─ build/           
├─ src/
│  ├─ main/
│  │  ├─ java/
│  │  │  ├─ org/DBPoultry 
│  │  │  │  └─ MyPackage/
│  │  │  │     └─ MyJava.java   ▷ Same package
│  │  ├─ kotlin/
│  │  │  ├─ org/DBPoultry 
│  │  │  │  └─ MyPackage/
│  │  │  │     └─ MyKotlin.kt   ▷ Same package
│  │  ├─ resources/ 
│  └─ test/         
└─ build.gradle.kts
```

Hence, if we want to use a Kotlin class that is in the package `kotlin/../MyPackage` inside a Java class in the package `java/../MyPackage` **we do not use** import. We simply declare the package name and use the Kotlin class like normal.

```java
// We want to use MyKotlin.kt inside MyJava.java
// Package of MyKotlin.kt is org.db_poultry.MyPackage

// CORRECT
package org.db_poultry.MyPackage;

// INCORRECT
// import org.db_poultry.MyPackage;
```

### Kotlin Classes

Everytime we use a class writtin in Kotlin we append `Kt`. This is to differentiate between Java-defined and Kotlin-defined classes!

For instance, we want to use the class `Foo` inside a Java file, we will append `Kt` at the end of the class name:

```java
package org.db_poultry.MyPackage;

import org.db_poultry.AnotherPackage.FooKt;
```

Or, we want to use a Kotlin-defined method `my_function` inside a Java file:

```java
package org.db_poultry.MyPackage;

public class App {
    public static void main(String[] args) {
        // I want to use method `my_function` inside a 
        // Kotlin-defined class MyKotlinClass.
        MyKotlinClassKt.myfunction();
    }
}
```

The name of the Kotlin class itself **does not contain** the suffix `Kt`. During the Kotlin compilation process, the compiler appends `Kt`. So, if we have a file `MyKotlinKt.kt`, once compiled this comes `MyKotlinKtKt.class` (notice that Kt is added again!).

### Java Classes

Calling Java-defined classes (or its methods) inside a Kotlin file remain the same. That is, it is the usual Java class calling syntax. 
