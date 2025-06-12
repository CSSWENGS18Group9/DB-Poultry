# Shipping



## Configuring `jpackage`

We first change our directory
```
app/
├── src/...               <-- [1]
├── build/...             <-- [2]
├── installer/            <-- [3]
│   ├── app-icon.png
│   ├── postgresql/       <-- [4]
│   └── scripts/          <-- [5]
└── shipping/             <-- will make more sense later!

[1] Don't do anything

[2] This gets populated when we run ./gradlew clean build

[3] Installer, a new directory that includes the app-icon and our "external" dependencies (in this case PostreSQL)

[4] The installer (`.exe`) file for PostgreSQL

[5] Installation scrips (`bat` or `exe`) files
```

So, before **shipping** we first run

```
./gradlew clean build
```

to populate our `build/` directory. Then, we add the installation executable files in our `installer/` directory. Also add the installation scripts and `app-icon` during this time.

Then create the fat jar,

```
./gradlew jar
```

to create our `jar` file inside `app/build/libs`, the file name of the `jar` file is: `app-X.X.jar` where `X.X` denotes the version number.

Then, we get the PostgreSQL installer as a (`.exe`) in [here](https://www.enterprisedb.com/downloads/postgres-postgresql-downloads), save the file as `postgresql-17.5.exe` inside `app/installer`. Finally, we can run the `jpackage` command in the terminal.

```
jpackage \
  --name DB-Poultry \                 <- app name
  --vendor "DB-Poultry DBMS Team" \   <- vendor (who made it)
  --app-version X.X \                 <- app version: X.X
  --input app/build/libs  \           <- location of `.jar`
  --main-jar app-X.X.jar  \           <- main `.jar` file
  --main-class org.db_poultry.AppKt \ <- the Driver class
  --type exe \                        <- executable type [1]
  --icon installer/app-icon.png \     <- app icon
  --dest shipping \                   <- shipment dir [2]

[1] Notice that we will create an `.exe` file, so this becomes a native WINDOWS application! Question: "but we used Java to WORO?", this does not necessarily break WORO. By replacing the `type` attribute we still get a fully functioning executable file!

[side note to 1] Quite frankly, given that we use JavaFX which only works on a single Architecture at a time (this is why it doesn't work on Apple Silicon), creating an `exe` file is rendered moot.

[2] This will contain the shipping executable--that is, the `exe` file here is what we will give the client.
```

Another cool thing is that they also don't need to download Java since `jpackage` handles that as well. Furthermore they get the correct JDK version as the JDK version `jpackage` bundles is the version of the JDK that called `jpackage`. That is, since we're using `jdk-21.0.7+6` the JDK version that `jpackage` include in the budle is `jdk-21.0.7+6`.

**Summary**: `jpackage`

- Bundles the correct JDK;
- create an executable file that runs the `.jar` file; and
- runs the installation steps of the external dependencies at start.

## Configuring PostgreSQL

Now that the user has everything they need, we need to configure PostgreSQL.

### Build Configuration at first run

> The source code I will be referring to here is from the database backup creation function, or `theLifesaver`. 

We will check the existence of a config file `.config.db_poultry` to determine if it is the first run of the program. In particular, if it does not exist then we will say that it is the first run of the program.

In that case, we will create the config file, make the backup directory, install PostgreSQL and configure it too.

Doing so, the user wouldn't need to download PostgreSQL and run preliminary SQL commands to give permisions and create the schema.

This routine is implemented inside