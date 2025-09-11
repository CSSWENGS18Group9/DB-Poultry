package org.db_poultry.theLifesaver;

import java.nio.file.Paths;

import static org.db_poultry.errors.GenerateErrorMessageKt.generateErrorMessage;

public class Variables {
    // ================ DOT CONFIG VARIABLES ============
    private static final String APP_FOLDER = ".db_poultry";
    private static final String DOT_CONFIG_FILENAME = "db_config";

    // ================ BACKUP VARIABLES ================
    private static final String BACKUP_FOLDER_NAME = "dbp_backups";
    private static final int BACKUP_INTERVALS = 7;                  // every X days, we create a backup
    private static final String BACKUP_FILE_PREFIX = "dbp_backup_"; // what comes after this is the date
    private static final int BACKUP_FOLDER_CAPACITY = 10;           // number of backups to store

    // ================ ST IMAGE DIR VARIABLES ==========
    private static final String ST_IMAGE_FOLDER_NAME = "supply_type_images";

    // Getters
    public static String getHomeDirectory(){
        return System.getProperty("user.home");
    }

    public static String getAppFolder(){
        return APP_FOLDER;
    }

    public static String getDotConfigPath() {
        try {
            return Paths.get(getHomeDirectory(), APP_FOLDER, DOT_CONFIG_FILENAME).toString();
        } catch (Exception e) {
            generateErrorMessage(
                    "Error at `getDotConfigPath` in `Variables`.",
                    "FATAL. Could not resolve config file location.",
                    "",
                    e
            );
        }

        return "";
    }

    public static String getBackupFolderPath() {
        try {
            return Paths.get(getHomeDirectory(), APP_FOLDER, BACKUP_FOLDER_NAME).toString();
        } catch (Exception e) {
            generateErrorMessage(
                    "Error at `getBackupFolderPath` in `Variables`.",
                    "FATAL. Could not resolve backup directory inside app folder.",
                    "",
                    e
            );
        }

        return "";
    }

    public static String getENVFilePath() {
        try {
            return Paths.get(getHomeDirectory(), APP_FOLDER, ".env").toString();
        } catch (Exception e) {
            generateErrorMessage(
                    "Error at `getENVFilePath` in `Variables`.",
                    "FATAL. Could not resolve env file inside app folder.",
                    "",
                    e
            );
        }

        return "";
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

    public static String getSTImageFolderName() {
        try {
            return Paths.get(getHomeDirectory(), APP_FOLDER, ST_IMAGE_FOLDER_NAME).toString();
        } catch (Exception e) {
            generateErrorMessage(
                    "Error at `getSTImageFolderName` in `Variables`.",
                    "FATAL. Could not resolve `supply_type_images` directory inside app folder",
                    "",
                    e
            );
        }

        return "";
    }
}
