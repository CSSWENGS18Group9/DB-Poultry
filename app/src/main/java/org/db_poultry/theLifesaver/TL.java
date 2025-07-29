package org.db_poultry.theLifesaver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import org.db_poultry.App;
import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;
import org.jetbrains.annotations.NotNull;

/*
 * dotConfigFile contents:
 * ==========
 * backup_folder_location dbp_backups
 * last_backup_date October-01-2004
 * backup_intervals 3
 * ==========
 * all are separated by a SINGLE space!
 */

public class TL {
    /**
     * deletes backup folder and the config file, essentially makes
     * it so that the next open of the DBMS is the first
     * open
     */
    public static void wipe(String dbName) {
        System.out.println("~ TL ../ Wiping...");

        // Delete the backup folder recursively
        try {
            Path backupFolderPath = Paths.get(Variables.getBackupFolderPath());

            if (Files.exists(backupFolderPath)) {
                Files.walkFileTree(backupFolderPath, new SimpleFileVisitor<>() {
                    @NotNull
                    @Override
                    public FileVisitResult visitFile(@NotNull Path file, @NotNull BasicFileAttributes attrs)
                            throws IOException {

                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @NotNull
                    @Override
                    public FileVisitResult postVisitDirectory(@NotNull Path dir, IOException exc)
                            throws IOException {

                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
                System.out.println("~ TL ../ Backup folder has been deleted.");

            } else {
                System.out.println("~ TL ../ Backup folder does not exist.");
            }
        } catch (IOException e) {
            generateErrorMessage(
                    "Error at `wipe` in `TL`",
                    "Failed to delete backup folder.",
                    "",
                    e);
        }

        // Delete the config file
        try {
            Path configFilePath = Paths.get(Variables.getDotConfigPath());

            if (Files.exists(configFilePath)) {
                Files.delete(configFilePath);
                System.out.println("~ TL ../ Config file has been deleted.");
            } else {
                System.out.println("~ TL ../ Config files does not exist.");
            }
        } catch (IOException e) {
            generateErrorMessage(
                    "Error at `wipe` in `TL`",
                    "Failed to delete config file.",
                    "",
                    e
            );
        }

        // drop the database and user
        System.out.println("~ TL ../ Dropping database and user: " + dbName);
        String dropDatabaseCommand = "DROP DATABASE IF EXISTS " + dbName + ";";
        String dropUserCommand = "DROP USER IF EXISTS " + dbName + ";";

        try {
            ProcessBuilder pb = new ProcessBuilder("psql", "-U", "postgres");
            pb.environment().put("PGPASSWORD", "password");
            pb.redirectErrorStream(true);

            Process psql = pb.start();

            // run commands
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(psql.getOutputStream()))) {
                writer.write(dropDatabaseCommand);
                writer.newLine();
                writer.flush();

                writer.write(dropUserCommand);
                writer.newLine();
                writer.flush();
            }

            int exitCode = psql.waitFor();
            if (exitCode == 0) {
                System.out.println("~ TL ../ Database and user successfully dropped.");
            } else {
                System.out.println("~ TL ../ Failed to drop database/user. Exit code: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            generateErrorMessage(
                    "Error at `wipe` in `TL`",
                    "Failed to drop PostgreSQL user and database.",
                    "",
                    e
            );
        }

        System.out.println("=================== IMPORTANT! " + "===================");
        System.out.println("=== IF YOU SEE THIS IN SHIPPED CODE    " + "        ===");
        System.out.println("=== THEN SOMEBODY MESSED UP            " + "        ===");
        System.out.println("=== YOU SHOULDN'T BE ABLE TO SEE THIS  " + "        ===");
        System.out.println("=== REMEMBER TO DELETE `wipe`          " + " ===");
        System.out.println("=== BEFORE SHIPPING                    " + "        ===");
        System.out.println("=================== IMPORTANT! " + "===================");
    }

    public static void TL_initPostgres(String username, String password) {
        System.out.println("~ TL ../ DB -- Checking if PostgreSQL exists.");

        // Check if psql exists
        try {
            Process check = new ProcessBuilder("psql", "--version")
                    .redirectErrorStream(true)
                    .start();

            check.waitFor();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(check.getInputStream()))) {
                String line = reader.readLine();
                if (line != null && line.toLowerCase().contains("psql")) {
                    System.out.println("~ TL ../ DB -- PostgreSQL already installed: " + line);
                } else {
                    System.out.println("~ TL ../ DB -- PostgreSQL not installed: " + line);
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("~ TL ../ DB -- PostgreSQL not found.");
            return;
        }

        // Create the SQL init script
        System.out.println("~ TL ../ DB -- Creating DB Admin user and privileges.");

        String[] script = {
                "CREATE USER " + username + " WITH PASSWORD '" + password + "';",
                "CREATE DATABASE " + username + " OWNER " + username + ";",
                "GRANT ALL PRIVILEGES ON DATABASE " + username + " TO " + username + ";",
                "ALTER USER " + username + " WITH SUPERUSER;"
        };

        try {
            Path ts = Files.createTempFile("db_init", ".sql");
            Files.write(ts, List.of(script));

            ProcessBuilder pb = new ProcessBuilder("psql", "-U", "postgres", "-f", ts.toString());
            pb.environment().put("PGPASSWORD", "password");
            pb.redirectErrorStream(true);

            Process psql = pb.start();

            int exitCode = psql.waitFor();
            if (exitCode == 0) {
                System.out.println("~ TL ../ DB -- Job's done.");
            } else {
                System.out.println("~ TL ../ DB -- PostgreSQL command failed with exit code: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            generateErrorMessage("Error at TL_init_postgres",
                    "FATAL. Failed to run SQL init script.",
                    "",
                    e
            );

            throw new RuntimeException(e);
        }

    }

    /**
     * checks if it is the first open and if it is create the
     * configuration file.
     */
    public static void TL_firstOpen(App app) {
        // the basis of checking for the "first open" of the DBMS
        // is if the `.config.dbpoultry` exists
        if (!Files.exists(Paths.get(Variables.getDotConfigPath()))) {
            // if it doesn't. Make it immediately!
            System.out.println("~ TL ../ Detected first startup.");
            System.out.println("~ TL ../ Initialize -- Config.");
            Config.TL_writeConfig(Util.getDateNow());
        } else {
            return;
        }

        // initialize postgresql
        System.out.println("~ TL ../ Initialize -- DB.");
        // TL_initPostgres(app.getDatabaseName());
        TL_initPostgres(app.getDatabaseName(), app.getDatabasePass());

        // make the backup folder
        System.out.println("~ TL ../ Initialize -- Backups.");
        Backup.TL_makeBackupFolder();

        // then create the first database backup
        System.out.println("~ TL ../ Initialize -- Creating first database backup.");
        Backup.TL_createDatabaseBackup(app.getDatabaseName(), app.getDatabasePass());
    }
}

