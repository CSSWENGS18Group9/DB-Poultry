package org.db_poultry.theLifesaver;

import org.db_poultry.App;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import static org.db_poultry.db.InitializeKt.cleanTables;
import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

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
    public static void wipe() {
        // Delete the backup folder recursively
        try {
            Path backupFolderPath = Paths.get(Variables.getBackupFolderPath());

            if (Files.exists(backupFolderPath)) {
                Files.walkFileTree(backupFolderPath, new SimpleFileVisitor<>() {
                    @NotNull
                    @Override
                    public FileVisitResult visitFile(@NotNull Path file, @NotNull BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @NotNull
                    @Override
                    public FileVisitResult postVisitDirectory(@NotNull Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
                System.out.println(">>> theLifesaver has deleted " + "the backups folder.");
            } else {
                System.out.println(">>> theLifesaver's backups " + "folder does not exist.");
            }
        } catch (IOException e) {
            generateErrorMessage("Error at `TL_murder` in `TL`", "Failed to delete backup folder.", "", e);
        }

        // Delete the config file
        try {
            Path configFilePath = Paths.get(Variables.getDotConfigPath());

            if (Files.exists(configFilePath)) {
                Files.delete(configFilePath);
                System.out.println(">>> theLifesaver has deleted " + "the config file.");
            } else {
                System.out.println(">>> theLifesaver's config file " + "does not exist.");
            }
        } catch (IOException e) {
            generateErrorMessage("Error at `TL_murder` in `TL`", "Failed to delete config file.", "", e);
        }

        System.out.println("=================== IMPORTANT! " + "===================");
        System.out.println("=== IF YOU SEE THIS IN SHIPPED CODE    " + "        ===");
        System.out.println("=== THEN SOMEBODY MESSED UP            " + "        ===");
        System.out.println("=== YOU SHOULDN'T BE ABLE TO SEE THIS  " + "        ===");
        System.out.println("=== REMEMBER TO DELETE `wipe`          " + " ===");
        System.out.println("=== BEFORE SHIPPING                    " + "        ===");
        System.out.println("=================== IMPORTANT! " + "===================");
    }

    private static void TL_initPostgres(String username, String password) {
        // check if postgres exists
        try {
            Process p = new ProcessBuilder("cmd", "/c", "where " + "psql").redirectErrorStream(true).start();
            int ret = p.waitFor();

            // 0 if psql is in path (psql is downloaded)
            // 1 otherwise
            if (ret != 0) {
                // download postgres using the installer inside
                // app/installer
                new ProcessBuilder("cmd", "/c", Depends.getPostgres()).inheritIO().start();
            }

        } catch (IOException | InterruptedException e) {
            generateErrorMessage("Error at TL_init_postgres", "FATAL. Failed to init postgres database.", "", e);
            throw new RuntimeException(e);
        }

        // then run psql
        // and run the following commands inside:
        // CREATE USER db_poultry WITH #####;
        // CREATE DATABASE db_poultry OWNER db_poultry;
        // GRANT ALL PRIVILEGES ON DATABASE db_poultry TO db_poultry;
        // ALTER USER db_poultry WITH SUPERUSER;

        String[] script = {
                "CREATE USER %s WITH %s;".formatted(username, password),
                "CREATE DATABASE %s OWNER %s;".formatted(username, username),
                "GRANT ALL PRIVILEGES %s ON  %sDATABASE;".formatted(username, username),
                "ALTER USER %s WITH SUPERUSER;".formatted(username)
        };

        try {
            Path ts = Files.createTempFile("db_init", ".sql");
            Files.write(ts, List.of(script));

            ProcessBuilder pb = new ProcessBuilder("psql", "-U", "postgres", "-f", ts.toString());
            pb.environment().put("PGPASSWORD", password);
            pb.redirectErrorStream(true);
            pb.inheritIO().start();
        } catch  (IOException e) {
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
            System.out.println(">>> theLifesaver detects: first " + "open.");
            Config.TL_writeConfig(Util.getDateNow());
        } else {
            return;
        }

        // initialize postgresql
        TL_initPostgres(app.getDatabaseName(), app.getDatabasePass());

        // make the backup folder
        Backup.TL_makeBackupFolder();

        // if it is the first open, we will clean all tables
        cleanTables(app.getConnection());

        // we also want to set the PostgreSQL privileges
        // TODO: make commandline calls here to give PostgreSQL
        //  privilages
        // do we even need that? that's like too complicated i
        // think...
    }
}
