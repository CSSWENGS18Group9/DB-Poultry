package org.db_poultry.theLifesaver;

import java.io.File;
import java.nio.file.Paths;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class Variables {
    // ================ DOT CONFIG VARIABLES ============
    private static final String DOT_CONFIG_PATH = "config.🐔";

    // ================ BACKUP VARIABLES ================
    private static final String BACKUP_FOLDER_PATH = "dbp_backups";
    private static final int BACKUP_INTERVALS = 7; // every X days, we create a backup
    private static final String BACKUP_FILE_PREFIX = "dbp_backup_"; // what comes after this is the date
    private static final int BACKUP_FOLDER_CAPACITY = 10; // the number of backups we want to store at a time

    public static String getDotConfigPath() {
        return DOT_CONFIG_PATH;
    }

    public static String getBackupFolderPath() {
        try {
            // Get the location of the running JAR file
            String jarPath = new File(Variables.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
            return Paths.get(jarPath, BACKUP_FOLDER_PATH).toString();
        } catch (Exception e) {
            generateErrorMessage(
                    "Error at `TL_loadConfig` in `Variables`.",
                    "FATAL. Get file location.",
                    "",
                    e
            );
        }

        return null;
    }

    public static int getBackupIntervals() {
        return BACKUP_INTERVALS;
    }

    public static String getBackupFilePrefix() {
        return BACKUP_FILE_PREFIX;
    }

    public static int getBackupFolderCapacity() {
        return BACKUP_FOLDER_CAPACITY;
    }
}
