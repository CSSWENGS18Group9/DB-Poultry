package org.db_poultry.theLifesaver;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;

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
     * deletes backup folder and the config file, essentially makes it so that the next open of the DBMS is the first
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
                System.out.println(">>> theLifesaver has deleted the backups folder.");
            } else {
                System.out.println(">>> theLifesaver's backups folder does not exist.");
            }
        } catch (IOException e) {
            generateErrorMessage("Error at `TL_murder` in `TL`", "Failed to delete backup folder.", "", e);
        }

        // Delete the config file
        try {
            Path configFilePath = Paths.get(Variables.getDotConfigPath());

            if (Files.exists(configFilePath)) {
                Files.delete(configFilePath);
                System.out.println(">>> theLifesaver has deleted the config file.");
            } else {
                System.out.println(">>> theLifesaver's config file does not exist.");
            }
        } catch (IOException e) {
            generateErrorMessage("Error at `TL_murder` in `TL`", "Failed to delete config file.", "", e);
        }

        System.out.println("=================== IMPORTANT! ===================");
        System.out.println("=== IF YOU SEE THIS IN SHIPPED CODE            ===");
        System.out.println("=== THEN SOMEBODY MESSED UP                    ===");
        System.out.println("=== YOU SHOULDN'T BE ABLE TO SEE THIS          ===");
        System.out.println("=== REMEMBER TO DELETE `wipe`           ===");
        System.out.println("=== BEFORE SHIPPING                            ===");
        System.out.println("=================== IMPORTANT! ===================");
    }

    /**
     * checks if it is the first open and if it is create the configuration file.
     */
    public static void TL_firstOpen(Connection conn) {
        // the basis of checking for the "first open" of the DBMS is if the `.config.dbpoultry` exists
        if (!Files.exists(Paths.get(Variables.getDotConfigPath()))) {
            // if it doesn't. Make it immediately!
            System.out.println(">>> theLifesaver detects: first open.");
            Config.TL_writeConfig(Util.getDateNow());
        } else {
            return;
        }

        // make the backup folder
        Backup.TL_makeBackupFolder();

        // if it is the first open, we will clean all tables
        cleanTables(conn);

        // we also want to set the PostgreSQL privileges
        // TODO: make commandline calls here to give PostgreSQL privilages
        // do we even need that? that's like too complicated i think...
    }
}
