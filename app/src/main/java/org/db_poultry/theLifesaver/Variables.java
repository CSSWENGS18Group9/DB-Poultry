package org.db_poultry.theLifesaver;

import java.io.File;
import java.nio.file.Paths;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class Variables {
    // ================ DOT CONFIG VARIABLES ============
    private static final String DOT_FOLDER_NAME = ".🐔";
    private static final String DOT_CONFIG_FILENAME = "db_config";

    // ================ BACKUP VARIABLES ================
    private static final String BACKUP_FOLDER_NAME = "dbp_backups";
    private static final int BACKUP_INTERVALS = 7; // every X days, we create a backup
    private static final String BACKUP_FILE_PREFIX = "dbp_backup_"; // what comes after this is the date
    private static final int BACKUP_FOLDER_CAPACITY = 10; // number of backups to store

    public static String getDotConfigPath() {
        try {
            String userHome = System.getProperty("user.home");
            return Paths.get(userHome, DOT_FOLDER_NAME, DOT_CONFIG_FILENAME).toString();
        } catch (Exception e) {
            generateErrorMessage(
                    "Error at `getDotConfigPath` in `Variables`.",
                    "FATAL. Could not resolve config.🐔 file location.",
                    "",
                    e
            );
        }

        return null;
    }

    public static String getBackupFolderPath() {
        try {
            String userHome = System.getProperty("user.home");
            return Paths.get(userHome, DOT_FOLDER_NAME, BACKUP_FOLDER_NAME).toString();
        } catch (Exception e) {
            generateErrorMessage(
                    "Error at `TL_loadConfig` in `Variables`.",
                    "FATAL. Could not resolve backup directory inside .🐔.",
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
